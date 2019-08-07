package com.project.controller;

import com.project.response.ServerResponse;
import com.project.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@EnableAsync
@RestController
public class OtherController {

    @Autowired
    private OtherService otherService;

    @GetMapping("/other/max_device_num.do")
    public ServerResponse getMaxDeviceNum(){
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
