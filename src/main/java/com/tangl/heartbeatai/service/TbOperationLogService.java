
package com.tangl.heartbeatai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.heartbeatai.entity.TbOperationLog;
import com.tangl.heartbeatai.enums.OperationTypeEnum;

/**
 * 操作日志Service（重构：新增标准化保存方法）
 */
public interface TbOperationLogService extends IService<TbOperationLog> {

    /**
     * 保存操作日志（标准化方法，直接传入核心参数即可）
     * @param userId 用户ID（匿名传0）
     * @param operationType 操作类型枚举
     * @param requestParam 请求参数
     * @param responseData 响应结果
     * @param isSuccess 操作是否成功
     */
    void saveOperationLog(Long userId, OperationTypeEnum operationType, Object requestParam, Object responseData, boolean isSuccess);
}
    