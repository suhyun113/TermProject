package server;

public class Question {
    private final String text;
    private final String answer;
    private boolean isCleared;

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
        this.isCleared = false;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrectAnswer(String playerAnswer) {
        return answer.equalsIgnoreCase(playerAnswer.trim());
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        this.isCleared = cleared;
    }
}
