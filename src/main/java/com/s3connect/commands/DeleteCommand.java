package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.config.ConfigLoader;
import com.s3connect.config.ConfigLoader.EnvironmentConfig;
import com.s3connect.util.S3ClientFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandLine.Command(
        name = "delete",
        description = "Delete an object from the specified bucket."
)
public class DeleteCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DeleteCommand.class);

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Specify the environment to use.", required = true)
    private String environment;

    @CommandLine.Option(names = {"-k", "--key"}, description = "Key of the object to delete.", required = true)
    private String objectKey;

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

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(objectKey)
                    .build());

            logger.info("Object deleted successfully: {}", objectKey);
        } catch (Exception e) {
            logger.error("Error deleting object: {}", e.getMessage(), e);
        }
    }
}