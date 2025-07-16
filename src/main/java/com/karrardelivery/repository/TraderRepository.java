package com.karrardelivery.repository;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long>, JpaSpecificationExecutor<Trader> {

    Trader findByName(String name);

    Optional<Trader> findByIdAndDeleted(Long id, boolean deleted);

    boolean existsByPhoneNumberAndDeleted(String phoneNumber, boolean deleted);

    boolean existsByNameAndDeleted(String name, boolean deleted);

    boolean existsByCodeAndDeleted(String code, boolean deleted);

    boolean existsByEmailAndDeleted(String email, boolean deleted);

    boolean existsByPhoneNumberAndDeletedAndIdNot(String phoneNumber, boolean deleted, Long id);

    boolean existsByEmailAndDeletedAndIdNot(String email, boolean deleted, Long id);

    boolean existsByNameAndDeletedAndIdNot(String name, boolean deleted, Long id);

    boolean existsByCodeAndDeletedAndIdNot(String code, boolean deleted, Long id);
}
