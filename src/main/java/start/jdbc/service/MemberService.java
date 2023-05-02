package start.jdbc.service;

import lombok.RequiredArgsConstructor;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository2;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
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
