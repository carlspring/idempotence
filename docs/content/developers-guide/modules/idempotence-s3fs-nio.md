# `idempotence-s3fs-nio`

This Maven module adds support for generating test files in S3 buckets using the
[carlspring/s3fs-nio](https://github.com/carlspring/s3fs-nio) library.

## Directory Structure (S3)

Test resources are copied to an S3 URI of the form:

```
s3:///[bucket-name]/[prefix]/[TestClass]-[testMethod]/[resource-path]
```

For example, with the default configuration:

```
s3:///idempotence-test-resources/MyTest-testSingleFile/foo.txt
```

## Configuration

Set the `org.carlspring.testing.idempotence.basedir` system property to an S3 URI:

| Example | Description |
|---------|-------------|
| `s3:///my-bucket` | Default AWS S3 endpoint, bucket `my-bucket` |
| `s3:///my-bucket/prefix` | Default AWS S3 endpoint, bucket `my-bucket`, key prefix `prefix` |
| `s3://localhost:9090/my-bucket` | Custom endpoint (e.g. MinIO), bucket `my-bucket` |

If no system property is set, the default base directory is `s3:///idempotence-test-resources`.

## Usage

Use the `S3TestResourceExtension` JUnit Jupiter extension instead of (or in addition to) the
standard `TestResourceExtension`:

```java
@ExtendWith(S3TestResourceExtension.class)
class MyTest {

    @Test
    @TestResources(@TestResource(source = "classpath:/foo.txt"))
    void testSingleFile() {
        // The file is available in S3 at:
        // s3:///idempotence-test-resources/MyTest-testSingleFile/foo.txt
    }

}
```

## S3 Credentials

S3 credentials and region are resolved by the AWS SDK's default credential provider chain.
For local development with MinIO, configure the endpoint via the system property and supply
credentials via the standard AWS environment variables or `~/.aws/credentials`.
