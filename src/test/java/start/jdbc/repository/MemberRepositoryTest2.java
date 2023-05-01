package start.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import start.jdbc.connection.ConnectionConst;
import start.jdbc.domain.Member;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static start.jdbc.connection.ConnectionConst.*;

@Slf4j
class MemberRepositoryTest2 {

    MemberRepository2 memberRepository;

    @BeforeEach
    void beforeEach() {
        // 쿼리 실행때마다 connection을 만드는 방식(비효율적)
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        memberRepository = new MemberRepository2(dataSource);
    }

    @Test
    void crud() throws SQLException, InterruptedException {
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

        Thread.sleep(1000);
    }


}