package com.s3connect.util;

import com.s3connect.config.ConfigLoader.EnvironmentConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class S3ClientFactory {

    public static S3Client createS3Client(EnvironmentConfig config) {
        return S3Client.builder()
                .endpointOverride(URI.create(config.getHost()))
                .region(Region.of(config.getLocation()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())
                ))
                .build();
    }
}