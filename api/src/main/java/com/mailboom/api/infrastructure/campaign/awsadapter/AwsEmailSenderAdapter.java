package com.mailboom.api.infrastructure.campaign.awsadapter;

import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.EmailSender;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.dto.GeneralMetricsDTO;
import com.mailboom.api.infrastructure.common.exception.EmailSendingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.util.List;


@Slf4j
@Component
public class AwsEmailSenderAdapter implements EmailSender {
    private final SesV2Client sesClient;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final String identityArn;

    private static final int BATCH_SIZE = 50;

    public AwsEmailSenderAdapter(SesV2Client sesClient,
                                 ContactRepository contactRepository,
                                 UserRepository userRepository,
                                 @Value("${mailboom.domain.arn}") String identityArn) {
        this.sesClient = sesClient;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.identityArn = identityArn;
    }

    @Override
    public void send(Campaign campaign) {
        User owner = userRepository.findById(campaign.getOwner()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Contact> recipientList = contactRepository.findAllByContactListId(campaign.getRecipientList());
        List<Contact> subscribed = recipientList.stream().filter(Contact::isSubscribed).toList();
        List<String> totalEmailsInList = subscribed.stream().map(Contact::getEmail).map(Email::toString).toList();

        EmailContent emailContent = EmailContent.builder().simple(Message.builder()
                        .subject(Content.builder().data(campaign.getSubject().value()).charset("UTF-8").build())
                        .body(Body.builder()
                                .html(Content.builder().data(campaign.getHtmlContent().value()).charset("UTF-8").build())
                                .build())
                        .build())
                .build();

        for (int i = 0; i < totalEmailsInList.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, totalEmailsInList.size());
            List<String> batch = totalEmailsInList.subList(i, endIndex);

            Destination destination = Destination.builder()
                    .bccAddresses(batch).build();

            SendEmailRequest emailRequest =
                    SendEmailRequest.builder()
                            .fromEmailAddress(campaign.getSenderIdentity().value())
                            .fromEmailAddressIdentityArn(identityArn)
                            .destination(destination)
                            .replyToAddresses(owner.getEmail().email())
                            .feedbackForwardingEmailAddress(owner.getEmail().email())
                            .content(emailContent)
                            .build();
            try {
                sesClient.sendEmail(emailRequest);

            } catch (SesV2Exception e) {
                log.error("Error sending email batch for campaign {}: {}", campaign.getId().value(), e.getMessage());
                throw new EmailSendingException("Error sending email batch for campaign " + campaign.getId().value() + e.getMessage());
            }


        }
    }


}
