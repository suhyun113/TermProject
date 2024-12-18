package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final LobbyManager lobbyManager;
    private PrintWriter out;
    private BufferedReader in;
    private Player player;
    private Lobby currentLobby;

    public ClientHandler(Socket clientSocket, LobbyManager lobbyManager) {
        this.clientSocket = clientSocket;
        this.lobbyManager = lobbyManager;
    }

    @Override
    public void run() {
        try {
            // 스트림 초기화
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // 서버에 연결되었다는 메시지 전송
            out.println("서버에 성공적으로 연결되었습니다.");

            // 닉네임 등록
            while (true) {
                out.println("닉네임을 입력하세요");
                String nickname = in.readLine();

                if (nickname == null || nickname.trim().isEmpty()) {
                    out.println("닉네임은 비어 있을 수 없습니다. 다시 입력하세요.");
                    continue;
                }

                if (lobbyManager.registerNickname(nickname)) {
                    player = new Player(nickname, clientSocket);
                    out.println("환영합니다, " + nickname + "님!");
                    break;
                } else {
                    out.println("이미 사용 중인 닉네임입니다. 다시 입력하세요.");
                }
            }

            // 빠른 시작 처리
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/quickStart")) {
                    currentLobby = lobbyManager.assignPlayerToLobby(player);
                    if (currentLobby.isFull()) {
                        currentLobby.notifyLobbyState();
                        currentLobby.notifyAllPlayers("대기실이 가득 찼습니다. 잠시 뒤에 게임이 시작됩니다.");
                        currentLobby.startGame();
                    } else {
                        out.println(currentLobby.getLobbyId() + "번 대기실에 입장했습니다. 다른 플레이어를 기다리는 중입니다.");
                    }
                } else if (message.equals("/quit")) {
                    out.println("게임을 종료합니다.");

                    // 닉네임 삭제 처리
                    synchronized (lobbyManager) {
                        if (player != null) {
                            lobbyManager.removeNickname(player.getNickname());
                        }
                    }
                    break;
                } else if (message.startsWith("/answer ")) {
                    if (currentLobby == null || !currentLobby.isGameStarted()) {
                        out.println("현재 게임이 진행 중이 아닙니다. /quickStart로 게임을 시작하세요.");
                        return; // return 대신 loop를 계속 진행
                    }

                    // GameSession에 정답 제출
                    String answer = message.substring(8).trim(); // "/answer " 이후의 문자열 추출
                    GameSession gameSession = currentLobby.getGameSession();
                    if (gameSession != null) {
                        gameSession.submitAnswer(player, answer); // GameSession에 정답 전달
                    } else {
                        out.println("현재 게임 세션이 활성화되지 않았습니다.");
                    }
                } else {
                    out.println("알 수 없는 명령입니다.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
