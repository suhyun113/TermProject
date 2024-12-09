package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

// 사용자 관리 및 연결을 처리하는 서버 메인 클래스
// 클라이언트 연결을 수락하고 ClientHandler에 위임

public class MultiClientServer {
    private static final int PORT = 8002; // 서버가 열릴 포트 번호

    public static void main(String[] args) {
        System.out.println("서버가 포트 " + PORT + "에서 시작되었습니다.");

        // 서버 소켓 생성 및 클라이언트 연결 관리
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LobbyManager lobbyManager = new LobbyManager(); // 대기실 관리 객체 초기화
            Thread adminThread = new Thread(() -> handleAdminCommands(lobbyManager));
            adminThread.start();

            // 무한 루프: 클라이언트 연결을 계속해서 수락
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새 클라이언트 연결: " + clientSocket.getInetAddress());

                // 각 클라이언트를 독립적으로 처리하기 위해 새로운 스레드 생성
                new Thread(new ClientHandler(clientSocket, lobbyManager)).start();
            }
        } catch (IOException e) {
            // 서버 소켓 생성 또는 연결 처리 중 오류 발생 시 메시지 출력
            System.err.println("서버 오류: " + e.getMessage());
        }
    }

    private static void handleAdminCommands(LobbyManager lobbyManager) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("서버 명령을 입력하세요 (players: 접속 중인 플레이어 확인, quit: 서버 종료): ");
            String command = scanner.nextLine().trim();

            if ("players".equalsIgnoreCase(command)) {
                Set<String> nicknames = lobbyManager.getAllNicknames();
                System.out.println("현재 접속 중인 플레이어:");
                if (nicknames.isEmpty()) {
                    System.out.println("- 없음");
                } else {
                    for (String nickname : nicknames) {
                        System.out.println("- " + nickname);
                    }
                }
            } else if ("quit".equalsIgnoreCase(command)) {
                System.out.println("서버를 종료합니다.");
                System.exit(0);
            } else {
                System.out.println("알 수 없는 명령입니다. 다시 입력하세요.");
            }
        }
    }
}
