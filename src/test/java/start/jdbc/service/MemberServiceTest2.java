package start.jdbc.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository3;
import start.jdbc.repository.MemberRepository4;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static start.jdbc.connection.ConnectionConst.*;

class MemberServiceTest2 {

    public static  final String member_a = "memberA";
    public static  final String member_b = "memberB";
    public static  final String member_ex = "ex";

    private MemberRepository4 memberRepository;
    private MemberService3 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepository4(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberService3(transactionManager, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(member_a);
        memberRepository.delete(member_b);
        memberRepository.delete(member_ex);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member a = new Member(member_a, 10000);
        Member b = new Member(member_b, 10000);
        memberRepository.save(a);
        memberRepository.save(b);

        //when
        memberService.accountTransfer(a.getMemberId(), b.getMemberId(), 2000);

        //then
        Member fa = memberRepository.findById(a.getMemberId());
        Member fb = memberRepository.findById(b.getMemberId());

        assertThat(fa.getMoney()).isEqualTo(8000);
        assertThat(fb.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("예외 이체")
    void accountTransferEx() throws SQLException {
        //given
        Member a = new Member(member_a, 10000);
        Member ex = new Member(member_ex, 10000);
        memberRepository.save(a);
        memberRepository.save(ex);

        //when
        assertThatThrownBy(() ->memberService.accountTransfer(a.getMemberId(), ex.getMemberId(), 2000))
                                             .isInstanceOf(IllegalStateException.class);

        //then
        Member fa = memberRepository.findById(a.getMemberId());
        Member fb = memberRepository.findById(ex.getMemberId());

        assertThat(fa.getMoney()).isEqualTo(10000);
        assertThat(fb.getMoney()).isEqualTo(10000);
    }

}