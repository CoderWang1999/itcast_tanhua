package com.tanhua.sso.enums;

public enum LogTypeEnum {
    /**
     * 操作类型,
     * 01为登录，0201为发动态，0202为浏览动态，0203为动态点赞，0204为动态喜欢，0205为评论，0206为动态取消点赞，0207为动态取消喜欢，0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
     */
    LOGIN("01", "登录"),
    MOVEMENTS_ADD("0201", "发动态"),
    MOVEMENTS_READ("0202", "浏览动态"),
    MOVEMENTS_LIKE("0203", "动态点赞"),
    MOVEMENTS_LOVE("0204", "动态喜欢"),
    MOVEMENTS_COMMENT("0205", "动态评论"),
    MOVEMENTS_UNLIKE("0206", "动态取消点赞"),
    MOVEMENTS_UNLOVE("0207", "动态取消喜欢"),
    VIDEO_ADD("0301", "发小视频"),
    VIDEO_LIKE("0302", "小视频点赞"),
    VIDEO_UNLIKE("0303", "小视频取消点赞"),
    VIDEO_COMMENT("0304", "小视频评论"),
    ;

    private String value;
    private String name;

    LogTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
