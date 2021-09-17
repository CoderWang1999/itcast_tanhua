package com.tanhua.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 * 问卷列表之选项
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_souloptions")
public class  SoulOptions  extends BasePojo{
    /**
     * 主键id
     */
    private Long id;
    /**
     * 所属于的问题id
     */
    private String questionId;
    /**
     * 选项
     */
    private String options;

    /**
     * 分数
     */
    private Long score;
}
