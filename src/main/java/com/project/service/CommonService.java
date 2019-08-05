package com.project.service;

import com.project.entity.MobileDetailInfo;
import com.project.entity.OvenDetailInfo;
import com.project.entity.OvenMobileRelation;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.request.BindRelationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    @Autowired
    private OvenDetailInfoRepository ovenDetailInfoRepository;

    @Autowired
    private MobileDetailInfoRepository mobileDetailInfoRepository;

    @Autowired
    private OvenMobileRelationRepository ovenMobileRelationRepository;

    /**
     * 绑定设备和手机APP信息
     * @param bindRelationRequest
     */
    public void bindRelationService(BindRelationRequest bindRelationRequest){
        String mobileId = bindRelationRequest.getMobileId();
        String ovenId = bindRelationRequest.getOvenId();
        String ovenRegisterId = bindRelationRequest.getOvenRegisterId();
        String mobileRegisterId = bindRelationRequest.getMobileRegisterId();
        String ovenName = bindRelationRequest.getOvenName();

        // 保存设备信息
        OvenDetailInfo ovenDetailInfo = new OvenDetailInfo();
        ovenDetailInfo.setOvenId(ovenId);
        ovenDetailInfo.setOvenName(ovenName);
        ovenDetailInfo.setRegistrationId(ovenRegisterId);
        ovenDetailInfo.setCreateTime(System.currentTimeMillis());
        ovenDetailInfo.setUpdateTime(System.currentTimeMillis());
        ovenDetailInfoRepository.save(ovenDetailInfo);

        //保存手机端信息
        MobileDetailInfo mobileDetailInfo = new MobileDetailInfo();
        mobileDetailInfo.setCreateTime(System.currentTimeMillis());
        mobileDetailInfo.setMobileId(mobileId);
        mobileDetailInfo.setRegistrationId(mobileRegisterId);
        mobileDetailInfo.setUpdateTime(System.currentTimeMillis());
        mobileDetailInfoRepository.save(mobileDetailInfo);

        // 保存手机和设备的绑定关系
        OvenMobileRelation ovenMobileRelation = new OvenMobileRelation();
        ovenMobileRelation.setOvenId(ovenId);
        ovenMobileRelation.setMobileId(mobileId);
        ovenMobileRelation.setUpdateDate(System.currentTimeMillis());
        ovenMobileRelationRepository.save(ovenMobileRelation);
    }
}
