package server;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private final List<Player> players; // 참여하는 플레이어 목록
    private final RoomManager roomManager; // 방 관리
    private final Map<Player, String> playerAnswers; // 플레이어별 정답 저장
    private Question currentQuestion; // 현재 문제

    public GameSession(List<Player> players) {
        this.players = players;
        this.roomManager = new RoomManager(new QuestionManager());
        this.playerAnswers = new HashMap<>(); // 각 플레이어의 정답 저장
    }

    public void start() {
        notifyPlayers("게임이 시작되었습니다! 협력하여 문제를 해결하세요!");
        moveToNextQuestion(); // 첫 번째 문제로 이동
    }

    public synchronized void submitAnswer(Player player, String answer) {
        if (!players.contains(player)) {
            notifyPlayer(player, "오류: 플레이어를 찾을 수 없습니다.");
            return;
        }

        // 정답 제출 상태 저장
        playerAnswers.put(player, answer);
        notifyPlayer(player, "정답을 제출했습니다: " + answer);

        // 개별적으로 정답 확인
        boolean isCorrect = currentQuestion.isCorrectAnswer(answer);
        notifyPlayer(player, "정답: " + (isCorrect ? "맞았습니다!" : "틀렸습니다!"));

        // 모든 플레이어가 제출 완료되었는지 확인
        if (playerAnswers.size() == players.size()) {
            handleAllAnswersSubmitted(); // 추가 동작
        }
    }

    private synchronized void handleAllAnswersSubmitted() {
        notifyPlayers("모든 플레이어가 정답을 제출했습니다.");

        // 정답 확인 (협력적 문제 풀이 결과 처리)
        boolean allCorrect = playerAnswers.values().stream()
                .allMatch(answer -> currentQuestion.isCorrectAnswer(answer));

        if (allCorrect) {
            notifyPlayers("두 플레이어 모두 정답입니다! 다음 문제로 이동합니다.");
            moveToNextQuestion(); // 다음 문제로 이동
        } else {
            notifyPlayers("모든 플레이어가 정답을 제출했지만, 정답이 틀렸습니다. 다시 시도하세요.");
        }

        // 상태 초기화
        playerAnswers.clear();
    }

    private void moveToNextQuestion() {
        currentQuestion = roomManager.getNextQuestion(); // 다음 문제 가져오기
        if (currentQuestion != null) {
            notifyPlayers("다음 문제: " + currentQuestion.getText());
        } else {
            notifyPlayers("모든 문제를 해결했습니다! 게임이 종료됩니다.");
        }
    }

    private void notifyPlayers(String message) {
        for (Player player : players) {
            notifyPlayer(player, message);
        }
    }

    private void notifyPlayer(Player player, String message) {
        try {
            PrintWriter out = new PrintWriter(player.getClientSocket().getOutputStream(), true);
            out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
