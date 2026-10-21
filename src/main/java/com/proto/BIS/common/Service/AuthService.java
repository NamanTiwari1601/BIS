package com.proto.BIS.common.Service;


import com.proto.BIS.common.DTO.AuthRequest;
import com.proto.BIS.common.DTO.AuthResponse;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    @Autowired
    UserRepo repo;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request){
        log.info("In authentication login for user:{}", request.getUserName());
        UserModel user= repo.findByUserName(request.getUserName()).orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        if(!passwordEncoder.matches(request.getUserPass(), user.getUserPass())){
            log.warn("Invalid password for user:{}", request.getUserName());
            throw  new IllegalArgumentException("Invalid password");
        }

        String token= jwtUtil.generateToken(user.getUserName(),user.isUserAdmin());
        return  new AuthResponse(token, user.getUserName(),user.isUserAdmin());
    }
}
