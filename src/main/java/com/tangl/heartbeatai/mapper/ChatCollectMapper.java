package com.tangl.heartbeatai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.heartbeatai.entity.ChatCollect;
import com.tangl.heartbeatai.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 话术收藏记录 - 持久层
 * BaseMapper<ChatCollect> 自动提供所有CRUD方法，无需手写SQL
 */
@Mapper // 必须加该注解，让Spring识别为Mybatis的Mapper接口，注入到容器
public interface ChatCollectMapper extends BaseMapper<ChatCollect> {

}