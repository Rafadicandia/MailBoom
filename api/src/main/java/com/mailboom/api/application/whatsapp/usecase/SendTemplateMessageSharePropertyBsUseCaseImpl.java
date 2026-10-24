package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.SendTemplateMessageToListUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.SendTemplateToListCommand;
import com.mailboom.api.domain.model.common.valueobjects.Phone;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.port.out.ClientConfigRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.TemplateRepository;
import com.mailboom.api.domain.port.out.WhatsAppGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class SendTemplateMessageSharePropertyBsUseCaseImpl implements SendTemplateMessageToListUseCase {
    private final TemplateRepository templateRepository;
    private final ClientConfigRepository clientConfigRepository;
    private final WhatsAppGateway whatsAppGateway;
    private final ContactRepository contactRepository;

    @Override
    public void execute(SendTemplateToListCommand command) {
        Template template = templateRepository.findByName(command.templateName()).orElseThrow(() -> new RuntimeException("Template not found"));
        List<Contact> recipientList = contactRepository.findAllByContactListId(command.to());

        for (Contact c : recipientList) {
            // Skip contacts without phone number
            if (c.getPhone() == null) {
                continue;
            }
            String to = c.getPhone().toString().trim();
            String name = c.getName().toString().trim();


            List<String> parameters = new ArrayList<>();
            parameters.add(name);

            ClientConfig clientConfig = clientConfigRepository.findById(template.getOwnerId()).orElseThrow(() -> new RuntimeException("WhatsApp configuration not found for user: " + template.getOwnerId()));
            whatsAppGateway.sendTemplateMessage(Phone.fromString(to), template, parameters, clientConfig);
            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                log.info("La pausa fue interrumpida");
                break;
            }
        }

    }
}
