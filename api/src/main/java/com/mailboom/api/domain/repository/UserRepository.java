package com.mailboom.api.domain.repository;

import com.mailboom.api.domain.model.User;
import com.mailboom.api.domain.model.valueobjects.UserId;

public interface UserRepository {
    User findById(UserId id);
    
    User save(User user);
    
    boolean existsByEmail(String email);
    
    User findByEmail(String email);
    
    void delete(UserId id);
    
    // Additional methods for user management
    void incrementEmailsSent(UserId userId, int quantity);
    
    boolean canUserSendEmails(UserId userId, int quantity);
    
    void resetMonthlyEmails(UserId userId);
}
