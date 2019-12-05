package com.project.service;

import com.project.entity.UserFeedBack;
import com.project.repository.MobileUserRepository;
import com.project.repository.UserFeedRepository;
import com.project.request.UserFeedRequest;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.util.DateUtil;
import com.project.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Slf4j
@Service
public class UserFeedService {

    private String userFeedPath = "/feed";

    private final static String USER_FEED_IMAGE = "http://47.103.98.105:8080/mobile/user/get_user_feed_image.do?username=%s&imagename=%s";

    @Value("${file.picture.path}")
    public String fileRootPath;

    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private MobileUserRepository mobileUserRepository;

    public ServerResponse uploadUserFeed(String username,String userFeedMsg,MultipartFile file){
        if (mobileUserRepository.findMobileUserByUsername(username) == null){
            return ServerResponse.createByErrorMessage("不存在该用户");
        }
        String imageName = DateUtil.getCurrentSecString()+".jpg";
        String filepath = fileRootPath + username + userFeedPath + "/"+ imageName;
        try {
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            FileUtil.writeFile(filepath,fileInputStream);
        } catch (IOException e) {
            return ServerResponse.createByError(ReturnInfo.EMPTY_PIC_ERROR.getMsg());
        }
        UserFeedBack userFeedBack = new UserFeedBack();
        userFeedBack.setUserFeedImage(String.format(USER_FEED_IMAGE,username,imageName));
        userFeedBack.setCreateTime(System.currentTimeMillis());
        userFeedBack.setUserFeedMsg(userFeedMsg);
        userFeedBack.setUsername(username);
        userFeedRepository.save(userFeedBack);
        return ServerResponse.createBySuccessMessage("上传反馈成功");
    }

    public void getUserFeedImage(String username,String imageName, HttpServletResponse response){
        try {
            String filepath = fileRootPath + username + "/feed/"+imageName;
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

    public ServerResponse getUserFeed(String username){
        List<UserFeedBack> userFeedBackList = userFeedRepository.findUserFeedBackByUsernameOrderByCreateTimeDesc(username);
        return ServerResponse.createBySuccess(userFeedBackList);
    }
}
