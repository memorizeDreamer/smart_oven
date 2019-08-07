package com.project.repository;

import com.project.entity.CodeString;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface CodeStringRepository extends CrudRepository<CodeString,Integer> {

    CodeString findFirstByMobileNumOrderByCreateTimeDesc(String mobileNum);
}
