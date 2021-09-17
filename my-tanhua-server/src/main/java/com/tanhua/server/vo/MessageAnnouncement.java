package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 * 公告内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAnnouncement {
    private String id;
    private String title;
    private String description;
    private String createDate;

}