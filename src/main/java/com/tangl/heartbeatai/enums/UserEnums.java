package com.tangl.heartbeatai.enums;

import lombok.Getter;

/**
 * 用户相关枚举
 */
public class UserEnums {

    /**
     * 性别枚举（匹配tb_user.gender）
     */
    @Getter
    public enum Gender {
        UNKNOWN(0, "未知"),
        MALE(1, "男"),
        FEMALE(2, "女");

        private final int code;
        private final String desc;

        Gender(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 会员等级枚举（匹配tb_user.member_level）
     */
    @Getter
    public enum MemberLevel {
        NORMAL(0, "普通用户"),
        MONTHLY(1, "月度会员"),
        YEARLY(2, "年度会员");

        private final int code;
        private final String desc;

        MemberLevel(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 支付状态枚举（匹配tb_member_order.pay_status）
     */
    @Getter
    public enum PayStatus {
        UNPAID(0, "未支付"),
        PAID(1, "已支付"),
        REFUNDED(2, "已退款");

        private final int code;
        private final String desc;

        PayStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}