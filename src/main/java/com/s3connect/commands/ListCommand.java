package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.S3ConnectCLI;
import com.s3connect.config.ConfigLoader.EnvironmentConfig;
import com.s3connect.util.S3ClientFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@CommandLine.Command(
        name = "list",
        description = "List objects in the specified bucket."
)
public class ListCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ListCommand.class);

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.ParentCommand
    private S3ConnectCLI parent;

    @CommandLine.Option(names = {"--prefix"}, description = "Filter objects by prefix.")
    private String prefix;

    @Override
    public void run() {
        if (parent.environment == null && parent.host == null) {
            spec.commandLine().usage(System.out);
            return;
        }
        try {
            EnvironmentConfig config = parent.resolveConfig();

            S3Client s3Client = S3ClientFactory.createS3Client(config, parent.trustAllCerts);

            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(config.getBucket());
            if (prefix != null) {
                requestBuilder.prefix(prefix);
            }
            ListObjectsV2Request request = requestBuilder.build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            List<S3Object> objects = response.contents();

            if (objects.isEmpty()) {
                logger.info("No objects found.");
            } else {
                for (S3Object object : objects) {
                    logger.info("- {} ({} bytes)", object.key(), object.size());
                }
            }
        } catch (Exception e) {
            logger.error("Error listing objects: {}", e.getMessage(), e);
        }
    }
}