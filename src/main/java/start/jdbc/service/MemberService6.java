package start.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository4;
import start.jdbc.repository.MemberRepositoryInter;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberService6 {

    private final MemberRepositoryInter memberRepository;

    // 기본적으로 AOP 기능이기 때문에 SpringContainer가 필요(Test에서 @SpringBootTest 필요)
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
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
