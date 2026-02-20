package com.mailboom.api.application.admin.usecase;

import com.mailboom.api.application.admin.in.port.GetAllContactListsUseCase;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.port.out.ContactListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GetAllContactListsUseCaseImpl implements GetAllContactListsUseCase {
    private final ContactListRepository contactListRepository;

    @Override
    public List<ContactList> execute() {
        return contactListRepository.findAll();
    }
}
