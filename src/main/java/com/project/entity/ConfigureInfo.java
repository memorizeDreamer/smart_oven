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
@Entity(name = "configure")
public class ConfigureInfo {
    @Id
    @Column(name = "config_id")
    private String configId;

    @Column(name = "config_value")
    private String configValue;

    @Column(name = "update_time")
    private Long updateTime;
}
