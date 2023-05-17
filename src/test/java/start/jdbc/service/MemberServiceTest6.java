package start.jdbc.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import start.jdbc.domain.Member;
import start.jdbc.repository.MemberRepository7;
import start.jdbc.repository.MemberRepositoryInter;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest6 {

    public static  final String member_a = "memberA";
    public static  final String member_b = "memberB";
    public static  final String member_ex = "ex";

    @Autowired
    private MemberRepositoryInter memberRepository;
    @Autowired
    private MemberService6 memberService;

    @TestConfiguration
    static class TestConfig {

        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepositoryInter memberRepository7() {
            return new MemberRepository7(dataSource);
        }

        @Bean
        MemberService6 memberService6() {
            return new MemberService6(memberRepository7());
        }

    }

    @AfterEach
    void after() {
        memberRepository.delete(member_a);
        memberRepository.delete(member_b);
        memberRepository.delete(member_ex);
    }

    @Test
    void aopCheck() {
        Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() {
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
    void accountTransferEx() {
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