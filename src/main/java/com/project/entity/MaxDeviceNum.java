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
@Entity(name = "max_device_num")
public class MaxDeviceNum {
    @Id
    private int id;

    @Column(name = "max_device_num")
    private int maxDeviceNum;// 最大设备数
}
