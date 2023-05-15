package start.jdbc.repository;

import start.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryInter {
    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}
