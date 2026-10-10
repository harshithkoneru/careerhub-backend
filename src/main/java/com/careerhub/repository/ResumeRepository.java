package com.careerhub.repository;
import com.careerhub.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ResumeRepository extends JpaRepository<Resume,String> {
    List<Resume> findAllByUserIdOrderByUploadedAtDesc(String userId);
    Optional<Resume> findByIdAndUserId(String id,String userId);
    long countByUserId(String userId);
    void deleteAllByUserId(String userId);
}
