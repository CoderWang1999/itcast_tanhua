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
@TableName("tb_soulquestion")
public class SoulQuestion extends BasePojo{
    /**
     * 主键id
     */
    private Long id;
    /**
     * 问题
     */
    private String question;
    /**
     * 级别---初级,中级,高级
     */
    private String type;
}