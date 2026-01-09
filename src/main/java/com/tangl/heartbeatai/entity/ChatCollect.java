package com.tangl.heartbeatai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_collect")
public class ChatCollect {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 收藏的话术内容 */
    private String content;

    /** 收藏时间 */
    private LocalDateTime createTime;
}