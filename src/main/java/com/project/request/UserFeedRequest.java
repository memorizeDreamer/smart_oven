package com.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedRequest {

    private String username;

    @JsonProperty(value = "user_feed_msg")
    private String userFeedMsg;

    private MultipartFile file;
}
