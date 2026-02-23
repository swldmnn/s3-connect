package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.config.ConfigLoader;
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

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Specify the environment to use.", required = true)
    private String environment;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Key of the object to download.", required = true)
    private String fileKey;

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

            String fileName = Paths.get(fileKey).getFileName().toString();
            Path destination = Paths.get(fileName);

            s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(config.getBucket())
                            .key(fileKey)
                            .build(),
                    destination
            );

            logger.info("Object downloaded successfully: {} -> {}", fileKey, destination.toAbsolutePath());
        } catch (Exception e) {
            logger.error("Error downloading object: {}", e.getMessage(), e);
        }
    }
}
