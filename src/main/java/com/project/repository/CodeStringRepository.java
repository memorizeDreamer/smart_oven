package com.project.repository;

import com.project.entity.CodeString;
import org.springframework.data.repository.CrudRepository;

public interface CodeStringRepository extends CrudRepository<CodeString,Integer> {
    CodeString findCodeStringByMobileNum(String mobileNum);
}
