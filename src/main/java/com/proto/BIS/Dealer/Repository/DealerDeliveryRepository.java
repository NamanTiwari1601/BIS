package com.proto.BIS.Dealer.Repository;

import com.proto.BIS.Dealer.Model.DealerDeliveryModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealerDeliveryRepository extends JpaRepository<DealerDeliveryModel, Long> {
    List<DealerDeliveryModel> findAllByDealer_DealerIdOrderByDeliveryDateDescDeliveryIdDesc(Long dealerId);
}
