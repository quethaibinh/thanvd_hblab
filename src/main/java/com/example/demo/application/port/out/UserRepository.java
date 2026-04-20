package com.example.demo.application.port.out;

import com.example.demo.domain.model.UserAccount;
import java.util.Optional;

public interface UserRepository {
    UserAccount save(UserAccount user);
    Optional<UserAccount> findById(String id);
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
