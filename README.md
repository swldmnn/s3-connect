# s3-connect

`s3-connect` is a simple CLI tool designed to interact with S3-compatible servers. It allows users to list objects in buckets, upload files, download objects, delete objects, and perform other operations using a straightforward command-line interface.

## Features
- List objects in a specified S3 bucket.
- Filter objects by prefix.
- Perform multipart uploads to S3 buckets.
- Download objects from S3 buckets.
- Delete objects from S3 buckets.
- Support for multiple environments via configuration file.
- Per-parameter overrides for host, port, bucket, location, access key, and secret key.
- Verbose mode for detailed debug output.

## Prerequisites
- Java 21 or higher installed.
- Gradle installed (or use the provided Gradle wrapper).

## Configuration

Connection details can be provided via a `config.yaml` file, CLI parameters, or a combination of both. CLI parameters always override values from the config file.

### config.yaml

Place `config.yaml` in `src/main/resources/`. Example:

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

## Running the CLI

### Convenience script (recommended)
Build and run using the wrapper script in the project root:
```bash
./s3-connect <command> [options]
```
The script builds automatically on first run and when source files change.

### Gradle
```bash
./gradlew run --args="<command> [options]"
```

## Global Options

These options apply to all commands and can be placed before or after the subcommand name.

| Option | Description |
|---|---|
| `-e, --environment` | Load connection details from a named environment in `config.yaml`. |
| `-h, --host` | Override host. |
| `-p, --port` | Override port. |
| `-b, --bucket` | Override bucket. |
| `-l, --location` | Override location/region. |
| `-a, --access-key` | Override access key. |
| `-s, --secret-key` | Override secret key. |
| `-v, --verbose` | Enable verbose debug output. |

## Commands

### list
List objects in the configured bucket:
```bash
./s3-connect list -e <environment> [--prefix <prefix>]
```
- `--prefix` (optional): Filter objects by prefix.

Examples:
```bash
./s3-connect list -e dev
./s3-connect list -e dev --prefix logs/
./s3-connect list -e dev --prefix logs/ -v
./s3-connect list -h https://s3.example.com -b my-bucket -l us-east-1 -a key -s secret
```

### multipart-upload
Upload a file using multipart upload:
```bash
./s3-connect multipart-upload -e <environment> -f <file-path> [-k <object-key>]
```
- `-f, --file` (required): Path to the local file to upload.
- `-k, --key` (optional): Object key in the bucket. Defaults to the filename.

Examples:
```bash
./s3-connect multipart-upload -e dev -f /path/to/file.tar.gz
./s3-connect multipart-upload -e dev -f /path/to/file.tar.gz -k backups/file.tar.gz
```

### download
Download an object from the bucket to the current directory:
```bash
./s3-connect download -e <environment> -o <object-key>
```
- `-o, --object` (required): Key of the object to download.

Examples:
```bash
./s3-connect download -e dev -o backups/file.tar.gz
./s3-connect download -e dev -o path/to/file.txt -b other-bucket
```

### delete
Delete an object from the bucket:
```bash
./s3-connect delete -e <environment> -k <object-key>
```
- `-k, --key` (required): Key of the object to delete.

Examples:
```bash
./s3-connect delete -e dev -k my-object-key
./s3-connect delete -e dev -k path/to/old-file.txt -v
```
