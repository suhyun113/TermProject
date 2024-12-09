package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient1 {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // 서버 주소
    private static final int SERVER_PORT = 8002;             // 서버 포트

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            String serverMessage;

            // 서버 메시지 처리 루프
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);

                // 닉네임 입력 요청 처리
                if (serverMessage.contains("닉네임을 입력하세요")) {
                    System.out.print("닉네임: ");
                    String nickname = scanner.nextLine();
                    out.println(nickname); // 서버로 닉네임 전송
                }

                // 대기실 입장 전 메뉴 처리
                if (serverMessage.contains("환영합니다")) {
                    System.out.println("1: 빠른 시작");
                    System.out.println("2: 종료");
                    System.out.print("선택: ");
                    String choice = scanner.nextLine();

                    if ("1".equals(choice)) {
                        out.println("/quickStart"); // 빠른 시작 명령 전송
                    } else if ("2".equals(choice)) {
                        out.println("/quit"); // 종료 명령 전송
                        break;
                    } else {
                        System.out.println("올바른 선택이 아닙니다. 다시 입력하세요.");
                    }
                }

                // 게임 종료 메시지를 받으면 프로그램 종료
                if (serverMessage.contains("게임이 시작됩니다!") || serverMessage.contains("게임을 종료합니다.")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }
}
