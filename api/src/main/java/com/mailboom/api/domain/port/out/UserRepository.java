package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);

    User save(User user);

    boolean existsByEmail(String email);

    User findByEmail(String email) throws Exception;

    void delete(UserId id);

}
