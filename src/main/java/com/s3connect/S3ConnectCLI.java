package com.s3connect;

import picocli.CommandLine;
import com.s3connect.commands.ListCommand;
import com.s3connect.commands.MultipartUploadCommand;
import com.s3connect.commands.DeleteCommand;
import com.s3connect.commands.DownloadCommand;
import com.s3connect.config.ConfigLoader;
import com.s3connect.config.ConfigLoader.EnvironmentConfig;
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

    @CommandLine.Option(names = {"-e", "--environment"}, description = "Environment to load from config.", scope = CommandLine.ScopeType.INHERIT)
    public String environment;

    @CommandLine.Option(names = {"-h", "--host"}, description = "Override host.", scope = CommandLine.ScopeType.INHERIT)
    public String host;

    @CommandLine.Option(names = {"-p", "--port"}, description = "Override port.", scope = CommandLine.ScopeType.INHERIT)
    public Integer port;

    @CommandLine.Option(names = {"-b", "--bucket"}, description = "Override bucket.", scope = CommandLine.ScopeType.INHERIT)
    public String bucket;

    @CommandLine.Option(names = {"-l", "--location"}, description = "Override location.", scope = CommandLine.ScopeType.INHERIT)
    public String location;

    @CommandLine.Option(names = {"-a", "--access-key"}, description = "Override access key.", scope = CommandLine.ScopeType.INHERIT)
    public String accessKey;

    @CommandLine.Option(names = {"-s", "--secret-key"}, description = "Override secret key.", scope = CommandLine.ScopeType.INHERIT)
    public String secretKey;

    @CommandLine.Option(names = {"-t", "--trust-all-certs"}, description = "Disable SSL certificate validation.", scope = CommandLine.ScopeType.INHERIT)
    public boolean trustAllCerts;

    public EnvironmentConfig resolveConfig() throws Exception {
        EnvironmentConfig config = new EnvironmentConfig();

        if (environment != null) {
            ConfigLoader configLoader = new ConfigLoader("src/main/resources/config.yaml");
            EnvironmentConfig envConfig = configLoader.getEnvironmentConfig(environment);
            if (envConfig == null) {
                throw new IllegalArgumentException("Environment not found: " + environment);
            }
            config.setHost(envConfig.getHost());
            config.setPort(envConfig.getPort());
            config.setBucket(envConfig.getBucket());
            config.setLocation(envConfig.getLocation());
            config.setAccessKey(envConfig.getAccessKey());
            config.setSecretKey(envConfig.getSecretKey());
        }

        if (host != null) config.setHost(host);
        if (port != null) config.setPort(port);
        if (bucket != null) config.setBucket(bucket);
        if (location != null) config.setLocation(location);
        if (accessKey != null) config.setAccessKey(accessKey);
        if (secretKey != null) config.setSecretKey(secretKey);

        if (config.getHost() == null) throw new IllegalStateException("Missing required parameter: host (-h) or environment (-e)");
        if (config.getBucket() == null) throw new IllegalStateException("Missing required parameter: bucket (-b) or environment (-e)");
        if (config.getLocation() == null) throw new IllegalStateException("Missing required parameter: location (-l) or environment (-e)");
        if (config.getAccessKey() == null) throw new IllegalStateException("Missing required parameter: access-key (-a) or environment (-e)");
        if (config.getSecretKey() == null) throw new IllegalStateException("Missing required parameter: secret-key (-s) or environment (-e)");

        return config;
    }

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
