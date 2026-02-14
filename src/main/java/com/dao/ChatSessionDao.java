package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.ChatSessionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatSessionDao extends BaseMapper<ChatSessionEntity> {
    List<ChatSessionEntity> selectListView(@Param("ew") Wrapper<ChatSessionEntity> wrapper);
    List<ChatSessionEntity> selectListView(Pagination page, @Param("ew") Wrapper<ChatSessionEntity> wrapper);
}