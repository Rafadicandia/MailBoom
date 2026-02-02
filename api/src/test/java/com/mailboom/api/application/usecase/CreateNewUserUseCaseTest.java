package com.mailboom.api.application.usecase;

import com.mailboom.api.application.exception.UserWithEmailAlreadyExistsException;
import com.mailboom.api.application.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.domain.model.valueobjects.PlanType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateNewUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserUseCaseImpl createNewUserUseCase;

    @Test
    void shouldCreateAndSaveUser_whenEmailDoesNotExist() {

        String email = "test@example.com";
        String password = "password123";
        String name = "Test User";

        CreateUserCommand command = new CreateUserCommand(email, password, name);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = createNewUserUseCase.execute(command);

        assertNotNull(result);
        assertEquals(email, result.getEmail().email());
        assertEquals(PlanType.FREE, result.getPlan());
        assertNotNull(result.getId());
        assertNotNull(result.getPassword().value());
        assertEquals(0, result.getEmailsSentThisMonth().amountOfEmails());

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {

        String email = "existing@example.com";
        String password = "password123";
        String name = "Existing User";

        CreateUserCommand command = new CreateUserCommand(email, password, name);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        UserWithEmailAlreadyExistsException exception = assertThrows(UserWithEmailAlreadyExistsException.class, () -> {
            createNewUserUseCase.execute(command);
        });

        assertEquals("User with email " + email + " already exists", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}
