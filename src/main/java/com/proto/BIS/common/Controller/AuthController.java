package com.proto.BIS.common.Controller;


import com.proto.BIS.common.DTO.AuthRequest;
import com.proto.BIS.common.DTO.AuthResponse;
import com.proto.BIS.common.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
@Tag(name="Authentication", description = "Login and token management")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Return JWT token on successful authentication")
    @ApiResponse(responseCode = "200",description = "Login successful")
    @ApiResponse(responseCode = "401", description="Invalid credentials")
    public ResponseEntity<?> login (@RequestBody AuthRequest request){
            log.info("In authentication login for user:{}", request.getUserName());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);

    }

}
