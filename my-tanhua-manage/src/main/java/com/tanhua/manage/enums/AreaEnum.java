package com.tanhua.manage.enums;

public enum AreaEnum {
    // 华中
    HUBEI("湖北", "华中地区"),
    HUNAN("湖南", "华中地区"),
    HENAN("河南", "华中地区"),
    //华北
    SHANXI("山西", "华北地区"),
    HEBEI("河北", "华北地区"),
    BEIJING("北京", "华北地区"),
    TIANJIN("天津", "华北地区"),
    NEIMENGGU("内蒙古", "华北地区"),
    // 华东
    ZHEJIANG("浙江", "华东地区"),
    ANHUI("安徽", "华东地区"),
    SHANGHAI("上海", "华东地区"),
    JIANGXI("江西", "华东地区"),
    SHANDONG("山东", "华东地区"),
    JIANGSU("江苏", "华东地区"),
    FUJIAN("福建", "华东地区"),
    TAIWAN("台湾", "华东地区"),
    // 华南
    GUANGXI("广西", "华南地区"),
    GUANGDONG("广东", "华南地区"),
    HAINAN("海南", "华南地区"),
    HONGKONG("香港", "华南地区"),
    MACAO("澳门", "华南地区"),
    // 西北
    QINGHAI("青海", "西北地区"),
    NINGXIA("宁夏", "西北地区"),
    SHANXI2("陕西", "西北地区"),
    GANSU("甘肃", "西北地区"),
    XINJIANG("新疆", "西北地区"),
    // 东北
    JILIN("吉林", "东北地区"),
    HEILONGJIANG("黑龙江", "东北地区"),
    LIAONING("辽宁", "东北地区"),
    // 西南
    GUIZHOU("贵州", "西南地区"),
    YUNNAN("云南", "西南地区"),
    CHONGQING("重庆", "西南地区"),
    SICHUAN("四川", "西南地区"),
    TIBET("西藏", "西南地区"),
    ;
    private String province;
    private String area;

    AreaEnum(String province, String area) {
        this.province = province;
        this.area = area;
    }

    public String getProvince() {
        return province;
    }

    public String getArea() {
        return area;
    }

    public static String getAreaByProvince(String province) {
        String area = "未知";
        for (AreaEnum areaEnum : values()) {
            if (areaEnum.province == province) {
                area = areaEnum.area;
                break;
            }
        }
        return area;
    }
}