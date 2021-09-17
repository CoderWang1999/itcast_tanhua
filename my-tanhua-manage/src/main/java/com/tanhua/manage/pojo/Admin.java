package com.tanhua.manage.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin  implements Serializable {
    private String uid;
    private String username;
    /**
     * json序列化时忽略
     */
    @JsonIgnore
    private String password;
    private String avatar;
}
