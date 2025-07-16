package com.karrardelivery.repository;

import com.karrardelivery.entity.management.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByPhoneAndDeletedFalse(String phone);

    boolean existsByPhoneAndDeletedFalse(String phone);

    boolean existsByPhoneAndDeletedFalseAndIdNot(String phone, Long id);

    List<User> findAllByDeletedFalse();

    Optional<User> findByIdAndDeletedFalse(Long id);
}
