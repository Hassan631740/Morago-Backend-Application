package com.morago_backend.repository;

import com.morago_backend.entity.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
    List<CallRecord> findAllByOrderByCreatedAtDatetimeDesc();
}


