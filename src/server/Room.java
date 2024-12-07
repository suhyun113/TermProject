package server;

// 방 정보를 관리하는 클래스

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final int roomId; // 방 ID
    private final List<Player> roomPlayers; // 방에 있는 플레이어 목록
    private final int maxPlayers; // 최대 플레이어 인원
    private boolean gameStarted; // 게임 시작 여부
    private Player leader; // 방 리더

    // Room 클래스 생성자
    public Room(int roomId, int maxPlayers) {
        this.roomId = roomId;
        this.roomPlayers = new ArrayList<>();
        this.maxPlayers = maxPlayers;
        this.gameStarted = false;
        this.leader = null;
    }

    // 방의 고유 ID 반환하는 메서드
    public int getRoomId() {
        return roomId;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public String startGame() {
        if (roomPlayers.size() < 2) {
            return "플레이어가 2명 이상이어야 게임을 시작할 수 있습니다.";
        }
        if (!roomPlayers.stream().allMatch(Player::isReady)) {
            return "모든 플레이어가 준비 상태가 아닙니다. 게임을 시작할 수 없습니다.";
        }
        gameStarted = true;
        notifyAllPlayers("게임이 시작되었습니다!");
        return "게임이 시작되었습니다!";
    }

    public void stopGame() {
        this.gameStarted = false;
    }

    public boolean addPlayerToRoom(Player player) {
        if (roomPlayers.size() < maxPlayers && !gameStarted) {
            roomPlayers.add(player);

            // 리더가 없는 경우, 첫 번째 플레이어를 리더로 설정
            if (leader == null) {
                leader = player;
                leader.setLeader(true); // 리더 설정
            }
            // 플레이어 추가 알림
            notifyAllPlayers(player.getNickname() + "님이 방에 추가되었습니다.");
            return true;
        }
        return false; // 방에 추가 실패
    }

    // 방에서 플레이어 제거
    public void removePlayerFromRoom(Player player) {
        roomPlayers.remove(player);

        // 플레이어가 나갔음을 알림
        notifyAllPlayers(player.getNickname() + "님이 방을 나갔습니다.");

        if (player == leader) { // 리더가 나간 경우
            if (!roomPlayers.isEmpty()) {
                // 리더 다음으로 들어왔던 사람(리스트의 첫 번째 사람)을 새로운 리더로 지정
                leader = roomPlayers.get(0);
                leader.setLeader(true);

                // 새 리더 지정 알림
                notifyAllPlayers("새 리더는 " + leader.getNickname() + "님입니다.");
            } else {
                leader = null;
            }
        }
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

    public Player getLeader() {
        return leader;
    }

    // 플레이어의 준비 상태 변경
    public void updatePlayerReadyStatus(Player player, boolean isReady) {
        if (!player.equals(leader)) { // 리더는 상태 변경 불가
            player.setReady(isReady);

            String statusMessage = player.getNickname() + "님이 " + (isReady ? "준비 완료 상태로 변경되었습니다." : "대기 중 상태로 변경되었습니다.");
            notifyAllPlayers(statusMessage);
        }
    }

    public void setPlayerReady(Player player, boolean isReady) {
        if (roomPlayers.contains(player)) {
            player.setReady(isReady); // Player class should have a setReady() method
        }
    }

    public boolean areAllPlayersReady() {
        for (Player player : roomPlayers) {
            if (!player.isReady()) {
                return false; // If any player is not ready, return false
            }
        }
        return true; // All players are ready
    }

    public void notifyAllPlayers(String message) {
        for (Player player : roomPlayers) {
            try {
                PrintWriter out = new PrintWriter(player.getClientSocket().getOutputStream(), true);
                out.println("=== 현재 방 상태 ===");
                for (Player p : roomPlayers) {
                    out.println("닉네임: " + p.getNickname() +
                            " | 준비 상태: " + (p.isReady() ? "준비 완료" : "대기 중") +
                            " | 리더: " + (p.isLeader() ? "O" : "X"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
