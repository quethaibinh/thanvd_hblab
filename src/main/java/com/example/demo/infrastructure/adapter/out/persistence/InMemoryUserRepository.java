package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.UserRepository;
import com.example.demo.domain.model.UserAccount;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

//@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserAccount> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> userIdsByEmail = new ConcurrentHashMap<>();

    @Override
    public UserAccount save(UserAccount userAccount) {
        usersById.put(userAccount.id(), userAccount);
        userIdsByEmail.put(userAccount.email(), userAccount.id());
        return userAccount;
    }

    @Override
    public Optional<UserAccount> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(userIdsByEmail.get(email))
                .map(usersById::get);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userIdsByEmail.containsKey(email);
    }
}
