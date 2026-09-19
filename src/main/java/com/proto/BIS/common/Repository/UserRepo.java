package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserModel,Integer> {
    Optional<UserModel> findByUserName(String userName);


}
