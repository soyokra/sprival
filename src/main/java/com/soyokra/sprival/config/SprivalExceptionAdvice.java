package com.soyokra.sprival.config;

import com.soyokra.sprival.app.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@ControllerAdvice
public class SprivalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseUtil<Object> exception(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.error(500, "Server Error");
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseUtil<Object> httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage(), e);
        return ResponseUtil.error(405, "Client Error: Method Not Allowed");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseUtil<Object> methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        // 请求参数异常
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        String message = "Client Error: ";
        for (ObjectError objectError: errorList) {
            if(objectError instanceof FieldError){
                message += ((FieldError) objectError).getField() + objectError.getDefaultMessage();
            }else {
                message += objectError.getDefaultMessage();
            }
            break;
        }
        log.warn(e.getMessage(), e);
        return ResponseUtil.error(400, message);
    }
}
