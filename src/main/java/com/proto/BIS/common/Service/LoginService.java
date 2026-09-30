package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.LoginModel;
import com.proto.BIS.common.Repository.LoginRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    @Autowired
    LoginRepo repo;

    public void loginUser(LoginModel mod){
        log.info("In login user");
        repo.save(mod);
    }
    public  void  logoutUser(LoginModel mod){
        log.info("In logout user");
        repo.save(mod);
    }
}
