package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {


    public final UserRepo repo;

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

            return repo.save(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserModel updateUser(UserModel mod){
        try{

            return repo.save(mod);
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
