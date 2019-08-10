package com.project.common;

import com.project.util.DateUtil;
import com.project.util.MD5Util;
import org.apache.commons.lang3.StringUtils;

public class AuthToken {

    /**
     * 获取token
     * @return
     */
    public static String getAuthToken(String moduleName,String controllerName,String methodName){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(moduleName);
        stringBuffer.append(controllerName);
        stringBuffer.append(methodName);
        stringBuffer.append(DateUtil.getCurrentDayString());
        return MD5Util.MD5Encode(new String(stringBuffer),"UTF-8");
    }

    public static Boolean checkToken(String sourceToken, String targetToken){
        if (StringUtils.isBlank(sourceToken) || StringUtils.isBlank(targetToken)){
            return false;
        }
        return sourceToken.equals(targetToken);
    }
}
