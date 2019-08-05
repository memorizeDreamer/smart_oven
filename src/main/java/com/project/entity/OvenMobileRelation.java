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
@Entity(name = "oven_mobile_relation")
public class OvenMobileRelation {
    @Id
    private int id;

    @Column(name = "oven_id")
    private String ovenId;

    @Column(name = "mobile_id")
    private String mobileId;

    @Column(name = "update_date")
    private Long updateDate;

    @Column(name = "oven_status")
    private int ovenStatus;//  1-正在工作   0-表示闲置

    @Column(name = "oven_type")
    private int ovenType;// 0-烤箱类型  1-微波炉类型

    @Column(name = "oven_online")
    private int ovenOnline; // 0-在线  1-离线

    @Column(name = "is_bind")
    private int isBind; // 是否绑定 0-绑定  1-未绑定'
}
