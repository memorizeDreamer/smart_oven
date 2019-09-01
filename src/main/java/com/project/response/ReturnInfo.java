package com.project.response;


public enum ReturnInfo {

    OPERATION_SUCCESS("操作成功", 204),
    OPERATION_FAIL("操作失败", 400),
    NEED_LOGIN("NEED_LOGIN",400),
    EMPTY_PIC_ERROR("上传的图片为空或文件传输错误",400),
    UNKNOWN_DEVICE("未绑定或不存在该设备",400),
    DEVICE_UNCONNECT("设备断开连接",400),
    DEVICE_ISRUNNING("设备正在运行",400);

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
