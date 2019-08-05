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
@Entity(name = "oven_detail_info")
public class OvenDetailInfo {
    @Id
    private int id;

    @Column(name = "oven_id")
    private String ovenId;

    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "oven_name")
    private String ovenName;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;
}
