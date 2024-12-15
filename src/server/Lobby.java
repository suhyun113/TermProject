package server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final int lobbyId;
    private final List<Player> players;
    private final int maxPlayers = 2; // 최대 2명
    private boolean gameStarted;
    private GameSession gameSession; // 현재 게임 세션

    public Lobby(int lobbyId) {
        this.lobbyId = lobbyId;
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.gameSession = null;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);
            return true;
        }
        return false;
    }

    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    public void startGame() {
        if (gameStarted) return; // 이미 게임이 시작되었다면 무시
        gameStarted = true;

        // 카운트다운 메시지
        try {
            for (int i = 3; i > 0; i--) {
                notifyAllPlayers(i + "...");
                Thread.sleep(1000); // 1초 대기
            }
            notifyAllPlayers(lobbyId + "번 대기실에서 게임이 시작됩니다!");

            // 게임 세션 생성 및 시작
            gameSession = new GameSession(players); // 게임 세션 생성
            gameSession.start(); // 게임 세션 실행
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public GameSession getGameSession() {
        return gameSession; // 현재 게임 세션 반환
    }

    public void notifyAllPlayers(String message) {
        for (Player player : players) {
            try {
                PrintWriter out = new PrintWriter(player.getClientSocket().getOutputStream(), true);
                out.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyLobbyState() {
        StringBuilder stateMessage = new StringBuilder("=== 현재 대기실 상태 ===\n");
        for (Player p : players) {
            stateMessage.append("닉네임: ").append(p.getNickname()).append("\n");
        }
        notifyAllPlayers(stateMessage.toString());
    }

    // 대기실이 비어 있는지 확인하는 메서드 추가
    public boolean isEmpty() {
        return players.isEmpty();
    }
}
