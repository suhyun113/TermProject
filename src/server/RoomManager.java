package server;

// 전체 방과 플레이어 관리 클래스

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private final Map<String, Player> players; // 닉네임으로 전체 플레이어 관리
    private final Map<Integer, Room> rooms; // 방 ID로 방 관리
    private int roomIdCounter; // 방 ID 생성용 카운터

    public RoomManager() {
        players = new HashMap<>();
        rooms = new HashMap<>();
        roomIdCounter = 1; // 초기 방 ID
    }

    // 닉네임 중복 확인 전체 플레이어 추가
    public boolean registerPlayer(String nickname, Player player) {
        if (players.containsKey(nickname)){
            return false; // 닉네임 중복
        }
        players.put(nickname, player); // 닉네임을 키로 플레이어 정보 저장
        return true;
    }

    // 플레이어 방에 배치 및 방 없을 경우 새로 생성
    public Room assignPlayerToRoom(Player player) {
        // 기존 방 중 적합한 방 탐색
        for (Room room : rooms.values()) {
            if (!room.isGameStarted() && room.addPlayerToRoom(player)) {
                return room; // 적합한 방에 플레이어 추가 성공
            }
        }
        // 적합한 방이 없을 경우 새 방 생성
        Room newRoom = new Room(roomIdCounter++, 4);
        newRoom.addPlayerToRoom(player);
        rooms.put(newRoom.getRoomId(), newRoom);
        return newRoom;
    }

    public void handlePlayerExit(Player player) {
        Room room = getRoomForPlayer(player);
        if (room != null) {
            room.removePlayerFromRoom(player);
            if (room.isEmpty()) {
                rooms.remove(room.getRoomId());
            }
        }
        players.remove(player.getNickname());
    }

    public void updatePlayerReadyStatus(Player player, boolean isReady) {
        Room room = getRoomForPlayer(player);
        if (room != null) {
            room.updatePlayerReadyStatus(player, isReady);
        }
    }

    // 특정 플레이어가 속한 방을 반환
    public Room getRoomForPlayer(Player player) {
        for (Room room : rooms.values()) {
            if (room.listRoomPlayers().contains(player)) {
                return room; // 플레이어가 속한 방 반환
            }
        }
        return null; // 방을 찾지 못한 경우
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId); // 방 ID로 방 반환
    }
}

