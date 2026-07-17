package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.LoginModel;
import com.proto.BIS.common.Repository.LoginRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
