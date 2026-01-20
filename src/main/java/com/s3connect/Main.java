package com.s3connect;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting S3 Connect CLI...");
        int exitCode = new CommandLine(new S3ConnectCLI()).execute(args);
        System.exit(exitCode);
    }
}