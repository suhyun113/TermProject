package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// 사용자 관리 및 연결을 처리하는 서버 메인 클래스
// 클라이언트 연결을 수락하고 ClientHandler에 위임

public class MultiClientServer {
    private static final int PORT = 8000; // 서버가 열릴 포트 번호

    public static void main(String[] args) {
        System.out.println("서버가 포트 " + PORT + "에서 시작되었습니다.");

        // 서버 소켓 생성 및 클라이언트 연결 관리
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            RoomManager roomManager = new RoomManager(); // 방 관리 객체 초기화

            // 무한 루프: 클라이언트 연결을 계속해서 수락
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새 클라이언트 연결: " + clientSocket.getInetAddress());

                // 각 클라이언트를 독립적으로 처리하기 위해 새로운 스레드 생성
                new Thread(new ClientHandler(clientSocket, roomManager)).start();
            }
        } catch (IOException e) {
            // 서버 소켓 생성 또는 연결 처리 중 오류 발생 시 메시지 출력
            System.err.println("서버 오류: " + e.getMessage());
        }
    }
}