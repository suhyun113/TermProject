package server;

import java.net.Socket;

// 클라이언트와의 통신을 처리하는 클래스
// 추후 구현 예정

public class ClientHandler implements Runnable {
    private final Socket clientSocket; // 클라이언트와의 연결 소켓
    private final RoomManager roomManager; // 방 관리 객체

    // 생성자 : MultiClientServer에서 매개변수로 전달
    public ClientHandler(Socket clientSocket, RoomManager roomManager) {
        this.clientSocket = clientSocket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        // 로직 미구현 상태
        System.out.println("ClientHandler 실행 (추후 구현 예정)");
    }
}

