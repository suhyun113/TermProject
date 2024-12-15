package server;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    public List<Question> getQuestions() {
        return questions;
    }

    public void startRoom(List<Player> players) {
        if (questions.isEmpty()) {
            notifyPlayers(players, "이 방에는 출제할 문제가 없습니다.");
            return;
        }

        for (Question question : questions) {
            notifyPlayers(players, "문제: " + question.getText());

            // 동기화 객체 초기화
            CountDownLatch latch = new CountDownLatch(2);
            String[] answers = new String[2]; // 두 플레이어의 답변 저장
            boolean[] hasAnswered = {false, false}; // 두 플레이어의 입력 상태 확인

            for (int i = 0; i < players.size(); i++) {
                int playerIndex = i;

                new Thread(() -> {
                    Player player = players.get(playerIndex);
                    try {
                        notifyPlayer(player, "정답을 입력하세요:");
                        String answer = player.getBufferedReader().readLine();

                        synchronized (hasAnswered) {
                            answers[playerIndex] = answer;
                            hasAnswered[playerIndex] = true;

                            // 다른 플레이어의 상태 확인
                            if (!hasAnswered[1 - playerIndex]) {
                                notifyPlayer(player, "다른 플레이어의 입력을 기다리는 중입니다.");
                            }
                        }

                        latch.countDown(); // 입력 완료
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            // 두 플레이어 입력 대기
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 정답 여부 확인 및 결과 출력
            synchronized (hasAnswered) {
                boolean isCorrect1 = question.isCorrectAnswer(answers[0]);
                boolean isCorrect2 = question.isCorrectAnswer(answers[1]);

                notifyPlayer(players.get(0), "정답: " + (isCorrect1 ? "맞았습니다!" : "틀렸습니다!"));
                notifyPlayer(players.get(1), "정답: " + (isCorrect2 ? "맞았습니다!" : "틀렸습니다!"));

                if (isCorrect1 && isCorrect2) {
                    question.setCleared(true);
                }
            }
        }

        notifyPlayers(players, "모든 문제가 종료되었습니다. 방을 나갑니다.");
    }

    private void notifyPlayers(List<Player> players, String message) {
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
