package com.supplychain.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supplychain.entity.User;
import com.supplychain.mapper.UserMapper;
import com.supplychain.util.JwtUtil;
import com.supplychain.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityUtils.clear();

        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return true;
        }

        String token = authorization.substring(7);
        if (!JwtUtil.validateToken(token)) {
            log.warn("Token 验证失败: {}", token);
            return true;
        }

        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            log.warn("无法从 Token 中获取用户ID");
            return true;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            return true;
        }

        SecurityUtils.setCurrentUser(user);
        log.debug("用户上下文已设置: userId={}, username={}", user.getId(), user.getUsername());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityUtils.clear();
    }
}
