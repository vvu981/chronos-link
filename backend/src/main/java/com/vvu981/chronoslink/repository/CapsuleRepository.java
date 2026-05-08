package com.vvu981.chronoslink.repository;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, UUID>, JpaSpecificationExecutor<Capsule> {
    // Spring traduce esto a: SELECT * FROM capsules WHERE owner_id = ?
    List<Capsule> findByOwner(User owner);
}