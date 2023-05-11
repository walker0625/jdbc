package start.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UnCheckedTest {

    @Test
    void callCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void callThrow() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUnchecked.class);
    }

    static class MyUnchecked extends RuntimeException {
        public MyUnchecked(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyUnchecked e) { // catch가 강제 되지는 않으나 잡지 않으면 밖으로 던져짐
                log.info("e catch {}", e.getMessage(), e);
            }
        }

        // 따로 선언하지 않아도 unchecked 예외는 밖으로 던져진다(선언도 가능)
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUnchecked("un");
        }
    }

}
