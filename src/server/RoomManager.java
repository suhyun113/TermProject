package server;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final QuestionManager questionManager;

    private Room currentRoom; // 현재 방
    private int currentQuestionIndex = 0; // 현재 문제 인덱스

    public RoomManager(QuestionManager questionManager) {
        this.questionManager = questionManager;
        initializeRooms();
        currentRoom = rooms.get(1); // 첫 번째 방으로 초기화
    }

    private void initializeRooms() {
        rooms.put(1, new Room(1, "수학", questionManager.getQuestionsByCategory("수학")));
        rooms.put(2, new Room(2, "상식", questionManager.getQuestionsByCategory("상식")));
        rooms.put(3, new Room(3, "넌센스", questionManager.getQuestionsByCategory("넌센스")));
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    public Question getNextQuestion() {
        if (currentRoom == null) {
            return null; // 모든 방이 끝난 경우
        }

        // 현재 방의 문제 가져오기
        if (currentQuestionIndex < currentRoom.getQuestions().size()) {
            return currentRoom.getQuestions().get(currentQuestionIndex++);
        } else {
            // 다음 방으로 이동
            currentRoom = rooms.get(currentRoom.getRoomId() + 1);
            currentQuestionIndex = 0;

            // 다음 방이 있다면 새 방의 첫 문제 반환
            if (currentRoom != null) {
                return getNextQuestion();
            } else {
                return null; // 모든 방과 문제가 종료된 경우
            }
        }
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void moveToNextRoom() {
        currentRoom = rooms.get(currentRoom.getRoomId() + 1);
        currentQuestionIndex = 0;
    }
}