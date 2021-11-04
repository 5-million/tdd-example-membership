package xyz.fivemillion.tdd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.fivemillion.tdd.error.MembershipError;

@Getter
@RequiredArgsConstructor
public class MembershipException extends RuntimeException {

    private final MembershipError errorCode;
}
