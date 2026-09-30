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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {


    public final UserRepo repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserModel> getUsers(){
        log.info("In get users");
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<UserModel> getUsers(Pageable pageable){
        log.info("In get paginated users: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repo.findAll(pageable);
    }

    public UserModel getUserById(int usrId){
        log.info("In get user by id: {}", usrId);
        try {
            return repo.findById(usrId).orElse(new UserModel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserModel addUser(UserModel mod){
        log.info("In add user");
        try {
            mod.setUserPass(passwordEncoder.encode(mod.getUserPass()));
            return repo.save(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserModel updateUser(UserModel mod){
        log.info("In update user");
        try {
            UserModel existing = repo.findById(mod.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            mod.setUserPass(existing.getUserPass());

            return repo.save(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void changePassword(int userId, ChangePasswordRequest password){
        log.info("In change password for user: {}", userId);
        UserModel user= repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password.getOldPassword(),user.getUserPass())){
            log.warn("Current password is incorrect for user: {}", userId);
            throw new IllegalArgumentException("Current Password Is Incorrect");
        }
        user.setUserPass(passwordEncoder.encode(password.getNewPassword()));
        repo.save(user);
    }
    @Transactional
    public void deleteUser(int usrId){
        log.info("In delete user: {}", usrId);

        try {
            repo.deleteById(usrId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
