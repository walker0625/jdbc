package start.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    static class MyChecked extends Exception {
        public MyChecked(String message) {
            super(message);
        }
    }

    @Test
    void catchTest() {
        Service service = new Service();
        service.catchEx();
    }

    @Test
    void throwTest() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.throwEx()).isInstanceOf(MyChecked.class);
    }

    static class Service {

        Repository repository = new Repository();

        public void catchEx() {
            try {
                repository.call();
            } catch (MyChecked e) {
                log.info("message {}", e.getMessage(), e);
            }
        }

        public void throwEx() throws MyChecked {
            repository.call();
        }

    }

    static  class Repository {
        public void call() throws MyChecked {
            throw new MyChecked("ex");
        }
    }
}
