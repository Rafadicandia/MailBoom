package com.mailboom.api.application.usecase;

import com.mailboom.api.application.port.in.CreateContactListUseCase;
import com.mailboom.api.application.port.in.ImportContactsFromFileUseCase;
import com.mailboom.api.domain.model.Contact;
import com.mailboom.api.domain.model.valueobjects.*;
import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.FileParserFactory;
import com.mailboom.api.infrastructure.dto.ContactData;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ImportContactsFromFileUseCaseImpl implements ImportContactsFromFileUseCase {

    private final ContactRepository contactRepository;
    private final FileParserFactory parserFactory;
    private final ContactListRepository contactListRepository;
    private final CreateContactListUseCase createContactListUseCase;

    @Override
    @Transactional
    public void execute(ContactListId listId, UserId ownerId, InputStream fileStream, String contentType) {

        if (!contactListRepository.findById(listId.value()).isPresent() && !contactListRepository.findById(listId.value()).get().getOwner().equals(ownerId)) {
            throw new IllegalArgumentException("Contact List is not assigned to owner id. Please proceed to create a new contact list");
        }

        ContactFileParser parser = parserFactory.getParser(contentType);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        List<ContactData> batch = new ArrayList<>();
        parser.parse(fileStream, contactData -> {
            batch.add(contactData);
            if (batch.size() >= 1000) {
                saveBatch(batch, listId);
                batch.clear();
            }
        });

        if (!batch.isEmpty()) {
            saveBatch(batch, listId);
        }
    }

    private void saveBatch(List<ContactData> data, ContactListId listId) {
        List<Contact> contacts = data.stream()
                .map(d -> Contact.create(ContactId.generate(), listId, new Email(d.email()), new Name(d.name()), d.attributes(), true))
                .toList();
        contactRepository.saveAll(contacts);
    }
}
