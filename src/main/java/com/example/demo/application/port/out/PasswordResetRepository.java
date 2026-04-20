package com.example.demo.application.port.out;

import com.example.demo.domain.model.PasswordResetRequest;
import java.util.Optional;

public interface PasswordResetRepository {
    PasswordResetRequest save(PasswordResetRequest request);
    Optional<PasswordResetRequest> findByToken(String token);
}
