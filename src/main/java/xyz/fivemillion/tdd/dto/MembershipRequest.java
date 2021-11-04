package xyz.fivemillion.tdd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import xyz.fivemillion.tdd.domain.MembershipType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static xyz.fivemillion.tdd.config.ValidationGroups.*;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
public class MembershipRequest {

    @NotNull(groups = {MembershipAddMarker.class, PointAccumulateMarker.class})
    @Min(value = 0, groups = {MembershipAddMarker.class, PointAccumulateMarker.class})
    private final Integer point;

    @NotNull(groups = {MembershipAddMarker.class})
    private final MembershipType membershipType;
}
