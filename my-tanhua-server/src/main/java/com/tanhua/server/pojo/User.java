package com.tanhua.server.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BasePojo {

    private Long id;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码，json序列化时忽略
     */
    @JsonIgnore
    private String password;

}
