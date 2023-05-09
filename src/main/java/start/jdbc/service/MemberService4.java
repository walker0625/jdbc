package start.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository4;

import java.sql.SQLException;

@Slf4j
public class MemberService4 {

    private final TransactionTemplate transactionTemplate;
    private final MemberRepository4 memberRepository;

    public MemberService4(PlatformTransactionManager transactionManager, MemberRepository4 memberRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        transactionTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

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
