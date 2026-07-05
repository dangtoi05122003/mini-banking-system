package com.minibank.UserService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Enum.EmailType;
import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    public void sendOtpEmail(String toEmail, String otp, EmailType type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject(getSubject(type));
            mimeMessageHelper.setText(buildTemplate(type, otp), true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }
    private String getSubject(EmailType type) {
        return switch (type) {
            case VERIFY_ACCOUNT -> "Xác thực tài khoản";
            case RESET_PASSWORD -> "Đặt lại mật khẩu";
        };
    }
    private String buildTemplate(EmailType type, String otp) {
        return switch (type) {
            case VERIFY_ACCOUNT -> buildVerification(otp);
            case RESET_PASSWORD -> buildResetPassword(otp);
        };
    }
    private String buildVerification(String otp) {
        return buildEmailContent("Xác thực tài khoản", otp);
    }
    private String buildResetPassword(String otp) {
        return buildEmailContent("Đặt lại mật khẩu", otp);
    }
    private String buildEmailContent(String descripton, String otp) {
        return """
            <div style="font-family: Arial; max-width:600px;margin:auto;padding:20px;">
                <h2 style="text-align:center;color:#0056b3;">%s</h2>
                <p>Mã OTP của bạn:</p>
                <div style="text-align:center;font-size:36px;font-weight:bold;background:#eee;padding:10px;border-radius:8px;">
                    %s
                </div>
                <p style="text-align:center;">Hiệu lực 5 phút</p>
            </div>
        """.formatted(descripton, otp);
    }
}