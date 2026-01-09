package com.tangl.heartbeatai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.heartbeatai.entity.ChatCollect;
import com.tangl.heartbeatai.entity.ChatHistory;

import java.util.List;

public interface ChatCollectService extends IService<ChatCollect> {

    // 查询收藏记录列表-分页版
    IPage<ChatCollect> pageByUserId(Page<ChatCollect> page, Long userId);

    /**
     * 根据用户ID查询收藏列表
     */
    List<ChatCollect> listByUserId(Long userId);

    /**
     * 收藏一条话术
     */
    boolean addCollect(Long userId, String content);

    /**
     * 校验收藏归属：该条收藏是否属于当前用户
     */
    boolean checkCollectOwner(Long collectId, Long userId);

    /**
     * 取消收藏（删除单条收藏记录）
     */
    boolean cancelCollect(Long id);

    /**
     * 根据ID查询单条收藏详情
     */
    ChatCollect getById(Long id);
}