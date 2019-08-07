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
@Entity(name = "oven_status")
public class OvenStatus {
    @Id
    private int id;

    @Column(name = "oven_id")
    private String ovenId;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "is_send")
    private int isSend;
}
