package com.yutian4060.avevasioncraftonline.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 接口统一返回包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {
    public static final long serialVersionUID = 42L;
    public static final String CODE_SUCCESS = "200";
    public static final String CODE_SYS_ERROR = "500";

    public String code;
    public String msg;
    public Object data;

    public static Result success() {
        return new Result(CODE_SUCCESS, "操作成功", null);
    }

    public static Result success(Object data) {
        return new Result(CODE_SUCCESS, "操作成功", data);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error(String msg) {
        return new Result(CODE_SYS_ERROR, msg, null);
    }

    public static Result error() {
        return new Result(CODE_SYS_ERROR, "系统错误", null);
    }

}
