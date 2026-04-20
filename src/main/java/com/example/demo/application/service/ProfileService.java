package com.example.demo.application.service;

import com.example.demo.application.port.in.ProfileUseCase;
import com.example.demo.application.port.out.UserRepository;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.shared.exception.NotFoundException;
import java.time.Clock;
import java.time.Instant;
public class ProfileService implements ProfileUseCase {

    private final UserRepository userRepository;
    private final Clock clock;

    public ProfileService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    public ProfileView getMyProfile(String userId) {
        return toView(findUser(userId));
    }

    @Override
    public ProfileView updateMyProfile(String userId, UpdateProfileCommand command) {
        UserAccount currentUser = findUser(userId);
        UserAccount updatedUser = currentUser.withProfile(
                command.fullName().trim(),
                blankToNull(command.avatarUrl()),
                blankToNull(command.bio()),
                Instant.now(clock));
        userRepository.save(updatedUser);
        return toView(updatedUser);
    }

    private UserAccount findUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private ProfileView toView(UserAccount userAccount) {
        return new ProfileView(
                userAccount.id(),
                userAccount.email(),
                userAccount.fullName(),
                userAccount.avatarUrl(),
                userAccount.bio(),
                userAccount.role().name());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
