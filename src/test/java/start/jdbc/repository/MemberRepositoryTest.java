package start.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import start.jdbc.domain.Member;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryTest {

    MemberRepository memberRepository = new MemberRepository();

    @Test
    void crud() throws SQLException {
        Member member1 = new Member("member7", 20000);
        memberRepository.save(member1);

        Member member = memberRepository.findById(member1.getMemberId());
        log.info("member {}", member);

        // lombok Equals/HashCode 때문에 값이 같으면 같은 값으로 인식
        assertThat(member1).isEqualTo(member);

        memberRepository.update("member1", 30000);
        Member memberu = memberRepository.findById("member1");
        assertThat(memberu.getMoney()).isEqualTo(30000);

        memberRepository.delete(member1.getMemberId());
        assertThatThrownBy(() -> memberRepository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }


}