package be.kdg.ip3.checkersbackend.portal.ai;


import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveRequest;
import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class AiClient {

    private final RestClient restClient;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AiMoveResponse requestAiMove(AiMoveRequest dto) {
        return restClient.post()
                .uri(aiServiceUrl)
                .body(dto)
                .retrieve()
                .body(AiMoveResponse.class);
    }
}


