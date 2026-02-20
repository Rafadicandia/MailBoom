package com.mailboom.api.application.admin.in.port;

import com.mailboom.api.application.admin.usecase.command.GetAllUsersCommand;
import com.mailboom.api.domain.model.user.User;

import java.util.List;

public interface GetAllUsersUseCase {
    List<User> execute(GetAllUsersCommand command);
}
