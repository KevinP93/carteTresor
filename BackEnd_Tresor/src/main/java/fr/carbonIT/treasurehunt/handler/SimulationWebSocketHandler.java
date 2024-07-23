package fr.carbonIT.treasurehunt.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SimulationWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws IOException {
        // Traitement des messages entrants
        String payload = message.getPayload();
        // Envoyer un message de réponse
        session.sendMessage(new TextMessage("Message reçu: " + payload));
    }

    public void sendUpdate(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }


    // Maintient d'une liste de sessions pour envoyer des mises à jour
    private final List<WebSocketSession> sessions = new ArrayList<>();
}
