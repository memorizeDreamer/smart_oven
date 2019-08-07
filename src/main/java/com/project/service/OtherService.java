package com.project.service;

import com.project.repository.MaxDeviceNumRepository;
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
    private MaxDeviceNumRepository maxDeviceNumRepository;

    /**
     * 获取最大设备数量
     * @return
     */
    public ServerResponse getMaxDeviceNum(){
        int maxNum = maxDeviceNumRepository.findMaxDeviceNumById(1);
        return ServerResponse.createBySuccess(maxNum);
    }

    /**
     * 删除七天以前的图片
     */
    public void deleteOldPicture(){
        String currentDay = DateUtil.getCurrentDayString();
        String filepath = "/Users/liufeng/Pictures/" + "20190804";
        Boolean deleteStatus = FileUtil.delAllFile(filepath);
        FileUtil.delFolder(filepath);
        log.info("删除{}的状态:{}",filepath,deleteStatus);
    }
}
