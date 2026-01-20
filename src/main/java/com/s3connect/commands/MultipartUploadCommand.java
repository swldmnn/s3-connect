package com.s3connect.commands;

import picocli.CommandLine;
import com.s3connect.config.ConfigLoader;
import com.s3connect.config.ConfigLoader.EnvironmentConfig;
import com.s3connect.util.S3ClientFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(
        name = "multipart-upload",
        description = "Perform a multipart upload to the specified bucket."
)
public class MultipartUploadCommand implements Runnable {

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Specify the environment to use.", required = true)
    private String environment;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path to the file to upload.", required = true)
    private String filePath;

    @CommandLine.Option(names = {"-k", "--key"}, description = "Key for the uploaded object.")
    private String objectKey;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    private boolean verbose;

    @Override
    public void run() {
        try {
            ConfigLoader configLoader = new ConfigLoader("src/main/resources/config.yaml");
            EnvironmentConfig config = configLoader.getEnvironmentConfig(environment);

            if (config == null) {
                System.err.println("Environment not found: " + environment);
                return;
            }

            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                System.err.println("File not found: " + filePath);
                return;
            }

            // Use the filename as the object key if not provided
            String key = (objectKey != null && !objectKey.isEmpty()) ? objectKey : file.getName();

            S3Client s3Client = S3ClientFactory.createS3Client(config);

            long partSize = 5 * 1024 * 1024; // 5 MB
            List<CompletedPart> completedParts = new ArrayList<>();

            String uploadId = s3Client.createMultipartUpload(CreateMultipartUploadRequest.builder()
                    .bucket(config.getBucket())
                    .key(key)
                    .build()).uploadId();

            try (FileInputStream fis = new FileInputStream(file);
                 FileChannel fileChannel = fis.getChannel()) {

                long fileSize = file.length();
                long position = 0;
                int partNumber = 1;

                while (position < fileSize) {
                    long bytesToRead = Math.min(partSize, fileSize - position);
                    ByteBuffer buffer = ByteBuffer.allocate((int) bytesToRead);
                    fileChannel.read(buffer);
                    buffer.flip();

                    UploadPartResponse uploadPartResponse = s3Client.uploadPart(UploadPartRequest.builder()
                                    .bucket(config.getBucket())
                                    .key(key)
                                    .uploadId(uploadId)
                                    .partNumber(partNumber)
                                    .contentLength(bytesToRead)
                                    .build(),
                            software.amazon.awssdk.core.sync.RequestBody.fromByteBuffer(buffer));

                    completedParts.add(CompletedPart.builder()
                            .partNumber(partNumber)
                            .eTag(uploadPartResponse.eTag())
                            .build());

                    position += bytesToRead;
                    partNumber++;
                }
            }

            s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                    .bucket(config.getBucket())
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build());

            System.out.println("Multipart upload completed successfully.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error during multipart upload: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        }
    }
}