package com.mailboom.api.infrastructure.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.SesV2ClientBuilder;

import java.net.URI;

@Configuration
public class AwsConfig {

    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final String endpoint;

    public AwsConfig(
            @Value("${spring.cloud.aws.region}") String region,
            @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${spring.cloud.aws.ses.endpoint:#{null}}") String endpoint
    ) {
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if ("NOT_SET".equals(accessKey) || "NOT_SET".equals(secretKey)) {
            throw new IllegalStateException(
                    "AWS credentials not configured. Check 'spring.cloud.aws.credentials.access-key' " +
                            "and 'spring.cloud.aws.credentials.secret-key' in your properties or environment variables."
            );
        }
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
    }

    @Bean
    public SesV2Client sesV2Client(AwsCredentialsProvider credentialsProvider) {
        if ("NOT_SET".equals(region)) {
            throw new IllegalStateException(
                    "AWS SES Client failed to initialize: Check if 'spring.cloud.aws.region' " +
                            "is set in your properties or environment variables."
            );
        }

        SesV2ClientBuilder builder = SesV2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient(AwsCredentialsProvider credentialsProvider) {
        if ("NOT_SET".equals(region)) {
            throw new IllegalStateException(
                    "AWS CloudWatch Client failed to initialize: Check if 'spring.cloud.aws.region' " +
                            "is set in your properties or environment variables."
            );
        }

        return CloudWatchAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
