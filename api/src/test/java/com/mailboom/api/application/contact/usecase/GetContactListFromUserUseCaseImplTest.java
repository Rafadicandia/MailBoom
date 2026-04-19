package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.GetContactListFromUserCommand;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetContactListFromUserUseCaseImplTest {
    @InjectMocks
    private GetContactListFromUserUseCaseImpl getContactListFromUserUseCase;

    @Mock
    private ContactListRepository contactListRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldGetContactListFromUser() {
        // Given
        var userId = new UserId(UUID.randomUUID());
        var command = new GetContactListFromUserCommand(userId.toString());
        var contactList = ContactList.create(userId, new com.mailboom.api.domain.model.common.valueobjects.Name("Test List"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(contactListRepository.findAllByUserId(userId)).thenReturn(List.of(contactList));

        // When
        var result = getContactListFromUserUseCase.execute(command);

        // Then
        assertEquals(List.of(contactList), result);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        var userId = new UserId(UUID.randomUUID());
        var command = new GetContactListFromUserCommand(userId.toString());

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> getContactListFromUserUseCase.execute(command));
    }
}
