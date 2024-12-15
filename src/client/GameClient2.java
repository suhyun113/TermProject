package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient2 {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8002;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            String serverMessage;

            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);

                if (serverMessage.contains("닉네임을 입력하세요")) {
                    System.out.print("닉네임: ");
                    String nickname = scanner.nextLine();
                    out.println(nickname);
                }

                if (serverMessage.contains("환영합니다")) {
                    System.out.println("1: 빠른 시작");
                    System.out.println("2: 종료");
                    System.out.print("선택: ");
                    String choice = scanner.nextLine();

                    if ("1".equals(choice)) {
                        out.println("/quickStart");
                    } else if ("2".equals(choice)) {
                        out.println("/quit");
                        break;
                    } else {
                        System.out.println("올바른 선택이 아닙니다. 다시 입력하세요.");
                    }
                    continue;
                }

                if (serverMessage.contains("게임 종료")) {
                    System.out.println("게임이 종료되었습니다. 초기 메뉴로 돌아갑니다.");
                    continue;
                }

                if (serverMessage.contains("문제:")) {
                    System.out.print("정답: ");
                    String answer = scanner.nextLine();
                    out.println("/answer " + answer);
                }
            }
        } catch (Exception e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }
}
