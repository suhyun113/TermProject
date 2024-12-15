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

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
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

        try {
            for (int i = 3; i > 0; i--) {
                notifyAllPlayers(i + "...");
                Thread.sleep(1000);
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
        return gameSession;
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

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public void endGameAndReset() {
        try {
            notifyAllPlayers("곧 " + lobbyId + "번 대기실이 삭제됩니다.");
            for (int i = 3; i > 0; i--) {
                notifyAllPlayers(i + "...");
                Thread.sleep(1000);
            }
            notifyAllPlayers(lobbyId + "번 대기실이 삭제되었습니다.");
            // 카운트다운 메시지
//            for (int i = 3; i > 0; i--) {
//                notifyAllPlayers("게임 종료 후 " + i + "초 후에 대기실이 삭제됩니다.");
//                Thread.sleep(1000);
//            }

            // 대기실 초기화 및 삭제
            notifyAllPlayers("초기 메뉴로 돌아갑니다.");
            players.clear();
            gameStarted = false;
            gameSession = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
