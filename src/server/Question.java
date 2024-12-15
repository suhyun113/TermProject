package server;

public class Question {
    private final String text;
    private final String answer;

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrectAnswer(String playerAnswer) {
        return answer.equalsIgnoreCase(playerAnswer.trim());
    }
}
