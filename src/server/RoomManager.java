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
                System.out.println(player.getNickname() + " 플레이어가 방 " + room.getRoomId() + "에 배치되었습니다.");
                return room; // 적합한 방에 플레이어 추가 성공
            }
        }
        // 적합한 방이 없을 경우 새 방 생성
        Room newRoom = new Room(roomIdCounter++, 4);
        newRoom.addPlayerToRoom(player);
        rooms.put(newRoom.getRoomId(), newRoom);
        System.out.println(player.getNickname() + " 플레이어가 새 방 " + newRoom.getRoomId() + "에 배치되었습니다.");
        return newRoom;
    }

    // 게임 진행 중 플레이어 나가는 상황 처리
    public void handlePlayerExit(Player player) {
        Room playerRoom = null;

        // 방 탐색 : 나간 플레이어가 속한 방 찾기
        for (Room room : rooms.values()) {
            if (room.listRoomPlayers().contains(player)) {
                playerRoom = room; // 방 찾기 성공
                room.removePlayerFromRoom(player); // 방에서 플레이어 제거
                break;
            }
        }

        if (playerRoom != null) {
            // 방이 비어 있으면 방의 플레이어 모두 삭제
            if (playerRoom.isEmpty()) {
                rooms.remove(playerRoom.getRoomId());
                players.remove(player.getNickname()); // 전체 플레이어 목록에서 제거
            }
            // 방에 최소 2명 이상 남아 있다면 게임 계속 진행
            else if (playerRoom.listRoomPlayers().size() >= 2) {
                System.out.println("게임 계속 진행 중...");
            }
        }
    }

    // 게임을 시작할 준비가 된 방은 자동 시작
    public void checkAndStartGames() {
        for (Room room : rooms.values()) {
            // 게임이 시작되지 않았고, 준비 완료된 방 확인
            if (!room.isGameStarted() && room.isReadyToStart()) {
                room.startGame(); // 게임 시작
                System.out.println("방 " + room.getRoomId() + "에서 게임이 시작되었습니다.");
            }
        }
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId); // 방 ID로 방 반환
    }
}

