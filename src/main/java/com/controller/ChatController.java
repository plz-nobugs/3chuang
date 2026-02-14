package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.LoginUser;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.entity.*;
import com.entity.vo.ChatMessageVO;
import com.entity.vo.ChatSessionVO;
import com.service.*;
import com.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 客服聊天
 */
@RestController
@RequestMapping("chat")
public class ChatController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private GukeService gukeService;

    @Autowired
    private DianzhuService dianzhuService;

    @Autowired
    private YuangongService yuangongService;

    /**
     * 获取用户的聊天会话列表
     */
    @GetMapping("/user/sessions")
    public R getUserSessions(@RequestParam Long userId) {
        List<ChatSessionEntity> sessions = chatSessionService.selectList(
                new EntityWrapper<ChatSessionEntity>()
                        .eq("user_id", userId)
                        .orderBy("updatetime", false)
        );

        List<ChatSessionVO> sessionVOs = new ArrayList<>();
        for (ChatSessionEntity session : sessions) {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setId(session.getId());
            vo.setSessionType(session.getSessionType());
            vo.setUserId(session.getUserId());
            vo.setUserAccount(session.getUserAccount());
            vo.setShopId(session.getShopId());
            vo.setShopAccount(session.getShopAccount());
            vo.setEmployeeId(session.getEmployeeId());
            vo.setEmployeeNumber(session.getEmployeeNumber());
            vo.setLastMessage(session.getLastMessage());
            vo.setUnreadCount(session.getUserUnread());
            vo.setUpdatetime(session.getUpdatetime());
            sessionVOs.add(vo);
        }

        return R.ok().put("data", sessionVOs);
    }

    /**
     * 获取商家的聊天会话列表
     */
    @GetMapping("/shop/sessions")
    public R getShopSessions(@RequestParam Long shopId) {
        List<ChatSessionEntity> sessions = chatSessionService.selectList(
                new EntityWrapper<ChatSessionEntity>()
                        .eq("shop_id", shopId)
                        .orderBy("updatetime", false)
        );

        List<ChatSessionVO> sessionVOs = new ArrayList<>();
        for (ChatSessionEntity session : sessions) {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setId(session.getId());
            vo.setSessionType(session.getSessionType());
            vo.setUserId(session.getUserId());
            vo.setUserAccount(session.getUserAccount());
            vo.setShopId(session.getShopId());
            vo.setShopAccount(session.getShopAccount());
            vo.setEmployeeId(session.getEmployeeId());
            vo.setEmployeeNumber(session.getEmployeeNumber());
            vo.setLastMessage(session.getLastMessage());
            vo.setUnreadCount(session.getShopUnread());
            vo.setUpdatetime(session.getUpdatetime());
            sessionVOs.add(vo);
        }

        return R.ok().put("data", sessionVOs);
    }

    /**
     * 获取员工的聊天会话列表
     */
    @GetMapping("/employee/sessions")
    public R getEmployeeSessions(@RequestParam Long employeeId) {
        // 获取员工信息
        YuangongEntity employee = yuangongService.selectById(employeeId);
        if (employee == null) {
            return R.error("员工不存在");
        }

        // 获取员工所属商家
        DianzhuEntity shop = null;
        List<DianzhuEntity> shops = dianzhuService.selectList(
                new EntityWrapper<DianzhuEntity>()
                        .eq("dianzhuzhanghao", employee.getMendianmingcheng())
        );

        if (shops != null && !shops.isEmpty()) {
            shop = shops.get(0);
        }

        if (shop == null) {
            return R.error("员工所属商家不存在");
        }

        // 查询与该商家相关的会话列表
        List<ChatSessionEntity> sessions = chatSessionService.selectList(
                new EntityWrapper<ChatSessionEntity>()
                        .eq("session_type", ChatConstants.SESSION_TYPE_USER_SHOP)
                        .eq("shop_id", shop.getId())
                        .orderBy("updatetime", false)
        );

        List<ChatSessionVO> sessionVOs = new ArrayList<>();
        for (ChatSessionEntity session : sessions) {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setId(session.getId());
            vo.setSessionType(session.getSessionType());
            vo.setUserId(session.getUserId());
            vo.setUserAccount(session.getUserAccount());
            vo.setShopId(session.getShopId());
            vo.setShopAccount(session.getShopAccount());
            vo.setEmployeeId(session.getEmployeeId());
            vo.setEmployeeNumber(session.getEmployeeNumber());
            vo.setLastMessage(session.getLastMessage());
            vo.setUnreadCount(session.getShopUnread());
            vo.setUpdatetime(session.getUpdatetime());
            sessionVOs.add(vo);
        }

        return R.ok().put("data", sessionVOs);
    }

    /**
     * 获取管理员的聊天会话列表
     */
    @GetMapping("/admin/sessions")
    public R getAdminSessions() {
        List<ChatSessionEntity> sessions = chatSessionService.selectList(
                new EntityWrapper<ChatSessionEntity>()
                        .in("session_type", Arrays.asList(ChatConstants.SESSION_TYPE_USER_ADMIN, ChatConstants.SESSION_TYPE_SHOP_ADMIN))
                        .orderBy("updatetime", false)
        );

        List<ChatSessionVO> sessionVOs = new ArrayList<>();
        for (ChatSessionEntity session : sessions) {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setId(session.getId());
            vo.setSessionType(session.getSessionType());
            vo.setUserId(session.getUserId());
            vo.setUserAccount(session.getUserAccount());
            vo.setShopId(session.getShopId());
            vo.setShopAccount(session.getShopAccount());
            vo.setEmployeeId(session.getEmployeeId());
            vo.setEmployeeNumber(session.getEmployeeNumber());
            vo.setLastMessage(session.getLastMessage());
            vo.setUnreadCount(session.getAdminUnread());
            vo.setUpdatetime(session.getUpdatetime());
            sessionVOs.add(vo);
        }

        return R.ok().put("data", sessionVOs);
    }

    /**
     * 查找或创建用户与商家的会话
     */
    @PostMapping("/user/create-shop-session")
    public R createUserShopSession(@RequestParam Long userId, @RequestParam Long shopId,
                                   @RequestParam(required = false) Long employeeId) {
        // 获取用户信息
        GukeEntity user = gukeService.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        // 获取商家信息
        DianzhuEntity shop = dianzhuService.selectById(shopId);
        if (shop == null) {
            return R.error("商家不存在");
        }

        // 获取员工信息（如果有）
        YuangongEntity employee = null;
        String employeeNumber = null;
        if (employeeId != null) {
            employee = yuangongService.selectById(employeeId);
            if (employee == null) {
                return R.error("员工不存在");
            }

            // 验证员工是否属于该商家
            if (!shop.getDianzhuzhanghao().equals(employee.getMendianmingcheng())) {
                return R.error("该员工不属于此商家");
            }

            employeeNumber = employee.getGonghao();
        }

        // 创建或获取会话
        ChatSessionEntity session = chatSessionService.getOrCreateSession(
                ChatConstants.SESSION_TYPE_USER_SHOP,
                userId, user.getZhanghao(),
                shopId, shop.getDianzhuzhanghao(),
                employeeId, employeeNumber
        );

        return R.ok().put("data", session);
    }

    /**
     * 查找或创建用户与平台的会话
     */
    @PostMapping("/user/create-admin-session")
    public R createUserAdminSession(@RequestParam Long userId) {
        // 获取用户信息
        GukeEntity user = gukeService.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        // 创建或获取会话
        ChatSessionEntity session = chatSessionService.getOrCreateSession(
                ChatConstants.SESSION_TYPE_USER_ADMIN,
                userId, user.getZhanghao(),
                null, null,
                null, null
        );

        return R.ok().put("data", session);
    }

    /**
     * 搜索商家 - 直接实现，不依赖MPUtil
     */
    @IgnoreAuth
    @RequestMapping("/search-shops")
    public R searchShops(@RequestParam(required = false) String keyword) {
       // System.out.println("搜索商家API被调用，关键词：" + keyword); // 确认API被调用的日志

        // 如果关键词为空，返回所有商家
        if (keyword == null || keyword.trim().isEmpty()) {
         //   System.out.println("关键词为空，返回所有商家");
            List<DianzhuEntity> allShops = dianzhuService.selectList(null);
        //    System.out.println("返回所有商家，共" + allShops.size() + "个");
            return R.ok().put("data", allShops);
        }

        // 使用EntityWrapper直接构建OR查询
        EntityWrapper<DianzhuEntity> wrapper = new EntityWrapper<>();
     //   System.out.println("构建查询条件：dianzhuzhanghao或dianzhuxingming包含" + keyword);

        // 使用OR条件直接构建LIKE查询
        wrapper.like("dianzhuzhanghao", "%" + keyword + "%")
                .or()
                .like("dianzhuxingming", "%" + keyword + "%");

        List<DianzhuEntity> shops = dianzhuService.selectList(wrapper);
      //  System.out.println("搜索结果：" + shops.size() + "个商家");

        return R.ok().put("data", shops);
    }

    /**
     * 获取所有商家 - 不分页
     */
    @IgnoreAuth
    @RequestMapping("/all-shops")
    public R getAllShops() {
     //   System.out.println("获取所有商家API被调用");
        List<DianzhuEntity> shops = dianzhuService.selectList(null);
     //   System.out.println("返回所有商家，共" + shops.size() + "个");
        return R.ok().put("data", shops);
    }

    @Autowired
    private SessionsHelper sessionsHelper;

    /**
     * 直接SQL查询商家 - 绕过MyBatis-Plus
     */
    @IgnoreAuth
    @RequestMapping("/direct-search-shops")
    public R directSearchShops(@RequestParam(required = false) String keyword) {
     //   System.out.println("直接SQL查询商家API被调用，关键词：" + keyword);
        List<Map<String, Object>> shops = sessionsHelper.searchShops(keyword);
        return R.ok().put("data", shops);
    }


    /**
     * 查找或创建商家与平台的会话
     */
    @PostMapping("/shop/create-admin-session")
    public R createShopAdminSession(@RequestParam Long shopId) {
        // 获取商家信息
        DianzhuEntity shop = dianzhuService.selectById(shopId);
        if (shop == null) {
            return R.error("商家不存在");
        }

        // 创建或获取会话
        ChatSessionEntity session = chatSessionService.getOrCreateSession(
                ChatConstants.SESSION_TYPE_SHOP_ADMIN,
                null, null,
                shopId, shop.getDianzhuzhanghao(),
                null, null
        );

        return R.ok().put("data", session);
    }

    /**
     * 获取会话消息
     */
    @GetMapping("/messages")
    public R getMessages(@RequestParam Long sessionId, @RequestParam Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<ChatMessageEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("session_id", sessionId);
        wrapper.orderBy("addtime", true); // 按时间升序排列

        // 分页查询
        PageUtils page = chatMessageService.queryPage(params, wrapper);

        // 转换为VO
        List<ChatMessageEntity> records = (List<ChatMessageEntity>) page.getList();
        List<ChatMessageVO> voList = new ArrayList<>();

        for (ChatMessageEntity record : records) {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setId(record.getId());
            vo.setSessionId(record.getSessionId());
            vo.setSenderType(record.getSenderType());
            vo.setSenderId(record.getSenderId());
            vo.setSenderName(record.getSenderName());
            vo.setContent(record.getContent());
            vo.setIsRead(record.getIsRead());
            vo.setAddtime(record.getAddtime());
            voList.add(vo);
        }

        // 使用兼容2.x版本的方式创建分页对象
        com.baomidou.mybatisplus.plugins.Page<ChatMessageVO> voPage =
                new com.baomidou.mybatisplus.plugins.Page<>();
        voPage.setRecords(voList);
        voPage.setCurrent(page.getCurrPage());
        voPage.setSize(page.getPageSize());
        voPage.setTotal(page.getTotal()); // 使用正确的方法

        return R.ok().put("data", new PageUtils(voPage));
    }

    /**
     * 发送消息(用户)
     */
    @PostMapping("/user/send")
    public R userSendMessage(@RequestParam Long sessionId, @RequestParam Long userId,
                             @RequestParam String content) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 验证用户是否是会话参与者
        if (!userId.equals(session.getUserId())) {
            return R.error("无权访问此会话");
        }

        // 获取用户信息
        GukeEntity user = gukeService.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        // 发送消息
        ChatMessageEntity message = chatMessageService.sendMessage(
                sessionId,
                ChatConstants.SENDER_TYPE_USER,
                userId,
                user.getZhanghao(),
                content
        );

        return R.ok().put("data", message);
    }

    /**
     * 发送消息(商家)
     */
    @PostMapping("/shop/send")
    public R shopSendMessage(@RequestParam Long sessionId, @RequestParam Long shopId,
                             @RequestParam String content) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 验证商家是否是会话参与者
        if (!shopId.equals(session.getShopId())) {
            return R.error("无权访问此会话");
        }

        // 获取商家信息
        DianzhuEntity shop = dianzhuService.selectById(shopId);
        if (shop == null) {
            return R.error("商家不存在");
        }

        // 发送消息
        ChatMessageEntity message = chatMessageService.sendMessage(
                sessionId,
                ChatConstants.SENDER_TYPE_SHOP,
                shopId,
                shop.getDianzhuzhanghao(),
                content
        );

        return R.ok().put("data", message);
    }

    /**
     * 发送消息(员工)
     */
    @PostMapping("/employee/send")
    public R employeeSendMessage(@RequestParam Long sessionId, @RequestParam Long employeeId,
                                 @RequestParam String content) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 获取员工信息
        YuangongEntity employee = yuangongService.selectById(employeeId);
        if (employee == null) {
            return R.error("员工不存在");
        }

        // 验证员工是否属于该商家
        if (session.getShopId() != null) {
            DianzhuEntity shop = dianzhuService.selectById(session.getShopId());
            if (shop != null && !shop.getDianzhuzhanghao().equals(employee.getMendianmingcheng())) {
                return R.error("该员工不属于此会话的商家");
            }
        }

        // 发送消息
        ChatMessageEntity message = chatMessageService.sendMessage(
                sessionId,
                ChatConstants.SENDER_TYPE_EMPLOYEE,
                employeeId,
                employee.getGonghao(), // 使用工号作为发送者名称
                content
        );

        return R.ok().put("data", message);
    }


    /**
     * 发送消息(管理员)
     */
    @PostMapping("/admin/send")
    public R adminSendMessage(@RequestParam Long sessionId, @RequestParam Long adminId,
                              @RequestParam String content, HttpServletRequest request) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 获取管理员信息
        UserEntity admin = userService.selectById(adminId);
        if (admin == null) {
            return R.error("管理员不存在");
        }

        // 验证是否是管理员
        if (!"管理员".equals(admin.getRole())) {
            return R.error("无管理员权限");
        }

        // 发送消息
        ChatMessageEntity message = chatMessageService.sendMessage(
                sessionId,
                ChatConstants.SENDER_TYPE_ADMIN,
                adminId,
                admin.getUsername(), // 使用用户名作为发送者名称
                content
        );

        return R.ok().put("data", message);
    }

    /**
     * 标记消息为已读(用户)
     */
    @PostMapping("/user/read")
    public R markUserMessagesAsRead(@RequestParam Long sessionId, @RequestParam Long userId) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 验证用户是否是会话参与者
        if (!userId.equals(session.getUserId())) {
            return R.error("无权访问此会话");
        }

        // 标记消息为已读
        chatMessageService.markMessagesAsRead(sessionId, ChatConstants.SENDER_TYPE_USER);

        return R.ok();
    }

    /**
     * 标记消息为已读(商家)
     */
    @PostMapping("/shop/read")
    public R markShopMessagesAsRead(@RequestParam Long sessionId, @RequestParam Long shopId) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 验证商家是否是会话参与者
        if (!shopId.equals(session.getShopId())) {
            return R.error("无权访问此会话");
        }

        // 标记消息为已读
        chatMessageService.markMessagesAsRead(sessionId, ChatConstants.SENDER_TYPE_SHOP);

        return R.ok();
    }

    /**
     * 标记消息为已读(员工)
     */
    @PostMapping("/employee/read")
    public R markEmployeeMessagesAsRead(@RequestParam Long sessionId, @RequestParam Long employeeId) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 获取员工信息
        YuangongEntity employee = yuangongService.selectById(employeeId);
        if (employee == null) {
            return R.error("员工不存在");
        }

        // 验证员工是否属于该商家
        if (session.getShopId() != null) {
            DianzhuEntity shop = dianzhuService.selectById(session.getShopId());
            if (shop != null && !shop.getDianzhuzhanghao().equals(employee.getMendianmingcheng())) {
                return R.error("该员工不属于此会话的商家");
            }
        }

        // 标记消息为已读
        chatMessageService.markMessagesAsRead(sessionId, ChatConstants.SENDER_TYPE_EMPLOYEE);

        return R.ok();
    }

    /**
     * 标记消息为已读(管理员)
     */
    @PostMapping("/admin/read")
    public R markAdminMessagesAsRead(@RequestParam Long sessionId, @RequestParam Long adminId) {
        // 验证会话是否存在
        ChatSessionEntity session = chatSessionService.selectById(sessionId);
        if (session == null) {
            return R.error("会话不存在");
        }

        // 获取管理员信息
        UserEntity admin = userService.selectById(adminId);
        if (admin == null) {
            return R.error("管理员不存在");
        }

        // 验证是否是管理员
        if (!"管理员".equals(admin.getRole())) {
            return R.error("无管理员权限");
        }

        // 标记消息为已读
        chatMessageService.markMessagesAsRead(sessionId, ChatConstants.SENDER_TYPE_ADMIN);

        return R.ok();
    }
}