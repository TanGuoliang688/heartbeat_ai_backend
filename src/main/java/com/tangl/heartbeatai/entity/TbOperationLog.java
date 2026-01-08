
package com.tangl.heartbeatai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;

/**
 * 操作日志表（重构：规范字段含义，补充自动填充）
 * 关联表：tb_operation_log
 */
@Data
@TableName("tb_operation_log")
public class TbOperationLog {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（匿名用户填0） */
    private Long userId;

    /** 操作类型：generate_words-生成话术，create_date-创建纪念日，buy_member-购买会员 */
    private String operationType;

    /** 操作内容（JSON格式，存储请求参数+响应结果，便于追溯） */
    private String operationContent;

    /** 操作IP地址（公网IP，非本地回环地址） */
    private String ip;

    /** 操作时间（自动填充，无需手动设置） */
    @TableField(fill = FieldFill.INSERT)
    private Date operationTime;

    /** 设备信息（如：Chrome/微信小程序/iPhone） */
    private String device;

    /** 操作状态：1-成功，0-失败（新增字段，标记操作结果） */
    private Integer status;
}
    