package com.karrardelivery.repository;

import com.karrardelivery.model.Emirate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmirateRepository extends JpaRepository<Emirate, Long> {
}
