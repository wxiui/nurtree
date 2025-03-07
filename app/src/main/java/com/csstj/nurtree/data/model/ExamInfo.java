package com.csstj.nurtree.data.model;

public class ExamInfo {

    private String resourceName;
    private int questionNums;
    private int bonusMins;
    private String resourceType;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getQuestionNums() {
        return questionNums;
    }

    public void setQuestionNums(int questionNums) {
        this.questionNums = questionNums;
    }

    public int getBonusMins() {
        return bonusMins;
    }

    public void setBonusMins(int bonusMins) {
        this.bonusMins = bonusMins;
    }


    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    private Long resourceId;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
