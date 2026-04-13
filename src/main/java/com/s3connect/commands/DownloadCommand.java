package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.S3ConnectCLI;
import com.s3connect.config.ConfigLoader.EnvironmentConfig;
import com.s3connect.util.S3ClientFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(
        name = "download",
        description = "Download an object from the specified bucket."
)
public class DownloadCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DownloadCommand.class);

    @CommandLine.ParentCommand
    private S3ConnectCLI parent;

    @CommandLine.Option(names = {"-o", "--object"}, description = "Key of the object to download.", required = true)
    private String objectKey;

    @Override
    public void run() {
        try {
            EnvironmentConfig config = parent.resolveConfig();

            S3Client s3Client = S3ClientFactory.createS3Client(config, parent.trustAllCerts);

            String fileName = Paths.get(objectKey).getFileName().toString();
            Path destination = Paths.get(fileName);

            s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(config.getBucket())
                            .key(objectKey)
                            .build(),
                    destination
            );

            logger.info("Object downloaded successfully: {} -> {}", objectKey, destination.toAbsolutePath());
        } catch (Exception e) {
            logger.error("Error downloading object: {}", e.getMessage(), e);
        }
    }
}
