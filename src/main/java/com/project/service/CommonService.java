package com.project.service;

import com.project.common.JPushMessage;
import com.project.entity.MobileDetailInfo;
import com.project.entity.OvenDetailInfo;
import com.project.entity.OvenMobileRelation;
import com.project.entity.OvenStatus;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.repository.OvenStatusRepository;
import com.project.request.BindRelationRequest;
import com.project.request.BindTransformToOven;
import com.project.request.RemoveBindRequest;
import com.project.response.ServerResponse;
import com.project.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private OvenStatusRepository ovenStatusRepository;

    @Autowired
    private JPushMessage jPushMessage;

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
        String groupId = bindRelationRequest.getGroupId();

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
        ovenDetailInfo.setGroupId(groupId);
        ovenDetailInfo.setCreateTime(System.currentTimeMillis());
        ovenDetailInfo.setUpdateTime(System.currentTimeMillis());
        ovenDetailInfoRepository.save(ovenDetailInfo);

        //保存手机端信息
        if (mobileDetailInfoRepository.findMobileDetailInfoByMobileId(mobileId) == null){
            MobileDetailInfo mobileDetailInfo = new MobileDetailInfo();
            mobileDetailInfo.setCreateTime(System.currentTimeMillis());
            mobileDetailInfo.setMobileId(mobileId);
            mobileDetailInfo.setTagId(mobileTagId);
            mobileDetailInfo.setUpdateTime(System.currentTimeMillis());
            mobileDetailInfoRepository.save(mobileDetailInfo);
        } else {
            log.info("已存在{}信息",mobileId);
        }

        // 保存手机和设备的绑定关系
        OvenMobileRelation ovenMobileRelation = new OvenMobileRelation();
        ovenMobileRelation.setOvenId(ovenId);
        ovenMobileRelation.setMobileId(mobileId);
        ovenMobileRelation.setUpdateDate(System.currentTimeMillis());
        ovenMobileRelationRepository.save(ovenMobileRelation);

        // 生成一条在线状态的记录，用于扫描在线状态
        OvenStatus ovenStatus = new OvenStatus();
        ovenStatus.setUpdateTime(System.currentTimeMillis());
        ovenStatus.setOvenId(ovenId);
        ovenStatus.setIsSend(0);
        ovenStatusRepository.save(ovenStatus);

        // 绑定成功后，需要把消息推送给烤箱
        BindTransformToOven bindTransformToOven = new BindTransformToOven(ovenId,ovenName,0,"绑定成功");
        jPushMessage.jPushMessage(JsonUtils.getStrFromObject(bindTransformToOven),ovenTagId);
        return ServerResponse.createBySuccessMessage("绑定成功");
    }

    public ServerResponse removeBindRelationService(RemoveBindRequest removeBindRequest){
        String mobileId = removeBindRequest.getMobileId();
        String ovenId = removeBindRequest.getOvenId();

        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        String ovenName = ovenDetailInfo.getOvenName();
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByErrorMessage("没有已绑定的信息");
        }
        if (StringUtils.isBlank(mobileId)){
            mobileId = ovenMobileRelation.getMobileId();
        }
        MobileDetailInfo mobileDetailInfo = mobileDetailInfoRepository.findMobileDetailInfoByMobileId(mobileId);

        ovenMobileRelationRepository.deleteByMobileIdAndAndOvenId(mobileId,ovenId);
        ovenDetailInfoRepository.deleteByOvenId(ovenId);
        // 并删除用于检测在线状态的记录
        OvenStatus ovenStatus = ovenStatusRepository.findOvenStatusByOvenId(ovenId);
        ovenStatusRepository.delete(ovenStatus);
        log.info("删除{}绑定关系成功",ovenId);
        //如果解绑是由烤箱发起的，需要推送消息给手机
        if (removeBindRequest.getType() == 1){
            jPushMessage.jPushMessage(ovenName+"烤箱已主动解绑",mobileDetailInfo.getTagId());
        }
        return ServerResponse.createBySuccessMessage("解绑成功");
    }
}
