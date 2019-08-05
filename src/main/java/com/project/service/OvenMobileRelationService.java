package com.project.service;

import com.project.common.JPushMessage;
import com.project.entity.MobileDetailInfo;
import com.project.entity.OvenDetailInfo;
import com.project.entity.OvenMobileRelation;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

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
        ovenMobileRelation.getIsBind();
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
        jPushMessage.jPushMessage(url,ovenDetailInfo.getRegistrationId());
        return ServerResponse.createBySuccess();
    }

    /**
     * 设备推送消息给手机APP
     * @param ovenId
     * @param jsonBody
     * @return
     */
    public ServerResponse transformData(String ovenId, String jsonBody){
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        String mobileId = ovenMobileRelation.getMobileId();
        MobileDetailInfo mobileDetailInfo = mobileDetailInfoRepository.findMobileDetailInfoByMobileId(mobileId);
        if (mobileDetailInfo == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        jPushMessage.jPushMessage(jsonBody,mobileDetailInfo.getRegistrationId());
        return ServerResponse.createBySuccess();
    }
}
