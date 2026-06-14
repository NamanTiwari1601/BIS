package com.proto.BIS.common.Controller;

import com.Proto.demo.common.Model.UserModel;
import com.Proto.demo.common.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService service;

    @RequestMapping("/Users")
    @ResponseBody
    public List<UserModel> getUsers(){
        return service.getUsers();
    }

    @RequestMapping("/getUser/{usrId}")
    @ResponseBody
    public UserModel getUserById(@PathVariable int usrId){
        return service.getUserById(usrId);
    }

    @PostMapping("/addUser")
    @ResponseBody
    public void addUser(@RequestBody UserModel mod){
        service.addUser(mod);
    }
    @PutMapping("/updateUser")
    @ResponseBody
    public void updateUser(@RequestBody UserModel mod){
        service.updateUser(mod);
    }

    @DeleteMapping("/deleteUser/{usrId}")
    public void deleteUser(@PathVariable int usrId){
        service.deleteUser(usrId);
    }
}
