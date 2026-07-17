package com.proto.BIS.Billing.Model;

import com.proto.BIS.common.Model.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @NotNull(message = "Staff Reference is required")
    private UserModel staff;

    @OneToMany(mappedBy = "bill", cascade=CascadeType.ALL,orphanRemoval = true)
    private List<BillItemModel> items;
}
