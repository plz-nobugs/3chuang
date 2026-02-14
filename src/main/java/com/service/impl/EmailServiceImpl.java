package com.service.impl;

import com.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service("emailService")
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${custom.mail.subject-prefix:易退智联}")
    private String subjectPrefix;

    @Override
    public boolean sendVerificationCode(String to, String code) {
        try {
            String subject = subjectPrefix + " - 邮箱验证码";
            String content = buildVerificationCodeContent(code);
            return sendHtmlMail(to, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 构建验证码邮件内容
     */
    private String buildVerificationCodeContent(String code) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset=\"UTF-8\">");
        content.append("<title>验证码</title>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }");
        content.append(".container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        content.append(".header { text-align: center; margin-bottom: 30px; }");
        content.append(".logo { font-size: 24px; font-weight: bold; color: #667eea; margin-bottom: 10px; }");
        content.append(".code-box { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0; }");
        content.append(".code { font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 10px 0; }");
        content.append(".footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        content.append("<div class=\"container\">");
        content.append("<div class=\"header\">");
        content.append("<div class=\"logo\">易退智联</div>");
        content.append("<div>智能退货物流系统</div>");
        content.append("</div>");
        content.append("<h2>邮箱验证码</h2>");
        content.append("<p>您好！感谢您使用易退智联服务。</p>");
        content.append("<p>您的验证码是：</p>");
        content.append("<div class=\"code-box\">");
        content.append("<div class=\"code\">").append(code).append("</div>");
        content.append("<div>请在5分钟内使用</div>");
        content.append("</div>");
        content.append("<p><strong>重要提醒：</strong></p>");
        content.append("<ul>");
        content.append("<li>验证码有效期为5分钟，请及时使用</li>");
        content.append("<li>请勿将验证码告知他人</li>");
        content.append("<li>如非本人操作，请忽略此邮件</li>");
        content.append("</ul>");
        content.append("<div class=\"footer\">");
        content.append("<p>此邮件为系统自动发送，请勿回复</p>");
        content.append("<p>© 2024 易退智联 版权所有</p>");
        content.append("</div>");
        content.append("</div>");
        content.append("</body>");
        content.append("</html>");

        return content.toString();
    }
}