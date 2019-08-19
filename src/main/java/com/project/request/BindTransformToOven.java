package com.project.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindTransformToOven {
    private String ovenId;

    private String ovenName;

    private int status; // 0 绑定成功   1 绑定失败

    private String message;
}
