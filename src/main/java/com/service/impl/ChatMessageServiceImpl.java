package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.ChatMessageDao;
import com.entity.ChatMessageEntity;
import com.entity.ChatSessionEntity;
import com.service.ChatMessageService;
import com.service.ChatSessionService;
import com.utils.ChatConstants;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("chatMessageService")
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageDao, ChatMessageEntity> implements ChatMessageService {

    @Autowired
    private ChatSessionService chatSessionService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ChatMessageEntity> page = this.selectPage(
                new Query<ChatMessageEntity>(params).getPage(),
                new EntityWrapper<ChatMessageEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<ChatMessageEntity> selectListView(Wrapper<ChatMessageEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ChatMessageEntity> wrapper) {
        Page<ChatMessageEntity> page = new Query<ChatMessageEntity>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        return new PageUtils(page);
    }

    @Override
    public ChatMessageEntity sendMessage(Long sessionId, String senderType, Long senderId,
                                         String senderName, String content) {
        // 创建消息实体
        ChatMessageEntity message = new ChatMessageEntity();
        message.setSessionId(sessionId);
        message.setSenderType(senderType);
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setContent(content);
        message.setIsRead(0); // 0表示未读
        message.setAddtime(new Date());

        // 保存消息
        this.insert(message);

        // 更新会话的最后消息和未读数
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session != null) {
            // 设置最后一条消息
            session.setLastMessage(content);
            session.setUpdatetime(new Date());

            // 根据发送者类型，更新对应的未读计数
            String recipientType = null;
            if (ChatConstants.SESSION_TYPE_USER_SHOP.equals(session.getSessionType())) {
                if (ChatConstants.SENDER_TYPE_USER.equals(senderType)) {
                    recipientType = ChatConstants.SENDER_TYPE_SHOP;
                } else {
                    recipientType = ChatConstants.SENDER_TYPE_USER;
                }
            } else if (ChatConstants.SESSION_TYPE_USER_ADMIN.equals(session.getSessionType())) {
                if (ChatConstants.SENDER_TYPE_USER.equals(senderType)) {
                    recipientType = ChatConstants.SENDER_TYPE_ADMIN;
                } else {
                    recipientType = ChatConstants.SENDER_TYPE_USER;
                }
            } else if (ChatConstants.SESSION_TYPE_SHOP_ADMIN.equals(session.getSessionType())) {
                if (ChatConstants.SENDER_TYPE_SHOP.equals(senderType) ||
                        ChatConstants.SENDER_TYPE_EMPLOYEE.equals(senderType)) {
                    recipientType = ChatConstants.SENDER_TYPE_ADMIN;
                } else {
                    recipientType = ChatConstants.SENDER_TYPE_SHOP;
                }
            }

            chatSessionService.updateUnreadCount(sessionId, recipientType);
        }

        return message;
    }

    @Override
    public void markMessagesAsRead(Long sessionId, String readerType) {
        // 获取所有未读消息
        List<ChatMessageEntity> unreadMessages = null;

        // 根据读者类型查找未读消息
        if (ChatConstants.SENDER_TYPE_USER.equals(readerType)) {
            unreadMessages = this.selectList(
                    new EntityWrapper<ChatMessageEntity>()
                            .eq("session_id", sessionId)
                            .eq("is_read", 0)
                            .notIn("sender_type", ChatConstants.SENDER_TYPE_USER)
            );
        } else if (ChatConstants.SENDER_TYPE_SHOP.equals(readerType) ||
                ChatConstants.SENDER_TYPE_EMPLOYEE.equals(readerType)) {
            unreadMessages = this.selectList(
                    new EntityWrapper<ChatMessageEntity>()
                            .eq("session_id", sessionId)
                            .eq("is_read", 0)
                            .notIn("sender_type", ChatConstants.SENDER_TYPE_SHOP, ChatConstants.SENDER_TYPE_EMPLOYEE)
            );
        } else if (ChatConstants.SENDER_TYPE_ADMIN.equals(readerType)) {
            unreadMessages = this.selectList(
                    new EntityWrapper<ChatMessageEntity>()
                            .eq("session_id", sessionId)
                            .eq("is_read", 0)
                            .notIn("sender_type", ChatConstants.SENDER_TYPE_ADMIN)
            );
        }

        // 标记消息为已读
        if (unreadMessages != null && !unreadMessages.isEmpty()) {
            for (ChatMessageEntity message : unreadMessages) {
                message.setIsRead(1); // 1表示已读
                this.updateById(message);
            }

            // 重置对应未读计数
            chatSessionService.resetUnreadCount(sessionId, readerType);
        }
    }
}