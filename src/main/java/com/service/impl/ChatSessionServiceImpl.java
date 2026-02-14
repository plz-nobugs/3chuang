package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.ChatSessionDao;
import com.entity.ChatSessionEntity;
import com.service.ChatSessionService;
import com.utils.ChatConstants;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("chatSessionService")
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionDao, ChatSessionEntity> implements ChatSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ChatSessionEntity> page = this.selectPage(
                new Query<ChatSessionEntity>(params).getPage(),
                new EntityWrapper<ChatSessionEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<ChatSessionEntity> selectListView(Wrapper<ChatSessionEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ChatSessionEntity> wrapper) {
        Page<ChatSessionEntity> page = new Query<ChatSessionEntity>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        return new PageUtils(page);
    }

    @Override
    public ChatSessionEntity getOrCreateSession(String sessionType, Long userId, String userAccount,
                                                Long shopId, String shopAccount,
                                                Long employeeId, String employeeNumber) {
        ChatSessionEntity sessionEntity = null;

        // 根据会话类型和参与者查找现有会话
        if (ChatConstants.SESSION_TYPE_USER_SHOP.equals(sessionType)) {
            // 用户与商家/员工会话
            if (employeeId != null) {
                // 包含员工的会话
                sessionEntity = this.selectOne(
                        new EntityWrapper<ChatSessionEntity>()
                                .eq("session_type", sessionType)
                                .eq("user_id", userId)
                                .eq("employee_id", employeeId)
                );
            } else {
                // 与商家的会话
                sessionEntity = this.selectOne(
                        new EntityWrapper<ChatSessionEntity>()
                                .eq("session_type", sessionType)
                                .eq("user_id", userId)
                                .eq("shop_id", shopId)
                                .isNull("employee_id")
                );
            }
        } else if (ChatConstants.SESSION_TYPE_USER_ADMIN.equals(sessionType)) {
            // 用户与平台会话
            sessionEntity = this.selectOne(
                    new EntityWrapper<ChatSessionEntity>()
                            .eq("session_type", sessionType)
                            .eq("user_id", userId)
            );
        } else if (ChatConstants.SESSION_TYPE_SHOP_ADMIN.equals(sessionType)) {
            // 商家与平台会话
            sessionEntity = this.selectOne(
                    new EntityWrapper<ChatSessionEntity>()
                            .eq("session_type", sessionType)
                            .eq("shop_id", shopId)
            );
        }

        // 如果不存在，创建新会话
        if (sessionEntity == null) {
            sessionEntity = new ChatSessionEntity();
            sessionEntity.setSessionType(sessionType);
            sessionEntity.setUserId(userId);
            sessionEntity.setUserAccount(userAccount);
            sessionEntity.setShopId(shopId);
            sessionEntity.setShopAccount(shopAccount);
            sessionEntity.setEmployeeId(employeeId);
            sessionEntity.setEmployeeNumber(employeeNumber);
            sessionEntity.setUserUnread(0);
            sessionEntity.setShopUnread(0);
            sessionEntity.setAdminUnread(0);
            sessionEntity.setLastMessage("");

            Date now = new Date();
            sessionEntity.setAddtime(now);
            sessionEntity.setUpdatetime(now);

            this.insert(sessionEntity);
        }

        return sessionEntity;
    }

    @Override
    public void updateUnreadCount(Long sessionId, String recipientType) {
        ChatSessionEntity session = this.selectById(sessionId);
        if (session != null) {
            if ("USER".equals(recipientType)) {
                session.setUserUnread(session.getUserUnread() + 1);
            } else if ("SHOP".equals(recipientType) || "EMPLOYEE".equals(recipientType)) {
                session.setShopUnread(session.getShopUnread() + 1);
            } else if ("ADMIN".equals(recipientType)) {
                session.setAdminUnread(session.getAdminUnread() + 1);
            }
            session.setUpdatetime(new Date());
            this.updateById(session);
        }
    }

    @Override
    public void resetUnreadCount(Long sessionId, String readerType) {
        ChatSessionEntity session = this.selectById(sessionId);
        if (session != null) {
            if ("USER".equals(readerType)) {
                session.setUserUnread(0);
            } else if ("SHOP".equals(readerType) || "EMPLOYEE".equals(readerType)) {
                session.setShopUnread(0);
            } else if ("ADMIN".equals(readerType)) {
                session.setAdminUnread(0);
            }
            session.setUpdatetime(new Date());
            this.updateById(session);
        }
    }
}