package com.project.repository;

import com.project.entity.OvenDetailInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface OvenDetailInfoRepository extends CrudRepository<OvenDetailInfo,Integer> {
    OvenDetailInfo findOvenDetailInfoByOvenId(String ovenId);

    @Transactional
    int deleteByOvenId(String ovenId);
}
