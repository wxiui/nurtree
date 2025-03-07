package com.csstj.nurtree.common;

import com.csstj.nurtree.data.model.ExamInfo;
import com.csstj.nurtree.data.model.Question;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListConversionExample {
    public static List<ExamInfo> convertList(List<?> list) {
        Gson gson = new Gson();
        List<ExamInfo> examInfoList = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map) {
                // 将 Map 转换为 JSON 字符串
                String json = gson.toJson(item);
                // 使用 Gson 重新解析为 ExamInfo 对象
                ExamInfo examInfo = gson.fromJson(json, ExamInfo.class);
                examInfoList.add(examInfo);
            }
        }
        return examInfoList;
    }

    public static List<Question> convertQuestionList(List<?> list) {
        Gson gson = new Gson();
        List<Question> questionList = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map) {
                // 将 Map 转换为 JSON 字符串
                String json = gson.toJson(item);
                // 使用 Gson 重新解析为 ExamInfo 对象
                Question question = gson.fromJson(json, Question.class);
                questionList.add(question);
            }
        }
        return questionList;
    }
    public static List<String> convertOptionList(List<?> list) {
        Gson gson = new Gson();
        List<String> optionList = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map) {
                // 将 Map 转换为 JSON 字符串
                String json = gson.toJson(item);
                // 使用 Gson 重新解析为 ExamInfo 对象
                String option = gson.fromJson(json, String.class);
                optionList.add(option);
            }
        }
        return optionList;
    }

    public static void main(String[] args) {
        // 模拟一个包含 LinkedTreeMap 的 List
        List<Map<String, Object>> originalList = new ArrayList<>();
        Map<String, Object> map1 = new java.util.HashMap<>();
        map1.put("name", "Math Exam");
        map1.put("score", 90);
        originalList.add(map1);

        List<ExamInfo> examInfoList = convertList(originalList);
        for (ExamInfo examInfo : examInfoList) {
            System.out.println("Exam Name: " + examInfo.getResourceName());
            System.out.println("Exam Score: " + examInfo.getBonusMins());
        }
    }
}