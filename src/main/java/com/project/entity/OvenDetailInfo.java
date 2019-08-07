package com.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "oven_detail_info")
public class OvenDetailInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "oven_status")
    private int ovenStatus;//  1-正在工作   0-表示闲置

    @Column(name = "oven_type")
    private int ovenType;// 0-烤箱类型  1-微波炉类型

    @Column(name = "oven_online")
    private int ovenOnline; // 0-在线  1-离线
}
