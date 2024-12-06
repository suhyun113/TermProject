package server;

// 방 정보를 관리하는 클래스

import java.util.List;

public class Room {
    private final int roomId; // 방 ID
    private final List<Player> players; // 방에 있는 플레이어 목록
    private final int maxCapacity; // 최대 플레이어 인원

    // Room 클래스 생성자
    public Room(int roomId, List<Player> players, int maxCapacity) {
        this.roomId = roomId;
        this.players = players;
        this.maxCapacity = maxCapacity;
    }

    // 방의 고유 ID 반환하는 메서드
    public int getRoomId() {
        return roomId;
    }

    // 방에 속한 플레이어 목록 반환하는 메서드
    public List<Player> getPlayers() {
        return players;
    }

    // 방의 최대 수용 인원 반환하는 메서드
    public int getMaxCapacity() {
        return maxCapacity;
    }
}
