package com.proto.BIS.common.Model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Entity
@Table(name = "s_usr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "Name cannot be null")
    @Size(min=2, max = 50, message="Name must be between 2 and 50 characters")
    private String userName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "invalid phone number")
    private String phNo;

    @Size(max=200,message="address too long")
    private String userAddress;

    @Past(message="Date of birth must be in the past")
    private LocalDate userDOB;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$",message="Invalid Blood Type")
    @Column(columnDefinition = "varchar(2)")
    private String userBloodType;

    private boolean userAdmin;

    @NotBlank(message="password must be at least 6 characters")
    @Size(min=6,message = "password must be at least 6 characters")
    private String  userPass;



}
