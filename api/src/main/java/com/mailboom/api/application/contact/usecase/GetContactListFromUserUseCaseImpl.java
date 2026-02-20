package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.GetContactListFromUserUseCase;
import com.mailboom.api.application.contact.usecase.command.GetContactListFromUserCommand;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GetContactListFromUserUseCaseImpl implements GetContactListFromUserUseCase {

    private final ContactListRepository contactListRepository;
    private final UserRepository userRepository;

    @Override
    public List<ContactList> execute(GetContactListFromUserCommand command) {
        UserId userId = new UserId(UUID.fromString(command.ownerId()));
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return contactListRepository.findAllByUserId(userId);
    }
}
