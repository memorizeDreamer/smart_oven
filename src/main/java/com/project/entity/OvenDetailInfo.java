package com.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(name = "tag_id")
    private String tagId;

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

    @Column(name = "nickname")
    private String nickname; // 烤箱昵称

    @Column(name = "oven_model")
    private String ovenModel;// 烤箱型号

    @Column(name = "oven_brand")
    private String ovenBrand; // 烤箱品牌

    @Column(name = "avata")
    private String avata; // 烤箱用户图像

    @Column(name = "sex")
    private int sex; // 性别 性别1=>男，2=>女

    @Column(name = "birth")
    private String birth; // 生日

    @Column(name = "address")
    private String address; // 地址
}
