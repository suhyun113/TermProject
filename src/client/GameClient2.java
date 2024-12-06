package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient2 {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // 서버 주소
    private static final int SERVER_PORT = 8002;             // 서버 포트

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            String serverMessage;
            boolean isLeader = false; // 리더 여부 판단
            boolean isInRoom = false; // 방에 입장했는지 여부

            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);

                // 닉네임 입력 요청 처리
                if (serverMessage.contains("닉네임을 입력하세요")) {
                    System.out.print("닉네임 입력: ");
                    String nickname = scanner.nextLine();
                    out.println(nickname); // 서버로 닉네임 전송
                }

                // 게임 메뉴 출력 (방 입장 전)
                if (!isInRoom && serverMessage.contains("환영합니다")) {
                    System.out.println("1: 빠른 시작");
                    System.out.println("2: 종료");
                    System.out.print("선택: ");
                    String choice = scanner.nextLine();

                    if ("1".equals(choice)) {
                        out.println("/quickStart"); // 빠른 시작 명령 서버로 전송
                    } else if ("2".equals(choice)) {
                        out.println("/quit"); // 종료 명령 서버로 전송
                        break;
                    } else {
                        System.out.println("올바른 선택이 아닙니다.");
                    }

                    if (serverMessage.contains("빠른 시작을 통해 방")) {
                        isInRoom = true; // 방 입장 상태 설정
                    }

                    if (serverMessage.contains("당신은 방의 리더입니다.")) {
                        isLeader = true; // 리더 상태 설정
                    }
                }

                // 방 입장 후 메뉴 제공
                if (isInRoom) {
                    if (isLeader) {
                        // 리더의 메뉴
                        System.out.println("1: 게임 시작");
                        System.out.println("2: 방 나가기");
                        System.out.print("선택: ");
                        String choice = scanner.nextLine();

                        if ("1".equals(choice)) {
                            out.println("/startGame"); // 게임 시작 명령 전송
                        } else if ("2".equals(choice)) {
                            out.println("/quit"); // 방 나가기 명령 전송
                            isInRoom = false; // 방 나가기 상태 변경
                        } else {
                            System.out.println("올바른 선택이 아닙니다.");
                        }
                    } else {
                        // 일반 플레이어의 메뉴
                        System.out.println("1: 준비 완료");
                        System.out.println("2: 대기 상태로 변경");
                        System.out.println("3: 방 나가기");
                        System.out.print("선택: ");
                        String choice = scanner.nextLine();

                        if ("1".equals(choice)) {
                            out.println("/ready"); // 준비 완료 명령 전송
                        } else if ("2".equals(choice)) {
                            out.println("/notReady"); // 대기 상태 명령 전송
                        } else if ("3".equals(choice)) {
                            out.println("/quit"); // 방 나가기 명령 전송
                            isInRoom = false; // 방 나가기 상태 변경
                        } else {
                            System.out.println("올바른 선택이 아닙니다.");
                        }
                    }
                }

                // 종료 명령 처리
                if (serverMessage.contains("연결 종료")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }
}
