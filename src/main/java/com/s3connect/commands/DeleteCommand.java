package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.S3ConnectCLI;
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

    @CommandLine.ParentCommand
    private S3ConnectCLI parent;

    @CommandLine.Option(names = {"-k", "--key"}, description = "Key of the object to delete.", required = true)
    private String objectKey;

    @Override
    public void run() {
        try {
            EnvironmentConfig config = parent.resolveConfig();

            S3Client s3Client = S3ClientFactory.createS3Client(config, parent.trustAllCerts);

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