package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.LoginModel;
import com.proto.BIS.common.Service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LoginService service;

    @PostMapping("/Login")
    public String login(@RequestBody LoginModel mod){
        log.info("In user login");
        service.loginUser(mod);
        return "User Login";
    }

    @PostMapping("/Logout")
    public String logout(@RequestBody LoginModel mod){
        log.info("In user logout");
        service.logoutUser(mod);

        return "User Logout";
    }


}
