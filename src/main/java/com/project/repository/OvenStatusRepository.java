package com.project.repository;

import com.project.entity.OvenStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OvenStatusRepository extends CrudRepository<OvenStatus,Integer> {
    OvenStatus findOvenStatusByOvenId(String ovenId);

    @Modifying
    @Query("update oven_status set update_time = ?1 where oven_id = ?2")
    int updateTime(Long updateTime, String ovenId);

    List<OvenStatus> findOvenStatusByUpdateTimeBefore(Long updateTime);
}
