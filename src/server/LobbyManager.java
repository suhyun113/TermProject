package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LobbyManager {
    private final Map<Integer, Lobby> lobbies; // 대기실 목록
    private final Set<String> nicknames; // 닉네임 목록
    private int lobbyIdCounter;

    public LobbyManager() {
        lobbies = new HashMap<>();
        nicknames = new HashSet<>();
        lobbyIdCounter = 1;
    }

    // 닉네임 등록
    public synchronized boolean registerNickname(String nickname) {
        if (nicknames.contains(nickname)) {
            return false; // 닉네임 중복
        }
        nicknames.add(nickname);
        return true;
    }

    // 닉네임 제거
    public synchronized void removeNickname(String nickname) {
        nicknames.remove(nickname);
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

    public void checkAndRemoveEmptyLobby(Lobby lobby) {
        if (lobby.isEmpty()){
            lobbies.remove(lobby.getLobbyId());
        }
    }

    public Set<String> getAllNicknames() {
        return new HashSet<>(nicknames); // 모든 닉네임 반환
    }
}
