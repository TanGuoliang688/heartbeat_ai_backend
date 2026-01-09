
package com.tangl.heartbeatai.enums;

import lombok.Getter;

/**
 * 操作类型枚举（重构：统一操作类型定义，避免魔法值）
 */
@Getter
public enum OperationTypeEnum {
    GENERATE_WORDS("generate_words", "生成话术"),
    CREATE_ANNIVERSARY("create_date", "创建纪念日"),
    BUY_MEMBER("buy_member", "购买会员"),
    USER_REGISTER("user_register", "用户注册"),
    USER_LOGIN("user_login", "用户登录"),
    COLLECT_ADD("collect_words", "收藏话术"),
    COLLECT_CANCEL("cancel_collection", "取消收藏"),
    HISTORY_DELETE("delete_history", "删除历史记录"),
    HISTORY_CLEAR("clear_history", "清空历史记录");

    private final String code;
    private final String desc;


    OperationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static OperationTypeEnum getByCode(String code) {
        for (OperationTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
    