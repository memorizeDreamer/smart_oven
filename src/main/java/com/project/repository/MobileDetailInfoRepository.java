package com.project.repository;

import com.project.entity.MobileDetailInfo;
import org.springframework.data.repository.CrudRepository;

public interface MobileDetailInfoRepository extends CrudRepository<MobileDetailInfo,Integer> {
    MobileDetailInfo findMobileDetailInfoByMobileId(String mobileId);
}
