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
@TableName("tb_soulquestionoptions")
public class Answers extends BasePojo {

    /**
     * 试题id
     */
    private String questionId;
    /**
     * 选项id
     */
    private String optionId;

}