package com.dao;

import com.entity.LockerstatusEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.LockerstatusVO;
import com.entity.view.LockerstatusView;


/**
 * 快递柜状态
 */
public interface LockerstatusDao extends BaseMapper<LockerstatusEntity> {

    List<LockerstatusVO> selectListVO(@Param("ew") Wrapper<LockerstatusEntity> wrapper);

    LockerstatusVO selectVO(@Param("ew") Wrapper<LockerstatusEntity> wrapper);

    List<LockerstatusView> selectListView(@Param("ew") Wrapper<LockerstatusEntity> wrapper);

    List<LockerstatusView> selectListView(Pagination page,@Param("ew") Wrapper<LockerstatusEntity> wrapper);

    LockerstatusView selectView(@Param("ew") Wrapper<LockerstatusEntity> wrapper);

}