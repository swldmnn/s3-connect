package com.s3connect;

import picocli.CommandLine;
import com.s3connect.commands.ListCommand;
import com.s3connect.commands.MultipartUploadCommand;
import com.s3connect.commands.DeleteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandLine.Command(
        name = "s3connect",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "CLI tool to interact with S3-compatible servers.",
        subcommands = {ListCommand.class, MultipartUploadCommand.class, DeleteCommand.class}
)
public class S3ConnectCLI implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(S3ConnectCLI.class);

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    private boolean verbose;

    @Override
    public void run() {
        if (verbose) {
            logger.info("Verbose mode enabled.");
        } else {
            logger.info("Basic mode enabled.");
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new S3ConnectCLI()).execute(args);
        System.exit(exitCode);
    }
}