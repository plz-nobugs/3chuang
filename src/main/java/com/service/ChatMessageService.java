package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.ChatMessageEntity;
import com.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface ChatMessageService extends IService<ChatMessageEntity> {
    PageUtils queryPage(Map<String, Object> params);
    List<ChatMessageEntity> selectListView(Wrapper<ChatMessageEntity> wrapper);
    PageUtils queryPage(Map<String, Object> params, Wrapper<ChatMessageEntity> wrapper);

    // 发送消息
    ChatMessageEntity sendMessage(Long sessionId, String senderType, Long senderId,
                                  String senderName, String content);

    // 标记消息为已读
    void markMessagesAsRead(Long sessionId, String readerType);
}