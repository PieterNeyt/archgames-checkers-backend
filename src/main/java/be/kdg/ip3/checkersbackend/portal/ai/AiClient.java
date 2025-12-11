package be.kdg.ip3.checkersbackend.portal.ai;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Component
public class AiClient {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

}


