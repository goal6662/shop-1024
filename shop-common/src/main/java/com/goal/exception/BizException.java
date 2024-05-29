package com.goal.exception;

import com.goal.enums.BizCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BizException extends RuntimeException {

    private int code;
    private String message;

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.message = bizCodeEnum.getMessage();
    }

}
