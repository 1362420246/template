package com.qbk.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResultUtil {

    public <T> RestResponse<T> ok(String msg,T data){
        return RestResponse.create(ResultStatus.SUCCESS.getErrorCode(),msg,data);
    }

    public <T> RestResponse<T> ok(T data){
       return ok(ResultStatus.SUCCESS.getErrorMsg(),data);
    }

    public <T> RestResponse<T> ok(){
        return ok(null);
    }

    public <T> RestResponse<T> error(){
        return RestResponse.create(ResultStatus.FAIL.getErrorCode(),ResultStatus.FAIL.getErrorMsg(),null);
    }

    public <T> RestResponse<T> error(String msg){
        return RestResponse.create(ResultStatus.FAIL.getErrorCode(),msg,null);
    }

    public  <T> RestResponse<T> common(ResultStatus resultStatus ,T data){
        return RestResponse.create(resultStatus.getErrorCode(),resultStatus.getErrorMsg(),data);
    }
    public  <T> RestResponse<T> common(ResultStatus resultStatus ){
        return RestResponse.create(resultStatus.getErrorCode(),resultStatus.getErrorMsg(),null);
    }

    public RestResponse<Object> common(Integer code, String msg) {
        return RestResponse.create(code,msg,null);
    }

    public <T> RestResponse<T> common(Integer code, String msg  , T data) {
        return RestResponse.create(code,msg,data);
    }
}
