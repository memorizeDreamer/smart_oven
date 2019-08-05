package com.project.repository;

import com.project.entity.OvenMobileRelation;
import org.springframework.data.repository.CrudRepository;

public interface OvenMobileRelationRepository extends CrudRepository<OvenMobileRelation,Integer> {
    OvenMobileRelation findOvenMobileRelationByOvenId(String ovenId);
}
