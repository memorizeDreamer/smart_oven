package com.project.service;

import com.project.repository.ConfigureInfoRepository;
import com.project.response.ServerResponse;
import com.project.util.DateUtil;
import com.project.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OtherService {

    @Value("${file.picture.path}")
    public String fileRootPath;

    @Autowired
    private ConfigureInfoRepository configureInfoRepository;

    /**
     * 获取最大设备数量
     * @return
     */
    public ServerResponse getMaxDeviceNum(){
        int maxNum = Integer.valueOf(configureInfoRepository.findConfigureInfoByConfigId("max_device_num").getConfigValue());
        return ServerResponse.createBySuccess(maxNum);
    }

    /**
     * 删除七天以前的图片
     */
    public void deleteOldPicture(){
        String currentDay = DateUtil.getCurrentDayString();
        String filepath = "/Users/liufeng/Pictures/" + currentDay;
        Boolean deleteStatus = FileUtil.delAllFile(filepath);
        FileUtil.delFolder(filepath);
        log.info("删除{}的状态:{}",filepath,deleteStatus);
    }
}
