package com.project.controller;

import com.project.common.AuthToken;
import com.project.entity.UserFeedBack;
import com.project.request.UserFeedRequest;
import com.project.response.ServerResponse;
import com.project.service.UserFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
public class UserFeedController {
    private final static String MODULE_NAME = "mobile";

    private final static String CONTROLLER_NAME = "user";

    @Autowired
    private UserFeedService userFeedService;

    @PostMapping("/mobile/user/upload_feed.do")
    public ServerResponse uploadUserFeed(@RequestParam("file") MultipartFile file,
                                         @RequestParam("username")String username,
                                         @RequestParam("user_feed_msg")String userFeedMsg,
                                         @RequestHeader("token")String token) {
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"upload_feed.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return userFeedService.uploadUserFeed(username,userFeedMsg,file);
    }

    @GetMapping("/mobile/user/get_user_feed_image.do")
    public void getUserFeedImage(@RequestParam("username")String username,
                                 @RequestParam("imagename")String imageName,
                                 HttpServletResponse response){
        userFeedService.getUserFeedImage(username,imageName, response);
    }

    @GetMapping("/mobile/user/get_user_feed.do")
    public ServerResponse getUserFeed(@RequestParam("username") String username,
                                      @RequestHeader("token")String token){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"get_user_feed.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return userFeedService.getUserFeed(username);
    }
}
