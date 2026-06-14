package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.LoginModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepo extends JpaRepository<LoginModel,Integer> {
}
