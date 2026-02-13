package com.mailboom.api.infrastructure.common.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
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
    public SesV2Client sesV2Client() {
        if ("NOT_SET".equals(region) || "NOT_SET".equals(accessKey)) {
            throw new IllegalStateException(
                    "AWS SES Client failed to initialize: Check if 'spring.cloud.aws.region.static' " +
                            "and credentials are set in your properties or environment variables."
            );
        }
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
            software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(accessKey, secretKey)
        );

        SesV2ClientBuilder builder = SesV2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }
}
