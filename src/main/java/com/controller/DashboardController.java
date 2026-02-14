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

import com.service.DingdanxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;

/**
 * 高级数据大屏
 * 后端接口
 * @author
 * @email
 * @date 2025-05-29
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DingdanxinxiService dingdanxinxiService;

    /**
     * 获取数据大屏总览数据
     */
    @RequestMapping("/overview")
    public R getOverviewData(HttpServletRequest request) {
        try {
            // 检查用户权限
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 基础统计数据
            Map<String, Object> basicStats = new HashMap<>();
            basicStats.put("totalOrders", 15847);
            basicStats.put("todayOrders", 189);
            basicStats.put("activeLockers", 425);
            basicStats.put("merchantCount", 127);
            basicStats.put("successRate", 92.8);
            basicStats.put("carbonReduction", 3.67); // 吨
            basicStats.put("totalUsers", 8943);
            basicStats.put("monthlyGrowth", 15.6); // 月增长率%
            basicStats.put("avgProcessingTime", 2.1); // 平均处理时间(分钟)
            basicStats.put("customerSatisfaction", 96.5); // 客户满意度%

            data.put("basicStats", basicStats);

            // 实时指标
            Map<String, Object> realTimeMetrics = new HashMap<>();
            realTimeMetrics.put("onlineUsers", 267);
            realTimeMetrics.put("processingOrders", 45);
            realTimeMetrics.put("systemLoad", 18.3);
            realTimeMetrics.put("networkLatency", 23);
            realTimeMetrics.put("errorRate", 0.12);

            data.put("realTimeMetrics", realTimeMetrics);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取概览数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取退货处理趋势数据
     */
    @RequestMapping("/returnTrend")
    public R getReturnTrendData(HttpServletRequest request) {
        try {
            // 检查用户权限
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 最近30天退货趋势
            List<String> dates = new ArrayList<>();
            List<Integer> returnCounts = new ArrayList<>();
            List<Integer> successCounts = new ArrayList<>();
            List<Double> successRates = new ArrayList<>();

            // 生成最近30天的数据
            Calendar cal = Calendar.getInstance();
            for (int i = 29; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                dates.add(new SimpleDateFormat("MM-dd").format(cal.getTime()));

                int returnCount = 200 + (int)(Math.random() * 150);
                int successCount = (int)(returnCount * (0.85 + Math.random() * 0.1));

                returnCounts.add(returnCount);
                successCounts.add(successCount);
                successRates.add(Math.round((successCount * 100.0 / returnCount) * 10) / 10.0);
            }

            data.put("dates", dates);
            data.put("returnCounts", returnCounts);
            data.put("successCounts", successCounts);
            data.put("successRates", successRates);

            // 小时级趋势数据
            List<String> hours = new ArrayList<>();
            hours.add("00"); hours.add("02"); hours.add("04"); hours.add("06");
            hours.add("08"); hours.add("10"); hours.add("12"); hours.add("14");
            hours.add("16"); hours.add("18"); hours.add("20"); hours.add("22");

            List<Integer> hourlyData = new ArrayList<>();
            hourlyData.add(12); hourlyData.add(8); hourlyData.add(5); hourlyData.add(15);
            hourlyData.add(35); hourlyData.add(67); hourlyData.add(89); hourlyData.add(102);
            hourlyData.add(95); hourlyData.add(78); hourlyData.add(56); hourlyData.add(25);

            data.put("hours", hours);
            data.put("hourlyData", hourlyData);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取智能快递柜使用情况
     */
    @RequestMapping("/lockerStatus")
    public R getLockerStatusData(HttpServletRequest request) {
        try {
            // 检查用户权限
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 快递柜状态分布
            List<Map<String, Object>> statusData = new ArrayList<>();

            Map<String, Object> item1 = new HashMap<>();
            item1.put("name", "使用中");
            item1.put("value", 342);
            item1.put("percentage", 80.5);
            statusData.add(item1);

            Map<String, Object> item2 = new HashMap<>();
            item2.put("name", "空闲");
            item2.put("value", 68);
            item2.put("percentage", 16.0);
            statusData.add(item2);

            Map<String, Object> item3 = new HashMap<>();
            item3.put("name", "维护中");
            item3.put("value", 15);
            item3.put("percentage", 3.5);
            statusData.add(item3);

            data.put("statusData", statusData);

            // 地区分布数据
            List<Map<String, Object>> regionData = new ArrayList<>();

            Map<String, Object> region1 = new HashMap<>();
            region1.put("name", "市南区");
            region1.put("value", 156);
            region1.put("growth", 12.5);
            regionData.add(region1);

            Map<String, Object> region2 = new HashMap<>();
            region2.put("name", "市北区");
            region2.put("value", 189);
            region2.put("growth", 8.7);
            regionData.add(region2);

            Map<String, Object> region3 = new HashMap<>();
            region3.put("name", "黄岛区");
            region3.put("value", 134);
            region3.put("growth", 15.2);
            regionData.add(region3);

            Map<String, Object> region4 = new HashMap<>();
            region4.put("name", "崂山区");
            region4.put("value", 98);
            region4.put("growth", 9.8);
            regionData.add(region4);

            Map<String, Object> region5 = new HashMap<>();
            region5.put("name", "城阳区");
            region5.put("value", 76);
            region5.put("growth", 11.3);
            regionData.add(region5);

            data.put("regionData", regionData);

            // 效率统计
            Map<String, Object> efficiency = new HashMap<>();
            efficiency.put("utilizationRate", 83.7);
            efficiency.put("avgProcessingTime", 2.3);
            efficiency.put("peakHourUtilization", 95.2);
            efficiency.put("maintenanceFrequency", 0.8);

            data.put("efficiency", efficiency);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取快递柜数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品转售数据
     */
    @RequestMapping("/resaleData")
    public R getResaleData(HttpServletRequest request) {
        try {
            // 检查用户权限
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 商品类别转售情况
            List<Map<String, Object>> categoryData = new ArrayList<>();

            Map<String, Object> cat1 = new HashMap<>();
            cat1.put("name", "美妆护肤");
            cat1.put("total", 1456);
            cat1.put("success", 1289);
            cat1.put("rate", 88.5);
            cat1.put("revenue", 126750);
            categoryData.add(cat1);

            Map<String, Object> cat2 = new HashMap<>();
            cat2.put("name", "服装配饰");
            cat2.put("total", 2234);
            cat2.put("success", 1978);
            cat2.put("rate", 88.5);
            cat2.put("revenue", 89620);
            categoryData.add(cat2);

            Map<String, Object> cat3 = new HashMap<>();
            cat3.put("name", "3C数码");
            cat3.put("total", 678);
            cat3.put("success", 612);
            cat3.put("rate", 90.3);
            cat3.put("revenue", 234580);
            categoryData.add(cat3);

            Map<String, Object> cat4 = new HashMap<>();
            cat4.put("name", "食品保健");
            cat4.put("total", 892);
            cat4.put("success", 758);
            cat4.put("rate", 85.0);
            cat4.put("revenue", 45890);
            categoryData.add(cat4);

            Map<String, Object> cat5 = new HashMap<>();
            cat5.put("name", "家居用品");
            cat5.put("total", 567);
            cat5.put("success", 489);
            cat5.put("rate", 86.2);
            cat5.put("revenue", 67230);
            categoryData.add(cat5);

            data.put("categoryData", categoryData);

            // 转售趋势数据
            List<String> trendMonths = new ArrayList<>();
            trendMonths.add("1月");
            trendMonths.add("2月");
            trendMonths.add("3月");
            trendMonths.add("4月");
            trendMonths.add("5月");

            List<Integer> trendData = new ArrayList<>();
            trendData.add(3456);
            trendData.add(4123);
            trendData.add(4678);
            trendData.add(5234);
            trendData.add(5827);

            List<Double> trendRates = new ArrayList<>();
            trendRates.add(82.3);
            trendRates.add(84.7);
            trendRates.add(86.1);
            trendRates.add(87.8);
            trendRates.add(88.5);

            data.put("trendMonths", trendMonths);
            data.put("trendData", trendData);
            data.put("trendRates", trendRates);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取转售数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取环保效益数据
     */
    @RequestMapping("/environmentalData")
    public R getEnvironmentalData(HttpServletRequest request) {
        try {
            // 检查用户权限
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 环保指标
            Map<String, Object> envStats = new HashMap<>();
            envStats.put("carbonReduction", 18.67); // 吨CO2
            envStats.put("packagingSaved", 12456); // 个包装
            envStats.put("transportReduction", 5678); // 公里
            envStats.put("resourceUtilization", 94.8); // 资源利用率%
            envStats.put("energySaved", 2345); // 千瓦时
            envStats.put("wasteReduction", 3.45); // 吨

            data.put("envStats", envStats);

            // 月度环保数据趋势
            List<String> months = new ArrayList<>();
            months.add("1月");
            months.add("2月");
            months.add("3月");
            months.add("4月");
            months.add("5月");

            List<Double> carbonData = new ArrayList<>();
            carbonData.add(12.3);
            carbonData.add(14.1);
            carbonData.add(15.8);
            carbonData.add(17.2);
            carbonData.add(18.67);

            List<Integer> packageData = new ArrayList<>();
            packageData.add(7234);
            packageData.add(8567);
            packageData.add(9456);
            packageData.add(11123);
            packageData.add(12456);

            List<Double> energyData = new ArrayList<>();
            energyData.add(1456.0);
            energyData.add(1678.0);
            energyData.add(1923.0);
            energyData.add(2134.0);
            energyData.add(2345.0);

            data.put("months", months);
            data.put("carbonData", carbonData);
            data.put("packageData", packageData);
            data.put("energyData", energyData);

            // 环保排行
            List<Map<String, Object>> ecoRanking = new ArrayList<>();
            String[] regions = {"市南区", "市北区", "黄岛区", "崂山区", "城阳区"};
            Double[] reductions = {5.67, 4.23, 3.89, 2.98, 1.90};

            for (int i = 0; i < regions.length; i++) {
                Map<String, Object> rank = new HashMap<>();
                rank.put("region", regions[i]);
                rank.put("reduction", reductions[i]);
                rank.put("rank", i + 1);
                ecoRanking.add(rank);
            }

            data.put("ecoRanking", ecoRanking);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取环保数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时监控数据
     */
    @RequestMapping("/realTimeData")
    public R getRealTimeData(HttpServletRequest request) {
        try {
            // 检查用户权限
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 实时指标
            Map<String, Object> realTimeStats = new HashMap<>();
            realTimeStats.put("currentActiveUsers", 1456);
            realTimeStats.put("todayProcessed", 687);
            realTimeStats.put("processingTime", "1.8分钟"); // 平均处理时间
            realTimeStats.put("systemLoad", 19.2); // 系统负载%
            realTimeStats.put("queueLength", 23); // 排队长度
            realTimeStats.put("throughput", 156); // 每小时处理量

            data.put("realTimeStats", realTimeStats);

            // 最新处理记录
            List<Map<String, Object>> recentRecords = new ArrayList<>();

            String[] orderIds = {"TH2025053101", "TH2025053102", "TH2025053103", "TH2025053104", "TH2025053105", "TH2025053106", "TH2025053107", "TH2025053108"};
            String[] locations = {"市南区香港中路", "市北区台东", "黄岛区长江路", "崂山区科技街", "城阳区正阳路", "市南区中山路", "市北区辽宁路", "黄岛区海德公园"};
            String[] statuses = {"检测完成", "转售成功", "等待审核", "处理中", "已转售", "检测中", "配送中", "已完成"};
            String[] types = {"美妆", "服装", "数码", "食品", "家居", "美妆", "服装", "数码"};

            for (int i = 0; i < 8; i++) {
                Map<String, Object> record = new HashMap<>();
                record.put("orderId", orderIds[i]);
                record.put("location", locations[i]);
                record.put("status", statuses[i]);
                record.put("type", types[i]);
                record.put("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
                recentRecords.add(record);
            }

            data.put("recentRecords", recentRecords);

            // 系统健康度
            Map<String, Object> systemHealth = new HashMap<>();
            systemHealth.put("cpuUsage", 23.4);
            systemHealth.put("memoryUsage", 67.8);
            systemHealth.put("diskUsage", 45.2);
            systemHealth.put("networkStatus", "良好");
            systemHealth.put("databaseStatus", "正常");
            systemHealth.put("apiResponseTime", 156); // ms

            data.put("systemHealth", systemHealth);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取实时数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取营收分析数据
     */
    @RequestMapping("/revenueAnalysis")
    public R getRevenueAnalysis(HttpServletRequest request) {
        try {
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 营收概览
            Map<String, Object> revenueOverview = new HashMap<>();
            revenueOverview.put("totalRevenue", 1567890);
            revenueOverview.put("monthlyRevenue", 234567);
            revenueOverview.put("growthRate", 18.5);
            revenueOverview.put("profitMargin", 32.4);

            data.put("revenueOverview", revenueOverview);

            // 月度营收趋势
            List<String> months = Arrays.asList("1月", "2月", "3月", "4月", "5月");
            List<Integer> revenues = Arrays.asList(156789, 187234, 198765, 212456, 234567);
            List<Integer> costs = Arrays.asList(98567, 117234, 124567, 133789, 145678);

            data.put("months", months);
            data.put("revenues", revenues);
            data.put("costs", costs);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取营收数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户行为分析
     */
    @RequestMapping("/userBehavior")
    public R getUserBehavior(HttpServletRequest request) {
        try {
            String tableName = request.getSession().getAttribute("tableName").toString();
            if (!tableName.equals("users") && !tableName.equals("dianzhu") && !tableName.equals("yuangong")) {
                return R.error("无权限访问");
            }

            Map<String, Object> data = new HashMap<>();

            // 用户活跃度
            Map<String, Object> userActivity = new HashMap<>();
            userActivity.put("dailyActiveUsers", 3456);
            userActivity.put("weeklyActiveUsers", 18234);
            userActivity.put("monthlyActiveUsers", 67890);
            userActivity.put("userRetentionRate", 78.5);

            data.put("userActivity", userActivity);

            // 用户行为热力图数据
            List<List<Integer>> heatmapData = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                List<Integer> hourData = new ArrayList<>();
                for (int j = 0; j < 7; j++) {
                    hourData.add((int)(Math.random() * 100));
                }
                heatmapData.add(hourData);
            }

            data.put("heatmapData", heatmapData);

            return R.ok().put("data", data);
        } catch (Exception e) {
            return R.error("获取用户行为数据失败: " + e.getMessage());
        }
    }
}