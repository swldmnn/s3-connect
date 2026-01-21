package com.s3connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting S3 Connect CLI...");
        int exitCode = new CommandLine(new S3ConnectCLI()).execute(args);
        System.exit(exitCode);
    }
}