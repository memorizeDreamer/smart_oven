package com.project.controller;

import com.project.request.TransformRequest;
import com.project.response.ServerResponse;
import com.project.service.MobileAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MobileController {
    @Autowired
    private MobileAppService mobileAppService;

    @ResponseBody
    @RequestMapping("/mobile/getPicture/{DATE_TIME}/{PHONE_NUM}/{OVEN_ID}/{TASK_ID}")
    public void getPicture(@PathVariable(value = "DATE_TIME") String datetime,
                           @PathVariable(value = "PHONE_NUM") String phoneNum,
                           @PathVariable(value = "OVEN_ID") String ovenId,
                           @PathVariable(value = "TASK_ID") String taskId,
                           HttpServletResponse response){
        mobileAppService.getPicture(datetime,phoneNum,ovenId,taskId,response);
    }

    @ResponseBody
    @PostMapping("/mobile/transform.do")
    public ServerResponse transformData(@RequestHeader String ovenId,
                                        @RequestBody TransformRequest transformRequest){
        return mobileAppService.transformData(ovenId,transformRequest);
    }
}
