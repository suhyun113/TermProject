package server;

// 방 정보를 관리하는 클래스

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final int roomId; // 방 ID
    private final List<Player> roomPlayers; // 방에 있는 플레이어 목록
    private final int maxCapacity; // 최대 플레이어 인원
    private boolean gameStarted;               // 게임 시작 여부

    // Room 클래스 생성자
    public Room(int roomId, int maxCapacity) {
        this.roomId = roomId;
        this.roomPlayers = new ArrayList<>();
        this.maxCapacity = maxCapacity;
        this.gameStarted = false;
    }

    // 방의 고유 ID 반환하는 메서드
    public int getRoomId() {
        return roomId;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        this.gameStarted = true;
    }

    public void stopGame() {
        this.gameStarted = false;
    }

    // 방에 플레이어 추가
    public boolean addPlayerToRoom(Player player) {
        if (roomPlayers.size() < maxCapacity && !gameStarted) {
            roomPlayers.add(player);
            return true;
        }
        return false; // 방이 꽉 찾거나 진행 중이면 추가 실패
    }

    // 방에서 플레이어 제거
    public void removePlayerFromRoom(Player player) {
        roomPlayers.remove(player);
    }

    public boolean isReadyToStart() {
        return roomPlayers.size() >= 2 && roomPlayers.stream().allMatch(Player::isReady);
    }

    // 방이 비어 있는지 확인
    public boolean isEmpty() {
        return roomPlayers.isEmpty();
    }

    // 방에 속한 플레이어 목록 반환하는 메서드
    public List<Player> listRoomPlayers() {
        return new ArrayList<>(roomPlayers);
    }
}
