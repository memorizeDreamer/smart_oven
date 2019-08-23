package com.project.controller;

import com.project.common.AuthToken;
import com.project.request.BindRelationRequest;
import com.project.request.RemoveBindRequest;
import com.project.response.ServerResponse;
import com.project.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @Autowired
    private CommonService commonService;

    private static final String MODULE_NAME = "common";

    private static final String CONTROLLER_NAME = "mobile";

    @PostMapping("/common/mobile/bind.do")
    public ServerResponse bindRelation(@RequestBody BindRelationRequest bindRelationRequest,
                                       @RequestHeader("token") String token){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"bind.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return commonService.bindRelationService(bindRelationRequest);
    }

    @PostMapping("/common/mobile/remove_bind.do")
    public ServerResponse removeBindRelation(@RequestBody RemoveBindRequest removeBindRequest,
                                             @RequestHeader("token") String token){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"remove_bind.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return commonService.removeBindRelationService(removeBindRequest);
    }

    @PostMapping("/common/mobile/get_all_images.do")
    public ServerResponse getAllImages(@RequestHeader("token")String token,
                                       @RequestHeader("start_time")String startTime,
                                       @RequestHeader("end_time")String endTime,
                                       @RequestHeader("oven_id")String ovenId,
                                       @RequestHeader("mobile_id")String mobileId){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"get_all_images.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return commonService.getAllImageService(startTime,endTime,ovenId,mobileId);
    }
}
