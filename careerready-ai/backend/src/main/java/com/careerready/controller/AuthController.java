package com.careerready.controller;

import com.careerready.entity.User;
import com.careerready.model.AuthRequest;
import com.careerready.model.AuthResponse;
import com.careerready.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Username already exists")
                    .build();
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // In a real app, hash this!
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .success(true)
                .message("Signup successful")
                .user(user)
                .build();
    }

    @PostMapping("/signin")
    public AuthResponse signin(@RequestBody AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(request.getPassword())) {
            return AuthResponse.builder()
                    .success(true)
                    .message("Signin successful")
                    .user(userOpt.get())
                    .build();
        }

        return AuthResponse.builder()
                .success(false)
                .message("Invalid username or password")
                .build();
    }

    @PostMapping("/update-profile")
    public AuthResponse updateProfile(@RequestBody User profile) {
        Optional<User> userOpt = userRepository.findByUsername(profile.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFullName(profile.getFullName());
            user.setTonePreference(profile.getTonePreference());
            user.setAddressPreference(profile.getAddressPreference());
            user.setPurpose(profile.getPurpose());
            userRepository.save(user);
            return AuthResponse.builder().success(true).user(user).build();
        }
        return AuthResponse.builder().success(false).message("User not found").build();
    }
}
