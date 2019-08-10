package com.project.repository;

import com.project.entity.UserFeedBack;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserFeedRepository extends CrudRepository<UserFeedBack,Integer> {
    List<UserFeedBack> findUserFeedBackByUsernameOrderByCreateTimeDesc(String username);
}
