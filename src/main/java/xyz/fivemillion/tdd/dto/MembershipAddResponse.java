package xyz.fivemillion.tdd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.fivemillion.tdd.domain.MembershipType;

@Getter
@Builder
@RequiredArgsConstructor
public class MembershipAddResponse {

    private final Long id;
    private final MembershipType membershipType;
}
