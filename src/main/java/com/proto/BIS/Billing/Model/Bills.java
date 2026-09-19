package com.proto.BIS.Billing.Model;

import com.proto.BIS.common.Model.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Entity
@Table(name="s_bill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billId;

    private LocalDateTime billDate;

    private double billAmount;
    private double billDues=0.0;


    @Column(columnDefinition = "boolean")
    private boolean isDue=false;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @NotNull(message = "Staff Reference is required")
    private UserModel staff;

    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String clientName;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email address")
    private String emailId;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "invalid phone number")
    private String clientPhNo;

    private String notes;

    private String paymentMode;

    private double discountPercent;

    private double gstPercent;
    @OneToMany(mappedBy = "bill", cascade=CascadeType.ALL,orphanRemoval = true)
    private List<BillItemModel> items;
}
