package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.dto.OrderDto;
import com.shop.entity.EmailNotice;
import com.shop.entity.Item;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.AuthTokenRepository;
import com.shop.repository.EmailNoticeRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.util.List;

@Async
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final AuthTokenService authTokenService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final EmailNoticeRepository emailNoticeRepository;

    public void sendEmail(String to, String subject, String text) {
        this.sendEmail(to, subject, text, false);
    }

    public void sendEmail(String to, String subject, String text, boolean html) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, html);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailAuthCode(String email, HttpSession httpSession) {
        String code = authTokenService.getTokenCode(email).substring(0, 6);
        String subject = "[Bueg] ?????????????????????.";
        String text = "????????? ??????????????? " + code + "?????????.";

        httpSession.setAttribute("emailConfirmCode", code);

        this.sendEmail(email, subject, text);
    }

    public void sendOrderEmail(String email, OrderDto orderDto) {
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

        String subject = "?????? ?????? ???????????????.";

        StringBuffer sb = new StringBuffer();
        sb.append("[Bueg] ?????? ?????? ???????????????.\n");
        sb.append("?????? ?????? : ");
        sb.append(item.getItemNm());
        sb.append("\n?????? ?????? : ");
        sb.append(orderDto.getCount());
        sb.append("\n?????? ?????? : ");
        sb.append(item.getPrice() * orderDto.getCount());
        sb.append("??? ?????????.\n");

        this.sendEmail(email, subject, sb.toString());
        this.addEmailCount();
    }

    public void sendCartOrderEmail(String email, List<OrderDto> orderDtoList, Integer totalPrice) {
        String subject = "?????? ?????? ???????????????.";

        StringBuffer sb = new StringBuffer("[Bueg] ?????? ?????? ???????????????.\n\n");

        for(OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

            sb.append(item.getItemNm());
            sb.append("(");
            sb.append(item.getPrice());
            sb.append(" ???) x ");
            sb.append(orderDto.getCount() + "???\n");
        }

        sb.append("\n?????? ?????? : ");
        sb.append(totalPrice);
        sb.append("???\n");

        this.sendEmail(email, subject, sb.toString());
        this.addEmailCount();
    }

    public void sendPasswordEmail(String email) {
        String subject = "[Bueg] ??????????????? ??????????????????";

        StringBuffer sb = new StringBuffer();
        sb.append("<a href='http://localhost:8080/members/updatePassword?code=");
        sb.append(authTokenService.createToken(email).getCode());
        sb.append("&email=");
        sb.append(email);
        sb.append("'>???????????? ???????????????</a>");

        this.sendEmail(email, subject, sb.toString(), true);
    }

    public void addEmailCount() {
        EmailNotice emailNotice = new EmailNotice();

        emailNoticeRepository.save(emailNotice);
    }

}