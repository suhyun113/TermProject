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
            boolean isLeader = false; // 리더 여부 판단
            boolean isInRoom = false; // 방에 입장했는지 여부
            boolean isReady = false; // 준비 상태
            boolean isWaiting = false; // 대기 중 상태

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
                    // 게임 시작 전 메뉴 제공
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
                }

                // 서버로부터 방 입장 메시지를 받으면 isInRoom을 true로 설정
                if (serverMessage.contains("에 입장했습니다.")) {
                    isInRoom = true; // 에 입장했다는 메시지가 포함된 메시지를 받으면 isInRoom을 true로 설정

                    // 리더인지 일반 플레이어인지를 확인하고 isLeader 설정
                    if (serverMessage.contains("리더")) { // 서버가 리더임을 알려주는 메시지일 경우
                        isLeader = true;
                    } else {
                        isLeader = false;
                    }

                    // 방에 입장한 후, 리더와 플레이어에 맞는 메뉴 제공
                    if (isLeader) {
                        System.out.println("1: 게임 시작");
                        System.out.println("2: 방 나가기");
                        System.out.print("선택: ");
                        String choice = scanner.nextLine();

                        if ("1".equals(choice)) {
                            if (isReady) {
                                out.println("/startGame"); // 게임 시작 명령 전송
                            } else {
                                System.out.println("모든 플레이어가 준비 완료 상태여야 게임을 시작할 수 있습니다.");
                            }
                        } else if ("2".equals(choice)) {
                            out.println("/quit"); // 방 나가기 명령 전송
                            isInRoom = false; // 방 나가기 상태 변경
                        } else {
                            System.out.println("올바른 선택이 아닙니다.");
                        }
                    } else { // 일반 플레이어일 때 메뉴 처리
                        System.out.println("1: 대기 중");
                        System.out.println("2: 준비 완료");
                        System.out.println("3: 방 나가기");
                        System.out.print("선택: ");
                        String choice = scanner.nextLine();

                        if ("1".equals(choice)) {
                            if (!isWaiting) {
                                isWaiting = true;
                                System.out.println("현재 대기 중 상태입니다.");
                                out.println("/notReady"); // 대기 중 상태 명령 전송
                            } else {
                                System.out.println("현재 대기 중입니다.");
                            }
                        } else if ("2".equals(choice)) {
                            if (isWaiting) {
                                isReady = true;
                                isWaiting = false;
                                System.out.println("준비 완료 상태로 변경되었습니다.");
                                out.println("/ready"); // 준비 완료 명령 전송
                            } else {
                                System.out.println("현재 준비 완료 상태입니다.");
                            }
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
