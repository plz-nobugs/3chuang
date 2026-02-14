package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.LockerstatusEntity;
import com.entity.view.LockerstatusView;

import com.service.LockerstatusService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;

/**
 * 快递柜状态
 * 后端接口
 */
@RestController
@RequestMapping("/lockerstatus")
public class LockerstatusController {
    @Autowired
    private LockerstatusService lockerstatusService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,LockerstatusEntity lockerstatus,
                  HttpServletRequest request){
        EntityWrapper<LockerstatusEntity> ew = new EntityWrapper<LockerstatusEntity>();
        PageUtils page = lockerstatusService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, lockerstatus), params), params));

        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,LockerstatusEntity lockerstatus,
                  HttpServletRequest request){
        EntityWrapper<LockerstatusEntity> ew = new EntityWrapper<LockerstatusEntity>();
        PageUtils page = lockerstatusService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, lockerstatus), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(LockerstatusEntity lockerstatus){
        EntityWrapper<LockerstatusEntity> ew = new EntityWrapper<LockerstatusEntity>();
        ew.allEq(MPUtil.allEQMapPre(lockerstatus, "lockerstatus"));
        return R.ok().put("data", lockerstatusService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(LockerstatusEntity lockerstatus){
        EntityWrapper<LockerstatusEntity> ew = new EntityWrapper<LockerstatusEntity>();
        ew.allEq(MPUtil.allEQMapPre(lockerstatus, "lockerstatus"));
        LockerstatusView lockerstatusView = lockerstatusService.selectView(ew);
        return R.ok("查询快递柜状态成功").put("data", lockerstatusView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        LockerstatusEntity lockerstatus = lockerstatusService.selectById(id);
        return R.ok().put("data", lockerstatus);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        LockerstatusEntity lockerstatus = lockerstatusService.selectById(id);
        return R.ok().put("data", lockerstatus);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody LockerstatusEntity lockerstatus, HttpServletRequest request){
        // 默认值
        if(lockerstatus.getUsed() == null) {
            lockerstatus.setUsed(0);
        }

        // 确保经纬度值在合理范围内
        if(lockerstatus.getLongitude() != null && (lockerstatus.getLongitude() < -180 || lockerstatus.getLongitude() > 180)) {
            return R.error("经度值必须在-180到180之间");
        }

        if(lockerstatus.getLatitude() != null && (lockerstatus.getLatitude() < -90 || lockerstatus.getLatitude() > 90)) {
            return R.error("纬度值必须在-90到90之间");
        }

        lockerstatusService.insert(lockerstatus);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody LockerstatusEntity lockerstatus, HttpServletRequest request){
        // 默认值
        if(lockerstatus.getUsed() == null) {
            lockerstatus.setUsed(0);
        }

        // 确保经纬度值在合理范围内
        if(lockerstatus.getLongitude() != null && (lockerstatus.getLongitude() < -180 || lockerstatus.getLongitude() > 180)) {
            return R.error("经度值必须在-180到180之间");
        }

        if(lockerstatus.getLatitude() != null && (lockerstatus.getLatitude() < -90 || lockerstatus.getLatitude() > 90)) {
            return R.error("纬度值必须在-90到90之间");
        }

        lockerstatusService.insert(lockerstatus);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody LockerstatusEntity lockerstatus, HttpServletRequest request){
        // 确保经纬度值在合理范围内
        if(lockerstatus.getLongitude() != null && (lockerstatus.getLongitude() < -180 || lockerstatus.getLongitude() > 180)) {
            return R.error("经度值必须在-180到180之间");
        }

        if(lockerstatus.getLatitude() != null && (lockerstatus.getLatitude() < -90 || lockerstatus.getLatitude() > 90)) {
            return R.error("纬度值必须在-90到90之间");
        }

        lockerstatusService.updateById(lockerstatus);
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        lockerstatusService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}