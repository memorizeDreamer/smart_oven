package com.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 烤箱运行记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "transform_record")
public class TransFormRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "oven_id")
    private String ovenId;//烤箱ID

    @Column(name = "mobile_id")
    private String mobileId;

    @Column(name = "transform_to")
    private int to;//传输方向：0 烤箱app向服务器传送 1 手机app向服务器传送

    @Column(name = "status")
    private int status;//烤箱状态 0 待机状态  1 加热状太

    @Column(name = "model")
    private int model;// 加热模式 1 上烤 2 下烤 3 上下考 4旋转 5 上烤+旋转 6解冻 7 发酵

    @Column(name = "up_tempture")
    private int uptempture;// 上管温度

    @Column(name = "down_tempture")
    private int downtempture;//下管温度

    @Column(name = "run_time")
    private int time;//加热时间

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "weight")
    private int weight; //解冻重量

    @Column(name = "need_send_pic")
    private int needSendPic; // 是否需要推送任务截图给手机
}
