package com.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 用户反馈
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_feed")
public class UserFeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    @Column(name = "user_feed_msg")
    private String userFeedMsg;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "user_feed_image")
    private String userFeedImage;
}
