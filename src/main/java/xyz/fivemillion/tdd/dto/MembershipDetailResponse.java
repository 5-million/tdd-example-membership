package xyz.fivemillion.tdd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import xyz.fivemillion.tdd.domain.MembershipType;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class MembershipDetailResponse {

    private final Long id;
    private final int point;
    private final MembershipType membershipType;
    private final LocalDateTime createdAt;
}
