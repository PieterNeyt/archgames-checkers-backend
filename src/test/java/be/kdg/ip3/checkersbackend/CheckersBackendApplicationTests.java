package be.kdg.ip3.checkersbackend;

import be.kdg.ip3.checkersbackend.portal.messaging.sender.CheckersMessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class CheckersBackendApplicationTests {

    @MockitoBean
    private CheckersMessagePublisher checkersMessagePublisher;
    @Test
    void contextLoads() {
    }
}

