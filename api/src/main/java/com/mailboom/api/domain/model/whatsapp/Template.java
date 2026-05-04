package com.mailboom.api.domain.model.whatsapp;

import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.model.whatsapp.valueobjects.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
public class Template {
    private final UUID id;
    //private final String idMeta;
    private final String name;
    private final Category category;
    private final ParameterFormat parameterFormat;
    private final List<TemplateComponent> components;
    private final Languajes language;
    @Setter
    private TemplateStatus status;
    private final UserId ownerId;

    public Template(UUID id, String name, Category category, ParameterFormat parameterFormat, List<TemplateComponent> components, Languajes language, TemplateStatus status, UserId ownerId) {
        this.id = id;
        //this.idMeta = idMeta;
        this.name = name;
        this.category = category;
        this.parameterFormat = parameterFormat;
        this.components = components;
        this.language = language;
        this.status = status;
        this.ownerId = ownerId;
    }

    public static Template create(UUID id, String name, Category category, ParameterFormat parameterFormat, List<TemplateComponent> components, Languajes language, TemplateStatus status, UserId ownerId){
        return new Template(id, name, category, parameterFormat, components, language, status, ownerId);
    }

    public void updateStatusApproved(){
        this.setStatus(TemplateStatus.APPROVED);
    }

}
