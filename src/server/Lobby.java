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
        notifyAllPlayers("1번 대기실에서 게임이 시작됩니다!");
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
}
