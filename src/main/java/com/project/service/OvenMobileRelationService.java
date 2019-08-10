package com.project.service;

import com.project.common.JPushMessage;
import com.project.entity.*;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.repository.OvenStatusRepository;
import com.project.request.TransformRequest;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.util.FileUtil;
import com.project.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class OvenMobileRelationService {
    private final static String GET_PICTURE_ROOT_URL = "http://127.0.0.1:8090/mobile/process/getPicture/";

    @Value("${file.picture.path}")
    public String fileRootPath;

    @Autowired
    private OvenMobileRelationRepository ovenMobileRelationRepository;

    @Autowired
    private OvenDetailInfoRepository ovenDetailInfoRepository;

    @Autowired
    private MobileDetailInfoRepository mobileDetailInfoRepository;

    @Autowired
    private OvenStatusRepository ovenStatusRepository;

    @Autowired
    private JPushMessage jPushMessage;

    public Set<String> offLineOvenSet = new HashSet<>();

    /**
     * 设备上传图片，并把回去图片的连接推送给手机APP
     * @param ovenId
     * @param multipartFile
     * @return
     */
    public ServerResponse collectPicture(String ovenId, MultipartFile multipartFile, String taskId){
        if (multipartFile == null){
            return ServerResponse.createByError(ReturnInfo.EMPTY_PIC_ERROR.getMsg());
        }
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        String fileSavePath = FileUtil.getFilePath(ovenMobileRelation,taskId); // 用于推送给手机APP
        String filepath = fileRootPath + fileSavePath;
        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            FileUtil.writeFile(filepath,fileInputStream);
        } catch (IOException e) {
            return ServerResponse.createByError(ReturnInfo.EMPTY_PIC_ERROR.getMsg());
        }
        String url = GET_PICTURE_ROOT_URL + fileSavePath.replace(".jpg","");
        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        return jPushMessage.jPushMessage(url,ovenDetailInfo.getTagId());
    }

    /**
     * 设备推送消息给手机APP
     * @param ovenId
     * @param transformRequest
     * @return
     */
    public ServerResponse transformData(String ovenId, TransformRequest transformRequest){
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        String mobileId = ovenMobileRelation.getMobileId();
        MobileDetailInfo mobileDetailInfo = mobileDetailInfoRepository.findMobileDetailInfoByMobileId(mobileId);
        if (mobileDetailInfo == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        return jPushMessage.jPushMessage(JsonUtils.getStrFromObject(transformRequest),mobileDetailInfo.getTagId());
    }

    /**
     * 定时接受心跳消息，更新烤箱的活跃时间，检测烤箱是否在线
     */
    public void checkOnline(String ovenId){
        OvenStatus ovenStatus = ovenStatusRepository.findOvenStatusByOvenId(ovenId);
        if (ovenStatus == null){
            ovenStatus = new OvenStatus();
            ovenStatus.setOvenId(ovenId);
            ovenStatus.setUpdateTime(System.currentTimeMillis());
            log.info("该设备为第一次发送状态记录");
            ovenStatusRepository.save(ovenStatus);
        } else {
            ovenStatusRepository.updateTime(System.currentTimeMillis(),ovenId);
            // 如果offlineovenset里面包含该ovenId，则表示之前该设备是离线状态，此时已经是上线状态，推送上线状态给手机
            if (offLineOvenSet.contains(ovenId)){
                OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
                OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
                MobileDetailInfo mobileDetailInfo = mobileDetailInfoRepository.findMobileDetailInfoByMobileId(ovenMobileRelation.getMobileId());
                jPushMessage.jPushMessage(ovenDetailInfo.getOvenName()+"设备已上线",mobileDetailInfo.getTagId());
                //推送完成之后，需要从offLineOvenSet中删除，并更新is_send字段为0
                offLineOvenSet.remove(ovenId);
                // 设备上线，则更新设备状态
                ovenDetailInfoRepository.updateOvenOffLine(0,ovenId);
                ovenStatusRepository.updateIsSend(0,ovenId);
            }
        }
    }

    /**
     * 定时扫描数据库，查看烤箱的上次活跃时间，检测是否在线
     */
    public void scanOvenIfHasOffline(){
        Long currentTime = System.currentTimeMillis();
        // 查询出三分钟以前的是数据
        // 差值在180000ms内表示在线;
        List<OvenStatus> ovenStatusList = ovenStatusRepository.findOvenStatusByUpdateTimeBefore(currentTime-180000);
        if (ovenStatusList != null || !ovenStatusList.isEmpty()){
            for (int i=0;i<ovenStatusList.size();i++){
                OvenStatus ovenStatus = ovenStatusList.get(i);
                OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenStatus.getOvenId());
                if (ovenMobileRelation == null){
                    log.info("{}未绑定，不需要检测",ovenStatus.getOvenId());
                    continue;
                }
                MobileDetailInfo mobileDetailInfo = mobileDetailInfoRepository.findMobileDetailInfoByMobileId(ovenMobileRelation.getMobileId());
                OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenMobileRelation.getOvenId());
                String ovenName = ovenDetailInfo.getOvenName();
                log.info("{}设备处于离线状态",ovenName);
                // 为1表示当前记录已经发送不需要再次发送，但是需要
                if (ovenStatus.getIsSend() == 1){
                    log.info("{}已发送过状态，不需要再发送",ovenName);
                    continue;
                }
                offLineOvenSet.add(ovenStatus.getOvenId());
                ServerResponse serverResponse = jPushMessage.jPushMessage(ovenName+"断开连接",mobileDetailInfo.getTagId());
                if (!serverResponse.isSuccess()){
                    log.error("{}推送失败:{}",ovenStatus.getOvenId(),serverResponse.getErrorMessage());
                } else {
                    //推送成功后，更新send状态为1
                    ovenStatusRepository.updateIsSend(1,ovenStatus.getOvenId());
                    // 并更新设备状态为离线
                    ovenDetailInfoRepository.updateOvenOffLine(1,ovenStatus.getOvenId());
                }
            }
        } else {
            log.info("没有已经离线的设备");
        }
    }
}
