package start.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;

public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        // 계속 넘겨서 쌓이는 형태로 오게 되고
        // 대부분의 예외는 복구가 불가(db/네트워크 문제)하여 계속 넘기는게 큰 의미가 없음 > service 레벨에서 처리가 어려움
        public void request() throws SQLException, ConnectException {
            service.call();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        // Service 레벨에서 SQLException(JDBC) 예외에 의존성이 생김 > 다른 sql 기술(JPAException 등)로 바꾸면 바꿔줘야 함
        public void call() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class Repository {

        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("con ex");
        }
    }

}
