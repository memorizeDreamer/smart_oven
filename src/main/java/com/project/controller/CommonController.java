package com.project.controller;

import com.project.request.BindRelationRequest;
import com.project.response.ServerResponse;
import com.project.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @Autowired
    private CommonService commonService;

    @PostMapping("/common/bind.do")
    public ServerResponse bindRelation(@RequestBody BindRelationRequest bindRelationRequest){
        return commonService.bindRelationService(bindRelationRequest);
    }

    @PostMapping("/common/remove_bind.do")
    public ServerResponse removeBindRelation(@RequestBody BindRelationRequest bindRelationRequest){
        return commonService.removeBindRelationService(bindRelationRequest);
    }
}
