package com.tangl.heartbeatai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 用户表实体（完全匹配tb_user表结构）
 */
@Data
@TableName("tb_user") // 匹配实际表名
public class TbUser {
    /** 用户ID（主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（昵称） */
    private String username;

    /** 手机号（脱敏存储，如138****1234） */
    private String phone;

    /** 登录密码（BCrypt加密）- 新增字段 */
    private String password;

    /** 头像URL */
    private String avatar;

    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;

    /** 出生日期 */
    private Date birthDate;

    /** 注册时间 */
    private Date registerTime;

    /** 最后登录时间 */
    private Date lastLoginTime;

    /** 会员等级：0-普通用户，1-月度会员，2-年度会员 */
    private Integer memberLevel;

    /** 会员过期时间（非会员为NULL） */
    private Date memberExpireTime;

    /** 扩展字段（JSON格式，存储临时配置） */
    private String extJson;

    /** 状态：1-正常，0-禁用 */
    private Integer status;
}