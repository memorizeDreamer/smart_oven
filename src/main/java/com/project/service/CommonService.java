package com.project.service;

import com.project.common.JPushMessage;
import com.project.entity.MobileDetailInfo;
import com.project.entity.OvenDetailInfo;
import com.project.entity.OvenMobileRelation;
import com.project.entity.OvenStatus;
import com.project.message.JPushMessageEntity;
import com.project.repository.MobileDetailInfoRepository;
import com.project.repository.OvenDetailInfoRepository;
import com.project.repository.OvenMobileRelationRepository;
import com.project.repository.OvenStatusRepository;
import com.project.request.BindRelationRequest;
import com.project.request.BindTransformToOven;
import com.project.request.RemoveBindRequest;
import com.project.response.ServerResponse;
import com.project.util.DateUtil;
import com.project.util.FileUtil;
import com.project.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private final static String GET_PICTURE_ROOT_URL = "http://47.103.85.203:8090/mobile/process/getPicture/";

    @Value("${file.picture.path}")
    public String fileRootPath;

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

        int sex = bindRelationRequest.getSex();
        String birthday = bindRelationRequest.getBirth();
        String address  = bindRelationRequest.getAddress();
        String ovenModel = bindRelationRequest.getOvenModel();
        String ovenBrand = bindRelationRequest.getOvenBrand();
        String nickname = bindRelationRequest.getNickname();
        String avata = bindRelationRequest.getAvata();

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
        ovenDetailInfo.setAddress(address);
        ovenDetailInfo.setNickname(nickname);
        ovenDetailInfo.setAvata(avata);
        ovenDetailInfo.setSex(sex);
        ovenDetailInfo.setOvenModel(ovenModel);
        ovenDetailInfo.setOvenBrand(ovenBrand);
        ovenDetailInfo.setBirth(birthday);
        ovenDetailInfo.setNeedSendPic(bindRelationRequest.getNeedSendPic());
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
        JPushMessageEntity jPushMessageEntity = new JPushMessageEntity(ovenId,mobileId,2,"绑定成功");
        jPushMessage.jPushMessage(JsonUtils.getStrFromObject(jPushMessageEntity),ovenTagId);
        return ServerResponse.createBySuccessMessage("绑定成功");
    }

    public ServerResponse removeBindRelationService(RemoveBindRequest removeBindRequest){
        String mobileId = removeBindRequest.getMobileId();
        String ovenId = removeBindRequest.getOvenId();

        OvenDetailInfo ovenDetailInfo = ovenDetailInfoRepository.findOvenDetailInfoByOvenId(ovenId);
        String ovenName = null;
        if (ovenDetailInfo == null){
            log.info("{}:没有该设备的详细信息",ovenId);
        }else {
            ovenName = ovenDetailInfo.getOvenName();
        }
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
            JPushMessageEntity jPushMessageEntity = new JPushMessageEntity(ovenId,mobileId,1,ovenName+"烤箱已主动解绑");
            jPushMessage.jPushMessage(JsonUtils.getStrFromObject(jPushMessageEntity),mobileDetailInfo.getTagId());
        }
        return ServerResponse.createBySuccessMessage("解绑成功");
    }

    /**
     * 获取某个烤箱下的时间段图片
     * @param startTime
     * @param endTime
     * @param ovenId
     * @return
     */
    public ServerResponse getAllImageService(String startTime, String endTime, String ovenId,String mobileId){
        if (!StringUtils.isBlank(startTime) && !StringUtils.isBlank(endTime)) {
            String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
            if (currentDay.compareTo(endTime) < 0 ){
                return ServerResponse.createByErrorMessage("结束日期超过当前日期");
            }
            List<String> dayList = new ArrayList<>();
            try {
                int index = 0;
                long count = DateUtil.getDiffDay(startTime,endTime);
                if (count < 0){
                    log.error("开始时间{}==结束时间{}解析出错",startTime,endTime);
                    return ServerResponse.createByErrorMessage("参数错误");
                }
                do {
                    long dayNum = DateUtil.afterDayDate(startTime,index);
                    dayList.add(new SimpleDateFormat("yyyyMMdd").format(new Date(dayNum)));
                    index++;
                } while (count >= index);
            } catch (Exception e){
                log.error("开始时间{}==结束时间{}解析出错",startTime,endTime);
                return ServerResponse.createByErrorMessage("参数错误");
            }
            List<ImageEntity> returnList = new ArrayList<>();
            for (String s : dayList){
                String filePath = fileRootPath + FileUtil.getFilePath(s, mobileId, ovenId);
                File file = new File(filePath);
                String[] taskFileNames = file.list(); // 所有taskId
                returnList.add(getImageEntity(s,taskFileNames,FileUtil.getFilePath(s, mobileId, ovenId)));
            }
            return ServerResponse.createBySuccess(returnList);
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }

    private ImageEntity getImageEntity(String dateTime, String[] taskFileNames,String filepath){
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setDatetime(dateTime);
        List<ImageEntity.ImageMsg> imageMsgList = new ArrayList<>();
        if (taskFileNames == null){
            imageEntity.setImages(imageMsgList);
            return imageEntity;
        }

        for (int i=0;i<taskFileNames.length;i++){
            ImageEntity.ImageMsg imageMsg = new ImageEntity.ImageMsg();
            String newFilePath = fileRootPath + filepath + taskFileNames[i];// 每个taskId的目录
            File file = new File(newFilePath);
            String[] imageFileNames = file.list(); // 所有taskId

            if (imageFileNames == null){
                log.info("{}下没有图片",taskFileNames[i]);
                continue;
            }
            List<String> fileUrlLists = new ArrayList<>();
            for (int j=0;j<imageFileNames.length;j++){
                String url = GET_PICTURE_ROOT_URL + filepath +"/"+ taskFileNames[i] +"/"+ imageFileNames[j].replace(".jpg","");
                fileUrlLists.add(url);
            }
            imageMsg.setImageUrlLit(fileUrlLists);
            imageMsg.setTaskId(taskFileNames[i]);
            imageMsgList.add(imageMsg);
        }
        imageEntity.setImages(imageMsgList);
        return imageEntity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageEntity{
        private String datetime;

        private List<ImageMsg> images;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ImageMsg{
            private String taskId;

            private List<String> imageUrlLit;
        }
    }
}
