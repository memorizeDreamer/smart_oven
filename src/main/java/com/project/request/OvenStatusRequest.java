package com.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvenStatusRequest {

    @JsonProperty(value = "oven_id")
    private String ovenId;

    @JsonProperty(value = "off_line")
    private int offLine;// 1 表示离线  0 表示在线
}
