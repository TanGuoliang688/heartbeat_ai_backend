package com.tangl.heartbeatai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.heartbeatai.common.Result;
import com.tangl.heartbeatai.dto.UserLoginDTO;
import com.tangl.heartbeatai.dto.UserRegisterDTO;
import com.tangl.heartbeatai.entity.TbUser;
import com.tangl.heartbeatai.mapper.TbUserMapper;
import com.tangl.heartbeatai.service.TbUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.tangl.heartbeatai.util.JwtUtil;

import java.util.Date;

/**
 * 用户Service实现（适配tb_user表）
 */
@Slf4j
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // 密码加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result<Void> register(UserRegisterDTO dto) {
        // 1. 校验密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.error(400, "两次密码不一致");
        }

        // 2. 校验手机号是否已注册
        TbUser existUser = userMapper.selectByPhone(dto.getPhone());
        if (existUser != null) {
            return Result.error(400, "手机号已注册");
        }

        // 3. 构建用户对象（匹配tb_user字段）
        TbUser user = new TbUser();
        user.setUsername("心动用户" + dto.getPhone().substring(7)); // 昵称
        user.setPhone(userMapper.maskPhone(dto.getPhone())); // 手机号脱敏存储
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 密码加密
        user.setRegisterTime(new Date()); // 注册时间
        user.setMemberLevel(0); // 普通用户
        user.setExtJson("{}"); // 默认扩展字段
        user.setStatus(1); // 正常状态

        // 4. 保存用户
        boolean saveSuccess = save(user);
        if (saveSuccess) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

    @Override
    public Result<String> login(UserLoginDTO dto) {
        // 1. 查询用户（按原始手机号）
        TbUser user = userMapper.selectByPhone(dto.getPhone());
        if (user == null) {
            return Result.error(400, "手机号未注册");
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error(400, "密码错误");
        }

        // 3. 校验用户状态
        if (user.getStatus() == 0) {
            return Result.error(400, "账号已禁用");
        }

        // 4. 更新最后登录时间
        user.setLastLoginTime(new Date());
        updateById(user);

        // 5. 生成JWT token（携带用户ID）
//        JwtUtil jwtUtil = new JwtUtil(); // 创建JwtUtil类的实例
        String token = jwtUtil.generateToken(user.getId(), dto.getPhone()); // 原始手机号用于生成token
        return Result.success("登录成功", token);
    }

    @Override
    public TbUser getUserByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        return userMapper.selectByPhone(phone);
    }
}