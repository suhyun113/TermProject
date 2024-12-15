package server;

import java.util.List;

public class Room {
    private final int roomId;
    private final String category;
    private final List<Question> questions;

    public Room(int roomId, String category, List<Question> questions) {
        this.roomId = roomId;
        this.category = category;
        this.questions = questions;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getCategory() {
        return category;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
