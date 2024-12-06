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

            // 닉네임 입력 및 등록
            while (true) {
                out.println("서버에 연결되었습니다. 닉네임을 입력하세요:");
                String nickname = in.readLine();

                if (nickname == null || nickname.trim().isEmpty()) {
                    out.println("닉네임은 비어 있을 수 없습니다. 다시 입력하세요.");
                    continue;
                }

                if (roomManager.registerPlayer(nickname, new Player(nickname, clientSocket))) {
                    player = new Player(nickname, clientSocket); // 플레이어 객체 생성
                    out.println("환영합니다, " + nickname + "!");
                    break;
                } else {
                    out.println("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력하세요.");
                }
            }

            // 방에 플레이어 배치
            Room assignedRoom = roomManager.assignPlayerToRoom(player);
            out.println("현재 방 " + assignedRoom.getRoomId() + "에 배치되었습니다.");

            // 클라이언트 메시지 수신 및 처리 루프
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[" + player.getNickname() + "]: " + message);

                // 방 생성 명령
                if (message.startsWith("/createRoom")) {
                    String[] parts = message.split(" ");
                    if (parts.length == 2) {
                        try {
                            int maxCapacity = Integer.parseInt(parts[1]);
                            Room newRoom = new Room(roomManager.assignPlayerToRoom(player).getRoomId(), maxCapacity);
                            out.println("새 방이 생성되었습니다. 방 ID: " + newRoom.getRoomId());
                        } catch (NumberFormatException e) {
                            out.println("잘못된 입력입니다. /createRoom <최대 인원> 형식으로 입력하세요.");
                        } catch (IllegalArgumentException e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        out.println("잘못된 입력입니다. /createRoom <최대 인원> 형식으로 입력하세요.");
                    }
                }

                // 방 입장 명령
                else if (message.startsWith("/joinRoom")) {
                    String[] parts = message.split(" ");
                    if (parts.length == 2) {
                        try {
                            int roomId = Integer.parseInt(parts[1]);
                            Room room = roomManager.getRoom(roomId);
                            if (room != null && room.addPlayerToRoom(player)) {
                                out.println("방 " + roomId + "에 입장했습니다.");
                            } else {
                                out.println("방에 입장할 수 없습니다. 방이 꽉 찼거나 게임이 진행 중입니다.");
                            }
                        } catch (NumberFormatException e) {
                            out.println("잘못된 입력입니다. /joinRoom <방 ID> 형식으로 입력하세요.");
                        }
                    } else {
                        out.println("잘못된 입력입니다. /joinRoom <방 ID> 형식으로 입력하세요.");
                    }
                }

                // 종료 명령
                else if (message.equals("/quit")) {
                    out.println("서버에서 연결을 종료합니다.");
                    break;
                }

                // 기타 메시지
                else {
                    out.println("명령을 인식하지 못했습니다.");
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트 통신 중 오류 발생: " + e.getMessage());
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