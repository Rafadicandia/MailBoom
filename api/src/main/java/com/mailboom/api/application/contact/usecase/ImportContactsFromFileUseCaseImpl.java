package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.ImportContactsFromFileUseCase;
import com.mailboom.api.application.contact.usecase.command.ImportContactsFromFileCommand;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.FileParserFactory;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.infrastructure.contact.dto.ContactData;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImportContactsFromFileUseCaseImpl implements ImportContactsFromFileUseCase {

    private final ContactRepository contactRepository;
    private final FileParserFactory parserFactory;
    private final ContactListRepository contactListRepository;

    @Override
    @Transactional
    public void execute(ImportContactsFromFileCommand command) {
        ContactListId contactListId = new ContactListId(UUID.fromString(command.listId()));
        Optional<ContactList> contactListOpt = contactListRepository.findById(contactListId);

        if (contactListOpt.isEmpty() || !contactListOpt.get().getOwner().equals(UserId.fromString(command.ownerId()))) {
            throw new IllegalArgumentException("Contact List not found or you don't have permission to access it.");
        }

        ContactFileParser parser = parserFactory.getParser(command.contentType());
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        List<ContactData> batch = new ArrayList<>();
        parser.parse(command.fileStream(), contactData -> {
            batch.add(contactData);
            if (batch.size() >= 1000) {
                saveBatch(batch, contactListId);
                batch.clear();
            }
        });

        if (!batch.isEmpty()) {
            saveBatch(batch, contactListId);
        }
    }

    private void saveBatch(List<ContactData> data, ContactListId listId) {
        List<Contact> contacts = data.stream()
                .map(d -> Contact.create(ContactId.generate(), listId, new Email(d.email()), new Name(d.name()), d.attributes(), true))
                .toList();
        try{
            contactRepository.saveAll(contacts);
            System.out.println("Contacts imported successfully");
            contactListRepository.save(contactListRepository.findById(listId).orElseThrow().updateTotalContacts(contacts.size()));
            System.out.println("Contact List updated successfully");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
