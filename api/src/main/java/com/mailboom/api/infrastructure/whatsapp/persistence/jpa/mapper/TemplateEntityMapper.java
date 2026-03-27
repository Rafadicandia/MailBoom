package com.mailboom.api.infrastructure.whatsapp.persistence.jpa.mapper;

import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.*;
import com.mailboom.api.infrastructure.whatsapp.persistence.jpa.entity.TemplateEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TemplateEntityMapper {

    public Template toDomain(TemplateEntity entity) {
        // Map the components from the entity map to a list of TemplateComponent
        List<TemplateComponent> components = mapComponentsToDomain(entity.getComponents());

        // Assuming TemplateStatus and Category are mapped properly
        // You might need to add checks or conversions if Enums in DB differ from domain
        
        return new Template(
                entity.getId(),
                entity.getName(),
                Category.valueOf(entity.getCategory()),
                ParameterFormat.valueOf(entity.getParameterFormat()),
                components,
                Languajes.fromCode(entity.getLanguage()),
                entity.getStatus(),
                entity.getOwnerId()
        );
    }

    public TemplateEntity toEntity(Template domain) {
        return new TemplateEntity(
                domain.getId(),
                domain.getName(),
                domain.getCategory().name(),
                domain.getParameterFormat().name(),
                mapComponentsToEntity(domain.getComponents()),
                domain.getLanguage().getCode(),
                domain.getStatus(),
                domain.getOwnerId()
        );
    }

    private List<TemplateComponent> mapComponentsToDomain(Map<String, Object> componentsMap) {
        List<TemplateComponent> components = new ArrayList<>();
        if (componentsMap != null) {
            for (Map.Entry<String, Object> entry : componentsMap.entrySet()) {
                try {
                    ComponentType type = ComponentType.valueOf(entry.getKey());
                    String text = String.valueOf(entry.getValue());
                    components.add(new TemplateComponent(type, text));
                } catch (IllegalArgumentException e) {
                    // Log or handle the case where the key doesn't match an enum
                }
            }
        }
        return components;
    }

    private Map<String, Object> mapComponentsToEntity(List<TemplateComponent> components) {
        Map<String, Object> map = new HashMap<>();
        if (components != null) {
            for (TemplateComponent component : components) {
                map.put(component.type().name(), component.text());
            }
        }
        return map;
    }
}
