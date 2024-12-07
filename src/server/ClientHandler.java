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
    private Room currentRoom; // 플레이어가 속한 방 객체

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
                if (currentRoom == null) { // 방에 입장하지 않았으면 방 입장 관련 명령만 처리
                    if (message.equals("/quickStart")) {
                        currentRoom = roomManager.assignPlayerToRoom(player); // 방 배치
                        if (currentRoom.getLeader().equals(player)) {
                            out.println("빠른 시작을 통해 방 " + currentRoom.getRoomId() + "에 입장했습니다. 당신은 방의 리더입니다.");
                        } else {
                            out.println("빠른 시작을 통해 방 " + currentRoom.getRoomId() + "에 입장했습니다. 당신은 일반 플레이어입니다.");
                        }
                    } else if (message.equals("/quit")) {
                        out.println("서버를 종료합니다.");
                        break; // 종료
                    } else {
                        out.println("방에 입장하기 전에 사용할 수 있는 명령어는 '/quickStart'뿐입니다.");
                    }
                } else { // 방에 입장했으면 방 관련 명령어 처리
                    switch (message) {
                        case "/quit":
                            roomManager.handlePlayerExit(player); // 방 나가기 처리
                            out.println("방을 나갔습니다.");
                            currentRoom = null; // 방 나간 후, 현재 방을 null로 설정
                            break; // 종료 조건
                        case "/startGame":
                            if (currentRoom.getLeader().equals(player)) {
                                if (currentRoom.areAllPlayersReady()) {
                                    out.println("게임을 시작합니다!");
                                    currentRoom.startGame(); // 게임 시작
                                } else {
                                    out.println("모든 플레이어가 준비 완료 상태여야 게임을 시작할 수 있습니다.");
                                }
                            }
                            break;
                        case "/ready":
                            currentRoom.setPlayerReady(player, true); // 플레이어 준비 상태 변경
                            out.println("준비 완료 상태로 변경되었습니다.");
                            break;
                        case "/notReady":
                            currentRoom.setPlayerReady(player, false); // 플레이어 준비 상태 취소
                            out.println("대기 중 상태로 변경되었습니다.");
                            break;
                        default:
                            out.println("잘못된 명령어입니다. 다시 입력하세요.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트 처리 중 오류: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
