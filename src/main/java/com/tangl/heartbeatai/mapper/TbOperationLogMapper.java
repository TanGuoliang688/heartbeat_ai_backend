package com.tangl.heartbeatai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.heartbeatai.entity.TbOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper（阶段2新增）
 */
@Mapper
public interface TbOperationLogMapper extends BaseMapper<TbOperationLog> {
}