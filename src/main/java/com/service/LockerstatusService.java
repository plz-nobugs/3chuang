package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.LockerstatusEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.LockerstatusVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.LockerstatusView;


/**
 * 快递柜状态
 */
public interface LockerstatusService extends IService<LockerstatusEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<LockerstatusVO> selectListVO(Wrapper<LockerstatusEntity> wrapper);

    LockerstatusVO selectVO(@Param("ew") Wrapper<LockerstatusEntity> wrapper);

    List<LockerstatusView> selectListView(Wrapper<LockerstatusEntity> wrapper);

    LockerstatusView selectView(@Param("ew") Wrapper<LockerstatusEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params,Wrapper<LockerstatusEntity> wrapper);

}