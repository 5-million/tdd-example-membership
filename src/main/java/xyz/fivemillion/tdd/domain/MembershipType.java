package xyz.fivemillion.tdd.domain;

import lombok.Getter;

@Getter
public enum MembershipType {

    NAVER("네이버"), KAKAO("카카오"), LINE("라인");

    private String companyName;

    MembershipType(String companyName) {
        this.companyName = companyName;
    }
}
