package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.config.ConfigLoader;
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

    @CommandLine.Option(names = {"-p", "--prefix"}, description = "Filter objects by prefix.")
    private String prefix;

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Specify the environment to use.", required = true)
    private String environment;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    private boolean verbose;

    @Override
    public void run() {
        try {
            ConfigLoader configLoader = new ConfigLoader("src/main/resources/config.yaml");
            EnvironmentConfig config = configLoader.getEnvironmentConfig(environment);

            if (config == null) {
                logger.error("Environment not found: {}", environment);
                return;
            }

            S3Client s3Client = S3ClientFactory.createS3Client(config);

            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(config.getBucket())
                    .prefix(prefix)
                    .build();

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