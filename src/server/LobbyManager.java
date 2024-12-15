package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LobbyManager {
    private static LobbyManager instance; // 싱글톤 인스턴스

    private final Map<Integer, Lobby> lobbies; // 대기실 목록
    private final Set<String> nicknames; // 닉네임 목록
    private int lobbyIdCounter;

    private LobbyManager() {
        lobbies = new HashMap<>();
        nicknames = new HashSet<>();
        lobbyIdCounter = 1;
    }

    // 싱글톤 인스턴스 반환
    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
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

    // 대기실 할당
    public synchronized Lobby assignPlayerToLobby(Player player) {
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

    public synchronized void removeLobby(int lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            for (Player player : lobby.getPlayers()) {
                nicknames.remove(player.getNickname()); // 닉네임 목록에서 제거
            }
            lobbies.remove(lobbyId); // 대기실 목록에서 제거
        }
    }

    // 플레이어가 속한 대기실 찾기
    public synchronized Lobby getLobbyByPlayer(Player player) {
        for (Lobby lobby : lobbies.values()) {
            if (lobby.getPlayers().contains(player)) {
                return lobby;
            }
        }
        return null; // 대기실을 찾을 수 없음
    }

    public synchronized Map<Integer, Lobby> getAllLobbies() {
        return new HashMap<>(lobbies); // 모든 대기실 목록 반환
    }

    public synchronized Set<String> getAllNicknames() {
        return new HashSet<>(nicknames); // 모든 닉네임 반환
    }
}
