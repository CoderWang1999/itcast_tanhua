package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearUserVo {

    private Long userId;
    private String avatar;
    private String nickname;

}
