package com.project.controller;

import com.project.common.AuthToken;
import com.project.common.Const;
import com.project.entity.CodeString;
import com.project.entity.MobileUser;
import com.project.response.ReturnInfo;
import com.project.response.ServerResponse;
import com.project.service.MobileUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private MobileUserService mobileUserService;

    private static final String CURRENT_USER = "currentUser";

    private static final String MODULE_NAME = "mobile";

    private static final String CONTROLLER_NAME = "user";
    /**
     * 用户登录
     * @param mobileUser
     * @param session
     * @return
     */
    @PostMapping(value = "/mobile/user/login.do")
    public ServerResponse login(@RequestBody MobileUser mobileUser,
                                @RequestHeader ("token") String token,
                                HttpSession session){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"login.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        String username = mobileUser.getUsername();
        String password = mobileUser.getPassword();
        String mobileNum = mobileUser.getMobile();
        ServerResponse serverResponse = mobileUserService.login(username,password,mobileNum);
        if(serverResponse.isSuccess()){
            session.setAttribute(CURRENT_USER,serverResponse.getData());
            session.setMaxInactiveInterval(-1);
            return serverResponse;
        }
        return serverResponse;
    }

    @RequestMapping("/mobile/user/login_status.do")
    public ServerResponse LoginStatus(@RequestHeader ("token") String token,
                                      HttpSession session){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"login_status.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        MobileUser mobileUser = (MobileUser) session.getAttribute(CURRENT_USER);
        if(mobileUser == null){
            return ServerResponse.createByError(ReturnInfo.NEED_LOGIN.getMsg());
        }
        return ServerResponse.createBySuccess("用户已登录");
    }

    @RequestMapping(value = "/mobile/user/logout.do")
    public ServerResponse logout(@RequestHeader ("token") String token,
                                 HttpSession session){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"logout.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
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
                                   @RequestHeader ("token") String token,
                                   @RequestHeader("codeString") String codeString){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"register.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return mobileUserService.register(mobileUser, anotherPassword, codeString);
    }

    @RequestMapping(value = "/mobile/user/check_valid.do")
    @ResponseBody
    public ServerResponse checkValid(String str, String anotherPassword, String type){
        return mobileUserService.checkValid(str,anotherPassword,type);
    }

    @RequestMapping(value = "/mobile/user/get_user_info.do")
    @ResponseBody
    public ServerResponse getUserInfo(@RequestHeader ("token") String token,
                                      HttpSession session){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"get_user_info.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        MobileUser mobileUser = (MobileUser) session.getAttribute(Const.CURRENT_USER);
        if(mobileUser != null){
            return mobileUserService.getUserInfo(mobileUser.getMobile());
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    @PutMapping(value = "/mobile/user/update_user_info.do")
    public ServerResponse updateUserInfo(@RequestHeader ("token") String token,
                                         @RequestBody MobileUser mobileUser,
                                         HttpSession session){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"update_user_info.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        MobileUser sessionMobileUser = (MobileUser) session.getAttribute(Const.CURRENT_USER);
        if(sessionMobileUser == null){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        return mobileUserService.updateUserInfo(mobileUser, session);
    }

    /*
     * 忘记密码时，先生成短信验证码
     * 随机生成4位数验证码
     * 存入到data_codeString表
     */
    @PostMapping(value = "/mobile/user/get_smsCodeString.do")
    public ServerResponse getSmsCodeString(@RequestHeader ("token") String token,
                                           @RequestBody CodeString codeStringModel){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"get_smsCodeString.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return mobileUserService.sendSmsCodeString(codeStringModel);
    }

    /*
     * 验证短信验证码，验证成功返回token给前端
     * 设置新密码时，和密码一起传入校验
     */
    @RequestMapping(value = "/mobile/user/forget_check_smsCodeString.do")
    @ResponseBody
    public ServerResponse forgetCheckSmsCodeString(String username, String enterSmsCodeString, String type){
        return mobileUserService.checkSmsCodeString(username,enterSmsCodeString,type);
    }

    @PostMapping(value = "/mobile/user/reset_password.do")
    public ServerResponse resetPassword(HttpSession session,
                                        @RequestHeader ("token") String token,
                                        @RequestHeader("passwordNew") String passwordNew,
                                        @RequestHeader("codeString") String codeString){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"reset_password.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        MobileUser mobileUser = (MobileUser) session.getAttribute(Const.CURRENT_USER);
        if(mobileUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return mobileUserService.resetPassword(passwordNew,mobileUser,codeString);
    }

    /*
     * @param username
     * @param passwordNew :新密码
     * @param forgetToken :token
     */
    @RequestMapping(value = "/mobile/user/forget_reset_password.do")
    public ServerResponse forgetRestPassword(@RequestBody MobileUser mobileUser,
                                             @RequestHeader ("token") String token,
                                             @RequestHeader("passwordNew") String passwordNew,
                                             @RequestHeader("codeString") String codeString){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"forget_reset_password.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return mobileUserService.forgetResetPassword(mobileUser,passwordNew,codeString);
    }

    @PostMapping("/mobile/user/upload_user_image.do")
    public ServerResponse uploadUserImage(@RequestHeader("username") String username,
                                          @RequestHeader("token")String token,
                                          @RequestBody MultipartFile file){
        String sourceToken = AuthToken.getAuthToken(MODULE_NAME,CONTROLLER_NAME,"upload_user_image.do");
        if (!AuthToken.checkToken(sourceToken,token)){
            return ServerResponse.createByErrorMessage("鉴权失败");
        }
        return mobileUserService.uploadUserImage(username,file);
    }

    @GetMapping("/mobile/user/get_user_image.do")
    public void getUserImage(@RequestParam("username")String username, HttpServletResponse response){
        mobileUserService.getPicture(username,response);
    }
}
