package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// 클라이언트와의 통신을 처리하는 클래스

public class ClientHandler implements Runnable {
    private final Socket clientSocket; // 클라이언트와의 연결 소켓
    private final RoomManager roomManager; // 방 관리 객체
    private PrintWriter out; // 클라이언트로부터의 출력 스트림
    private BufferedReader in; // 클라이언트로부터의 입력 스트림
    private Player player; // 현재 플레이어 객체

    // 생성자 : MultiClientServer에서 매개변수로 전달
    public ClientHandler(Socket clientSocket, RoomManager roomManager) {
        this.clientSocket = clientSocket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try {
            // 스트림 초기화
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // 닉네임 등록
            while (true) {
                out.println("서버에 연결되었습니다. 닉네임을 입력하세요");
                String nickname = in.readLine();

                if (nickname == null || nickname.trim().isEmpty()) {
                    out.println("닉네임은 비어 있을 수 없습니다. 다시 입력하세요.");
                    continue;
                }

                if (roomManager.registerPlayer(nickname, new Player(nickname, clientSocket))) {
                    player = new Player(nickname, clientSocket);
                    out.println("환영합니다, " + nickname + "!");
                    break;
                } else {
                    out.println("이미 사용 중인 닉네임입니다. 다시 입력하세요.");
                }
            }

            // 명령어 처리 루프
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/quickStart")) {
                    Room room = roomManager.assignPlayerToRoom(player); // 방 배치
                    out.println("빠른 시작을 통해 방" + room.getRoomId() + "에 입장했습니다.");

                    if (player.isLeader()) {
                        out.println("당신은 방의 리더입니다."); // 리더 메시지 전송
                    }
                } else if (message.equals("/ready")) {
                    roomManager.updatePlayerReadyStatus(player, true); // 준비 상태로 변경
                } else if (message.equals("/notReady")) {
                    roomManager.updatePlayerReadyStatus(player, false); // 대기 상태로 변경
                } else if (message.equals("/startGame") && player.isLeader()) {
                    Room room = roomManager.getRoomForPlayer(player); // 플레이어가 속한 방 가져옴
                    if (room != null) {
                        String startMessage = room.startGame();
                        out.println(startMessage);
                    }
                }

                // 종료 명령
                else if (message.equals("/quit")) {
                    out.println("서버에서 연결을 종료합니다.");
                    break;
                } else {
                    out.println("알 수 없는 명령입니다.");
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트와의 통신 오류: " + e.getMessage());
        } finally {
            // 연결 종료 시 처리
            if (player != null) {
                roomManager.handlePlayerExit(player);
            }
            closeConnection();
        }
    }

    // 연결 종료 처리
    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
        }
        System.out.println("클라이언트 연결 종료");
    }
}