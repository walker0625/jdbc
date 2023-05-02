package start.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository3;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberService2 {

    private final DataSource dataSource;
    private final MemberRepository3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection connection = dataSource.getConnection(); // 유지할 세션의 connection
        try {
            connection.setAutoCommit(false); // 트랜잭션 시작

            bizLogic(fromId, toId, money, connection);

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
    }

    private void bizLogic(String fromId, String toId, int money, Connection connection) throws SQLException {
        Member from = memberRepository.findById(connection, fromId);
        Member to = memberRepository.findById(connection, toId);

        memberRepository.update(connection, from.getMemberId(), from.getMoney() - money);
        exception(to);
        memberRepository.update(connection, to.getMemberId(), to.getMoney() + money);
    }

    private void release(Connection connection) {
        if(connection != null) {
            try {
                connection.setAutoCommit(true); // 기본 상태로 pool로 돌려주기 위함
                connection.close(); // pool로 돌아감
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void exception(Member to) {
        if(to.getMemberId().equals("ex")) {
            throw new IllegalStateException("예외발생");
        }
    }

}
