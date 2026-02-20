package com.mailboom.api.application.user.in.port;

import com.mailboom.api.application.user.usecase.command.DeleteUserCommand;

public interface DeleteUserUseCase {
    void execute(DeleteUserCommand command);
}
