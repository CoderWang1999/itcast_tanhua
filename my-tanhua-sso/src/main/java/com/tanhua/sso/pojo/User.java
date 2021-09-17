package com.tanhua.sso.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BasePojo {

    private Long id;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * json序列化时忽略
     */
    @JsonIgnore
    private String password;

}