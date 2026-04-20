package com.example.demo.application.port.in;

public interface ProfileUseCase {
    ProfileView getMyProfile(String userId);
    ProfileView updateMyProfile(String userId, UpdateProfileCommand command);

    record UpdateProfileCommand(String fullName, String avatarUrl, String bio) {
    }

    record ProfileView(
            String id,
            String email,
            String fullName,
            String avatarUrl,
            String bio,
            String role) {
    }
}
