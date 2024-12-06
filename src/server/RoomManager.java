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
        roomIdCounter = 1;
    }

    // 닉네임 중복 확인 플레이어 추가
    public boolean addPlayer(String nickname, Player player) {
        if (players.containsKey(nickname)){
            return false; // 닉네임 중복
        }
        players.put(nickname, player); // 닉네임으로 플레이어 정보 저장
        return true;
    }
}

