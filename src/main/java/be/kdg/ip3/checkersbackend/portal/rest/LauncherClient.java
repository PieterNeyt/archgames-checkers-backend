package be.kdg.ip3.checkersbackend.portal.rest;


import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.domain.SessionId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LauncherClient {

    private final RestTemplate client;

    @Value("${launcher.api.url}")
    private String launcherUrl;

    public LauncherClient(RestTemplate client) {
        this.client = client;
    }

    public SessionInfo validateSession(SessionId sessionId) {
        String url = launcherUrl + "/api/lobbies/sessions/" + sessionId.id();
        return client.getForObject(url, SessionInfo.class);
    }
}
