# s3-connect

`s3-connect` is a simple CLI tool designed to interact with S3-compatible servers. It allows users to test connections, list objects in buckets, and perform other operations using a straightforward command-line interface.

## Features
- List objects in a specified S3 bucket.
- Filter objects by prefix.
- Support for multiple environments via configuration.
- Verbose mode for detailed output.

## Prerequisites
- Java 21 or higher installed.
- Gradle installed (or use the provided Gradle wrapper).
- A valid `config.yaml` file in the `src/main/resources/` directory with environment configurations.

## Configuration
The `config.yaml` file should define environments and their respective S3 connection details. Example:

```yaml
environments:
  dev:
    host: "https://s3.dev.example.com"
    port: 443
    bucket: "my-bucket"
    accessKey: "your-access-key"
    secretKey: "your-secret-key"
    location: "us-east-1"
```

## Usage

### Build the Project
To build the project, run:
```bash
./gradlew clean build
```

### Run the CLI
To run the CLI, use:
```bash
./gradlew run --args="<command> [options]"
```

### Commands

#### List Objects
List objects in a specified bucket:
```bash
./gradlew run --args="list -e <environment> [-p <prefix>] [-v]"
```
- `-e, --environment` (required): Specify the environment to use.
- `-p, --prefix` (optional): Filter objects by prefix.
- `-v, --verbose` (optional): Enable verbose output.

Example:
```bash
./gradlew run --args="list -e dev -p logs/ -v"
```
