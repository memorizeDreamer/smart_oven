package com.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "image_info")
public class ImageInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "send_result")
    private String sendResult;

    @Column(name = "has_send_to_mobile")
    private int hasSendToMobile;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "oven_id")
    private String ovenId;

    @Column(name = "mobile_id")
    private String mobileId;

    @Column(name = "create_time")
    private Long createTime;
}
