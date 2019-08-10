package com.project.response;


public enum ReturnInfo {

    OPERATION_SUCCESS("操作成功", 200),
    OPERATION_FAIL("操作失败", 400),
    NEED_LOGIN("NEED_LOGIN",10000),
    EMPTY_PIC_ERROR("上传的图片为空或文件传输错误",10001),
    UNKNOWN_DEVICE("未绑定或不存在该设备",10002),
    DEVICE_UNCONNECT("设备断开连接",10003),
    DEVICE_ISRUNNING("设备正在运行",10003);

    private String errorMessage;
    private int errorCode;

    ReturnInfo(String msg, int code) {
        this.errorMessage = msg;
        this.errorCode = code;
    }

    public String getMsg() {
        return this.errorMessage;
    }

    public void setMsg(String msg) {
        this.errorMessage = msg;
    }

    public int getCode() {
        return this.errorCode;
    }

    public void setCode(int code) {
        this.errorCode = code;
    }
}
