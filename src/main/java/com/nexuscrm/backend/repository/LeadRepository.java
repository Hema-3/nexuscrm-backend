package com.nexuscrm.backend.repository;

import com.nexuscrm.backend.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    @Query("SELECT l FROM Lead l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(l.status) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Lead> searchLeads(@Param("searchTerm") String searchTerm, Pageable pageable);
}
