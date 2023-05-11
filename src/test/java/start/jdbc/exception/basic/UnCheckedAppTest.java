package start.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unChecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(RuntimeSQLEx.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();

        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e); // trace 프린트
        }
    }

    static class Controller {
        Service service = new Service();

        // 계속 넘겨서 쌓이는 형태로 오게 되고
        // 대부분의 예외는 복구가 불가(db/네트워크 문제)하여 계속 넘기는게 큰 의미가 없음 > service 레벨에서 처리가 어려움
        public void request() {
            service.call();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        // Service 레벨에서 SQLException(JDBC) 예외에 의존성이 생김 > 다른 sql 기술(JPAException 등)로 바꾸면 바꿔줘야 함
        public void call() {
            repository.call();
            networkClient.call();
        }
    }

    static class Repository {

        public void call() {

            // checked Exception을 unchecked Exception으로 바꿔서 던짐
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLEx(e);
                //throw new RuntimeSQLEx(); - 주의! e(cause)를 빼먹으면 trace가 발생하지 않아 전환전 에러의 문제 파악이 안됨
            }

        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectionEx("con fail");
        }
    }

    static class RuntimeConnectionEx extends RuntimeException {
        public RuntimeConnectionEx(String message) {
            super(message);
        }
    }

    static class RuntimeSQLEx extends RuntimeException {

        public RuntimeSQLEx() {}

        public RuntimeSQLEx(Throwable cause) { // 기존 예외의 내용을 담아줌
            super(cause);
        }
    }

}