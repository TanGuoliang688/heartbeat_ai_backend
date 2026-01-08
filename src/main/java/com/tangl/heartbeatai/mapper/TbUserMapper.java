package com.tangl.heartbeatai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.heartbeatai.entity.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper（适配tb_user）
 */
@Mapper
public interface TbUserMapper extends BaseMapper<TbUser> {
    /** 根据原始手机号查询用户（登录用） */
    TbUser selectByPhone(@Param("phone") String phone);

    /** 脱敏手机号（13800138000 → 138****8000） */
    default String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}