package com.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.response.ServerResponse;
import com.project.service.OvenMobileRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
                                        @RequestBody String jsonBody){
        return ovenMobileRelationService.transformData(ovenId,jsonBody);
    }


}
