package com.proto.BIS.common.Repository;

import com.Proto.demo.common.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserModel,Integer> {
}
