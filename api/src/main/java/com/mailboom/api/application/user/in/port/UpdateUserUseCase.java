package com.mailboom.api.application.user.in.port;

import com.mailboom.api.application.user.usecase.command.UpdateUserCommand;
import com.mailboom.api.domain.model.user.User;

public interface UpdateUserUseCase {
    User execute(UpdateUserCommand command);
}
