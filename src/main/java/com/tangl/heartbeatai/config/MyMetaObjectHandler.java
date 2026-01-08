
package com.tangl.heartbeatai.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * MyBatis-Plus字段填充配置（重构：自动填充operationTime字段）
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充操作时间（对应TbOperationLog的operationTime字段）
        this.strictInsertFill(metaObject, "operationTime", Date.class, new Date());
        // 若其他实体有创建时间/更新时间，可在此补充
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 日志表无需更新填充，空实现即可
    }
}
    