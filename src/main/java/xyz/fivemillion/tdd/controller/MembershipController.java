package xyz.fivemillion.tdd.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.fivemillion.tdd.domain.MembershipType;
import xyz.fivemillion.tdd.dto.MembershipAddResponse;
import xyz.fivemillion.tdd.dto.MembershipDetailResponse;
import xyz.fivemillion.tdd.dto.MembershipRequest;
import xyz.fivemillion.tdd.service.MembershipService;

import java.util.List;

import static xyz.fivemillion.tdd.config.ValidationGroups.MembershipAddMarker;
import static xyz.fivemillion.tdd.config.ValidationGroups.PointAccumulateMarker;

@NoArgsConstructor
final class MembershipConstants {
    public final static String USER_ID_HEADER = "X-USER-ID";
}

@RestController
@RequiredArgsConstructor
public class MembershipController extends DefaultRestController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/membership")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(MembershipConstants.USER_ID_HEADER) final String userId,
            @RequestBody @Validated(MembershipAddMarker.class) final MembershipRequest request) {

        MembershipAddResponse membershipResponse =
                membershipService.addMembership(userId, request.getMembershipType(), request.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).body(membershipResponse);
    }

    @GetMapping("/api/v1/membership/list")
    public ResponseEntity<List<MembershipDetailResponse>> getMembershipList(
            @RequestHeader(MembershipConstants.USER_ID_HEADER) final String userId
    ) {
        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/membership/detail")
    public ResponseEntity<MembershipDetailResponse> getMembership(
            @RequestHeader(MembershipConstants.USER_ID_HEADER) final String userId,
            @RequestParam("membershipType") final MembershipType membershipType
            ) {
        return ResponseEntity.ok(membershipService.getMembership(userId, membershipType));
    }

    @DeleteMapping("/api/v1/membership/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMembership(
            @RequestHeader(MembershipConstants.USER_ID_HEADER) final String userId,
            @PathVariable("id") final Long membershipId) {
        membershipService.deleteMembership(membershipId, userId);
    }

    @PostMapping("/api/v1/membership/{id}/accumulate")
    @ResponseStatus(HttpStatus.OK)
    public void accumulatePoint(
            @RequestHeader(MembershipConstants.USER_ID_HEADER) final String userId,
            @PathVariable("id") final Long membershipId,
            @RequestBody @Validated(PointAccumulateMarker.class) MembershipRequest request) {
        membershipService.accumulatePoint(membershipId, userId, request.getPoint());
    }
}
