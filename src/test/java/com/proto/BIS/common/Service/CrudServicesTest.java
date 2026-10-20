package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.LoginModel;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.LoginRepo;
import com.proto.BIS.common.Repository.ProductRepo;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudServicesTest {
    @Mock ProductRepo productRepo;
    @Mock WorkRepo workRepo;
    @Mock LoginRepo loginRepo;
    @Mock UserRepo userRepo;
    @Mock PasswordEncoder encoder;
    @InjectMocks ProductService productService;
    @InjectMocks WorkService workService;
    @InjectMocks LoginService loginService;
    @InjectMocks UserService userService;

    @org.junit.jupiter.api.BeforeEach
    void injectFieldDependencies() {
        ReflectionTestUtils.setField(userService, "passwordEncoder", encoder);
    }

    @Test
    void productAndWorkServicesDelegateToRepositories() {
        ProductModel product = new ProductModel();
        ServicesModel work = new ServicesModel();
        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(workRepo.findById(1)).thenReturn(Optional.of(work));
        assertSame(product, productService.getProductsById(1));
        assertSame(work, workService.getProductsById(1));
        productService.deleteProduct(1);
        workService.deleteProduct(1);
        verify(productRepo).deleteById(1);
        verify(workRepo).deleteById(1);
    }

    @Test
    void loginAndLogoutSaveTheModel() {
        LoginModel model = new LoginModel();
        loginService.loginUser(model);
        loginService.logoutUser(model);
        verify(loginRepo, times(2)).save(model);
    }

    @Test
    void userServiceEncodesNewPasswordAndPreservesPasswordOnUpdate() {
        UserModel user = new UserModel();
        user.setUserPass("plain");
        when(encoder.encode("plain")).thenReturn("encoded");
        when(userRepo.save(user)).thenReturn(user);
        assertSame(user, userService.addUser(user));
        assertEquals("encoded", user.getUserPass());

        UserModel existing = new UserModel();
        existing.setUserId(1);
        existing.setUserPass("old");
        UserModel update = new UserModel();
        update.setUserId(1);
        update.setUserPass("new");
        when(userRepo.findById(1)).thenReturn(Optional.of(existing));
        when(userRepo.save(update)).thenReturn(update);
        userService.updateUser(update);
        assertEquals("old", update.getUserPass());
    }
}