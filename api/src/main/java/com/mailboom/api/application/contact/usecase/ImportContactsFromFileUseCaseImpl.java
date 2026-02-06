package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.ImportContactsFromFileUseCase;
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
import com.mailboom.api.infrastructure.dto.ContactData;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImportContactsFromFileUseCaseImpl implements ImportContactsFromFileUseCase {

    private final ContactRepository contactRepository;
    private final FileParserFactory parserFactory;
    private final ContactListRepository contactListRepository;

    @Override
    @Transactional
    public void execute(ContactListId listId, UserId ownerId, InputStream fileStream, String contentType) {
        Optional<ContactList> contactListOpt = contactListRepository.findById(new ContactListId(listId.value()));

        if (contactListOpt.isEmpty() || !contactListOpt.get().getOwner().equals(ownerId)) {
            throw new IllegalArgumentException("Contact List not found or you don't have permission to access it.");
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
