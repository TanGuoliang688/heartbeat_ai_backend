package com.tangl.heartbeatai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.heartbeatai.entity.ChatHistory;
import java.util.List;

public interface ChatHistoryService extends IService<ChatHistory> {

    // 查询历史记录列表-分页版
    IPage<ChatHistory> pageByUserId(Page<ChatHistory> page, Long userId);

    /**
     * 根据用户ID查询历史记录列表
     */
    List<ChatHistory> listByUserId(Long userId);

    /**
     * 校验历史记录归属：该条记录是否属于当前用户
     */
    boolean checkHistoryOwner(Long historyId, Long userId);

    /**
     * 根据ID删除单条历史记录
     */
    boolean deleteById(Long id);

    /**
     * 清空指定用户的所有历史记录
     */
    boolean clearByUserId(Long userId);

    /**
     * 保存话术生成的历史记录【核心方法】
     * 生成话术成功后自动调用
     */
    void saveHistory(Long userId, String inputContent, String generateContent);
}