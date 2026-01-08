package com.tangl.heartbeatai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 纪念日表实体
 *
 * @TableName tb_anniversary
 */
@Data
@TableName(value = "tb_anniversary")
public class TbAnniversary {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 纪念日名称（如“表白日”“生日”）
     */
    private String anniversaryName;

    /**
     * 纪念日日期（如2025-05-20）
     */
    private Date anniversaryDate;

    /**
     * 提前提醒天数（默认3天）
     */
    private Integer remindDays;

    /**
     * 是否每年重复：1-是，0-否
     */
    private Integer isRepeat;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态：1-有效，0-删除
     */
    private Integer status;
}