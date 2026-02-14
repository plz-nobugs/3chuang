package com.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VerificationCodeCache {

    // 存储验证码的缓存
    private final Map<String, CodeInfo> codeCache = new ConcurrentHashMap<>();

    // 验证码有效期（5分钟）
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;

    // 验证码长度
    private static final int CODE_LENGTH = 6;

    /**
     * 生成验证码
     */
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 存储验证码
     */
    public void storeCode(String email, String code) {
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_TIME;
        codeCache.put(email, new CodeInfo(code, expireTime));
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String inputCode) {
        CodeInfo codeInfo = codeCache.get(email);
        if (codeInfo == null) {
            return false;
        }

        // 检查是否过期
        if (System.currentTimeMillis() > codeInfo.getExpireTime()) {
            codeCache.remove(email);
            return false;
        }

        // 验证码正确后删除
        boolean isValid = codeInfo.getCode().equals(inputCode);
        if (isValid) {
            codeCache.remove(email);
        }

        return isValid;
    }

    /**
     * 检查是否有有效的验证码
     */
    public boolean hasValidCode(String email) {
        CodeInfo codeInfo = codeCache.get(email);
        if (codeInfo == null) {
            return false;
        }

        // 检查是否过期
        if (System.currentTimeMillis() > codeInfo.getExpireTime()) {
            codeCache.remove(email);
            return false;
        }

        return true;
    }

    /**
     * 获取剩余时间（秒）
     */
    public long getRemainingTime(String email) {
        CodeInfo codeInfo = codeCache.get(email);
        if (codeInfo == null) {
            return 0;
        }

        long remaining = (codeInfo.getExpireTime() - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * 验证码信息内部类
     */
    private static class CodeInfo {
        private String code;
        private long expireTime;

        public CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }

        public String getCode() {
            return code;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
}