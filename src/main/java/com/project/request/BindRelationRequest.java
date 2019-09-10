package com.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindRelationRequest {
    @NotNull
    @JsonProperty(value = "oven_id")
    private String ovenId; // 烤箱id

    @NotNull
    @JsonProperty(value = "mobile_id")
    private String mobileId; // 手机id

    @NotNull
    @JsonProperty(value = "oven_tag_id")
    private String ovenTagId; // 烤箱的tag

    @NotNull
    @JsonProperty(value = "mobile_tag_id")
    private String mobileTagId; // 手机的tag

    @NotNull
    @JsonProperty(value = "oven_name")
    private String ovenName; // 烤箱名字

    @JsonProperty(value = "group_id")
    private String groupId; // 属于哪个商户

    @JsonProperty(value = "serial_number")
    private String serialNumber; // 序列号

    @JsonProperty(value = "nickname")
    private String nickname; // 烤箱昵称

    @JsonProperty(value = "oven_model")
    private String ovenModel;// 烤箱型号

    @JsonProperty(value = "oven_brand")
    private String ovenBrand; // 烤箱品牌

    @JsonProperty(value = "avata")
    private String avata; // 烤箱用户图像

    @JsonProperty(value = "sex")
    private int sex; // 性别 性别1=>男，2=>女

    @JsonProperty(value = "birth")
    private String birth; // 生日

    @JsonProperty(value = "address")
    private String address; // 地址

    @JsonProperty(value = "need_send_pic")
    private int needSendPic; // 是否需要推送任务截图给手机
}
