package com.project.service;

import com.project.entity.MobileDetailInfo;
import com.project.entity.OvenDetailInfo;
import com.project.entity.OvenMobileRelation;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.request.BindRelationRequest;
import com.project.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
    public ServerResponse bindRelationService(BindRelationRequest bindRelationRequest){
        String mobileId = bindRelationRequest.getMobileId();
        String ovenId = bindRelationRequest.getOvenId();
        String ovenTagId = bindRelationRequest.getOvenTagId();
        String mobileTagId = bindRelationRequest.getMobileTagId();
        String ovenName = bindRelationRequest.getOvenName();

        //查看改手机已经绑定的数量
        List<OvenMobileRelation> ovenMobileRelationList = ovenMobileRelationRepository.findOvenMobileRelationByMobileIdOrderByUpdateDateDesc(mobileId);
        log.info("找到{}已经绑定的数量{}",mobileId,ovenMobileRelationList.size());
        if (ovenMobileRelationList.size() > 20){
            return ServerResponse.createByErrorMessage("最多绑定20个设备");
        }
        //检测改烤箱是否已经被绑定
        OvenMobileRelation existOvenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (existOvenMobileRelation != null){
            return ServerResponse.createByError("该烤箱已经被绑定",existOvenMobileRelation);
        }

        // 保存设备信息
        OvenDetailInfo ovenDetailInfo = new OvenDetailInfo();
        ovenDetailInfo.setOvenId(ovenId);
        ovenDetailInfo.setOvenName(ovenName);
        ovenDetailInfo.setTagId(ovenTagId);
        ovenDetailInfo.setCreateTime(System.currentTimeMillis());
        ovenDetailInfo.setUpdateTime(System.currentTimeMillis());
        ovenDetailInfoRepository.save(ovenDetailInfo);

        //保存手机端信息
        MobileDetailInfo mobileDetailInfo = new MobileDetailInfo();
        mobileDetailInfo.setCreateTime(System.currentTimeMillis());
        mobileDetailInfo.setMobileId(mobileId);
        mobileDetailInfo.setTagId(mobileTagId);
        mobileDetailInfo.setUpdateTime(System.currentTimeMillis());
        mobileDetailInfoRepository.save(mobileDetailInfo);

        // 保存手机和设备的绑定关系
        OvenMobileRelation ovenMobileRelation = new OvenMobileRelation();
        ovenMobileRelation.setOvenId(ovenId);
        ovenMobileRelation.setMobileId(mobileId);
        ovenMobileRelation.setUpdateDate(System.currentTimeMillis());
        ovenMobileRelationRepository.save(ovenMobileRelation);
        return ServerResponse.createBySuccessMessage("绑定成功");
    }

    public ServerResponse removeBindRelationService(BindRelationRequest bindRelationRequest){
        String mobileId = bindRelationRequest.getMobileId();
        String ovenId = bindRelationRequest.getOvenId();

        // 删除绑定关系
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByMobileIdAndOvenId(mobileId,ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByErrorMessage("没有已绑定的信息");
        } else {
            ovenMobileRelationRepository.deleteByMobileIdAndAndOvenId(mobileId,ovenId);
            ovenDetailInfoRepository.deleteByOvenId(ovenId);
            log.info("删除{}绑定关系成功",ovenId);
            return ServerResponse.createBySuccessMessage("解绑成功");
        }
    }
}
