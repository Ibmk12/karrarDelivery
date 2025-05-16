package com.karrardelivery.repository;
import com.karrardelivery.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

    Trader findByName(String name);
}
