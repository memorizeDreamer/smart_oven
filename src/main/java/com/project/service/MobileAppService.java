package com.project.service;

import com.project.common.JPushMessage;
import com.project.entity.OvenDetailInfo;
import com.project.entity.OvenMobileRelation;
import com.project.request.TransformRequest;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.util.FileUtil;
import com.project.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MobileAppService {
    @Value("${file.picture.path}")
    public String fileRootPath;

    @Autowired
    private OvenMobileRelationRepository ovenMobileRelationRepository;

    @Autowired
    private OvenDetailInfoRepository ovenDetailInfoRepository;

    @Autowired
    private JPushMessage jPushMessage;

    public void getPicture(String dateTime, String phoneNum, String ovenId,String taskId, HttpServletResponse response){
        try {
            OvenMobileRelation ovenMobileRelation = new OvenMobileRelation();
            ovenMobileRelation.setMobileId(phoneNum);
            ovenMobileRelation.setOvenId(ovenId);
            String filepath = fileRootPath + FileUtil.getFilePath(dateTime,phoneNum,ovenId,taskId);
            File file = new File(filepath);
            @SuppressWarnings("resource")
            InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024]; // 图片文件流缓存池
            while (is.read(buffer) != -1) {
                os.write(buffer);
            }
            os.flush();
        } catch (IOException ioe) {
            log.error(ioe.toString());
        }
    }

    /**
     * 手机APP推送消息给设备
     * @param ovenId
     * @param transformRequest
     * @return
     */
    public ServerResponse transformData(String ovenId, TransformRequest transformRequest){
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        if (ovenDetailInfo == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        jPushMessage.jPushMessage(JsonUtils.getStrFromObject(transformRequest),ovenDetailInfo.getTagId());
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取当前mobile下所有绑定的烤箱
     * @param mobileId
     * @return
     */
    public ServerResponse getOvenMsgUnderCurrentMobile(String mobileId){
        List<OvenMobileRelation> ovenMobileRelationList = ovenMobileRelationRepository.findOvenMobileRelationByMobileIdOrderByUpdateDateDesc(mobileId);
        List<OvenDetailInfo> ovenDetailInfoList = new ArrayList<>();
        for (OvenMobileRelation ovenMobileRelation : ovenMobileRelationList){
            OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenMobileRelation.getOvenId());
            ovenDetailInfoList.add(ovenDetailInfo);
        }
        return ServerResponse.createBySuccess(ovenDetailInfoList);
    }
}
