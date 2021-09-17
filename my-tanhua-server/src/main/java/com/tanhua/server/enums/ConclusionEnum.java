package com.tanhua.server.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author ZJWzxy
 * 鉴定结果的枚举类
 *
 */
@SuppressWarnings("all")
public enum ConclusionEnum implements IEnum<String> {

    MAOTOUTING("猫头鹰：他们的共同特质为重计划、条理、细节精准。在行为上，表现出喜欢理性思考与分析、较重视制度、结构、规范。他们注重执行游戏规则、循规蹈矩、巨细靡遗、重视品质、敬业负责。"),
    BAITU("白兔型：平易近人、敦厚可靠、避免冲突与不具批判性。在行为上，表现出不慌不忙、冷静自持的态度。他们注重稳定与中长程规划，现实生活中，常会反思自省并以和谐为中心，即使面对困境，亦能泰然自若，从容应付。"),
    HULI("狐狸型 ：人际关系能力极强，擅长以口语表达感受而引起共鸣，很会激励并带动气氛。他们喜欢跟别人互动，重视群体的归属感，基本上是比较「人际导向」。由于他们富同理心并乐于分享，具有很好的亲和力，在服务业、销售业、传播业及公共关系等领域中，狐狸型的领导者都有很杰出的表现。"),
    SHIZI("狮子型：性格为充满自信、竞争心强、主动且企图心强烈，是个有决断力的领导者。一般而言，狮子型的人胸怀大志，勇于冒险，看问题能够直指核心，并对目标全力以赴。他们在领导风格及决策上，强调权威与果断，擅长危机处理，此种性格最适合开创性与改革性的工作。");

    private String desc;

    ConclusionEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return this.desc;
    }
}
