package com.s3connect;

import picocli.CommandLine;
import com.s3connect.commands.ListCommand;

@CommandLine.Command(
        name = "s3connect",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "CLI tool to interact with S3-compatible servers.",
        subcommands = {ListCommand.class}
)
public class S3ConnectCLI implements Runnable {

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable verbose output.")
    private boolean verbose;

    @Override
    public void run() {
        if (verbose) {
            System.out.println("Verbose mode enabled.");
        } else {
            System.out.println("Basic mode enabled.");
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new S3ConnectCLI()).execute(args);
        System.exit(exitCode);
    }
}