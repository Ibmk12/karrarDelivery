package com.karrardelivery.repository;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long>, JpaSpecificationExecutor<Trader> {

    Trader findByName(String name);

    boolean existsByPhoneNumberAndDeleted(String phoneNumber, boolean deleted);

    boolean existsByEmailAndDeleted(String email, boolean deleted);
}
