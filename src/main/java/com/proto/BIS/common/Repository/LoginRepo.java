package com.proto.BIS.common.Repository;

import com.Proto.demo.common.Model.LoginModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepo extends JpaRepository<LoginModel,Integer> {
}
