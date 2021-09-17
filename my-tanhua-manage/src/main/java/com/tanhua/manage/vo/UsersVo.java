package com.tanhua.manage.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Pageable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersVo {
    private Integer id;//用户Id
    private String nickname;
    private String mobile;
    private String sex;
    private String personalSignature;
    private Integer age;
    private Integer countBeLiked;//被喜欢人数
    private Integer countLiked;//喜欢人数
    private Integer countMatching;//配对人数
    private String occupation;//职业

}
