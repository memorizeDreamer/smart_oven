package com.project.repository;

import com.project.entity.OvenMobileRelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OvenMobileRelationRepository extends CrudRepository<OvenMobileRelation,Integer> {
    OvenMobileRelation findOvenMobileRelationByOvenId(String ovenId);

    List<OvenMobileRelation> findOvenMobileRelationByMobileIdOrderByUpdateDateDesc(String mobileId);

    @Transactional
    int deleteByMobileIdAndAndOvenId(String mobileId,String ovenId);

    @Transactional
    OvenMobileRelation findOvenMobileRelationByMobileIdAndOvenId(String mobileId,String ovenId);
}
