package com.example.demo.application.port.out;

import com.example.demo.domain.model.PasswordResetRequest;

public interface PasswordResetRepository {
    PasswordResetRequest save(PasswordResetRequest request);
}
