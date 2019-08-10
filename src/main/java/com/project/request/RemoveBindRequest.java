package com.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveBindRequest {
    @JsonProperty(value = "oven_id")
    private String ovenId;

    @JsonProperty(value = "mobile_id")
    private String mobileId;

    private int type;// 绑定由谁发起  0-表示手机APP，1表示烤箱
}
