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

import com.entity.DingdanxinxiEntity;
import com.entity.view.DingdanxinxiView;

import com.service.DingdanxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 订单信息
 * 后端接口
 * @author
 * @email
 * @date 2021-04-27 11:11:19
 */
@RestController
@RequestMapping("/dingdanxinxi")
public class DingdanxinxiController {
    @Autowired
    private DingdanxinxiService dingdanxinxiService;



    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DingdanxinxiEntity dingdanxinxi,
                  HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("guke")) {
            dingdanxinxi.setZhanghao((String)request.getSession().getAttribute("username"));
        }
        EntityWrapper<DingdanxinxiEntity> ew = new EntityWrapper<DingdanxinxiEntity>();
        PageUtils page = dingdanxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, dingdanxinxi), params), params));

        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DingdanxinxiEntity dingdanxinxi,
                  HttpServletRequest request){
        EntityWrapper<DingdanxinxiEntity> ew = new EntityWrapper<DingdanxinxiEntity>();
        PageUtils page = dingdanxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, dingdanxinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( DingdanxinxiEntity dingdanxinxi){
        EntityWrapper<DingdanxinxiEntity> ew = new EntityWrapper<DingdanxinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre( dingdanxinxi, "dingdanxinxi"));
        return R.ok().put("data", dingdanxinxiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DingdanxinxiEntity dingdanxinxi){
        EntityWrapper< DingdanxinxiEntity> ew = new EntityWrapper< DingdanxinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre( dingdanxinxi, "dingdanxinxi"));
        DingdanxinxiView dingdanxinxiView =  dingdanxinxiService.selectView(ew);
        return R.ok("查询订单信息成功").put("data", dingdanxinxiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DingdanxinxiEntity dingdanxinxi = dingdanxinxiService.selectById(id);
        return R.ok().put("data", dingdanxinxi);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DingdanxinxiEntity dingdanxinxi = dingdanxinxiService.selectById(id);
        return R.ok().put("data", dingdanxinxi);
    }

    /**
     * 获取审核数据（包含照片和检测报告）
     */
    @RequestMapping("/auditInfo/{id}")
    public R auditInfo(@PathVariable("id") Long id){
        try {
            System.out.println("获取审核信息，订单ID: " + id);

            DingdanxinxiEntity dingdanxinxi = dingdanxinxiService.selectById(id);
            if (dingdanxinxi == null) {
                return R.error("订单不存在");
            }

            // 创建审核数据容器
            Map<String, Object> auditData = new HashMap<>();

            // 修改：使用绝对路径，以/开头
            dingdanxinxi.setShangfangzhaopian("/springboot142f7/upload/sample_top.jpg");
            dingdanxinxi.setXiafangzhaopian("/springboot142f7/upload/sample_bottom.jpg");
            dingdanxinxi.setHoufangzhaopian("/springboot142f7/upload/sample_back.jpg");
            dingdanxinxi.setZuocezhaopian("/springboot142f7/upload/sample_left.jpg");
            dingdanxinxi.setYoucezhaopian("/springboot142f7/upload/sample_right.jpg");

            auditData.put("orderInfo", dingdanxinxi);
            // 创建模拟的AI检测报告
            Map<String, Object> detectReport = new HashMap<>();
            detectReport.put("overallScore", 87.5);
            detectReport.put("damageLevel", "轻微");
            detectReport.put("integrityCheck", "良好");
            detectReport.put("qualityAssessment", "符合退货标准");
            detectReport.put("detectTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            detectReport.put("recommendation", "根据AI检测分析，该商品外观完整，包装状态良好，符合退货标准，建议通过退货申请。商品各个角度拍摄清晰，未发现明显损坏痕迹。");

            // 创建详细检测结果
            List<Map<String, Object>> detectDetails = new ArrayList<>();

            Map<String, Object> detail1 = new HashMap<>();
            detail1.put("item", "外观完整性");
            detail1.put("score", 92);
            detail1.put("status", "优秀");
            detectDetails.add(detail1);

            Map<String, Object> detail2 = new HashMap<>();
            detail2.put("item", "包装状态");
            detail2.put("score", 88);
            detail2.put("status", "良好");
            detectDetails.add(detail2);

            Map<String, Object> detail3 = new HashMap<>();
            detail3.put("item", "标签清晰度");
            detail3.put("score", 85);
            detail3.put("status", "清晰");
            detectDetails.add(detail3);

            Map<String, Object> detail4 = new HashMap<>();
            detail4.put("item", "物品完整性");
            detail4.put("score", 90);
            detail4.put("status", "完整");
            detectDetails.add(detail4);

            detectReport.put("details", detectDetails);
            auditData.put("detectReport", detectReport);

            System.out.println("审核数据创建成功: " + auditData);

            return R.ok().put("data", auditData);

        } catch (Exception e) {
            System.err.println("获取审核数据失败: " + e.getMessage());
            e.printStackTrace();
            return R.error("获取审核数据失败: " + e.getMessage());
        }
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DingdanxinxiEntity dingdanxinxi, HttpServletRequest request){
        dingdanxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        //ValidatorUtils.validateEntity(dingdanxinxi);
        dingdanxinxiService.insert(dingdanxinxi);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DingdanxinxiEntity dingdanxinxi, HttpServletRequest request){
        dingdanxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        //ValidatorUtils.validateEntity(dingdanxinxi);
        dingdanxinxiService.insert(dingdanxinxi);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DingdanxinxiEntity dingdanxinxi, HttpServletRequest request){
        try {
            System.out.println("更新订单审核信息: " + dingdanxinxi.getId());
            System.out.println("审核状态: " + dingdanxinxi.getSfsh());
            System.out.println("审核回复: " + dingdanxinxi.getShhf());

            //ValidatorUtils.validateEntity(dingdanxinxi);
            dingdanxinxiService.updateById(dingdanxinxi);//全部更新

            return R.ok("审核更新成功");
        } catch (Exception e) {
            System.err.println("更新审核信息失败: " + e.getMessage());
            e.printStackTrace();
            return R.error("更新失败: " + e.getMessage());
        }
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        dingdanxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 提醒接口
     */
    @RequestMapping("/remind/{columnName}/{type}")
    public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request,
                         @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
        map.put("column", columnName);
        map.put("type", type);

        if(type.equals("2")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            Date remindStartDate = null;
            Date remindEndDate = null;
            if(map.get("remindstart")!=null) {
                Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
                c.setTime(new Date());
                c.add(Calendar.DAY_OF_MONTH,remindStart);
                remindStartDate = c.getTime();
                map.put("remindstart", sdf.format(remindStartDate));
            }
            if(map.get("remindend")!=null) {
                Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
                c.setTime(new Date());
                c.add(Calendar.DAY_OF_MONTH,remindEnd);
                remindEndDate = c.getTime();
                map.put("remindend", sdf.format(remindEndDate));
            }
        }

        Wrapper<DingdanxinxiEntity> wrapper = new EntityWrapper<DingdanxinxiEntity>();
        if(map.get("remindstart")!=null) {
            wrapper.ge(columnName, map.get("remindstart"));
        }
        if(map.get("remindend")!=null) {
            wrapper.le(columnName, map.get("remindend"));
        }

        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("guke")) {
            wrapper.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }

        int count = dingdanxinxiService.selectCount(wrapper);
        return R.ok().put("count", count);
    }



}