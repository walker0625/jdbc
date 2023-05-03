package start.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import start.jdbc.domain.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepository4 {

    private final DataSource dataSource;

    public MemberRepository4(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());

            pstmt.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); // try에서 예외가 발생해도 자원 정리를 보장하기 위해서
        }

    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()) { // pk로 조회한 결과이므로 max 1개
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            } else {
                throw  new NoSuchElementException("member not found");
            }
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int result = pstmt.executeUpdate();
            log.info("result {}", result);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            int result = pstmt.executeUpdate();
            log.info("result {}", result);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); // try에서 예외가 발생해도 자원 정리를 보장하기 위해서
        }
    }

    // pool을 사용하게 되면 close를 할때 연결을 끊는 것이 아니라, connection을 pool에 반납하게 됨
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 트랜잭션 동기화를 위해서는 DataSourceUtils 사용
        // 바로 닫는게 아니라 TransactionSynchronizationManager가 관리하지 않는 connection만 바로 닫음
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private  Connection getConnection() throws SQLException {

        // 트랜잭션 동기화를 위해서는 DataSourceUtils 사용
        // TransactionSynchronizationManager를 통해서 멀티쓰레드에 안전한 connection을 가지고 옴
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info(String.valueOf(connection));
        return connection;
    }

}