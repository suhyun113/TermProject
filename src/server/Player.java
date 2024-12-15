package server;

import java.net.Socket;

public class Player {
    private final String nickname; // 플레이어 닉네임
    private final Socket clientSocket; // 플레이어와 연결된 소켓

    // Player 클래스 생성자
    public Player(String nickname, Socket clientSocket) throws Exception {
        this.nickname = nickname;
        this.clientSocket = clientSocket;
    }

    // 플레이어 닉네임 반환하는 메서드
    public String getNickname() {
        return nickname;
    }

    // 플레이어와 연결된 소켓 반환하는 메서드
    public Socket getClientSocket() {
        return clientSocket;
    }
}
