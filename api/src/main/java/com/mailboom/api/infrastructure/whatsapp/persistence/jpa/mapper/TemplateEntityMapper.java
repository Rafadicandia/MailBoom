package com.mailboom.api.infrastructure.whatsapp.persistence.jpa.mapper;

import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.Template;
import com.mailboom.api.domain.model.whatsapp.valueobjects.*;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
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
                entity.getStatus() == null ? null : TemplateStatus.valueOf(entity.getStatus()),
                new UserId(entity.getOwnerId().getId())
        );
    }

    public TemplateEntity toEntity(Template domain) {
        // Map the components from the domain list to a map for the entity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(domain.getOwnerId().value());


        return new TemplateEntity(
                domain.getId(),
                domain.getName(),
                domain.getCategory().name(),
                domain.getParameterFormat().name(),
                mapComponentsToEntity(domain.getComponents()),
                domain.getLanguage().getCode(),
                domain.getStatus().toString(),
                userEntity
        );
    }

    private List<TemplateComponent> mapComponentsToDomain(Map<String, Object> componentsMap) {
        List<TemplateComponent> components = new ArrayList<>();
        if (componentsMap != null && componentsMap.containsKey("components")) {
            // New format: components is a list of objects
            List<Map<String, Object>> componentsList = (List<Map<String, Object>>) componentsMap.get("components");
            if (componentsList != null) {
                for (Map<String, Object> componentMap : componentsList) {
                    ComponentType type = ComponentType.valueOf((String) componentMap.get("type"));
                    String text = (String) componentMap.get("text");
                    Object example = componentMap.get("example");
                    
                    List<Button> buttons = null;
                    if (componentMap.containsKey("buttons")) {
                        List<Map<String, Object>> buttonsList = (List<Map<String, Object>>) componentMap.get("buttons");
                        if (buttonsList != null) {
                            buttons = new ArrayList<>();
                            for (Map<String, Object> buttonMap : buttonsList) {
                                String buttonType = (String) buttonMap.get("type");
                                String buttonText = (String) buttonMap.get("text");
                                String buttonUrl = (String) buttonMap.get("url");
                                buttons.add(new Button(buttonType, buttonText, buttonUrl));
                            }
                        }
                    }
                    
                    components.add(new TemplateComponent(type, text, example, buttons));
                }
            }
        } else if (componentsMap != null) {
            // Backward compatibility: old format was Map<String, String> with type as key and text as value
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
        List<Map<String, Object>> componentsList = new ArrayList<>();
        if (components != null) {
            for (TemplateComponent component : components) {
                Map<String, Object> componentMap = new HashMap<>();
                componentMap.put("type", component.type().name());
                if (component.text() != null) {
                    componentMap.put("text", component.text());
                }
                if (component.example() != null) {
                    componentMap.put("example", component.example());
                }
                if (component.buttons() != null && !component.buttons().isEmpty()) {
                    List<Map<String, Object>> buttonsList = new ArrayList<>();
                    for (Button button : component.buttons()) {
                        Map<String, Object> buttonMap = new HashMap<>();
                        buttonMap.put("type", button.type());
                        buttonMap.put("text", button.text());
                        if (button.url() != null) {
                            buttonMap.put("url", button.url());
                        }
                        buttonsList.add(buttonMap);
                    }
                    componentMap.put("buttons", buttonsList);
                }
                componentsList.add(componentMap);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("components", componentsList);
        return result;
    }
}
