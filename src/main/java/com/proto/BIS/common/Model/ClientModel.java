package com.proto.BIS.common.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Entity
@Table(name = "s_client")
@SuppressWarnings("JpaDataSourceORMInspection")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clientId;

    @NotBlank(message = "Client name cannot be blank")
    @Size(max = 255, message = "Client name cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String clientName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    @Column(unique = true, length = 20)
    private String clientPhone;

    @Email(message = "Invalid email address")
    @Size(max = 255, message = "Client email cannot exceed 255 characters")
    @Column(length = 255)
    private String clientEmail;

    @NotBlank(message = "Client status cannot be blank")
    @Size(max = 10, message = "Client status cannot exceed 10 characters")
    @Column(nullable = false, length = 10)
    private String status = "Active";

    @Size(max = 500, message = "Client notes cannot exceed 500 characters")
    @Column(length = 500)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}