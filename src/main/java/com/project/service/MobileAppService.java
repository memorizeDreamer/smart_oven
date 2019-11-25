package com.project.service;

import com.alibaba.fastjson.JSONObject;
import com.project.common.JPushMessage;
import com.project.entity.*;
import com.project.message.JPushMessageEntity;
import com.project.repository.*;
import com.project.request.TransformRequest;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.util.FileUtil;
import com.project.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private TransformRecordRepository transformRecordRepository;

    @Autowired
    private MobileDetailInfoRepository mobileDetailInfoRepository;

    @Autowired
    private ImageInfoRepository imageInfoRepository;

    @Autowired
    private JPushMessage jPushMessage;

    public void getPicture(String dateTime, String phoneNum, String ovenId,String taskId, String fileName, HttpServletResponse response){
        try {
            OvenMobileRelation ovenMobileRelation = new OvenMobileRelation();
            ovenMobileRelation.setMobileId(phoneNum);
            ovenMobileRelation.setOvenId(ovenId);
            String filepath = fileRootPath + FileUtil.getFilePath(dateTime,phoneNum,ovenId,taskId, fileName);
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
     * @param transformRequest
     * @return
     */
    public ServerResponse transformData(TransformRequest transformRequest){
        log.info(JsonUtils.getStrFromObject(transformRequest));
        String ovenId = transformRequest.getId();
        String mobileId = transformRequest.getMobileId();
        if (StringUtils.isBlank(ovenId) || StringUtils.isBlank(mobileId)){
            return ServerResponse.createByErrorMessage("请传入烤箱ID和手机ID");
        }
        OvenMobileRelation ovenMobileRelation = ovenMobileRelationRepository.findOvenMobileRelationByOvenId(ovenId);
        if (ovenMobileRelation == null){
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        if (ovenDetailInfo == null){
            // 设备未绑定
            return ServerResponse.createByError(ReturnInfo.UNKNOWN_DEVICE.getMsg());
        }
        if (ovenDetailInfo.getOvenOnline() == 1){
            //设备断网
            return ServerResponse.createByReturnInfo(ReturnInfo.DEVICE_UNCONNECT);
        }
        // 如果是手机向烤箱发送启动指令，则会判断烤箱是否正在运行
        int status = transformRequest.getStatus();
        if (transformRequest.getTo() == 1){
            // 烤箱正在预热或者加热状态下，都不允许再次发送预热或者加热命令
            if (status == 1 || status == 2){
                if (ovenDetailInfo.getOvenStatus() == 1 || ovenDetailInfo.getOvenStatus() == 2){
                    return ServerResponse.createByReturnInfo(ReturnInfo.DEVICE_ISRUNNING);
                }
            }
            // 更新烤箱状态
            JPushMessageEntity jPushMessageEntity = new JPushMessageEntity(ovenId,null,6,JsonUtils.getStrFromObject(transformRequest));
            ServerResponse serverResponse = jPushMessage.jPushMessage(JsonUtils.getStrFromObject(jPushMessageEntity),ovenDetailInfo.getTagId());
            if (serverResponse.isSuccess()){
                ovenDetailInfoRepository.updateOvenNeedSendStatus(transformRequest.getNeedSendPic(),ovenId);
            } else {
                return serverResponse;
            }
        } else {
            JPushMessageEntity jPushMessageEntity = new JPushMessageEntity(ovenId,mobileId,7,JsonUtils.getStrFromObject(transformRequest));
            MobileDetailInfo mobileDetailInfo = mobileDetailInfoRepository.findMobileDetailInfoByMobileId(mobileId);
            ServerResponse serverResponse = jPushMessage.jPushMessage(JsonUtils.getStrFromObject(jPushMessageEntity), mobileDetailInfo.getTagId());
            if (serverResponse.isSuccess()){
                ovenDetailInfoRepository.updateOvenStatus(status,transformRequest.getNeedSendPic(),ovenId);
                log.info("更新烤箱状态{}",status == 0 ? "闲置" : "工作");
            } else {
                return serverResponse;
            }
        }
        //存下发送记录
        TransFormRecord transFormRecord = new TransFormRecord();
        transFormRecord.setCreateTime(System.currentTimeMillis());
        transFormRecord.setDowntempture(transformRequest.getDowntempture());
        transFormRecord.setModel(transformRequest.getModel());
        transFormRecord.setOvenId(transformRequest.getId());
        transFormRecord.setTime(transformRequest.getTime());
        transFormRecord.setTo(transformRequest.getTo());
        transFormRecord.setMobileId(transformRequest.getMobileId());
        transFormRecord.setStatus(transformRequest.getStatus());
        transFormRecord.setWeight(transformRequest.getWeight());
        transFormRecord.setNeedSendPic(transformRequest.getNeedSendPic());
        transformRecordRepository.save(transFormRecord);
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

    /**
     * 获取烤箱状态，最新一张图片的url，是否需要延时摄影
     * @param ovenId
     * @return
     */
    public ServerResponse getOvenStatus(String ovenId){
        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        if (ovenDetailInfo == null){
            return ServerResponse.createByErrorMessage("不存在该烤箱ID");
        }
        int status = ovenDetailInfo.getOvenStatus();

        ImageInfoEntity imageInfoEntity = imageInfoRepository.findFirstByOvenIdOrderByCreateTimeDesc(ovenId);
        if (imageInfoEntity == null){
            return ServerResponse.createByErrorMessage("该烤箱没有任务图片");
        }
        String imageUrl = imageInfoEntity.getImageUrl();
        Boolean isNeedSendPic = !imageUrl.contains(OvenMobileRelationService.UNKNOWN_MOBILE_PIC);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oven_status",status);
        jsonObject.put("is_need_send_pic",isNeedSendPic);
        jsonObject.put("image_url",imageUrl);
        return ServerResponse.createBySuccess(jsonObject);
    }
}
