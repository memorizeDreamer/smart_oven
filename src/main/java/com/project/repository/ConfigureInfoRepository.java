package com.project.repository;

import com.project.entity.ConfigureInfo;
import org.springframework.data.repository.CrudRepository;

public interface ConfigureInfoRepository extends CrudRepository<ConfigureInfo, Integer> {
    ConfigureInfo findConfigureInfoByConfigId(String configId);
}
