package be.kdg.ip3.checkersbackend.portal.rest;


import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.domain.SessionId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
public class LauncherClient {

    private final RestClient restClient;

    @Value("${launcher.api.url}")
    private String launcherUrl;

    public LauncherClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public SessionInfo validateSession(SessionId sessionId) {
        return restClient.get()
                .uri(launcherUrl + "/api/lobbies/sessions/{id}", sessionId.id())
                .retrieve()
                .body(SessionInfo.class);
    }
}