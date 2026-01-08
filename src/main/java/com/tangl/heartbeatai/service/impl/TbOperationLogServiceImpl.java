
package com.tangl.heartbeatai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.heartbeatai.entity.TbOperationLog;
import com.tangl.heartbeatai.enums.OperationTypeEnum;
import com.tangl.heartbeatai.mapper.TbOperationLogMapper;
import com.tangl.heartbeatai.service.TbOperationLogService;
import com.tangl.heartbeatai.util.LogUtils;
import org.springframework.stereotype.Service;

/**
 * 操作日志Service实现（重构：调用工具类完成日志组装）
 */
@Service
public class TbOperationLogServiceImpl extends ServiceImpl<TbOperationLogMapper, TbOperationLog> implements TbOperationLogService {

    @Override
    public void saveOperationLog(Long userId, OperationTypeEnum operationType, Object requestParam, Object responseData, boolean isSuccess) {
        // 1. 构建日志对象
        TbOperationLog operationLog = new TbOperationLog();
        // 2. 填充基础信息
        operationLog.setUserId(userId == null ? 0 : userId);
        operationLog.setOperationType(operationType.getCode());
        operationLog.setStatus(isSuccess ? 1 : 0); // 1-成功，0-失败
        // 3. 工具类获取IP和设备信息
        operationLog.setIp(LogUtils.getRealIp());
        operationLog.setDevice(LogUtils.parseDeviceInfo());
        // 4. 工具类格式化操作内容（JSON格式）
        String operationContent = LogUtils.formatOperationContent(
                operationType.getDesc(), requestParam, responseData, isSuccess
        );
        operationLog.setOperationContent(operationContent);
        // 5. 保存日志（MyBatis-Plus自带方法）
        this.save(operationLog);
    }
}
    