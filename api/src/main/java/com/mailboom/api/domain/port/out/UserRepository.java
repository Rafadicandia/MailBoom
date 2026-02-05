package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;

public interface UserRepository {
    User findById(UserId id);

    User save(User user);

    boolean existsByEmail(String email);

    User findByEmail(String email) throws Exception;

    void delete(UserId id);

    void incrementEmailsSent(UserId userId, int quantity);

    boolean canUserSendEmails(UserId userId, int quantity);

    void resetMonthlyEmails(UserId userId);
}
