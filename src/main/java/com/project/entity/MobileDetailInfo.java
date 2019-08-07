package com.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "mobile_detail_info")
public class MobileDetailInfo {
    @Id
    private int id;

    @Column(name = "mobile_id")
    private String mobileId;

    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "group_id")
    private String groupId;
}
