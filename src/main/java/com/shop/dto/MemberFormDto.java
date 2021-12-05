package com.shop.dto;

import com.shop.constant.Bank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=8, max=16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
    private String password;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    @NotEmpty(message = "상세 주소는 필수 입력 값입니다.")
    private String addressDetail;

    @NotNull(message = "환불 은행은 필수 입력 값입니다.")
    private Bank refundBank;

    @NotEmpty(message = "환불 계좌 번호는 입력 값입니다.")
    private String refundAccount;

    @Override
    public String toString() {
        return "MemberFormDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", refundBank=" + refundBank +
                ", refundAccount='" + refundAccount + '\'' +
                '}';
    }
}
