package com.tangl.heartbeatai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.heartbeatai.entity.ChatHistory;
import com.tangl.heartbeatai.mapper.ChatHistoryMapper;
import com.tangl.heartbeatai.service.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    // 查询历史记录列表-分页版
    @Override
    public IPage<ChatHistory> pageByUserId(Page<ChatHistory> page, Long userId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId)
                .orderByDesc(ChatHistory::getCreateTime);
        return this.page(page, wrapper);
    }
    /**
     * 根据用户ID查询历史记录
     */
    @Override
    public List<ChatHistory> listByUserId(Long userId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId)
                .orderByDesc(ChatHistory::getCreateTime); // 按创建时间倒序，最新的在最前面
        return this.list(wrapper);
    }

    /**
     * 校验历史记录归属，防止越权删除/查看
     */
    @Override
    public boolean checkHistoryOwner(Long historyId, Long userId) {
        ChatHistory chatHistory = this.getById(historyId);
        // 记录不存在 或 记录的用户ID和当前操作用户ID不一致 → 返回false 无权限
        return chatHistory != null && chatHistory.getUserId().equals(userId);
    }

    /**
     * 删除单条历史记录
     */
    @Override
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }

    /**
     * 清空当前用户的所有历史记录
     */
    @Override
    public boolean clearByUserId(Long userId) {
        LambdaUpdateWrapper<ChatHistory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId);
        return this.remove(wrapper);
    }

    /**
     * 核心方法：自动保存话术生成记录
     * 无需返回值，Controller层调用无需判断，失败会抛异常
     */
    @Override
    public void saveHistory(Long userId, String inputContent, String generateContent) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUserId(userId);
        chatHistory.setInputContent(inputContent);
        chatHistory.setGenerateContent(generateContent);
        chatHistory.setCreateTime(LocalDateTime.now());
        this.save(chatHistory);
    }
}