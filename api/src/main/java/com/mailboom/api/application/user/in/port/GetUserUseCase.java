package com.mailboom.api.application.user.in.port;

import com.mailboom.api.application.user.usecase.command.GetUserCommand;
import com.mailboom.api.domain.model.user.User;

public interface GetUserUseCase {
    User execute(GetUserCommand command);
}
