package com.pandev.repository;

import com.pandev.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
