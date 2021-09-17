package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "voice")
public class Voice implements Serializable {

    private static final long serialVersionUID = 985383095092298754L;
    private ObjectId id;

    private Long vid;
    private Long userId;
    private String avatar;//头像
    private String nickname;//昵称
    private String gender; //性别
    private Integer age; //年龄
    private String soundUrl; //语音地址
    private Long created; //发送时间
    private Integer state;

}