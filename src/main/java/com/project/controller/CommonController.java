package com.project.controller;

import com.project.request.BindRelationRequest;
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
    public void bindRelation(@RequestBody BindRelationRequest bindRelationRequest){
        commonService.bindRelationService(bindRelationRequest);
    }
}
