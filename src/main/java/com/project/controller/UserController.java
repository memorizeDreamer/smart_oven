package com.project.controller;

import com.project.common.Const;
import com.project.entity.CodeString;
import com.project.entity.MobileUser;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.service.MobileUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private MobileUserService mobileUserService;

    private static final String CURRENT_USER = "currentUser";

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping(value = "/mobile/user/login.do")
    public ServerResponse login(@RequestHeader("username") String username, @RequestHeader("password")String password,HttpSession session){
        ServerResponse serverResponse = mobileUserService.login(username,password);
        if(serverResponse.isSuccess()){
            session.setAttribute(CURRENT_USER,serverResponse.getData());
            session.setMaxInactiveInterval(-1);
            return serverResponse;
        }
        return serverResponse;
    }

    @RequestMapping("/login_status.do")
    @ResponseBody
    public ServerResponse LoginStatus(HttpSession session){
        MobileUser mobileUser = (MobileUser) session.getAttribute(CURRENT_USER);
        if(mobileUser == null){
            return ServerResponse.createByError(ReturnInfo.NEED_LOGIN.getMsg());
        }
        log.info("用户已登录");
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "/logout.do")
    @ResponseBody
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(CURRENT_USER);
        return ServerResponse.createBySuccessMessage("用户已退出");
    }

    /**
     * @param mobileUser
     * @param anotherPassword 确认框内的密码
     * @param codeString 验证码
     */
    @PostMapping(value = "/mobile/user/register.do")
    public ServerResponse register(@RequestBody MobileUser mobileUser,
                                   @RequestHeader("anotherPassword") String anotherPassword,
                                   @RequestHeader("codeString") String codeString){
        return mobileUserService.register(mobileUser, anotherPassword, codeString);
    }

    @RequestMapping(value = "/check_valid.do")
    @ResponseBody
    public ServerResponse checkValid(String str, String anotherPassword, String type, HttpServletResponse response, String callback){
        return mobileUserService.checkValid(str,anotherPassword,type);
    }

    @RequestMapping(value = "/get_user_info.do")
    @ResponseBody
    public ServerResponse getUserInfo(HttpSession session,HttpServletResponse response){
        MobileUser mobileUser = (MobileUser) session.getAttribute(Const.CURRENT_USER);
        if(mobileUser != null){
            return ServerResponse.createBySuccess(mobileUser);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    /*
     * 忘记密码时，先生成短信验证码
     * 随机生成4位数验证码
     * 存入到data_codeString表
     */
    @PostMapping(value = "/mobile/user/get_smsCodeString.do")
    public ServerResponse getSmsCodeString(@RequestBody CodeString codeStringModel){
        return mobileUserService.sendSmsCodeString(codeStringModel);
    }

    /*
     * 验证短信验证码，验证成功返回token给前端
     * 设置新密码时，和密码一起传入校验
     */
    @RequestMapping(value = "/forget_check_smsCodeString.do")
    @ResponseBody
    public ServerResponse forgetCheckSmsCodeString(String username, String enterSmsCodeString, String type, HttpServletResponse response, String callback){
        return mobileUserService.checkSmsCodeString(username,enterSmsCodeString,type);
    }

    /*
     * @param username
     * @param passwordNew :新密码
     * @param forgetToken :token
     */
    @RequestMapping(value = "/forget_reset_password.do")
    @ResponseBody
    public ServerResponse forgetRestPassword(String username, String passwordNew, String forgetToken){
        return mobileUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
}
