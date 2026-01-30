package com.mailboom.api.application.usecase;

import com.mailboom.api.application.exception.UserWithEmailAlreadyExistsException;
import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.repository.UserRepository;
import com.mailboom.api.domain.model.valueobjects.PlanType;
import com.mailboom.api.infrastructure.persistence.adapter.UserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
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
    private UserRepositoryAdapter userRepository;


    @InjectMocks
    private CreateNewUserUseCase createNewUserUseCase;

    @Test
    void shouldCreateAndSaveUser_whenEmailDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        PlanType plan = PlanType.FREE;

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = createNewUserUseCase.execute(email, password, plan);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail().email());
        assertEquals(plan, result.getPlan());
        assertNotNull(result.getId());
        assertNotNull(result.getPassword().value());
        assertEquals(0, result.getEmailsSentThisMonth().amountOfEmails());

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        String email = "existing@example.com";
        String password = "password123";
        PlanType plan = PlanType.FREE;

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        UserWithEmailAlreadyExistsException exception = assertThrows(UserWithEmailAlreadyExistsException.class, () -> {
            createNewUserUseCase.execute(email, password, plan);
        });

        assertEquals("User with email " + email + " already exists", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}
