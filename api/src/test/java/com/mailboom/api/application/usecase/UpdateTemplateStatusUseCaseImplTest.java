package com.mailboom.api.application.usecase;

import com.mailboom.api.application.whatsapp.usecase.UpdateTemplateStatusUseCaseImpl;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.Category;
import com.mailboom.api.domain.model.whatsapp.valueobjects.Languajes;
import com.mailboom.api.domain.model.whatsapp.valueobjects.ParameterFormat;
import com.mailboom.api.domain.model.whatsapp.valueobjects.TemplateStatus;
import com.mailboom.api.domain.port.out.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTemplateStatusUseCaseImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private UpdateTemplateStatusUseCaseImpl updateTemplateStatusUseCase;

    private UUID templateId;
    private Template template;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        template = new Template(
            templateId,
            "test_template",
            Category.MARKETING,
            ParameterFormat.NAMED,
            Collections.emptyList(),
            Languajes.ENGLISH_US,
            TemplateStatus.PENDING,
            UserId.generate()
        );
    }

    @Test
    void updateStatus_shouldUpdateAndSaveTemplate_whenTemplateExists() {
        // Given
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(templateRepository.save(any(Template.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        updateTemplateStatusUseCase.updateStatus(templateId, TemplateStatus.APPROVED);

        // Then
        assertEquals(TemplateStatus.APPROVED, template.getStatus());
        verify(templateRepository).findById(templateId);
        verify(templateRepository).save(template);
    }

    @Test
    void updateStatus_shouldThrowException_whenTemplateDoesNotExist() {
        // Given
        when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> updateTemplateStatusUseCase.updateStatus(templateId, TemplateStatus.APPROVED));
        
        assertEquals("Template not found", exception.getMessage());
        verify(templateRepository).findById(templateId);
        verify(templateRepository, never()).save(any());
    }
}
