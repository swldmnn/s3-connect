package com.s3connect.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigLoader {

    private final Map<String, EnvironmentConfig> environments;

    public ConfigLoader(String configFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigFile configFile = mapper.readValue(new File(configFilePath), ConfigFile.class);
        this.environments = configFile.getEnvironments();
    }

    public EnvironmentConfig getEnvironmentConfig(String environment) {
        return environments.get(environment);
    }

    public static class ConfigFile {
        private Map<String, EnvironmentConfig> environments;

        public Map<String, EnvironmentConfig> getEnvironments() {
            return environments;
        }

        public void setEnvironments(Map<String, EnvironmentConfig> environments) {
            this.environments = environments;
        }
    }

    public static class EnvironmentConfig {
        private String host;
        private int port;
        private String bucket;
        private String accessKey;
        private String secretKey;
        private String location;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}