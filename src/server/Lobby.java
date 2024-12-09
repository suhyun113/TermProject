package server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final int lobbyId;
    private final List<Player> players;
    private final int maxPlayers = 2; // 최대 2명
    private boolean gameStarted;

    public Lobby(int lobbyId) {
        this.lobbyId = lobbyId;
        this.players = new ArrayList<>();
        this.gameStarted = false;
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
        gameStarted = true;

        // 카운트다운 메시지
        try {
            for (int i = 3; i > 0; i--) {
                notifyAllPlayers(i + "...");
                Thread.sleep(1000); // 1초 대기
            }
            notifyAllPlayers(lobbyId + "번 대기실에서 게임이 시작됩니다!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
