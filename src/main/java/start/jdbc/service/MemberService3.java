package start.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository4;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberService3 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepository4 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }

    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member from = memberRepository.findById(fromId);
        Member to = memberRepository.findById(toId);

        memberRepository.update(from.getMemberId(), from.getMoney() - money);
        exception(to);
        memberRepository.update(to.getMemberId(), to.getMoney() + money);
    }

    private void exception(Member to) {
        if(to.getMemberId().equals("ex")) {
            throw new IllegalStateException("예외발생");
        }
    }

}
