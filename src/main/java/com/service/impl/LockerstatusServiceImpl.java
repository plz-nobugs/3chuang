package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;

import com.dao.LockerstatusDao;
import com.entity.LockerstatusEntity;
import com.service.LockerstatusService;
import com.entity.vo.LockerstatusVO;
import com.entity.view.LockerstatusView;

@Service("lockerstatusService")
public class LockerstatusServiceImpl extends ServiceImpl<LockerstatusDao, LockerstatusEntity> implements LockerstatusService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<LockerstatusEntity> page = this.selectPage(
                new Query<LockerstatusEntity>(params).getPage(),
                new EntityWrapper<LockerstatusEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<LockerstatusEntity> wrapper) {
        Page<LockerstatusView> page =new Query<LockerstatusView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<LockerstatusVO> selectListVO(Wrapper<LockerstatusEntity> wrapper) {
        return baseMapper.selectListVO(wrapper);
    }

    @Override
    public LockerstatusVO selectVO(Wrapper<LockerstatusEntity> wrapper) {
        return baseMapper.selectVO(wrapper);
    }

    @Override
    public List<LockerstatusView> selectListView(Wrapper<LockerstatusEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public LockerstatusView selectView(Wrapper<LockerstatusEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }
}