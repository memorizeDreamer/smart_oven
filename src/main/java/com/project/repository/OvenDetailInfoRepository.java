package com.project.repository;

import com.project.entity.OvenDetailInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface OvenDetailInfoRepository extends CrudRepository<OvenDetailInfo,Integer> {
    OvenDetailInfo findOvenDetailInfoByOvenId(String ovenId);

    @Transactional
    int deleteByOvenId(String ovenId);

    // 更新烤箱状态
    @Transactional
    @Modifying
    @Query("update oven_detail_info set oven_online = ?1 where oven_id = ?2")
    int updateOvenOffLine(int isOnline, String ovenId);

    // 更新烤箱状态
    @Transactional
    @Modifying
    @Query("update oven_detail_info set oven_status = ?1,need_send_pic = ?2 where oven_id = ?3")
    int updateOvenStatus(int status, int needSendPic, String ovenId);

    // 更新烤箱状态
    @Transactional
    @Modifying
    @Query("update oven_detail_info set oven_status = ?1 where oven_id = ?2")
    int onlyUpdateOvenStatus(int status, String ovenId);

    // 更新烤箱状态
    @Transactional
    @Modifying
    @Query("update oven_detail_info set need_send_pic = ?1 where oven_id = ?2")
    int updateOvenNeedSendStatus(int needSendPic, String ovenId);
}
