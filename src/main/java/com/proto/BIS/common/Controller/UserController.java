package com.proto.BIS.common.Controller;

import com.proto.BIS.common.DTO.ChangePasswordRequest;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/User")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name="User", description = "Manages Users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService service;

    @GetMapping("/getUsers")
    @Operation(summary = "get all Users", description = "Returns a list of all users")
    public List<UserModel> getUsers(){
        log.info("In get users");
        return service.getUsers();
    }

    @GetMapping("/page")
    @Operation(summary = "get users page", description = "Returns a paginated list of users")
    public ResponseEntity<Page<UserModel>> getUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("In get users page: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getUsers(pageable));
    }

    @GetMapping("/getUser/{usrId}")
    @Operation(summary = "get user by id", description="Return a user  by userId")
    public UserModel getUserById(@PathVariable int usrId){
        log.info("In get user by id: {}", usrId);
        return service.getUserById(usrId);
    }

    @PostMapping("/addUser")
    @Operation(summary = "Add a new User")
    public ResponseEntity<UserModel> addUser(@Valid @RequestBody UserModel mod){
        log.info("In add user");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addUser(mod));
    }
    @PutMapping("/updateUser")
    @Operation(summary = "Update a User")
    public ResponseEntity<UserModel> updateUser(@RequestBody UserModel mod){
       log.info("In update user");
       return ResponseEntity.ok( service.updateUser(mod));
    }
    @DeleteMapping("/deleteUser/{usrId}")
    @Operation(summary = "Delete a user by Id")
    public void deleteUser(@PathVariable int usrId){
        log.info("In delete user: {}", usrId);
        service.deleteUser(usrId);
    }

    @PutMapping("/changePassword/{userId}")
    @Operation(summary="change password of a user")
    public ResponseEntity<?> changePassword(@PathVariable int userId, @RequestBody ChangePasswordRequest request){
        log.info("In change password for user: {}", userId);
        try{
            service.changePassword(userId, request);
            return ResponseEntity.ok("Password Changed Successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
