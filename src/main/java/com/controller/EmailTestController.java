package com.controller;

import com.annotation.IgnoreAuth;
import com.service.EmailService;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件测试控制器
 * 用于测试邮件发送功能是否正常
 */
@RestController
@RequestMapping("/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    /**
     * 测试发送简单邮件
     */
    @IgnoreAuth
    @PostMapping("/sendSimpleMail")
    public R testSimpleMail(@RequestParam String to) {
        try {
            boolean success = emailService.sendSimpleMail(
                    to,
                    "测试邮件",
                    "这是一封测试邮件，如果您收到此邮件，说明邮件服务配置正常。"
            );

            if (success) {
                return R.ok("测试邮件发送成功");
            } else {
                return R.error("测试邮件发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("发送测试邮件时出错：" + e.getMessage());
        }
    }

    /**
     * 测试发送验证码邮件
     */
    @IgnoreAuth
    @PostMapping("/sendCodeMail")
    public R testCodeMail(@RequestParam String to) {
        try {
            boolean success = emailService.sendVerificationCode(to, "123456");

            if (success) {
                return R.ok("测试验证码邮件发送成功");
            } else {
                return R.error("测试验证码邮件发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("发送测试验证码邮件时出错：" + e.getMessage());
        }
    }
}