package com.mailboom.api.application.port.in;

import com.mailboom.api.application.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.User;

public interface CreateUserUseCase {
    User execute(CreateUserCommand command);
}
