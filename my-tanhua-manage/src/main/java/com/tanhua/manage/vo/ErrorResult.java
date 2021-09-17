package com.tanhua.manage.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author ZJWzxy
 * @date 2021/04/07
 */
@Data
@Builder
public class ErrorResult {

    private String errCode;
    private String errMessage;

}
