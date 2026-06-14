package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.ServicesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepo extends JpaRepository<ServicesModel,Integer> {
}
