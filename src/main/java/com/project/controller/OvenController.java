package com.project.controller;

import com.project.entity.TransformRequest;
import com.project.response.ServerResponse;
import com.project.service.OvenMobileRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@EnableScheduling
@EnableAsync
@RestController("/oven")
public class OvenController {

    @Autowired
    private OvenMobileRelationService ovenMobileRelationService;

    @PostMapping("/upload/picture.do")
    public ServerResponse collectPicture(@RequestHeader String ovenId,
                                         @RequestBody MultipartFile file){
        return ovenMobileRelationService.collectPicture(ovenId,file);
    }

    @PostMapping("/transform_data.do")
    public ServerResponse transformData(@RequestHeader String ovenId,
                                        @RequestBody TransformRequest transformRequest){
        return ovenMobileRelationService.transformData(ovenId,transformRequest);
    }

    @PostMapping("/check_online.do")
    public void checkOnline(@RequestHeader String ovenId){
        ovenMobileRelationService.checkOnline(ovenId);
    }

    /**
     * 每分钟执行一次扫描
     */
//    @Async
//    @Scheduled(cron = "0 0/1 * * * ?")
    public void scanOvenIfHasOffLine() {
        ovenMobileRelationService.scanOvenIfHasOffline();
    }
}
