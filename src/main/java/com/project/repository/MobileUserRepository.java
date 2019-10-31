package com.project.repository;

import com.project.entity.MobileUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface MobileUserRepository extends CrudRepository<MobileUser,Integer> {
    MobileUser findMobileUserByUsernameAndPassword(String username,String password);

    MobileUser findMobileUserByMobileAndPassword(String mobileNum, String password);

    MobileUser findMobileUserByUsername(String username);

    MobileUser findMobileUserByMobile(String mobileNum);

    MobileUser findMobileUserById(int id);

    @Transactional
    @Modifying
    @Query("update mobile_user set update_time = ?1 where username = ?2")
    int updateTime(Long updateTime, String username);

    @Transactional
    @Modifying
    @Query("update mobile_user set update_time = ?1 , password =?2 where username = ?3")
    int updatePassword(Long updateTime, String password, String username);

    @Transactional
    @Modifying
    @Query("update mobile_user set user_image = ?1 , update_time =?2 where username = ?3")
    int updateUserImage(String userImage, Long updateTime, String username);

    @Transactional
    @Modifying
    @Query("update mobile_user set username=?1 , nickname = ?2 , sex =?3 , birthday=?4 , address=?5 , update_time=?6 where id = ?7")
    int updateUserInfo(String username, String nickname, String sex, String birthday, String address, Long updateTime, int id);
}
