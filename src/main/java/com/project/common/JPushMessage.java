package com.project.common;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.PlatformNotification;
import com.project.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JPushMessage {

    @Value("${jiguang.master_secret}")
    private String masterSecret;

    @Value("${jiguang.app_key}")
    private String appKey;

    public ServerResponse jPushMessage(String message, String tagId){
        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, clientConfig);
        PushPayload payload = buildPushObjectAllAliasAlert(message,tagId);
        try {
            log.info(payload.toString());
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);
            return ServerResponse.createBySuccessMessage("推送成功");
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            log.error("Sendno: " + payload.getSendno());
            return ServerResponse.createByErrorMessage("极光推送连接失败，请稍后重试");

        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            log.error("Sendno: " + payload.getSendno());
            return ServerResponse.createByErrorMessage(e.getErrorMessage());
        }
    }

    public PushPayload buildPushObjectAllAliasAlert(String alert, String tagId) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag(tagId))
                .setMessage(Message.newBuilder()
                        .setMsgContent(alert)
                        .build())
                .setOptions(Options.newBuilder().setApnsProduction(false).setTimeToLive(1).build())
                .build();
    }
}
