package com.project.repository;

import com.project.entity.MobileUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface MobileUserRepository extends CrudRepository<MobileUser,Integer> {
    MobileUser findMobileUserByUsernameAndPassword(String username,String password);

    MobileUser findMobileUserByUsername(String username);

    MobileUser findMobileUserByMobile(String mobileNum);

    @Transactional
    @Modifying
    @Query("update mobile_user set registerTime = ?1 where username = ?2")
    int updateTime(Long updateTime, String username);

    @Transactional
    @Modifying
    @Query("update mobile_user set update_time = ?1 , password =?2 where username = ?3")
    int updatePassword(Long updateTime, String password, String username);
}
