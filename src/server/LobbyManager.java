package server;

import java.util.HashMap;
import java.util.Map;

public class LobbyManager {
    private final Map<Integer, Lobby> lobbies;
    private int lobbyIdCounter;

    public LobbyManager() {
        lobbies = new HashMap<>();
        lobbyIdCounter = 1;
    }

    public Lobby assignPlayerToLobby(Player player) {
        // 기존 대기실 중 비어 있는 대기실 찾기
        for (Lobby lobby : lobbies.values()) {
            if (!lobby.isGameStarted() && !lobby.isFull()) {
                lobby.addPlayer(player);
                return lobby;
            }
        }

        // 새로운 대기실 생성
        Lobby newLobby = new Lobby(lobbyIdCounter++);
        newLobby.addPlayer(player);
        lobbies.put(newLobby.getLobbyId(), newLobby);
        return newLobby;
    }
}
