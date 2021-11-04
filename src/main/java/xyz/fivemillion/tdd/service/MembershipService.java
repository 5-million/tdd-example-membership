package xyz.fivemillion.tdd.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.tdd.domain.Membership;
import xyz.fivemillion.tdd.domain.MembershipType;
import xyz.fivemillion.tdd.dto.MembershipDetailResponse;
import xyz.fivemillion.tdd.dto.MembershipAddResponse;
import xyz.fivemillion.tdd.error.MembershipError;
import xyz.fivemillion.tdd.exception.MembershipException;
import xyz.fivemillion.tdd.repository.MembershipRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final PointService ratePointService;

    @Transactional
    public MembershipAddResponse addMembership(String userId, MembershipType membershipType, int point) {
        if (membershipRepository.findByUserIdAndMembershipType(userId, membershipType) != null)
            throw new MembershipException(MembershipError.DUPLICATED_MEMBERSHIP_REGISTER);

        Membership membership = membershipRepository.save(
                Membership.builder()
                        .userId(userId)
                        .membershipType(membershipType)
                        .point(point)
                        .build()
        );

        MembershipAddResponse response = MembershipAddResponse.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .build();

        return response;
    }

    public MembershipDetailResponse getMembership(String userId, MembershipType membershipType) {
        Membership findResult = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);

        if (findResult == null)
            throw new MembershipException(MembershipError.MEMBERSHIP_NOT_FOUND);

        return MembershipDetailResponse.builder()
                .id(findResult.getId())
                .membershipType(findResult.getMembershipType())
                .point(findResult.getPoint())
                .createdAt(findResult.getCreatedAt())
                .build();
    }

    public List<MembershipDetailResponse> getMembershipList(String userId) {
        List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream().map(v -> MembershipDetailResponse.builder()
                .id(v.getId())
                .point(v.getPoint())
                .membershipType(v.getMembershipType())
                .createdAt(v.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteMembership(Long membershipId, String userId) {
        Optional<Membership> opt = membershipRepository.findById(membershipId);

        if(opt.isEmpty())
            throw new MembershipException(MembershipError.MEMBERSHIP_NOT_FOUND);

        Membership membership = opt.get();
        if(!membership.getUserId().equals(userId))
            throw new MembershipException(MembershipError.NOT_MEMBERSHIP_OWNER);

        membershipRepository.deleteById(membershipId);
    }

    @Transactional
    public void accumulatePoint(Long membershipId, String userId, int price) {
        Optional<Membership> opt = membershipRepository.findById(membershipId);
        if(opt.isEmpty())
            throw new MembershipException(MembershipError.MEMBERSHIP_NOT_FOUND);

        Membership membership = opt.get();
        if(!membership.getUserId().equals(userId))
            throw new MembershipException(MembershipError.NOT_MEMBERSHIP_OWNER);

        int point = ratePointService.calculateAmount(price);
        membership.addPoint(point);
    }
}
