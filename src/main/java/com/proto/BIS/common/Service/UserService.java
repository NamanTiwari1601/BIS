package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.ChangePasswordRequest;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {


    public final UserRepo repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserModel> getUsers() {
        log.info("In get users");
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<UserModel> getUsers(Pageable pageable) {
        log.info("In get paginated users: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repo.findAll(pageable);
    }

    public UserModel getUserById(int usrId) {
        log.info("In get user by id: {}", usrId);
        try {
            return repo.findById(usrId).orElse(new UserModel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserModel addUser(UserModel mod) {
        log.info("In add user");
        validatePasswordForCreate(mod);
        validateUserFields(mod);
        validateAadhaar(mod, null);
        applyDefaults(mod);
        mod.setUserPass(passwordEncoder.encode(mod.getUserPass()));
        return repo.save(mod);
    }

    @Transactional
    public UserModel updateUser(UserModel mod) {
        log.info("In update user");
        UserModel existing = repo.findById(mod.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        validateUserFields(mod);
        validateAadhaar(mod, mod.getUserId());
        mod.setUserPass(existing.getUserPass());

        if (mod.getWorkTiming() == null || mod.getWorkTiming().isBlank()) {
            mod.setWorkTiming(existing.getWorkTiming());
        }

        return repo.save(mod);
    }

    private void validateAadhaar(UserModel user, Integer currentUserId) {
        if (user.getAadhaarNumber() == null || user.getAadhaarNumber().isBlank()) {
            return;
        }

        String aadhaar = user.getAadhaarNumber().trim();
        repo.findByAadhaarNumber(aadhaar)
                .stream()
                .filter(existing -> !Objects.equals(existing.getUserId(), currentUserId))
                .findFirst()
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("A user with this Aadhaar number already exists");
                });

        user.setAadhaarNumber(aadhaar);
    }

    private void validateUserFields(UserModel user) {
        if (user.getWorkTiming() != null && !user.getWorkTiming().isBlank()
                && !List.of("FULL_DAY", "HALF_DAY").contains(user.getWorkTiming().trim().toUpperCase())) {
            throw new IllegalArgumentException("Work timing must be FULL_DAY or HALF_DAY");
        }
        if (user.getWorkTiming() != null) {
            user.setWorkTiming(user.getWorkTiming().trim().toUpperCase());
        }
    }

    private void validatePasswordForCreate(UserModel user) {
        if (user.getUserPass() == null || user.getUserPass().isBlank()) {
            throw new IllegalArgumentException("Password is required when creating a user");
        }
        if (user.getUserPass().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    private void applyDefaults(UserModel user) {
        if (user.getWorkTiming() == null || user.getWorkTiming().isBlank()) {
            user.setWorkTiming("FULL_DAY");
        }
    }

    public void changePassword(int userId, ChangePasswordRequest password) {
        log.info("In change password for user: {}", userId);
        UserModel user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password.getOldPassword(), user.getUserPass())) {
            log.warn("Current password is incorrect for user: {}", userId);
            throw new IllegalArgumentException("Current Password Is Incorrect");
        }
        user.setUserPass(passwordEncoder.encode(password.getNewPassword()));
        repo.save(user);
    }

    @Transactional
    public void deleteUser(int usrId) {
        log.info("In delete user: {}", usrId);

        try {
            repo.deleteById(usrId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
