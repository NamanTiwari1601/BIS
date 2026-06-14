package com.proto.BIS.common.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
@Entity
@Table(name= "s_login")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int loginId;
    private int login_usrid;
    private Date login_datetime;
    private char login_flag;




}
