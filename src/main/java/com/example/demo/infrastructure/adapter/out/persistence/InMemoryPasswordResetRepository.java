package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.PasswordResetRepository;
import com.example.demo.domain.model.PasswordResetRequest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

//@Repository
public class InMemoryPasswordResetRepository implements PasswordResetRepository {

    private final Map<String, PasswordResetRequest> requestsById = new ConcurrentHashMap<>();

    @Override
    public PasswordResetRequest save(PasswordResetRequest request) {
        requestsById.put(request.id(), request);
        return request;
    }

    @Override
    public Optional<PasswordResetRequest> findByToken(String token) {
        return requestsById.values().stream()
                .filter(request -> request.resetToken().equals(token))
                .findFirst();
    }
}
