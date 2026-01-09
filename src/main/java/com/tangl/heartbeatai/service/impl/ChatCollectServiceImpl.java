package com.tangl.heartbeatai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.heartbeatai.entity.ChatCollect;
import com.tangl.heartbeatai.entity.ChatHistory;
import com.tangl.heartbeatai.mapper.ChatCollectMapper;
import com.tangl.heartbeatai.service.ChatCollectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatCollectServiceImpl extends ServiceImpl<ChatCollectMapper, ChatCollect> implements ChatCollectService {

    // 查询收藏记录列表-分页版
    @Override
    public IPage<ChatCollect> pageByUserId(Page<ChatCollect> page, Long userId) {
        LambdaQueryWrapper<ChatCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatCollect::getUserId, userId)
                .orderByDesc(ChatCollect::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 根据用户ID查询收藏列表
     */
    @Override
    public List<ChatCollect> listByUserId(Long userId) {
        LambdaQueryWrapper<ChatCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatCollect::getUserId, userId)
                .orderByDesc(ChatCollect::getCreateTime); // 最新收藏的在最前面
        return this.list(wrapper);
    }

    // 带【防重复收藏】的addCollect方法，推荐使用这个版本
    @Override
    public boolean addCollect(Long userId, String content) {
        // 先校验：当前用户是否已经收藏过该内容
        LambdaQueryWrapper<ChatCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatCollect::getUserId, userId)
                .eq(ChatCollect::getContent, content);
        if (this.count(wrapper) > 0) {
            return false; // 已经收藏过，返回false，Controller层可提示"已收藏"
        }
        ChatCollect chatCollect = new ChatCollect();
        chatCollect.setUserId(userId);
        chatCollect.setContent(content);
        chatCollect.setCreateTime(LocalDateTime.now());
        return this.save(chatCollect);
    }

    /**
     * 校验收藏归属，防止越权取消/查看别人的收藏
     */
    @Override
    public boolean checkCollectOwner(Long collectId, Long userId) {
        ChatCollect chatCollect = this.getById(collectId);
        // 记录不存在 或 记录的用户ID和当前操作用户ID不一致 → 返回false 无权限
        return chatCollect != null && chatCollect.getUserId().equals(userId);
    }

    /**
     * 取消收藏 = 删除该条收藏记录
     */
    @Override
    public boolean cancelCollect(Long id) {
        return this.removeById(id);
    }

    /**
     * 查询单条收藏详情
     */
    @Override
    public ChatCollect getById(Long id) {
        return super.getById(id);
    }
}