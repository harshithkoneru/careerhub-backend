package com.careerhub.repository;
import com.careerhub.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface InterviewRepository extends JpaRepository<Interview,String> {
    List<Interview> findAllByUserIdOrderByDateAscTimeAsc(String userId);
    List<Interview> findAllByUserIdAndStatusOrderByDateDescTimeDesc(String userId,InterviewStatus status);
    List<Interview> findByUserIdAndStatusAndDateGreaterThanEqualOrderByDateAscTimeAsc(String userId,InterviewStatus status,LocalDate date);
    long countByStatus(InterviewStatus status);
    void deleteAllByUserId(String userId);
}
