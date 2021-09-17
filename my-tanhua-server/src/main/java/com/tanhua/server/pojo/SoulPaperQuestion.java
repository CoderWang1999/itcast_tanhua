package com.tanhua.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soulpaperquestion")
public class SoulPaperQuestion {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 问卷id
     */
    private Long paperId;
    /**
     * 问题id
     */
    private Long questionId;
}
