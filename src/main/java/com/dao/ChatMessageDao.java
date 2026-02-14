package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.ChatMessageEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatMessageDao extends BaseMapper<ChatMessageEntity> {
    List<ChatMessageEntity> selectListView(@Param("ew") Wrapper<ChatMessageEntity> wrapper);
    List<ChatMessageEntity> selectListView(Pagination page, @Param("ew") Wrapper<ChatMessageEntity> wrapper);
}