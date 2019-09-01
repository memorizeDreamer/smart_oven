package com.project.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JPushMessageEntity {
    @JsonProperty(value = "oven_id")
    private String ovenId; // 烤箱ID

    @JsonProperty("mobile_id")
    private String mobileId; // 手机id

    private int type; // 类型  1-解绑  2-绑定  3-设备上线  4-设备下线  5-烤箱向手机推送图片  6-手机向烤箱发送烤制指令  7-烤箱想手机发送指令

    private String message; // 推送消息，推送的信息
}
