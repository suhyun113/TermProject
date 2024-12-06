package server;

// 플레이어 정보를 관리하는 클래스

import java.net.Socket;

public class Player {
    private final String nickname; // 플레이어 닉네임
    private final Socket socket; // 플레이어와 연결된 소켓
    private boolean isReady; // 플레이어의 준비 상태

    // Player 클래스 생성자
    public Player(String nickname, Socket socket) {
        this.nickname = nickname;
        this.socket = socket;
        this.isReady = false;
    }

    // 플레이어 닉네임 반환하는 메서드
    public String getNickname() {
        return nickname;
    }

    // 플레이어와 연결된 소켓 반환하는 메서드
    public Socket getSocket() {
        return socket;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
    }
}
