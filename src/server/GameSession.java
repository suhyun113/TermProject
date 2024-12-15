package server;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private final List<Player> players; // 참여하는 플레이어 목록
    private final RoomManager roomManager; // 방 관리
    private final Map<Player, String> playerAnswers; // 플레이어별 정답 저장
    private Room currentRoom; // 현재 방
    private Question currentQuestion; // 현재 문제
    private int questionsSolved; // 현재 방에서 해결한 문제 수

    public GameSession(List<Player> players) {
        this.players = players;
        this.roomManager = new RoomManager(new QuestionManager());
        this.playerAnswers = new HashMap<>();
        this.questionsSolved = 0;
    }

    public void start() {
        // 게임 시작 안내 메시지
        notifyPlayers("처음 게임을 시작하면 총 3개의 방으로 이루어져 있으며 모든 방을 클리어해야 방을 탈출할 수 있습니다.");
        notifyPlayers("2명에서 협동하여 클리어해보세요!");

        // 첫 번째 방 입장
        currentRoom = roomManager.getCurrentRoom();
        enterRoom(currentRoom);
    }

    private void enterRoom(Room room) {
        if (room == null) {
            notifyPlayers("모든 방을 클리어했습니다! 축하합니다, 탈출에 성공했습니다!");
            return;
        }

        // 방 입장 메시지 출력
        notifyPlayers(room.getRoomId() + "번 방에 입장했습니다. 이 방은 " + room.getCategory() + " 영역입니다.");
        questionsSolved = 0; // 현재 방에서 해결한 문제 수 초기화
        moveToNextQuestion(); // 첫 번째 문제 출력
    }

    private void moveToNextQuestion() {
        currentQuestion = roomManager.getNextQuestion();
        if (currentQuestion != null) {
            notifyPlayers("문제: " + currentQuestion.getText());
        } else {
            // 모든 문제를 풀었으면 다음 방으로 이동
            notifyPlayers("모든 문제를 해결했습니다!");
            roomManager.moveToNextRoom();
            enterRoom(roomManager.getCurrentRoom());
        }
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

        // 모든 플레이어가 정답을 제출했는지 확인
        if (playerAnswers.size() == players.size()) {
            handleAllAnswersSubmitted();
        }
    }

    private synchronized void handleAllAnswersSubmitted() {
        notifyPlayers("모든 플레이어가 정답을 제출했습니다.");

        // 정답 확인
        boolean allCorrect = playerAnswers.values().stream()
                .allMatch(answer -> currentQuestion.isCorrectAnswer(answer));

        if (allCorrect) {
            notifyPlayers("두 플레이어가 정답입니다!");
            questionsSolved++;

            // 방의 문제를 모두 풀었는지 확인
            if (questionsSolved == currentRoom.getQuestions().size()) {
                notifyPlayers(currentRoom.getRoomId() + "번 방을 클리어했습니다!");
                roomManager.moveToNextRoom();
                enterRoom(roomManager.getCurrentRoom()); // 다음 방으로 이동
            } else {
                moveToNextQuestion(); // 현재 방의 다음 문제로 이동
            }
        } else {
            notifyPlayers("정답이 틀렸습니다. 다시 시도하세요.");
            resetForRetry(); // 상태 초기화 및 문제 재출력
        }

        playerAnswers.clear(); // 상태 초기화
    }

    private void resetForRetry() {
        notifyPlayers("문제: " + currentQuestion.getText());
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
