package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.LoginModel;
import com.proto.BIS.common.Repository.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginService {

    @Autowired
    LoginRepo repo;

    public void loginUser(LoginModel mod){
        repo.save(mod);
    }
    public  void  logoutUser(LoginModel mod){
        repo.save(mod);
    }
}
