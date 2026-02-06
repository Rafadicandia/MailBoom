package com.mailboom.api.application.user.in.port;

import com.mailboom.api.application.user.usecase.command.CreateUserCommand;
import com.mailboom.api.domain.model.user.User;

public interface CreateUserAdminUseCase {
    User execute(CreateUserCommand command);
}
