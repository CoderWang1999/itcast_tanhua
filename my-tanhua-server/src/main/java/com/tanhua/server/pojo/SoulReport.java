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
@TableName("tb_soulreport")
public class SoulReport extends BasePojo{
    /**
     * 主键id
     */
    private Long id;
    /**
     *用户id
     */
    private Long userId;
    /**
     * 问卷id
     */
    private Long paperId;
    /**
     * 分数
     */
    private Long score;
}