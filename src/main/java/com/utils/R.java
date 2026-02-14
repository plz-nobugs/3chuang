package com.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public static R error() {
		return error(500, "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(500, msg);
	}

	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("code", 0);
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Integer getCode() {
		return (Integer) this.get("code");
	}

	public String getMsg() {
		return (String) this.get("msg");
	}

	public Object getData() {
		return this.get("data");
	}

	// 添加一些便捷方法
	public R data(Object data) {
		return this.put("data", data);
	}

	public R message(String message) {
		return this.put("msg", message);
	}

	public R code(Integer code) {
		return this.put("code", code);
	}

	// 判断是否成功
	public boolean isSuccess() {
		Integer code = getCode();
		return code != null && code == 0;
	}

	// 判断是否失败
	public boolean isError() {
		return !isSuccess();
	}
}