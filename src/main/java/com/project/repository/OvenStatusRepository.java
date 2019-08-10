package com.project.repository;

import com.project.entity.OvenStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OvenStatusRepository extends CrudRepository<OvenStatus,Integer> {
    OvenStatus findOvenStatusByOvenId(String ovenId);

    @Transactional
    @Modifying
    @Query("update oven_send_status set update_time = ?1 where oven_id = ?2")
    int updateTime(Long updateTime, String ovenId);

    @Transactional
    @Modifying
    @Query("update oven_send_status set is_send = ?1 where oven_id = ?2")
    int updateIsSend(int isSend, String ovenId);

    List<OvenStatus> findOvenStatusByUpdateTimeBefore(Long updateTime);
}
