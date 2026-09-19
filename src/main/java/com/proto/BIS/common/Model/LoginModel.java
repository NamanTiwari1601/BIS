package com.proto.BIS.common.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;

@Component
@Entity
@Table(name= "s_login")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginId;

    private Long login_usrid;

    @CreationTimestamp
    private LocalDateTime login_datetime;

    @Column(columnDefinition = "char(1)")
    private char  login_flag;





}
