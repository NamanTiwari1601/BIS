package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.ChangePasswordRequest;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {


    public final UserRepo repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserModel> getUsers(){
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserModel getUserById(int usrId){
        try {
            return repo.findById(usrId).orElse(new UserModel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserModel addUser(UserModel mod){
        try {
            mod.setUserPass(passwordEncoder.encode(mod.getUserPass()));
            return repo.save(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserModel updateUser(UserModel mod){
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
        UserModel user= repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(user.getUserPass(),password.getOldPassword())){
            throw new IllegalArgumentException("Current Password Is Incorrect");
        }
        user.setUserPass(passwordEncoder.encode(password.getNewPassword()));
        repo.save(user);
    }
    @Transactional
    public void deleteUser(int usrId){

        try {
            repo.deleteById(usrId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
