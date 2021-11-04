package xyz.fivemillion.tdd.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import xyz.fivemillion.tdd.domain.Membership;
import xyz.fivemillion.tdd.domain.MembershipType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired MembershipRepository membershipRepository;

    @Test
    void 멤버십_레포지토리_연결() {
        assertNotNull(membershipRepository);
    }

    @Test
    void 멤버십_등록() {
        //given
        Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        //when
        Membership expected = membershipRepository.save(membership);

        //then
        assertNotNull(membership.getId());
        assertEquals(expected.getUserId(), "userId");
        assertEquals(expected.getMembershipType(), MembershipType.NAVER);
        assertEquals(expected.getPoint(), 10000);
    }

    @Test
    void 멤버십_중복() {
        //given
        Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        //when
        membershipRepository.save(membership);
        Membership findResult =
                membershipRepository.findByUserIdAndMembershipType("userId", MembershipType.NAVER);

        //then
       assertNotNull(findResult);
       assertNotNull(findResult.getId());
       assertEquals(findResult.getUserId(), "userId");
       assertEquals(findResult.getMembershipType(), MembershipType.NAVER);
       assertEquals(findResult.getPoint(), 10000);
    }

    @Test
    public void 맴버십_조회_size_is_0() {
        //given

        //when
        List<Membership> result = membershipRepository.findAllByUserId("12345");

        //then
        assertEquals(0, result.size());
    }

    @Test
    public void 맴버십_조회_size_is_2() {
        //given
        final Membership naverMembership = Membership.builder()
                .userId("12345")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        final Membership kakaoMembership = Membership.builder()
                .userId("12345")
                .membershipType(MembershipType.KAKAO)
                .point(5000)
                .build();

        membershipRepository.save(naverMembership);
        membershipRepository.save(kakaoMembership);

        //when
        List<Membership> result = membershipRepository.findAllByUserId("12345");

        //then
        assertEquals(2, result.size());
    }

    @Test
    public void 맴버십추가후삭제() {
        //given
        Membership membership = Membership.builder()
                .userId("12345")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .createdAt(LocalDateTime.now())
                .build();

        Membership savedMembership = membershipRepository.save(membership);

        //when
        membershipRepository.deleteById(savedMembership.getId());

        //then
    }
}
