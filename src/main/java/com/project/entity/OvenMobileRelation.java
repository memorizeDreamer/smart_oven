package com.project.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "oven_mobile_relation")
public class OvenMobileRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "oven_id")
    private String ovenId;

    @Column(name = "mobile_id")
    private String mobileId;

    @Column(name = "update_date")
    private Long updateDate;
}
