package com.project.repository;

import com.project.entity.OvenDetailInfo;
import org.springframework.data.repository.CrudRepository;

public interface OvenDetailInfoRepository extends CrudRepository<OvenDetailInfo,Integer> {
    OvenDetailInfo findOvenDetailInfoByOvenId(String ovenId);
}
