package com.project.controller;

import com.project.common.AuthToken;
import com.project.request.TransformRequest;
import com.project.response.ServerResponse;
import com.project.service.MobileAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class MobileController {
    @Autowired
    private MobileAppService mobileAppService;

    private static final String MODULE_NAME = "mobile";

    private static final String CONTROLLER_NAME = "process";

    @RequestMapping("/mobile/process/getPicture/{DATE_TIME}/{PHONE_NUM}/{OVEN_ID}/{TASK_ID}/{FILE_NAME}")
    public void getPicture(@PathVariable(value = "DATE_TIME") String datetime,
                           @PathVariable(value = "PHONE_NUM") String phoneNum,
                           @PathVariable(value = "OVEN_ID") String ovenId,
                           @PathVariable(value = "TASK_ID") String taskId,
                           @PathVariable(value = "FILE_NAME") String fileName,
                                             HttpServletResponse response){
        mobileAppService.getPicture(datetime,phoneNum,ovenId,taskId,fileName,response);
    }

    @PostMapping("/mobile/process/transform.do")
    public ServerResponse transformData(@RequestHeader("oven_id") String ovenId,
                                        @RequestHeader("token") String token,
                                        @RequestBody TransformRequest transformRequest){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"transform.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return mobileAppService.transformData(ovenId,transformRequest);
    }

    @PostMapping("/mobile/process/report_device.do")
    public ServerResponse reportDevice(){
        return null;
    }

    @PostMapping("/mobile/process/get_ovens_msg.do")
    public ServerResponse getOvenMsgUnderCurrentMobile(@RequestHeader("mobile_id") String mobileId,
                                                       @RequestHeader("token") String token){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"get_ovens_msg.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return mobileAppService.getOvenMsgUnderCurrentMobile(mobileId);
    }
}
