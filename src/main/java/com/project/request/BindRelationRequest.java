package com.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindRelationRequest {
    @JsonProperty(value = "oven_id")
    private String ovenId;

    @JsonProperty(value = "mobile_id")
    private String mobileId;

    @JsonProperty(value = "oven_tag_id")
    private String ovenTagId;

    @JsonProperty(value = "mobile_tag_id")
    private String mobileTagId;

    @JsonProperty(value = "oven_name")
    private String ovenName;

    @JsonProperty(value = "group_id")
    private String groupId;

    @JsonProperty(value = "serial_number")
    private String serialNumber;
}
