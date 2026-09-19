package com.proto.BIS.common.Controller;

import com.proto.BIS.common.DTO.ChangePasswordRequest;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="User", description = "Manages Users")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/getUsers")
    @Operation(summary = "get all Users", description = "Returns a list of all users")
    public List<UserModel> getUsers(){
        return service.getUsers();
    }

    @GetMapping("/getUser/{usrId}")
    @Operation(summary = "get user by id", description="Return a user  by userId")
    public UserModel getUserById(@PathVariable int usrId){
        return service.getUserById(usrId);
    }

    @PostMapping("/addUser")
    @Operation(summary = "Add a new User")
    public ResponseEntity<UserModel> addUser(@Valid @RequestBody UserModel mod){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addUser(mod));
    }
    @PutMapping("/updateUser")
    @Operation(summary = "Update a User")
    public ResponseEntity<UserModel> updateUser(@RequestBody UserModel mod){
       return ResponseEntity.ok( service.updateUser(mod));
    }
    @DeleteMapping("/deleteUser/{usrId}")
    @Operation(summary = "Delete a user by Id")
    public void deleteUser(@PathVariable int usrId){
        service.deleteUser(usrId);
    }

    @PutMapping("/changePassword/{userId}")
    @Operation(summary="change password of a user")
    public ResponseEntity<?> changePassword(@PathVariable int userId, @RequestBody ChangePasswordRequest request){
        try{
            service.changePassword(userId, request);
            return ResponseEntity.ok("Password Changed Successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
