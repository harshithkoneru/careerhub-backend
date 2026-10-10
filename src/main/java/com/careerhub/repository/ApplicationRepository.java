package com.careerhub.repository;
import com.careerhub.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
public interface ApplicationRepository extends JpaRepository<JobApplication,String>, JpaSpecificationExecutor<JobApplication> {
    boolean existsByUserIdAndCompanyIgnoreCaseAndRoleIgnoreCase(String userId,String company,String role);
    boolean existsByUserIdAndCompanyIgnoreCaseAndRoleIgnoreCaseAndIdNot(String userId,String company,String role,String id);
    Page<JobApplication> findByUserId(String userId,Pageable pageable);
    Page<JobApplication> findByUserIdAndStatus(String userId,ApplicationStatus status,Pageable pageable);
    Page<JobApplication> findByUserIdAndRoleContainingIgnoreCase(String userId,String role,Pageable pageable);
    List<JobApplication> findAllByUserId(String userId);
    long countByStatus(ApplicationStatus status);
    void deleteAllByUserId(String userId);
}
