package com.proto.BIS.common.Service;


import com.proto.BIS.common.DTO.AuthRequest;
import com.proto.BIS.common.DTO.AuthResponse;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserRepo repo;

    @Autowired
    JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest request){
        UserModel user= repo.findByUserName(request.getUserName()).orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        if(!user.getUserPass().equals(request.getUserPass())){
            throw  new IllegalArgumentException("Invalid password");
        }

        String token= jwtUtil.generateToken(user.getUserName(),user.isUserAdmin());
        return  new AuthResponse(token, user.getUserName(),user.isUserAdmin());
    }
}
