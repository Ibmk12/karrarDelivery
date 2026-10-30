package com.karrardelivery.repository;

import com.karrardelivery.entity.SavedOrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedOrderViewRepository extends JpaRepository<SavedOrderView, Long> {

    List<SavedOrderView> findByUserPhoneOrderByCreatedAtAscIdAsc(String userPhone);

    Optional<SavedOrderView> findByIdAndUserPhone(Long id, String userPhone);

    long countByUserPhone(String userPhone);
}
