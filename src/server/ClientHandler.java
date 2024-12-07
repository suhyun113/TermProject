package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket; // 클라이언트와의 연결 소켓
    private final RoomManager roomManager; // 방 관리 객체
    private PrintWriter out; // 클라이언트로부터의 출력 스트림
    private BufferedReader in; // 클라이언트로부터의 입력 스트림
    private Player player; // 현재 플레이어 객체

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
                    out.println("환영합니다, " + nickname + "님!");
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
                    if (room.getLeader().equals(player)) {
                        out.println("빠른 시작을 통해 방 " + room.getRoomId() + "에 입장했습니다. 당신은 방의 리더입니다.");
                    } else {
                        out.println("빠른 시작을 통해 방 " + room.getRoomId() + "에 입장했습니다. 당신은 일반 플레이어입니다.");
                    }
                } else if (message.equals("/quit")) {
                    roomManager.handlePlayerExit(player); // 방 나가기 처리
                    out.println("방을 나갔습니다.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트 처리 중 오류: " + e.getMessage());
        }
    }
}
