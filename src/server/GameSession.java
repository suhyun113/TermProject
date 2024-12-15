package server;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private final List<Player> players;
    private final RoomManager roomManager;
    private final Map<Player, String> playerAnswers;
    private Room currentRoom;
    private Question currentQuestion;
    private int questionsSolved;

    public GameSession(List<Player> players) {
        this.players = players;
        this.roomManager = new RoomManager(new QuestionManager());
        this.playerAnswers = new HashMap<>();
        this.questionsSolved = 0;
    }

    public void start() {
        notifyPlayers("게임을 시작합니다! 협동하여 방을 클리어하세요.");
        currentRoom = roomManager.getCurrentRoom();
        enterRoom(currentRoom);
    }

    private void enterRoom(Room room) {
        if (room == null) {
            notifyGameClear();
            return;
        }

        notifyPlayers(room.getRoomId() + "번 방에 입장했습니다. 이 방은 " + room.getCategory() + " 영역입니다.");
        questionsSolved = 0;
        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        currentQuestion = roomManager.getNextQuestion();
        if (currentQuestion != null) {
            notifyPlayers("문제: " + currentQuestion.getText());
        } else {
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

        playerAnswers.put(player, answer);
        notifyPlayer(player, "정답을 제출했습니다: " + answer);

        boolean isCorrect = currentQuestion.isCorrectAnswer(answer);
        notifyPlayer(player, "정답: " + (isCorrect ? "맞았습니다!" : "틀렸습니다!"));

        if (playerAnswers.size() == players.size()) {
            handleAllAnswersSubmitted();
        }
    }

    private synchronized void handleAllAnswersSubmitted() {
        notifyPlayers("모든 플레이어가 정답을 제출했습니다.");

        boolean allCorrect = playerAnswers.values().stream()
                .allMatch(answer -> currentQuestion.isCorrectAnswer(answer));

        if (allCorrect) {
            notifyPlayers("두 플레이어가 정답입니다!");
            questionsSolved++;

            if (questionsSolved == currentRoom.getQuestions().size()) {
                notifyPlayers("이 방을 클리어했습니다!");
                roomManager.moveToNextRoom();
                enterRoom(roomManager.getCurrentRoom());
            } else {
                moveToNextQuestion();
            }
        } else {
            notifyPlayers("정답이 틀렸습니다. 다시 시도하세요.");
            resetForRetry();
        }

        playerAnswers.clear();
    }

    private void resetForRetry() {
        notifyPlayers("문제: " + currentQuestion.getText());
    }

    private void notifyGameClear() {
        notifyPlayers("모든 방을 클리어했습니다! 축하합니다, 탈출에 성공했습니다!");
        notifyPlayers("게임이 종료되었습니다. 초기 메뉴로 돌아갑니다.");

        // 대기실 초기화 및 초기 메뉴 복귀 처리
        for (Player player : players) {
            LobbyManager lobbyManager = LobbyManager.getInstance(); // Singleton 패턴 또는 인스턴스 참조 방식
            Lobby lobby = lobbyManager.getLobbyByPlayer(player); // 플레이어가 속한 대기실 가져오기
            if (lobby != null) {
                lobby.endGameAndReset(); // 대기실 초기화
            }
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
