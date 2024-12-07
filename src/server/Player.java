package server;

// 플레이어 정보를 관리하는 클래스

import java.net.Socket;

public class Player {
    private final String nickname; // 플레이어 닉네임
    private final Socket clientSocket; // 플레이어와 연결된 소켓
    private boolean isReady; // 플레이어의 준비 상태
    private boolean isLeader; // 리더 여부

    // Player 클래스 생성자
    public Player(String nickname, Socket clientSocket) {
        this.nickname = nickname;
        this.clientSocket = clientSocket;
        this.isReady = false;
        this.isLeader = false;
    }

    // 플레이어 닉네임 반환하는 메서드
    public String getNickname() {
        return nickname;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
        this.isReady = isLeader; // 리더는 항상 준비 상태
    }

    // 플레이어와 연결된 소켓 반환하는 메서드
    public Socket getClientSocket() {
        return clientSocket;
    }
}
