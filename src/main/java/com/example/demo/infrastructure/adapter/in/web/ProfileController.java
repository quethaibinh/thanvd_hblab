package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.port.in.ProfileUseCase;
import com.example.demo.infrastructure.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileUseCase profileUseCase;

    public ProfileController(ProfileUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    @GetMapping("/me")
    public ProfileUseCase.ProfileView getMyProfile(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return profileUseCase.getMyProfile(principal.userId());
    }

    @PutMapping("/me")
    public ProfileUseCase.ProfileView updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return profileUseCase.updateMyProfile(principal.userId(), new ProfileUseCase.UpdateProfileCommand(
                request.fullName(),
                request.avatarUrl(),
                request.bio()));
    }

    public record UpdateProfileRequest(
            @NotBlank(message = "Full name is required")
            String fullName,
            String avatarUrl,
            String bio) {
    }
}
