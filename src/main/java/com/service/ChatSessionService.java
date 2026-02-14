package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.ChatSessionEntity;
import com.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface ChatSessionService extends IService<ChatSessionEntity> {
    PageUtils queryPage(Map<String, Object> params);
    List<ChatSessionEntity> selectListView(Wrapper<ChatSessionEntity> wrapper);
    PageUtils queryPage(Map<String, Object> params, Wrapper<ChatSessionEntity> wrapper);

    // 查找或创建会话
    ChatSessionEntity getOrCreateSession(String sessionType, Long userId, String userAccount,
                                         Long shopId, String shopAccount,
                                         Long employeeId, String employeeNumber);

    // 更新会话未读消息计数
    void updateUnreadCount(Long sessionId, String recipientType);

    // 重置未读消息计数
    void resetUnreadCount(Long sessionId, String readerType);
}