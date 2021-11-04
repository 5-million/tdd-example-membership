package xyz.fivemillion.tdd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.fivemillion.tdd.domain.Membership;
import xyz.fivemillion.tdd.domain.MembershipType;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByUserIdAndMembershipType(String userId, MembershipType membershipType);
    List<Membership> findAllByUserId(String userId);
}
