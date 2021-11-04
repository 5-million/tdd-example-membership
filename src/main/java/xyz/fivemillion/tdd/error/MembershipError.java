package xyz.fivemillion.tdd.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MembershipError {

    DUPLICATED_MEMBERSHIP_REGISTER(HttpStatus.BAD_REQUEST, "이미 등록된 맴버십"),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "unknown exception"),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 맴버십"),
    NOT_MEMBERSHIP_OWNER(HttpStatus.BAD_REQUEST, "맴버십 오너가 아님");

    private final HttpStatus httpStatus;
    private final String description;


}
