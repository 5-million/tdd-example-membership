package xyz.fivemillion.tdd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.tdd.domain.Membership;
import xyz.fivemillion.tdd.domain.MembershipType;
import xyz.fivemillion.tdd.dto.MembershipDetailResponse;
import xyz.fivemillion.tdd.dto.MembershipAddResponse;
import xyz.fivemillion.tdd.error.MembershipError;
import xyz.fivemillion.tdd.exception.MembershipException;
import xyz.fivemillion.tdd.repository.MembershipRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private RatePointService ratePointService;

    @InjectMocks
    private MembershipService membershipService;

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final int point = 10000;

    @Test
    void 이미_존재하는_멤버십_등록() {
        //given
        given(membershipRepository.findByUserIdAndMembershipType(userId, membershipType))
                .willReturn(Membership.builder().build());

        //when
        MembershipException result = assertThrows(MembershipException.class, () -> {
            membershipService.addMembership(userId, membershipType, point);
        });

        //then
        assertEquals(result.getErrorCode(), MembershipError.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @Test
    void 멤버십_등록_성공() {
        //given
        given(membershipRepository.findByUserIdAndMembershipType(userId, membershipType)).willReturn(null);
        given(membershipRepository.save(any(Membership.class))).willReturn(buildMembership());

        //when
        MembershipAddResponse result = membershipService.addMembership(userId, membershipType, point);

        //then
        assertNotNull(result.getId());
        assertEquals(result.getMembershipType(), MembershipType.NAVER);

        //verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    private Membership buildMembership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .membershipType(MembershipType.NAVER)
                .point(point)
                .build();
    }

    @Test
    public void 맴버십목록조회() {
        //given
        given(membershipRepository.findAllByUserId("12345")).willReturn(
                Arrays.asList(
                        Membership.builder().build(),
                        Membership.builder().build(),
                        Membership.builder().build()
                )
        );

        //when
        List<MembershipDetailResponse> result = membershipService.getMembershipList("12345");

        //then
        assertEquals(3, result.size());
    }

    @Test
    public void 맴버십상세조회실패_존재하지않음() {
        //given
        given(membershipRepository.findByUserIdAndMembershipType("12345", MembershipType.NAVER))
                .willReturn(null);

        //when
        MembershipException result = assertThrows(
                MembershipException.class,
                () -> membershipService.getMembership("12345", MembershipType.NAVER)
        );

        //then
        assertEquals(MembershipError.MEMBERSHIP_NOT_FOUND, result.getErrorCode());
    }

    @Test
    public void 맴버십상세조회성공() {
        //given
        given(membershipRepository.findByUserIdAndMembershipType("12345", MembershipType.NAVER)).willReturn(
                Membership.builder()
                        .id(-1L)
                        .membershipType(MembershipType.NAVER)
                        .point(10000)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        //when
        MembershipDetailResponse result = membershipService.getMembership("12345", MembershipType.NAVER);

        //then
        assertNotNull(result.getId());
        assertEquals(MembershipType.NAVER, result.getMembershipType());
        assertEquals(10000, result.getPoint());
    }

    @Test
    public void 맴버십삭제실패_존재하지않음() {
        //given
        given(membershipRepository.findById(-1L)).willReturn(Optional.empty());

        //when
        MembershipException result =
                assertThrows(MembershipException.class, () -> membershipService.deleteMembership(-1L, "12345"));

        //then
        assertEquals(MembershipError.MEMBERSHIP_NOT_FOUND, result.getErrorCode());
    }

    @Test
    public void 맴버십삭제실패_본인이아님() {
        //given
        given(membershipRepository.findById(-1L)).willReturn(Optional.ofNullable(buildMembership()));

        //when
        MembershipException result =
                assertThrows(MembershipException.class, () -> membershipService.deleteMembership(-1L, "12346"));

        //then
        assertEquals(MembershipError.NOT_MEMBERSHIP_OWNER, result.getErrorCode());
    }

    @Test
    public void 맴버십삭제성공() {
        //given
        given(membershipRepository.findById(-1L)).willReturn(Optional.ofNullable(buildMembership()));

        //when
        membershipService.deleteMembership(-1L, userId);

        //then
        verify(membershipRepository, times(1)).deleteById(-1L);
    }

    @Test
    public void 포인트적립실패_존재하지않음() {
        //given
        given(membershipRepository.findById(-1L)).willReturn(Optional.empty());

        //when
        MembershipException result =
                assertThrows(MembershipException.class, () -> membershipService.accumulatePoint(-1L, "12345", 10000));

        //then
        assertEquals(MembershipError.MEMBERSHIP_NOT_FOUND, result.getErrorCode());
    }

    @Test
    public void 포인트적립실패_본인이아님() {
        //given
        given(membershipRepository.findById(-1L)).willReturn(Optional.ofNullable(buildMembership()));

        //when
        MembershipException result =
                assertThrows(MembershipException.class, () -> membershipService.accumulatePoint(-1L, "12345", 10000));

        //then
        assertEquals(MembershipError.NOT_MEMBERSHIP_OWNER, result.getErrorCode());
    }

    @Test
    public void 포인트적립성공() {
        //given
        Membership membership = buildMembership();
        given(membershipRepository.findById(-1L)).willReturn(Optional.ofNullable(membership));

        //when
        membershipService.accumulatePoint(-1L, userId, 10000);

        //then
        verify(ratePointService, times(1)).calculateAmount(10000);
    }
}
