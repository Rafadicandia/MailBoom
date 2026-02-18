package com.mailboom.api.infrastructure.common.metricsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CloudWatchClient {

    private final CloudWatchAsyncClient cloudWatchAsyncClient;

    public CloudWatchClient(CloudWatchAsyncClient cloudWatchAsyncClient) {
        this.cloudWatchAsyncClient = cloudWatchAsyncClient;
    }

    public CompletableFuture<Double> getMetric(String metricName) {
        Instant end = Instant.now();
        Instant start = end.minusSeconds(24 * 60 * 60); // Last 24 hours

        log.info("Fetching CloudWatch metric: {} from {} to {}", metricName, start, end);

        Metric metric = Metric.builder()
                .namespace("AWS/SES")
                .metricName(metricName)
                .build();

        MetricStat metricStat = MetricStat.builder()
                .metric(metric)
                .period(86400) // 1 day period
                .stat("Sum")
                .build();

        MetricDataQuery metricDataQuery = MetricDataQuery.builder()
                .id("m1")
                .metricStat(metricStat)
                .returnData(true)
                .build();

        GetMetricDataRequest request = GetMetricDataRequest.builder()
                .startTime(start)
                .endTime(end)
                .metricDataQueries(metricDataQuery)
                .build();

        return cloudWatchAsyncClient.getMetricData(request)
                .thenApply(response -> {
                    log.info("CloudWatch response for {}: {} results", metricName, response.metricDataResults().size());
                    
                    List<MetricDataResult> results = response.metricDataResults();
                    if (results.isEmpty()) {
                        log.warn("No metric data results for metric: {}", metricName);
                        return 0.0;
                    }
                    
                    MetricDataResult result = results.get(0);
                    List<Double> values = result.values();
                    List<Instant> timestamps = result.timestamps();
                    
                    log.info("Metric {} - Values: {}, Timestamps: {}", metricName, values, timestamps);
                    
                    if (values.isEmpty()) {
                        log.warn("No values found for metric: {}", metricName);
                        return 0.0;
                    }
                    
                    // Return the most recent value (last in the list)
                    Double value = values.get(values.size() - 1);
                    log.info("Metric {} value: {}", metricName, value);
                    return value;
                })
                .exceptionally(throwable -> {
                    log.error("Error fetching metric {}: {}", metricName, throwable.getMessage(), throwable);
                    return 0.0;
                });
    }
}
