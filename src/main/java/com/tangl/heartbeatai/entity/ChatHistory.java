package com.tangl.heartbeatai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_history")
public class ChatHistory {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户输入内容 */
    private String inputContent;

    /** 生成的话术内容 */
    private String generateContent;

    /** 创建时间 */
    private LocalDateTime createTime;
}