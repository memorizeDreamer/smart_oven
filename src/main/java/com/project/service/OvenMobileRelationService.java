package com.project.service;

import com.project.common.JPushMessage;
import com.project.entity.*;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.repository.OvenStatusRepository;
import com.project.request.OvenStatusRequest;
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
import java.util.List;

@Slf4j
@Service
public class OvenMobileRelationService {
    private final static String GET_PICTURE_ROOT_URL = "http://127.0.0.1:8090/mobile/getPicture/";

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

    /**
     * 设备上传图片，并把回去图片的连接推送给手机APP
     * @param ovenId
     * @param multipartFile
     * @return
     */
    public ServerResponse collectPicture(String ovenId, MultipartFile multipartFile){
        if (multipartFile == null){
            return ServerResponse.createByError(ReturnInfo.EMPTY_PIC_ERROR.getMsg());
        }
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        String fileSavePath = FileUtil.getFilePath(ovenMobileRelation); // 用于推送给手机APP
        String filepath = fileRootPath + fileSavePath;
        try {
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            FileUtil.writeFile(filepath,fileInputStream);
        } catch (IOException e) {
            return ServerResponse.createByError(ReturnInfo.EMPTY_PIC_ERROR.getMsg());
        }
        String url = GET_PICTURE_ROOT_URL + fileSavePath.replace(".jpg","");
        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        jPushMessage.jPushMessage(url,ovenDetailInfo.getTagId());
        return ServerResponse.createBySuccess();
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
        jPushMessage.jPushMessage(JsonUtils.getStrFromObject(transformRequest),mobileDetailInfo.getTagId());
        return ServerResponse.createBySuccess();
    }

    /**
     * 定时接受心跳消息，更新烤箱的活跃时间，检测烤箱是否在线
     */
    public void checkOnline(String ovenId){
        OvenStatus ovenStatus = ovenStatusRepository.findOvenStatusByOvenId(ovenId);
        if (ovenStatus == null){
            ovenStatus.setOvenId(ovenId);
            ovenStatus.setUpdateTime(System.currentTimeMillis());
            ovenStatusRepository.save(ovenStatus);
        } else {
            ovenStatusRepository.updateTime(System.currentTimeMillis(),ovenId);
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
                // 为1表示当前记录已经发送
                if (ovenStatus.getIsSend() == 1){
                    continue;
                }
                OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenStatus.getOvenId());
                OvenStatusRequest ovenStatusRequest = new OvenStatusRequest(ovenStatus.getOvenId(),1);
                jPushMessage.jPushMessage(JsonUtils.getStrFromObject(ovenStatusRequest),ovenMobileRelation.getMobileId());
            }
        }
    }
}
