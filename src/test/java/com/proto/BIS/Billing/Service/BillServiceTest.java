package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.Model.BillItemModel;
import com.proto.BIS.Billing.Model.BillItemStockModel;
import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Repository.BillRepo;
import com.proto.BIS.Billing.Repository.DuesRepo;
import com.proto.BIS.Package.Repository.PackageRepository;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.ProductRepo;
import com.proto.BIS.common.Repository.ServiceProductRepository;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock private BillRepo billRepo;
    @Mock private ProductRepo productRepo;
    @Mock private WorkRepo serviceRepo;
    @Mock private PackageRepository packageRepo;
    @Mock private ServiceProductRepository serviceProductRepository;
    @Mock private UserRepo userRepo;
    @Mock private DueService dueService;
    @Mock private DuesRepo duesRepo;

    @InjectMocks
    private BillService billService;

    @Test
    void deletingBillCanRestoreProductStockAndDeleteRelatedDues() {
        UserModel admin = adminUser();
        ProductModel product = new ProductModel();
        product.setProductQuantity(3L);
        BillItemModel item = new BillItemModel();
        item.setProduct(product);
        item.setQuantity(2);
        Bills bill = new Bills();
        bill.setItems(List.of(item));
        DuesModel due = new DuesModel();

        when(userRepo.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(billRepo.findById(12)).thenReturn(Optional.of(bill));
        when(duesRepo.findByBill_BillId(12)).thenReturn(List.of(due));

        billService.deleteBill(12, true, true, "admin");

        assertEquals(5L, product.getProductQuantity());
        verify(productRepo).save(product);
        verify(duesRepo).deleteAll(List.of(due));
        verify(billRepo).delete(bill);
    }

    @Test
    void deletingBillCanKeepStockAndDetachRelatedDues() {
        UserModel admin = adminUser();
        ProductModel product = new ProductModel();
        product.setProductQuantity(3L);
        BillItemModel item = new BillItemModel();
        item.setProduct(product);
        item.setQuantity(2);
        Bills bill = new Bills();
        bill.setItems(List.of(item));
        DuesModel due = new DuesModel();
        due.setBill(bill);

        when(userRepo.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(billRepo.findById(12)).thenReturn(Optional.of(bill));
        when(duesRepo.findByBill_BillId(12)).thenReturn(List.of(due));

        billService.deleteBill(12, false, false, "admin");

        assertEquals(3L, product.getProductQuantity());
        assertNull(due.getBill());
        verify(productRepo, never()).save(product);
        verify(duesRepo).saveAll(List.of(due));
        verify(duesRepo, never()).deleteAll(List.of(due));
        verify(billRepo).delete(bill);
    }

    @Test
    void deletingServiceBillRestoresItsSavedStockUsage() {
        UserModel admin = adminUser();
        ProductModel consumedProduct = new ProductModel();
        consumedProduct.setProductQuantity(4L);
        ServicesModel service = new ServicesModel();
        BillItemStockModel consumption = new BillItemStockModel();
        consumption.setProduct(consumedProduct);
        consumption.setQuantity(3L);
        BillItemModel item = new BillItemModel();
        item.setServices(service);
        item.setQuantity(1);
        item.setStockUsageCaptured(true);
        item.setStockConsumptions(List.of(consumption));
        Bills bill = new Bills();
        bill.setItems(List.of(item));

        when(userRepo.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(billRepo.findById(12)).thenReturn(Optional.of(bill));
        when(duesRepo.findByBill_BillId(12)).thenReturn(List.of());

        billService.deleteBill(12, true, false, "admin");

        assertEquals(7L, consumedProduct.getProductQuantity());
        verify(serviceProductRepository, never()).findByService_ServiceId(service.getServiceId());
        verify(productRepo).save(consumedProduct);
    }

    @Test
    void nonAdminCannotDeleteBill() {
        UserModel staff = new UserModel();
        staff.setUserName("staff");
        staff.setUserAdmin(false);
        when(userRepo.findByUserName("staff")).thenReturn(Optional.of(staff));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> billService.deleteBill(12, true, true, "staff"));

        assertEquals(403, error.getStatusCode().value());
        verify(billRepo, never()).findById(12);
    }

    private UserModel adminUser() {
        UserModel admin = new UserModel();
        admin.setUserName("admin");
        admin.setUserAdmin(true);
        return admin;
    }
}
