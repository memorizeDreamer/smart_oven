package com.project.repository;

import com.project.entity.MaxDeviceNum;
import org.springframework.data.repository.CrudRepository;

public interface MaxDeviceNumRepository extends CrudRepository<MaxDeviceNum,Integer> {
    int findMaxDeviceNumById(int id);
}
