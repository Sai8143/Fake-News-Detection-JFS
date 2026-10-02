package com.truthlens.repository;

import com.truthlens.entity.AnalysisRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisRecord, Long> {

    List<AnalysisRecord> findAllByOrderByCreatedAtDesc();

    List<AnalysisRecord> findTop20ByOrderByCreatedAtDesc();

    long countByVerdictClass(String verdictClass);
}
