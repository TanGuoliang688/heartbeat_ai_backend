package com.tangl.heartbeatai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.dto.ChatWordsRequest;
import com.tangl.heartbeatai.entity.ChatCollect;
import com.tangl.heartbeatai.entity.ChatHistory;
import com.tangl.heartbeatai.enums.OperationTypeEnum;
import com.tangl.heartbeatai.service.ChatCollectService;
import com.tangl.heartbeatai.service.ChatHistoryService;
import com.tangl.heartbeatai.service.ChatWordsService;
import com.tangl.heartbeatai.service.TbOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 话术生成控制器（含收藏、历史记录完整功能-最终完善版）
 * 包含：话术生成+自动存历史、收藏增删查、历史增删查清空、分页查询、防越权、重复收藏处理
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/chat")
@Tag(name = "话术生成接口", description = "话术生成、收藏管理、历史记录管理（完整功能）")
public class ChatWordsController {

    @Autowired
    private ChatWordsService chatWordsService;
    @Autowired
    private TbOperationLogService operationLogService;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatCollectService chatCollectService;

    // ====================== 收藏功能 【核心优化：重复收藏处理】 ======================

    /**
     * 收藏话术
     *
     * @param content 话术内容（非空）
     * @param request 请求对象（获取用户ID）
     * @return 收藏结果
     */
    @Operation(summary = "收藏话术", description = "匿名用户（userId=0）不允许收藏，同一句话术仅可收藏一次")
    @PostMapping("/collect-add")
    public Result<String> addCollect(
            @Parameter(description = "话术内容，不能为空", required = true)
            @RequestParam @NotBlank(message = "收藏内容不能为空") String content,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 匿名用户禁止收藏
        if (userId == 0L) {
            return Result.error(403, "匿名用户不允许收藏，请先登录");
        }
        try {
            boolean result = chatCollectService.addCollect(userId, content);
            if (result) {
                // 收藏成功
                operationLogService.saveOperationLog(userId, OperationTypeEnum.COLLECT_ADD, content, Result.success("收藏成功"), true);
                return Result.success("收藏成功");
            } else {
                // 重复收藏，友好提示，不记失败日志
                return Result.success("已收藏该话术，无需重复收藏");
            }
        } catch (Exception e) {
            log.error("收藏话术异常，用户ID:{}，内容:{}", userId, content, e);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.COLLECT_ADD, content, Result.error("收藏失败"), false);
            return Result.error("收藏失败，请稍后重试");
        }
    }

    /**
     * 取消收藏话术
     *
     * @param id      收藏记录ID
     * @param request 请求对象（获取用户ID）
     * @return 取消结果
     */
    @Operation(summary = "取消收藏话术", description = "仅能取消自己的收藏，收藏ID不存在则提示")
    @PostMapping("/collect-cancel")
    public Result<String> cancelCollect(
            @Parameter(description = "收藏记录主键ID", required = true)
            @RequestParam Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == 0L) {
            return Result.error(403, "匿名用户不允许取消收藏，请先登录");
        }
        try {
            // 先判断收藏记录是否存在 + 校验收藏归属（防止越权取消）
            ChatCollect chatCollect = chatCollectService.getById(id);
            if (chatCollect == null) {
                return Result.error(404, "收藏记录不存在");
            }
            if (!chatCollect.getUserId().equals(userId)) {
                return Result.error(403, "无权取消他人的收藏内容");
            }
            boolean result = chatCollectService.cancelCollect(id);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.COLLECT_CANCEL, id, Result.success("取消收藏成功"), result);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏异常，用户ID:{}，收藏ID:{}", userId, id, e);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.COLLECT_CANCEL, id, Result.error("取消收藏失败"), false);
            return Result.error("取消收藏失败，请稍后重试");
        }
    }

    /**
     * 分页查询用户收藏列表
     *
     * @param pageNum  页码，默认1
     * @param pageSize 页容量，默认10
     * @param request  请求对象（获取用户ID）
     * @return 分页收藏列表
     */
    @Operation(summary = "分页查询收藏列表", description = "匿名用户返回空列表，按收藏时间倒序展示，最新收藏在前")
    @GetMapping("/collect-list")
    public Result<IPage<ChatCollect>> collectList(
            @Parameter(description = "页码，默认值：1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数，默认值：10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            Page<ChatCollect> page = new Page<>(pageNum, pageSize);
            IPage<ChatCollect> collectPage = chatCollectService.pageByUserId(page, userId);
            return Result.success("查询收藏列表成功", collectPage);
        } catch (Exception e) {
            log.error("查询收藏列表异常，用户ID:{}", userId, e);
            return Result.error("查询收藏列表失败，请稍后重试");
        }
    }

    /**
     * 查询单条收藏详情
     *
     * @param id      收藏记录ID
     * @param request 请求对象（获取用户ID）
     * @return 收藏详情
     */
    @Operation(summary = "查询单条收藏详情", description = "仅能查询自己的收藏，收藏ID不存在则提示")
    @GetMapping("/collect-detail")
    public Result<ChatCollect> collectDetail(
            @Parameter(description = "收藏记录主键ID", required = true)
            @RequestParam Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == 0L) {
            return Result.error(403, "匿名用户不允许查询收藏详情，请先登录");
        }
        try {
            ChatCollect detail = chatCollectService.getById(id);
            if (detail == null) {
                return Result.error(404, "收藏记录不存在");
            }
            // 校验归属
            if (!detail.getUserId().equals(userId)) {
                return Result.error(403, "无权查询他人的收藏内容");
            }
            return Result.success("查询收藏详情成功", detail);
        } catch (Exception e) {
            log.error("查询收藏详情异常，用户ID:{}，收藏ID:{}", userId, id, e);
            return Result.error("查询收藏详情失败，请稍后重试");
        }
    }

    // ====================== 历史记录功能 【优化：空指针+友好提示】 ======================

    /**
     * 分页查询用户历史记录列表
     *
     * @param pageNum  页码，默认1
     * @param pageSize 页容量，默认10
     * @param request  请求对象（获取用户ID）
     * @return 分页历史记录列表
     */
    @Operation(summary = "分页查询历史记录列表", description = "匿名用户返回自己的历史记录，按生成时间倒序展示，最新记录在前")
    @GetMapping("/history-list")
    public Result<IPage<ChatHistory>> historyList(
            @Parameter(description = "页码，默认值：1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数，默认值：10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            Page<ChatHistory> page = new Page<>(pageNum, pageSize);
            IPage<ChatHistory> historyPage = chatHistoryService.pageByUserId(page, userId);
            return Result.success("查询历史记录成功", historyPage);
        } catch (Exception e) {
            log.error("查询历史记录异常，用户ID:{}", userId, e);
            return Result.error("查询历史记录失败，请稍后重试");
        }
    }

    /**
     * 删除单条历史记录
     *
     * @param id      历史记录ID
     * @param request 请求对象（获取用户ID）
     * @return 删除结果
     */
    @Operation(summary = "删除单条历史记录", description = "仅能删除自己的历史记录，历史ID不存在则提示")
    @PostMapping("/history-delete")
    public Result<String> deleteHistory(
            @Parameter(description = "历史记录主键ID", required = true)
            @RequestParam Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            // 先判断历史记录是否存在 + 校验归属
            ChatHistory chatHistory = chatHistoryService.getById(id);
            if (chatHistory == null) {
                return Result.error(404, "历史记录不存在");
            }
            if (!chatHistory.getUserId().equals(userId)) {
                return Result.error(403, "无权删除他人的历史记录");
            }
            boolean result = chatHistoryService.deleteById(id);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.HISTORY_DELETE, id, Result.success("删除历史记录成功"), result);
            return Result.success("删除历史记录成功");
        } catch (Exception e) {
            log.error("删除历史记录异常，用户ID:{}，历史ID:{}", userId, id, e);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.HISTORY_DELETE, id, Result.error("删除历史记录失败"), false);
            return Result.error("删除历史记录失败，请稍后重试");
        }
    }

    /**
     * 清空用户所有历史记录
     *
     * @param request 请求对象（获取用户ID）
     * @return 清空结果
     */
    @Operation(summary = "清空所有历史记录", description = "匿名用户清空自己的历史记录，登录用户清空个人全部记录")
    @PostMapping("/history-clear")
    public Result<String> clearHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            boolean result = chatHistoryService.clearByUserId(userId);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.HISTORY_CLEAR, userId, Result.success("清空历史记录成功"), result);
            return Result.success("清空历史记录成功");
        } catch (Exception e) {
            log.error("清空历史记录异常，用户ID:{}", userId, e);
            operationLogService.saveOperationLog(userId, OperationTypeEnum.HISTORY_CLEAR, userId, Result.error("清空历史记录失败"), false);
            return Result.error("清空历史记录失败，请稍后重试");
        }
    }

    // ====================== 话术生成功能 【无修改，保留最优版】 ======================
    @Operation(summary = "生成话术", description = "自动保存生成记录到历史，支持匿名用户生成，生成结果实时入库")
    @PostMapping("/generate-words")
    public Result<String> generateWords(
            @Validated @RequestBody ChatWordsRequest requestParam,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("接收到话术生成请求：userId={}, 入参={}", userId, requestParam);
        try {
            // 1. 核心业务逻辑-生成话术
            String words = chatWordsService.generateChatWords(requestParam);
            Result<String> successResult = Result.success("话术生成成功", words);

            // 2. 自动保存历史记录-核心需求
            chatHistoryService.saveHistory(userId, requestParam.getInputContent(), words);

            // 3. 保存操作日志
            operationLogService.saveOperationLog(
                    userId,
                    OperationTypeEnum.GENERATE_WORDS,
                    requestParam,
                    successResult,
                    true
            );
            return successResult;
        } catch (Exception e) {
            log.error("话术生成失败，userId={}, 入参={}", userId, requestParam, e);
            Result<String> errorResult = Result.error("话术生成失败，请稍后重试");
            operationLogService.saveOperationLog(
                    userId,
                    OperationTypeEnum.GENERATE_WORDS,
                    requestParam,
                    errorResult,
                    false
            );
            return errorResult;
        }
    }
}