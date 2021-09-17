package com.tanhua.manage.enums;

public enum AgeRangeEnum {

    UNDER_TWENTY(0, 20, "20以下"),
    TWENTY(20, 30, "20-29岁"),
    THIRTY(30, 40, "30-39岁"),
    FORTY(40, 50, "40-50岁"),
    OVER_FIFTY(50, 200, "50以上");

    private int min;
    private int max;
    private String desc;

    AgeRangeEnum(int min, int max, String desc) {
        this.min = min;
        this.max = max;
        this.desc = desc;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getDesc() {
        return desc;
    }
}