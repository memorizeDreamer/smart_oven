package com.project.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindRelationRequest {
    private String ovenId;

    private String mobileId;

    private String ovenRegisterId;

    private String mobileRegisterId;

    private String ovenName;
}
