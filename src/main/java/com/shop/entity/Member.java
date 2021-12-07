package com.shop.entity;

import com.shop.constant.Bank;
import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private String addressDetail;

    @Enumerated(EnumType.STRING)
    private Bank refundBank;

    private String refundAccount;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String phone;  //SMS 알림 전송받을 휴대폰 번호 추가
    
    private int point; // 포인트 컬럼 추가

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "member")
    private OAuth2Member oAuth2Member;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setAddressDetail(memberFormDto.getAddressDetail());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        member.setPhone(memberFormDto.getPhone());
        member.setPoint(1000); // 신규 가입시 포인트 추가
        member.setRefundBank(memberFormDto.getRefundBank());
        member.setRefundAccount(memberFormDto.getRefundAccount());
        return member;
    }

    public void updatePoint(int accPoint, int usedPoint) {
        int restPoint = this.point - accPoint + usedPoint;
        this.point = restPoint;
    }
}
