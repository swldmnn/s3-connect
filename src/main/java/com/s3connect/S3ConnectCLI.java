package com.s3connect;

import picocli.CommandLine;
import com.s3connect.commands.ListCommand;
import com.s3connect.commands.MultipartUploadCommand;
import com.s3connect.commands.DeleteCommand;
import com.s3connect.commands.DownloadCommand;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

@CommandLine.Command(
        name = "s3connect",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "CLI tool to interact with S3-compatible servers.",
        subcommands = {
            ListCommand.class,
            MultipartUploadCommand.class,
            DeleteCommand.class,
            DownloadCommand.class
        }
)
public class S3ConnectCLI implements Runnable {

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable verbose output.", scope = CommandLine.ScopeType.INHERIT)
    public boolean verbose;

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Specify the environment to use.", required = true, scope = CommandLine.ScopeType.INHERIT)
    public String environment;

    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    static void configureLogging(boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.setLevel(verbose ? Level.DEBUG : Level.INFO);
    }

    public static void main(String[] args) {
        S3ConnectCLI cli = new S3ConnectCLI();
        int exitCode = new CommandLine(cli)
                .setExecutionStrategy(parseResult -> {
                    configureLogging(cli.verbose);
                    return new CommandLine.RunLast().execute(parseResult);
                })
                .execute(args);
        System.exit(exitCode);
    }
}
