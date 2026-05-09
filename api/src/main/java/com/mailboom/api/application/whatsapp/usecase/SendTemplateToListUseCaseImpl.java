package com.mailboom.api.application.whatsapp.usecase;

import com.mailboom.api.application.whatsapp.port.in.SendTemplateToListUseCase;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class SendTemplateToListUseCaseImpl implements SendTemplateToListUseCase {

    private final TemplateRepository templateRepository;
    private final ClientConfigRepository clientConfigRepository;
    private final WhatsAppGateway whatsAppGateway;
    private final ContactRepository contactRepository;

    @Override
    public void execute(SendTemplateToListCommand command) {
        Template template = templateRepository.findByName(command.templateName()).orElseThrow(() -> new RuntimeException("Template not found"));
        List<Contact> recipientList = contactRepository.findAllByContactListId(command.to());

        for (Contact c : recipientList) {
            String to = c.getPhone().toString().trim();
            String name = c.getName().toString().trim();
            List<String> extract = (List<String>) c.getCustomFields().get("Pedido");
            String phrase = String.join(" en ", extract);
            List<String> parameters = new ArrayList<>();
            parameters.add(name);
            parameters.add(phrase);
            ClientConfig clientConfig = clientConfigRepository.findById(template.getOwnerId()).orElseThrow(() -> new RuntimeException("WhatsApp configuration not found for user: " + template.getOwnerId()));
            whatsAppGateway.sendTemplateMessage(Phone.fromString(to), template, parameters, clientConfig);
            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                System.err.println("La pausa fue interrumpida");
                break;
            }
        }

    }


}
