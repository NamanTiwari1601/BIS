package com.proto.BIS.common.Service;

import com.Proto.demo.common.Model.UserModel;
import com.Proto.demo.common.Repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepo repo;

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
    public void  addUser(UserModel mod){
        try {

            repo.save(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateUser(UserModel mod){
        try{
            repo.save(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
