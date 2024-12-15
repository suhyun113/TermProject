package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Player {
    private final String nickname; // 플레이어 닉네임
    private final Socket clientSocket; // 플레이어와 연결된 소켓
    private final BufferedReader in; // 입력 스트림

    // Player 클래스 생성자
    public Player(String nickname, Socket clientSocket) throws Exception {
        this.nickname = nickname;
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    // 플레이어 닉네임 반환하는 메서드
    public String getNickname() {
        return nickname;
    }

    // 플레이어와 연결된 소켓 반환하는 메서드
    public Socket getClientSocket() {
        return clientSocket;
    }

    public BufferedReader getBufferedReader() {
        return in;
    }
}
