package com.project.controller;

import com.project.common.AuthToken;
import com.project.response.ServerResponse;
import com.project.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@EnableAsync
@RestController
public class OtherController {

    @Autowired
    private OtherService otherService;

    private static final String MODULE_NAME = "other";

    private static final String CONTROLLER_NAME = "config";

    @GetMapping("/other/config/max_device_num.do")
    public ServerResponse getMaxDeviceNum(@RequestHeader("token") String token){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"max_device_num.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return otherService.getMaxDeviceNum();
    }

    /**
     * 每天凌晨1点删除七天以前的图片
     */
    @Async
    @Scheduled(cron = "0 0 1 ? * *")
    @PostMapping("/other/delete_picture.do")
    public void deleteOldPicture(){
        otherService.deleteOldPicture();
    }
}
