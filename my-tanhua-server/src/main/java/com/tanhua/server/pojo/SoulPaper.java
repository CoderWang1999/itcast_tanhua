package com.tanhua.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 * 问卷列表之问卷
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soulpaper")
public class SoulPaper extends BasePojo{
    /**
     * 问卷编号
     */
    private String id;
    /**
     * 问卷名字
     */
    private String name;
    /**
     * 封面
     */
    private String cover;
    /**
     * 级别---初级,中级,高级
     */
    private String level;
    /**
     * 星别-----（例如：2颗星，3颗星，5颗星）
     */
    private Integer star;

}
