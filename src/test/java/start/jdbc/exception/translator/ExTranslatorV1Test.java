package start.jdbc.exception.translator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import start.jdbc.domain.Member;
import start.jdbc.repository.ex.MyDbException;
import start.jdbc.repository.ex.MyDuplicateKeyException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static start.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init () {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(driverManagerDataSource);
        service = new Service(repository);
    }

    @Test
    void saveDuId() {
        service.create("sameId");
        service.create("sameId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("saveId : {}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복 > 복구 시도");
                String newId = generateNewId(memberId);
                log.info("newId : {}", newId);
                repository.save(new Member(newId, 0));
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }

    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values (?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if(e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                }

                throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }

}
