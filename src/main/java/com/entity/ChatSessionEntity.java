package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天会话
 */
@TableName("chat_session")
public class ChatSessionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话类型 (USER_SHOP: 用户与商家, USER_ADMIN: 用户与平台, SHOP_ADMIN: 商家与平台)
     */
    private String sessionType;

    /**
     * 用户ID (当涉及用户时)
     */
    private Long userId;

    /**
     * 用户账号 (冗余字段，便于查询)
     */
    private String userAccount;

    /**
     * 商家ID (当涉及商家时)
     */
    private Long shopId;

    /**
     * 商家账号 (冗余字段，便于查询)
     */
    private String shopAccount;

    /**
     * 店铺员工ID (当涉及店铺员工时)
     */
    private Long employeeId;

    /**
     * 店铺员工工号 (冗余字段，便于查询)
     */
    private String employeeNumber;

    /**
     * 最后一条消息
     */
    private String lastMessage;

    /**
     * 未读消息数(用户)
     */
    private Integer userUnread;

    /**
     * 未读消息数(商家/员工)
     */
    private Integer shopUnread;

    /**
     * 未读消息数(平台)
     */
    private Integer adminUnread;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date updatetime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopAccount() {
        return shopAccount;
    }

    public void setShopAccount(String shopAccount) {
        this.shopAccount = shopAccount;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Integer getUserUnread() {
        return userUnread;
    }

    public void setUserUnread(Integer userUnread) {
        this.userUnread = userUnread;
    }

    public Integer getShopUnread() {
        return shopUnread;
    }

    public void setShopUnread(Integer shopUnread) {
        this.shopUnread = shopUnread;
    }

    public Integer getAdminUnread() {
        return adminUnread;
    }

    public void setAdminUnread(Integer adminUnread) {
        this.adminUnread = adminUnread;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}