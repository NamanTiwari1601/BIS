package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/User")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/getUsers")
    public List<UserModel> getUsers(){
        return service.getUsers();
    }

    @GetMapping("/getUser/{usrId}")
    public UserModel getUserById(@PathVariable int usrId){
        return service.getUserById(usrId);
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserModel> addUser(@Valid @RequestBody UserModel mod){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addUser(mod));
    }
    @PutMapping("/updateUser")
    public ResponseEntity<UserModel> updateUser(@RequestBody UserModel mod){
       return ResponseEntity.ok( service.updateUser(mod));
    }
    @DeleteMapping("/deleteUser/{usrId}")
    public void deleteUser(@PathVariable int usrId){
        service.deleteUser(usrId);
    }
}
