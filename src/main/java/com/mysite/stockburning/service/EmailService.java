package com.mysite.stockburning.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;


@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    public String generateCode(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for(int i=0;i<6;i++){
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    public void sendEmail(String receiver, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setRecipients(Message.RecipientType.TO, receiver);
        message.setSubject("StockBurning 회원가입 인증코드");

        StringBuilder mailContent = new StringBuilder();
        mailContent.append("<div>");
        mailContent.append("인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">");
        mailContent.append(code);
        mailContent.append("</strong><br>이메일 인증 절차에 따라 이메일 인증코드를 발급해드립니다.<br>인증코드는 이메일 발송 시점으로부터 3분동안 유효합니다.");

        message.setText(mailContent.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress("cdh3946@gmail.com", "StockBurning"));
        javaMailSender.send(message);
    }
    public void verifyUserEmail(String receiver, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setRecipients(Message.RecipientType.TO, receiver);
        message.setSubject("StockBurning 아이디찾기 인증코드");

        StringBuilder mailContent = new StringBuilder();
        mailContent.append("<div>");
        mailContent.append("인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">");
        mailContent.append(code);
        mailContent.append("</strong><br>이메일 인증 절차에 따라 이메일 인증코드를 발급해드립니다.<br>인증코드는 이메일 발송 시점으로부터 3분동안 유효합니다.");

        message.setText(mailContent.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress("cdh3946@gmail.com", "StockBurning"));
        javaMailSender.send(message);
    }
    public void sendTempPasswd(String receiver, String tempPasswd) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setRecipients(Message.RecipientType.TO, receiver);
        message.setSubject("StockBurning 임시 비밀번호 발급");

        StringBuilder mailContent = new StringBuilder();
        mailContent.append("<div>");
        mailContent.append("안녕하세요 요청하신 임시 비밀번호는 다음과 같습니다.<br><strong style=\"font-size: 30px;\">");
        mailContent.append(tempPasswd);
        mailContent.append("</strong><br>이메일 인증 절차에 따라 임시 비밀번호를 발급해드립니다.<br>임시 비밀번호는 비밀번호 수정을 통해 변경해주시길 바랍니다.");

        message.setText(mailContent.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress("cdh3946@gmail.com", "StockBurning"));
        javaMailSender.send(message);
    }
}
