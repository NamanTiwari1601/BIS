package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.ServicesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkRepo extends JpaRepository<ServicesModel,Integer> {

}
