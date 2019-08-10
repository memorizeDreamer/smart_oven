package com.project.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@SuppressWarnings("deprecation")
@Data
@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候,如果是null的对象,key也会消失
public class ServerResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int errorCode;
    private String errorMessage;
    private T data;
    private T total;

    private ServerResponse(String errorMessage){
        this.errorMessage = errorMessage;
    }

    private ServerResponse(int errorCode){
        this.errorCode = errorCode;
    }
    private ServerResponse(int errorCode,T data){
        this.errorCode = errorCode;
        this.data = data;
    }


    private ServerResponse(int errorCode,String errorMessage,T data){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    private ServerResponse(int errorCode,String errorMessage,T data,T total){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.total = total;
    }

    private ServerResponse(int errorCode,String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess(){
        return this.errorCode == ReturnInfo.OPERATION_SUCCESS.getCode();
    }

    public int getErrorCode(){
        return errorCode;
    }
    public T getData(){
        return data;
    }
    public String getErrorMessage(){
        return errorMessage;
    }
    public T getTotal(){
        return total;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ReturnInfo.OPERATION_SUCCESS.getCode(),ReturnInfo.OPERATION_SUCCESS.getMsg());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String errorMessage){
        return new ServerResponse<T>(ReturnInfo.OPERATION_SUCCESS.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ReturnInfo.OPERATION_SUCCESS.getCode(),ReturnInfo.OPERATION_SUCCESS.getMsg(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String errorMessage,T data){
        return new ServerResponse<T>(ReturnInfo.OPERATION_SUCCESS.getCode(),errorMessage,data);
    }

    public static <T> ServerResponse<T> createByReturnInfo(ReturnInfo returnInfo){
        return new ServerResponse<T>(returnInfo.getCode(),returnInfo.getMsg());
    }

    public static <T> ServerResponse<T> createBySuccess(T data,T total){
        return new ServerResponse<T>(ReturnInfo.OPERATION_SUCCESS.getCode(),ReturnInfo.OPERATION_SUCCESS.getMsg(),data,total);
    }

    public static <T> ServerResponse<T> createBySuccess(String errorMessage,T data,T total){
        return new ServerResponse<T>(ReturnInfo.OPERATION_SUCCESS.getCode(),errorMessage,data,total);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ReturnInfo.OPERATION_FAIL.getCode(),ReturnInfo.OPERATION_FAIL.getMsg());
    }

    public static <T> ServerResponse<T> createByError(String msg,T data){
        return new ServerResponse<T>(ReturnInfo.OPERATION_FAIL.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(String errorMessage,T data,T total){
        return new ServerResponse<T>(ReturnInfo.OPERATION_FAIL.getCode(),errorMessage,data,total);
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ReturnInfo.OPERATION_FAIL.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

    public static <T> ServerResponse<T> createByError(String msg){
        return new ServerResponse<T>(ReturnInfo.OPERATION_FAIL.getCode(),msg);
    }
}
