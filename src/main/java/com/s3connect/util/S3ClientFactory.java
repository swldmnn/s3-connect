package com.s3connect.util;

import com.s3connect.config.ConfigLoader.EnvironmentConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.auth.aws.scheme.AwsV4AuthScheme;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.AttributeMap;

import java.net.URI;

public class S3ClientFactory {

    public static S3Client createS3Client(EnvironmentConfig config, boolean trustAllCerts) {
        UrlConnectionHttpClient.Builder httpClientBuilder = UrlConnectionHttpClient.builder();

        SdkHttpClient httpClient;
        if (trustAllCerts) {
            AttributeMap attributeMap = AttributeMap.builder()
                    .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
                    .build();
            httpClient = httpClientBuilder.buildWithDefaults(attributeMap);
        } else {
            httpClient = httpClientBuilder.build();
        }

        return S3Client.builder()
                .endpointOverride(URI.create(config.getHost()))
                .region(Region.of(config.getLocation()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())
                ))
                .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .chunkedEncodingEnabled(false)
                            .build())
                .putAuthScheme(AwsV4AuthScheme.create())
                .httpClient(httpClient)
                .build();
    }
}