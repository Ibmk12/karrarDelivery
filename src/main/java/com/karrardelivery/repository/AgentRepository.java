package com.karrardelivery.repository;
import com.karrardelivery.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    Agent findByName(String name);

    Optional<Agent> findByIdAndDeleted(Long id, boolean deleted);

    boolean existsByPhoneNumberAndDeleted(String phoneNumber, boolean deleted);

    boolean existsByNameAndDeleted(String name, boolean deleted);

//    boolean existsByCodeAndDeleted(String code, boolean deleted);

    boolean existsByEmailAndDeleted(String email, boolean deleted);

    boolean existsByPhoneNumberAndDeletedAndIdNot(String phoneNumber, boolean deleted, Long id);

    boolean existsByEmailAndDeletedAndIdNot(String email, boolean deleted, Long id);

    boolean existsByNameAndDeletedAndIdNot(String name, boolean deleted, Long id);

//    boolean existsByCodeAndDeletedAndIdNot(String code, boolean deleted, Long id);
}
