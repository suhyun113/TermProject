package server;

import java.util.*;

public class QuestionManager {
    private final Map<String, List<Question>> questionsByCategory = new HashMap<>();

    public QuestionManager() {
        initializeQuestions();
    }

    // 질문 데이터 초기화
    private void initializeQuestions() {
        // 수학 질문
        questionsByCategory.put("수학", List.of(
                new Question("5 + 3 = ?", "8"),
                new Question("10 / 2 = ?", "5"),
                new Question("7 * 6 = ?", "42")
        ));

        // 상식 질문
        questionsByCategory.put("상식", List.of(
                new Question("한국의 수도는?", "서울"),
                new Question("지구는 몇 개의 대륙으로 이루어져 있나요?(숫자만 입력해)", "7"),
                new Question("피라미드가 위치한 나라는?", "이집트")
        ));

        // 넌센스 질문
        questionsByCategory.put("넌센스", List.of(
                new Question("타이타닉의 구명 보트에는 몇 명이 탈 수 있을까?(숫자만 입력해)", "9"),
                new Question("가장 추운 바다는?", "썰렁해"),
                new Question("세상에서 가장 뜨거운 과일은?", "천도복숭아")
        ));
    }

    // 카테고리에 해당하는 질문을 반환
    public List<Question> getQuestionsByCategory(String category) {
        return questionsByCategory.getOrDefault(category, List.of());
    }
}
