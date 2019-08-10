package com.project.service;

import com.project.common.Const;
import com.project.common.SmsInterface;
import com.project.entity.CodeString;
import com.project.entity.ConfigureInfo;
import com.project.entity.MobileUser;
import com.project.repository.CodeStringRepository;
import com.project.repository.ConfigureInfoRepository;
import com.project.repository.MobileUserRepository;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.util.DateUtil;
import com.project.util.FileUtil;
import com.project.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MobileUserService {
    @Value("${file.picture.path}")
    public String fileRootPath;

    @Autowired
    private MobileUserRepository mobileUserRepository;

    @Autowired
    private CodeStringRepository codeStringRepository;

    @Autowired
    private ConfigureInfoRepository configureInfoRepository;

    private final static String USER_HEAD_IMAGE = "http://47.103.85.203:8090/mobile/user/get_user_image.do?username=";

    @Value("${config.key.max_code_num}")
    private String maxCodeNumKey;

    public ServerResponse login(String username, String password) {
        ServerResponse validResponse = this.checkValid(username,password);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        String md5Password = MD5Util.MD5Encode(password,"UTF-8");
        MobileUser mobileUser  = mobileUserRepository.findMobileUserByUsernameAndPassword(username,md5Password);
        //更新登陆时间
        mobileUserRepository.updateTime(System.currentTimeMillis(),username);
        mobileUser.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",mobileUser);
    }

    /*
     * 普通用户注册
     * @param user
     * @param anotherPassword 确认输入框内的密码
     * @param codeString 验证码
     */
    public ServerResponse register(MobileUser mobileUser, String anotherPassword,String codeString){
        //检测用户名是否合法
        ServerResponse validResponse = this.checkValid(mobileUser.getUsername(),anotherPassword, Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //检测手机号是否已经注册
        if (!checkMobileIsResgister(mobileUser.getMobile())){
            return ServerResponse.createByErrorMessage("该手机号已经注册");
        }
        //检测密码6-20位,两次密码是否一致
        validResponse = this.checkValid(mobileUser.getPassword(),anotherPassword,Const.PASSWORD);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkSmsCodeString(mobileUser.getMobile(), codeString,Const.REGISTER);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        //MD5加密
        mobileUser.setPassword(MD5Util.MD5Encode(mobileUser.getPassword(),"UTF-8"));
        mobileUser.setCreateTime(System.currentTimeMillis());
        mobileUser.setUpdateTime(System.currentTimeMillis());
        mobileUserRepository.save(mobileUser);
        return ServerResponse.createBySuccessMessage("注册成功");
    }


    /**
     * 检测手机号是否已经注册
     * @param mobileNum
     * @return
     */
    public Boolean checkMobileIsResgister(String mobileNum){
        MobileUser mobileUser = mobileUserRepository.findMobileUserByMobile(mobileNum);
        return mobileUser == null;
    }

    /**
     * 校验用户名和密码
     * @param username
     * @param password
     * @return
     */
    public ServerResponse<String> checkValid(String username, String password) {
        if (org.apache.commons.lang3.StringUtils.isBlank(username)) {
            return ServerResponse.createByErrorMessage("账号不能为空");
        }
        log.info(username+":current user attemp to login!");
        MobileUser mobileUser = null;
        try {
            mobileUser = mobileUserRepository.findMobileUserByUsername(username);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find user: "+username);
        }
        if (mobileUser == null) {
            return ServerResponse.createByErrorMessage("该账号不存在");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(password)) {
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
        String md5Password = MD5Util.MD5Encode(password,"UTF-8");
        try {
            mobileUser  = mobileUserRepository.findMobileUserByUsernameAndPassword(username,md5Password);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find user: "+username);
            mobileUser = null;
        }
        if (mobileUser==null) {
            return ServerResponse.createByErrorMessage("账号或密码错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    public ServerResponse<String> checkValid(String str,String anotherPassword,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if("mobilenum".equals(type)){
                //限定手机号为全数据，且为11位
                if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
                    return ServerResponse.createByErrorMessage("手机号不能为空");
                }else if(str.length()!=11 && !isNumeric(str)) {
                    return ServerResponse.createByErrorMessage("手机号格式错误");
                }
                MobileUser mobileUser = null;
                try {
                    mobileUser = mobileUserRepository.findMobileUserByMobile(str);
                } catch (EmptyResultDataAccessException e) {
                    log.error("can not find mobilenum: "+str);
                    mobileUser = null;
                }
                if(mobileUser != null){
                    return ServerResponse.createByErrorMessage("该手机号已经注册");
                }
            }
            if ("username".equals(type)){
                if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
                    return ServerResponse.createByErrorMessage("用户名不能为空");
                }else if(str.length() < 6 || str.length() > 11) {
                    return ServerResponse.createByErrorMessage("用户名格式错误");
                }
                MobileUser mobileUser = null;
                try {
                    mobileUser = mobileUserRepository.findMobileUserByUsername(str);
                } catch (EmptyResultDataAccessException e) {
                    log.error("can not find username: "+str);
                    mobileUser = null;
                }
                if(mobileUser != null){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if("password".equals(type)){
                //两次密码是否一致,密码6-16位
                if (org.apache.commons.lang3.StringUtils.isBlank(str)||org.apache.commons.lang3.StringUtils.isBlank(anotherPassword)) {
                    return ServerResponse.createByErrorMessage("密码不能为空");
                }
                if (!str.equals(anotherPassword)) {
                    return ServerResponse.createByErrorMessage("两次密码不一致");
                }
                if (str.length()<6) {
                    return ServerResponse.createByErrorMessage("密码不能少于6位");
                }
                if (str.length()>16) {
                    return ServerResponse.createByErrorMessage("密码不能超过20位");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /*
     * 随机生成4位验证码
     * 调用短信接口，发送验证
     * 并存入验证码
     */
    public ServerResponse sendSmsCodeString(CodeString codeStringModel){
        if (org.apache.commons.lang3.StringUtils.isBlank(codeStringModel.getMobileNum())) {
            return ServerResponse.createByErrorMessage("手机号不能为空");
        }
        if (!checkMobileVaild(codeStringModel.getMobileNum())) {
            return ServerResponse.createByErrorMessage("手机号不合法");
        }
        List<CodeString> codeStringList = codeStringRepository.findAllByMobileNumAndCreateTimeAfter(codeStringModel.getMobileNum(), DateUtil.beforeHourDate(new Date(),1));
        int codeStringSendNums = codeStringList.size();
        log.info("{}已发送验证码次数{}",codeStringModel.getMobileNum(),codeStringSendNums);
        ConfigureInfo configureInfo = configureInfoRepository.findConfigureInfoByConfigId(maxCodeNumKey);
        int maxCodeNum = Integer.valueOf(configureInfo.getConfigValue());
        if (codeStringList != null && maxCodeNum < codeStringSendNums){
            return ServerResponse.createByErrorMessage("验证码次数过多");
        }
        int max=9999;
        int min=1000;
        Random random = new Random();
        String codeString = ""+(random.nextInt(max)%(max-min+1) + min);
        log.info("用户 "+codeStringModel.getMobileNum()+"的短信验证码为："+codeString);
        // 调用短信接口,发送短信
        String result = SmsInterface.sendTplSms(codeStringModel.getMobileNum(), codeString);
        log.info("发送短信验证的状态："+result);
        codeStringModel.setCodeString(codeString);
        codeStringModel.setCreateTime(System.currentTimeMillis());
        codeStringModel.setUpdateTime(System.currentTimeMillis());
        codeStringRepository.save(codeStringModel);
        return ServerResponse.createBySuccessMessage("验证码已发送");
    }

    /*
     * 判断手机号是否符合规范
     */
    private boolean checkMobileVaild(String mobileNum){
        String regexString = "^1[0-9]{10}$";
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(mobileNum);
        return m.matches() && !"0".equals(mobileNum.charAt(1));
    }

    /*
     * 用户输入收到的验证码
     * 验证是否正确，时间是否失效,5分钟
     * 验证成功返回token
     */
    public ServerResponse checkSmsCodeString(String mobileNum,String enterSmsCodeString,String type){
        CodeString codeString = codeStringRepository.findFirstByMobileNumOrderByCreateTimeDesc(mobileNum);
        if (codeString==null) {
            return ServerResponse.createByErrorMessage("请确认输入信息是否正确");
        }
        Long createTime = codeString.getCreateTime();
        Long currentTime = System.currentTimeMillis();
        if (currentTime - createTime > 300000){
            return ServerResponse.createByErrorMessage("验证码已失效");
        }
        if(!(org.apache.commons.lang3.StringUtils.isBlank(enterSmsCodeString))){
            if (Const.FORGETPASSWORD.equals(type)) {
                if (enterSmsCodeString.equals(codeString.getCodeString())) {
                    String forgetToken = UUID.randomUUID().toString();
                    return ServerResponse.createBySuccess(forgetToken);
                }
                return ServerResponse.createByErrorMessage("验证码不正确，请重新输入");
            }
            if (Const.REGISTER.equals(type)) {
                log.debug(codeString.getCodeString()+"update time : "+codeString.getCreateTime());

                if (enterSmsCodeString.equals(codeString.getCodeString())) {
                    return ServerResponse.createBySuccessMessage("验证码验证成功");
                }
                return ServerResponse.createByErrorMessage("验证码不正确，请重新输入");
            }
        }
        return ServerResponse.createByErrorMessage("验证码不能为空");
    }

    /*
     * @param passwordNew :传入新密码
     * @param forgetToken :token
     */
    public ServerResponse forgetResetPassword(MobileUser mobileUser, String passwordNew,String codeString){
        ServerResponse serverResponse = checkSmsCodeString(mobileUser.getMobile(),codeString,Const.FORGETPASSWORD);
        if (serverResponse.getErrorCode() != ReturnInfo.OPERATION_SUCCESS.getCode()){
            return serverResponse;
        }
        String username = mobileUser.getUsername();
        MobileUser existMobileUser = null;
        try {
            existMobileUser = mobileUserRepository.findMobileUserByUsername(username);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find codestring: "+username);
            existMobileUser = null;
        }
        if (existMobileUser == null) {
            return ServerResponse.createByErrorMessage("该账号不存在");
        }
        if (passwordNew.length() > 16 || passwordNew.length() < 6){
            return ServerResponse.createByErrorMessage("密码长度不在6-16");
        }
        String md5Password  = MD5Util.MD5Encode(passwordNew,"UTF-8");
        mobileUserRepository.updatePassword(System.currentTimeMillis(),md5Password,username);
        return ServerResponse.createBySuccessMessage("修改密码成功");
    }

    public ServerResponse<String> resetPassword(String passwordNew,MobileUser mobileUser,String codeString){
        ServerResponse serverResponse = checkSmsCodeString(mobileUser.getMobile(),codeString,Const.FORGETPASSWORD);
        if (serverResponse.getErrorCode() != ReturnInfo.OPERATION_SUCCESS.getCode()){
            return serverResponse;
        }
        if (passwordNew.length() > 16 || passwordNew.length() < 6){
            return ServerResponse.createByErrorMessage("密码长度不在6-16");
        }
        String password = MD5Util.MD5Encode(passwordNew,"UTF-8");
        mobileUserRepository.updatePassword(System.currentTimeMillis(),password,mobileUser.getUsername());
        return ServerResponse.createBySuccessMessage("密码更新成功");
    }

    public ServerResponse uploadUserImage(String username,MultipartFile file){
        String filepath = fileRootPath + username + "/head_image.jpg";
        try {
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            FileUtil.writeFile(filepath,fileInputStream);
        } catch (IOException e) {
            return ServerResponse.createByError(ReturnInfo.EMPTY_PIC_ERROR.getMsg());
        }
        // 上传完成之后，需要更新用户图像记录
        mobileUserRepository.updatePassword(USER_HEAD_IMAGE+username,System.currentTimeMillis(),username);
        return ServerResponse.createBySuccessMessage("上传图像成功");
    }

    public void getPicture(String username, HttpServletResponse response){
        try {
            String filepath = fileRootPath + username  + "/head/head_image.jpg";
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

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        return pattern.matcher(str).matches();
    }
}
