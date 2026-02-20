package com.mailboom.api.infrastructure.common.metricsservice;

import com.mailboom.api.infrastructure.common.dto.GeneralMetricsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.GetAccountRequest;
import software.amazon.awssdk.services.sesv2.model.GetAccountResponse;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class GetGeneralMetricsService {
    private final SesV2Client sesV2Client;
    private final CloudWatchClient cloudWatchClient;

    public GeneralMetricsDTO getAccountGeneralMetrics() {
        try {
            GetAccountResponse response = sesV2Client.getAccount(GetAccountRequest.builder().build());
            log.info("SES GetAccount response received");
            
            // Fetch all SES metrics asynchronously
            // SES CloudWatch metric names: Send, Delivery, Bounce, Complaint, Reject
            CompletableFuture<Double> sentFuture = cloudWatchClient.getMetric("Send");
            CompletableFuture<Double> deliveredFuture = cloudWatchClient.getMetric("Delivery");
            CompletableFuture<Double> bouncesFuture = cloudWatchClient.getMetric("Bounce");
            CompletableFuture<Double> complaintsFuture = cloudWatchClient.getMetric("Complaint");
            CompletableFuture<Double> rejectsFuture = cloudWatchClient.getMetric("Reject");
            
            // Wait for all futures to complete
            CompletableFuture.allOf(sentFuture, deliveredFuture, bouncesFuture, complaintsFuture, rejectsFuture).join();
            
            long totalSent = sentFuture.join().longValue();
            long totalDelivered = deliveredFuture.join().longValue();
            long totalBounces = bouncesFuture.join().longValue();
            long totalComplaints = complaintsFuture.join().longValue();
            long totalRejects = rejectsFuture.join().longValue();

            log.info("Metrics - Sent: {}, Delivered: {}, Bounces: {}, Complaints: {}, Rejects: {}", 
                    totalSent, totalDelivered, totalBounces, totalComplaints, totalRejects);

            return new GeneralMetricsDTO(totalDelivered, totalBounces, totalComplaints, totalRejects);
        } catch (SesV2Exception e) {
            log.error("Error getting account metrics from AWS SES V2: {}", e.getMessage());
            return new GeneralMetricsDTO(0, 0, 0, 0);
        } catch (Exception e) {
            log.error("Unexpected error getting account metrics: {}", e.getMessage(), e);
            return new GeneralMetricsDTO(0, 0, 0, 0);
        }
    }
}
