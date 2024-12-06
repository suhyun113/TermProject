package server;

// 전체 방과 플레이어 관리 클래스

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private final Map<String, Player> players; // 닉네임으로 플레이어 관리
    private final Map<Integer, Room> rooms; // 방 ID로 방 관리
    private int roomIdCounter; // 방 ID 생성용 카운터

    public RoomManager() {
        players = new HashMap<>();
        rooms = new HashMap<>();
        roomIdCounter = 1; // 초기 방 ID
    }

    // 닉네임 중복 확인 플레이어 추가
    public boolean addPlayer(String nickname, Player player) {
        if (players.containsKey(nickname)){
            return false; // 닉네임 중복
        }
        players.put(nickname, player); // 닉네임으로 플레이어 정보 저장
        return true;
    }

    // 플레이어 제거
    public void removePlayer(String nickname) {
        players.remove(nickname);
    }

    // 방 생성
    public Room createRoom(int maxCapacity) {
        if (maxCapacity <= 0 || maxCapacity > 4) {
            throw new IllegalArgumentException("방의 최대 인원 수는 1명 이상 4명 이하여야 합니다.");
        }

        Room newRoom = new Room(roomIdCounter++, maxCapacity); // 고유 ID 부여 후 증가
        rooms.put(newRoom.getRoomId(), newRoom); // 방 저장
        return newRoom;
    }

    // 방 삭제 => 반환값 필요x
    public void deleteRoom(int roomId) {
        rooms.get(roomId);
    }

    // 방 찾기
    public Room searchRoom(int roomId) {
        return rooms.get(roomId);
    }
}

