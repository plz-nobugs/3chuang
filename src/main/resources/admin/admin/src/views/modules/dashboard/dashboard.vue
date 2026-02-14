<template>
  <div class="enhanced-dashboard">
    <!-- 顶部导航栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="dashboard-title">
          <i class="el-icon-data-analysis"></i>
          易退智联 - 智能物流数据大屏
        </h1>
        <div class="subtitle">Intelligent Logistics Data Dashboard</div>
      </div>
      <div class="header-right">
        <div class="time-display">{{ currentTime }}</div>
        <div class="weather-info">
          <i class="el-icon-sunny"></i>
          <span>青岛 25°C</span>
        </div>
        <el-button
            type="primary"
            icon="el-icon-refresh"
            size="small"
            @click="refreshData"
            :loading="loading"
            class="refresh-btn"
        >
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片区域 -->
    <div class="metrics-container">
      <div class="metrics-row">
        <div
            class="metric-card primary-card"
            v-for="(metric, index) in coreMetrics"
            :key="index"
            :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <div class="card-header">
            <div class="card-icon" :class="metric.iconClass">
              <i :class="metric.icon"></i>
            </div>
            <div class="card-trend" :class="metric.trendClass">
              <i :class="metric.trendIcon"></i>
              <span>{{ metric.trend }}</span>
            </div>
          </div>
          <div class="card-body">
            <div class="metric-value">
              <span class="value-number">{{ formatNumber(metric.value) }}</span>
              <span class="value-unit">{{ metric.unit }}</span>
            </div>
            <div class="metric-label">{{ metric.label }}</div>
            <div class="metric-description">{{ metric.description }}</div>
          </div>
          <div class="card-footer">
            <div class="progress-bar">
              <div
                  class="progress-fill"
                  :style="{ width: metric.progress + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表容器 -->
    <div class="charts-grid">
      <!-- 第一行：趋势分析和快递柜状态 -->
      <div class="chart-row">
        <div class="chart-container large-chart">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-trend-charts"></i>
              退货处理趋势分析
            </h3>
            <div class="chart-controls">
              <el-radio-group v-model="trendPeriod" size="mini" @change="updateTrendChart">
                <el-radio-button label="day">今日</el-radio-button>
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-content">
            <div id="trendChart" class="chart-element"></div>
          </div>
        </div>

        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-box"></i>
              智能快递柜状态
            </h3>
            <div class="status-summary">
              <span class="summary-item">
                <span class="dot active"></span>
                活跃 {{ lockerStats.active }}
              </span>
              <span class="summary-item">
                <span class="dot idle"></span>
                空闲 {{ lockerStats.idle }}
              </span>
            </div>
          </div>
          <div class="chart-content">
            <div id="lockerChart" class="chart-element"></div>
          </div>
        </div>
      </div>

      <!-- 第二行：地区分布和转售分析 -->
      <div class="chart-row">
        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-map-location"></i>
              区域业务分布
            </h3>
            <div class="region-stats">
              <span>覆盖 {{ regionData.length }} 个区域</span>
            </div>
          </div>
          <div class="chart-content">
            <div id="regionChart" class="chart-element"></div>
          </div>
        </div>

        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-goods"></i>
              商品转售效果
            </h3>
            <div class="resale-summary">
              总转售率: <strong class="highlight">{{ avgResaleRate }}%</strong>
            </div>
          </div>
          <div class="chart-content">
            <div id="resaleChart" class="chart-element"></div>
          </div>
        </div>
      </div>

      <!-- 第三行：环保效益和实时监控 -->
      <div class="chart-row">
        <div class="chart-container large-chart">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-sunrise"></i>
              绿色环保效益统计
            </h3>
            <div class="env-highlights">
              <div class="highlight-item">
                <span class="highlight-value">{{ environmentStats.carbonReduction }}t</span>
                <span class="highlight-label">碳减排</span>
              </div>
              <div class="highlight-item">
                <span class="highlight-value">{{ environmentStats.energySaved }}kWh</span>
                <span class="highlight-label">节能</span>
              </div>
            </div>
          </div>
          <div class="chart-content">
            <div id="environmentChart" class="chart-element"></div>
          </div>
        </div>

        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-monitor"></i>
              实时处理监控
            </h3>
            <div class="real-time-indicator">
              <span class="status-dot pulse"></span>
              <span>实时更新</span>
            </div>
          </div>
          <div class="chart-content">
            <div class="real-time-list">
              <div
                  class="real-time-item"
                  v-for="(record, index) in recentRecords"
                  :key="record.orderId"
                  :style="{ animationDelay: `${index * 0.1}s` }"
              >
                <div class="item-left">
                  <div class="order-id">{{ record.orderId }}</div>
                  <div class="order-type">{{ record.type }}</div>
                </div>
                <div class="item-center">
                  <div class="location">{{ record.location }}</div>
                  <div class="time">{{ record.time }}</div>
                </div>
                <div class="item-right">
                  <div class="status-badge" :class="getStatusClass(record.status)">
                    {{ record.status }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 第四行：系统健康度和营收分析 -->
      <div class="chart-row">
        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-cpu"></i>
              系统健康监控
            </h3>
            <div class="health-indicator">
              <span class="health-status good">系统正常</span>
            </div>
          </div>
          <div class="chart-content">
            <div class="health-metrics">
              <div class="health-item">
                <div class="health-label">CPU使用率</div>
                <div class="health-progress">
                  <div class="progress-bar">
                    <div class="progress-fill cpu" :style="{ width: systemHealth.cpuUsage + '%' }"></div>
                  </div>
                  <span class="progress-text">{{ systemHealth.cpuUsage }}%</span>
                </div>
              </div>
              <div class="health-item">
                <div class="health-label">内存使用率</div>
                <div class="health-progress">
                  <div class="progress-bar">
                    <div class="progress-fill memory" :style="{ width: systemHealth.memoryUsage + '%' }"></div>
                  </div>
                  <span class="progress-text">{{ systemHealth.memoryUsage }}%</span>
                </div>
              </div>
              <div class="health-item">
                <div class="health-label">磁盘使用率</div>
                <div class="health-progress">
                  <div class="progress-bar">
                    <div class="progress-fill disk" :style="{ width: systemHealth.diskUsage + '%' }"></div>
                  </div>
                  <span class="progress-text">{{ systemHealth.diskUsage }}%</span>
                </div>
              </div>
              <div class="health-item">
                <div class="health-label">API响应时间</div>
                <div class="health-value">{{ systemHealth.apiResponseTime }}ms</div>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-container">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-money"></i>
              营收分析概览
            </h3>
            <div class="revenue-summary">
              <span>月增长 <strong class="positive">+{{ revenueGrowth }}%</strong></span>
            </div>
          </div>
          <div class="chart-content">
            <div id="revenueChart" class="chart-element"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EnhancedDashboard',
  data() {
    return {
      loading: false,
      currentTime: '',
      trendPeriod: 'day',

      // 核心指标数据
      coreMetrics: [
        {
          label: '累计订单',
          value: 15847,
          unit: '',
          trend: '+12.5%',
          trendClass: 'trend-up',
          trendIcon: 'el-icon-top',
          icon: 'el-icon-box',
          iconClass: 'icon-primary',
          description: '比昨日增长189单',
          progress: 85
        },
        {
          label: '活跃快递柜',
          value: 425,
          unit: '台',
          trend: '+5.8%',
          trendClass: 'trend-up',
          trendIcon: 'el-icon-top',
          icon: 'el-icon-office-building',
          iconClass: 'icon-success',
          description: '使用率 83.7%',
          progress: 84
        },
        {
          label: '处理成功率',
          value: 92.8,
          unit: '%',
          trend: '+2.1%',
          trendClass: 'trend-up',
          trendIcon: 'el-icon-top',
          icon: 'el-icon-circle-check',
          iconClass: 'icon-warning',
          description: '持续优化提升',
          progress: 93
        },
        {
          label: '碳排放减少',
          value: 3.67,
          unit: '吨',
          trend: '+18.2%',
          trendClass: 'trend-up',
          trendIcon: 'el-icon-top',
          icon: 'el-icon-sunrise',
          iconClass: 'icon-info',
          description: '环保贡献持续增长',
          progress: 76
        }
      ],

      // 快递柜统计
      lockerStats: {
        active: 342,
        idle: 68,
        maintenance: 15
      },

      // 地区数据
      regionData: [],

      // 环保统计
      environmentStats: {
        carbonReduction: 18.67,
        energySaved: 2345
      },

      // 系统健康
      systemHealth: {
        cpuUsage: 23.4,
        memoryUsage: 67.8,
        diskUsage: 45.2,
        apiResponseTime: 156
      },

      // 实时记录
      recentRecords: [],

      // 图表数据
      trendData: {},
      lockerData: {},
      resaleData: {},
      revenueData: {},

      // 计算属性数据
      avgResaleRate: 88.5,
      revenueGrowth: 18.5,

      // 定时器
      timeTimer: null,
      dataTimer: null
    }
  },

  mounted() {
    this.initDashboard();
    this.startTimeUpdate();
    this.startDataRefresh();
  },

  beforeDestroy() {
    if (this.timeTimer) clearInterval(this.timeTimer);
    if (this.dataTimer) clearInterval(this.dataTimer);
  },

  methods: {
    async initDashboard() {
      this.loading = true;
      try {
        await this.loadAllData();
        await this.loadECharts();
        setTimeout(() => {
          this.initAllCharts();
        }, 100);
      } catch (error) {
        console.error('初始化失败:', error);
        this.$message.error('数据加载失败');
      } finally {
        this.loading = false;
      }
    },

    async loadAllData() {
      try {
        // 并行加载所有数据
        const [overview, trend, locker, resale, env, realTime, revenue] = await Promise.all([
          this.$http.get('dashboard/overview'),
          this.$http.get('dashboard/returnTrend'),
          this.$http.get('dashboard/lockerStatus'),
          this.$http.get('dashboard/resaleData'),
          this.$http.get('dashboard/environmentalData'),
          this.$http.get('dashboard/realTimeData'),
          this.$http.get('dashboard/revenueAnalysis')
        ]);

        // 处理概览数据
        if (overview?.data?.code === 0) {
          const basicStats = overview.data.data.basicStats;
          this.updateCoreMetrics(basicStats);
        }

        // 处理趋势数据
        if (trend?.data?.code === 0) {
          this.trendData = trend.data.data;
        }

        // 处理快递柜数据
        if (locker?.data?.code === 0) {
          this.lockerData = locker.data.data;
          this.regionData = locker.data.data.regionData || [];
          this.lockerStats = {
            active: locker.data.data.statusData?.[0]?.value || 0,
            idle: locker.data.data.statusData?.[1]?.value || 0,
            maintenance: locker.data.data.statusData?.[2]?.value || 0
          };
        }

        // 处理转售数据
        if (resale?.data?.code === 0) {
          this.resaleData = resale.data.data;
          this.avgResaleRate = this.calculateAvgResaleRate(resale.data.data.categoryData);
        }

        // 处理环保数据
        if (env?.data?.code === 0) {
          this.environmentStats = env.data.data.envStats;
        }

        // 处理实时数据
        if (realTime?.data?.code === 0) {
          this.recentRecords = realTime.data.data.recentRecords || [];
          this.systemHealth = realTime.data.data.systemHealth || this.systemHealth;
        }

        // 处理营收数据
        if (revenue?.data?.code === 0) {
          this.revenueData = revenue.data.data;
          this.revenueGrowth = revenue.data.data.revenueOverview?.growthRate || 0;
        }

      } catch (error) {
        console.error('数据加载失败:', error);
        throw error;
      }
    },

    loadECharts() {
      return new Promise((resolve) => {
        if (window.echarts) {
          resolve();
          return;
        }
        const script = document.createElement('script');
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/echarts/5.4.3/echarts.min.js';
        script.onload = resolve;
        document.head.appendChild(script);
      });
    },

    initAllCharts() {
      if (!window.echarts) return;

      this.initTrendChart();
      this.initLockerChart();
      this.initRegionChart();
      this.initResaleChart();
      this.initEnvironmentChart();
      this.initRevenueChart();
    },

    initTrendChart() {
      const chart = window.echarts.init(document.getElementById('trendChart'));

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: '#409EFF',
          textStyle: { color: '#fff' }
        },
        legend: {
          data: ['退货量', '处理成功', '成功率'],
          textStyle: { color: '#606266' },
          top: 20
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.trendData.dates || [],
          axisLine: { lineStyle: { color: '#DCDFE6' } },
          axisLabel: { color: '#909399' }
        },
        yAxis: [
          {
            type: 'value',
            name: '订单数',
            axisLine: { lineStyle: { color: '#DCDFE6' } },
            axisLabel: { color: '#909399' },
            splitLine: { lineStyle: { color: '#F2F3F5', type: 'dashed' } }
          },
          {
            type: 'value',
            name: '成功率(%)',
            axisLine: { lineStyle: { color: '#DCDFE6' } },
            axisLabel: { color: '#909399' }
          }
        ],
        series: [
          {
            name: '退货量',
            type: 'bar',
            data: this.trendData.returnCounts || [],
            itemStyle: {
              color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#79BBFF' }
              ])
            },
            barWidth: '40%'
          },
          {
            name: '处理成功',
            type: 'bar',
            data: this.trendData.successCounts || [],
            itemStyle: {
              color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#67C23A' },
                { offset: 1, color: '#95D475' }
              ])
            },
            barWidth: '40%'
          },
          {
            name: '成功率',
            type: 'line',
            yAxisIndex: 1,
            data: this.trendData.successRates || [],
            lineStyle: { color: '#E6A23C', width: 3 },
            itemStyle: { color: '#E6A23C' },
            smooth: true
          }
        ]
      };

      chart.setOption(option);
    },

    initLockerChart() {
      const chart = window.echarts.init(document.getElementById('lockerChart'));

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'horizontal',
          bottom: 10,
          left: 'center',
          textStyle: { color: '#606266' }
        },
        series: [
          {
            name: '快递柜状态',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '45%'],
            data: this.lockerData.statusData || [],
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}\n{c}台'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ],
        color: ['#67C23A', '#409EFF', '#F56C6C']
      };

      chart.setOption(option);
    },

    initRegionChart() {
      const chart = window.echarts.init(document.getElementById('regionChart'));

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}台<br/>增长率: {d}%'
        },
        series: [
          {
            name: '区域分布',
            type: 'pie',
            radius: '70%',
            center: ['50%', '50%'],
            data: this.regionData.map(item => ({
              name: item.name,
              value: item.value
            })),
            roseType: 'area',
            itemStyle: {
              borderRadius: 5
            },
            label: {
              show: true,
              formatter: '{b}\n{c}台'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ],
        color: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7']
      };

      chart.setOption(option);
    },

    initResaleChart() {
      const chart = window.echarts.init(document.getElementById('resaleChart'));

      const categories = this.resaleData.categoryData?.map(item => item.name) || [];
      const successRates = this.resaleData.categoryData?.map(item => item.rate) || [];
      const revenues = this.resaleData.categoryData?.map(item => item.revenue) || [];

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        legend: {
          data: ['转售率', '营收'],
          textStyle: { color: '#606266' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            color: '#909399',
            rotate: 30
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '转售率(%)',
            axisLabel: { color: '#909399' }
          },
          {
            type: 'value',
            name: '营收(元)',
            axisLabel: { color: '#909399' }
          }
        ],
        series: [
          {
            name: '转售率',
            type: 'bar',
            data: successRates,
            itemStyle: {
              color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#79BBFF' }
              ])
            }
          },
          {
            name: '营收',
            type: 'line',
            yAxisIndex: 1,
            data: revenues,
            lineStyle: { color: '#67C23A', width: 3 },
            itemStyle: { color: '#67C23A' }
          }
        ]
      };

      chart.setOption(option);
    },

    initEnvironmentChart() {
      const chart = window.echarts.init(document.getElementById('environmentChart'));

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['碳减排(吨)', '包装节省(千个)', '节能(kWh)'],
          textStyle: { color: '#606266' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月'],
          axisLabel: { color: '#909399' }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#909399' }
        },
        series: [
          {
            name: '碳减排(吨)',
            type: 'line',
            data: [12.3, 14.1, 15.8, 17.2, 18.67],
            smooth: true,
            itemStyle: { color: '#67C23A' },
            areaStyle: {
              color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
              ])
            }
          },
          {
            name: '包装节省(千个)',
            type: 'line',
            data: [7.23, 8.57, 9.46, 11.12, 12.46],
            smooth: true,
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '节能(kWh)',
            type: 'line',
            data: [1.46, 1.68, 1.92, 2.13, 2.35],
            smooth: true,
            itemStyle: { color: '#E6A23C' }
          }
        ]
      };

      chart.setOption(option);
    },

    initRevenueChart() {
      const chart = window.echarts.init(document.getElementById('revenueChart'));

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['营收', '成本'],
          textStyle: { color: '#606266' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月'],
          axisLabel: { color: '#909399' }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#909399',
            formatter: function(value) {
              return (value / 1000).toFixed(0) + 'k';
            }
          }
        },
        series: [
          {
            name: '营收',
            type: 'bar',
            data: [156789, 187234, 198765, 212456, 234567],
            itemStyle: {
              color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#67C23A' },
                { offset: 1, color: '#95D475' }
              ])
            }
          },
          {
            name: '成本',
            type: 'bar',
            data: [98567, 117234, 124567, 133789, 145678],
            itemStyle: {
              color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#F56C6C' },
                { offset: 1, color: '#F89898' }
              ])
            }
          }
        ]
      };

      chart.setOption(option);
    },

    updateCoreMetrics(basicStats) {
      if (!basicStats) return;

      this.coreMetrics[0].value = basicStats.totalOrders || 0;
      this.coreMetrics[1].value = basicStats.activeLockers || 0;
      this.coreMetrics[2].value = basicStats.successRate || 0;
      this.coreMetrics[3].value = basicStats.carbonReduction || 0;
    },

    calculateAvgResaleRate(categoryData) {
      if (!categoryData || categoryData.length === 0) return 0;
      const totalRate = categoryData.reduce((sum, item) => sum + (item.rate || 0), 0);
      return Math.round((totalRate / categoryData.length) * 10) / 10;
    },

    updateTrendChart() {
      // 根据选择的时间段更新趋势图表
      this.initTrendChart();
    },

    formatNumber(value) {
      if (!value) return '0';
      return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },

    getStatusClass(status) {
      const statusMap = {
        '检测完成': 'status-completed',
        '转售成功': 'status-success',
        '等待审核': 'status-pending',
        '处理中': 'status-processing',
        '已转售': 'status-sold',
        '检测中': 'status-checking',
        '配送中': 'status-shipping',
        '已完成': 'status-finished'
      };
      return statusMap[status] || 'status-default';
    },

    startTimeUpdate() {
      this.updateTime();
      this.timeTimer = setInterval(this.updateTime, 1000);
    },

    updateTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    startDataRefresh() {
      this.dataTimer = setInterval(() => {
        this.refreshData();
      }, 30000); // 30秒刷新一次
    },

    async refreshData() {
      try {
        await this.loadAllData();
        this.$message.success('数据已更新');
      } catch (error) {
        console.error('刷新数据失败:', error);
      }
    }
  }
}
</script>

<style scoped>
/* 主容器样式 */
.enhanced-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  color: #303133;
  position: relative;
  overflow-x: hidden;
}

.enhanced-dashboard::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
      radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.3) 0%, transparent 50%);
  pointer-events: none;
}

/* 顶部导航栏 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 15px;
  padding: 20px 30px;
  margin-bottom: 25px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.header-left .dashboard-title {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-left .subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin-top: 5px;
  font-weight: 300;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.time-display {
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  font-family: 'Courier New', monospace;
}

.weather-info {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.refresh-btn {
  border-radius: 20px !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  background: rgba(255, 255, 255, 0.1) !important;
  color: #fff !important;
}

/* 核心指标卡片 */
.metrics-container {
  margin-bottom: 25px;
  position: relative;
  z-index: 1;
}

.metrics-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.metric-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 25px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  animation: slideInUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(30px);
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 15px 15px 0 0;
}

.metric-card:hover {
  transform: translateY(-10px);
  box-shadow:
      0 20px 40px rgba(0, 0, 0, 0.15),
      0 5px 15px rgba(0, 0, 0, 0.08);
}

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.icon-primary { background: linear-gradient(135deg, #409EFF, #79BBFF); }
.icon-success { background: linear-gradient(135deg, #67C23A, #95D475); }
.icon-warning { background: linear-gradient(135deg, #E6A23C, #F7CD8A); }
.icon-info { background: linear-gradient(135deg, #909399, #C0C4CC); }

.card-trend {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  font-weight: 600;
  padding: 5px 10px;
  border-radius: 20px;
}

.trend-up {
  color: #67C23A;
  background: rgba(103, 194, 58, 0.1);
}

.trend-down {
  color: #F56C6C;
  background: rgba(245, 108, 108, 0.1);
}

.card-body {
  margin-bottom: 20px;
}

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 8px;
}

.value-number {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.value-unit {
  font-size: 18px;
  color: #909399;
  font-weight: 500;
}

.metric-label {
  font-size: 16px;
  color: #606266;
  font-weight: 500;
  margin-bottom: 5px;
}

.metric-description {
  font-size: 12px;
  color: #909399;
}

.card-footer {
  margin-top: 20px;
}

.progress-bar {
  height: 6px;
  background: #F2F3F5;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 3px;
  transition: width 1s ease;
}

/* 图表网格 */
.charts-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.chart-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-row:nth-child(2) {
  grid-template-columns: 1fr 1fr;
}

.chart-row:nth-child(4) {
  grid-template-columns: 1fr 1fr;
}

.chart-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 25px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.chart-container:hover {
  transform: translateY(-5px);
  box-shadow:
      0 15px 35px rgba(0, 0, 0, 0.15),
      0 5px 15px rgba(0, 0, 0, 0.08);
}

.large-chart {
  grid-column: span 1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #F2F3F5;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-content {
  position: relative;
}

.chart-element {
  height: 350px;
  width: 100%;
}

/* 图表控件样式 */
.chart-controls .el-radio-group {
  background: #F2F3F5;
  border-radius: 20px;
  padding: 2px;
}

.status-summary {
  display: flex;
  gap: 15px;
  font-size: 12px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.active { background: #67C23A; }
.dot.idle { background: #409EFF; }

.region-stats,
.resale-summary,
.revenue-summary {
  font-size: 14px;
  color: #606266;
}

.highlight {
  color: #67C23A;
  font-weight: 600;
}

.positive {
  color: #67C23A;
}

/* 环保效益高亮 */
.env-highlights {
  display: flex;
  gap: 20px;
}

.highlight-item {
  text-align: center;
}

.highlight-value {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #67C23A;
}

.highlight-label {
  font-size: 12px;
  color: #909399;
}

/* 实时监控 */
.real-time-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #67C23A;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67C23A;
}

.pulse {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(103, 194, 58, 0); }
  100% { box-shadow: 0 0 0 0 rgba(103, 194, 58, 0); }
}

.real-time-list {
  max-height: 350px;
  overflow-y: auto;
  padding-right: 10px;
}

.real-time-item {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #F8F9FA, #F2F3F5);
  border-radius: 10px;
  border-left: 4px solid #409EFF;
  transition: all 0.3s ease;
  animation: slideInRight 0.5s ease-out forwards;
  opacity: 0;
  transform: translateX(30px);
}

.real-time-item:hover {
  transform: translateX(5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

@keyframes slideInRight {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.item-left {
  flex: 0 0 120px;
}

.order-id {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.order-type {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.item-center {
  flex: 1;
  padding: 0 15px;
}

.location {
  font-size: 13px;
  color: #606266;
}

.time {
  font-size: 11px;
  color: #C0C4CC;
  margin-top: 2px;
}

.item-right {
  flex: 0 0 80px;
  text-align: right;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  color: #fff;
}

.status-completed { background: #67C23A; }
.status-success { background: #409EFF; }
.status-pending { background: #E6A23C; }
.status-processing { background: #909399; }
.status-sold { background: #67C23A; }
.status-checking { background: #F56C6C; }
.status-shipping { background: #E6A23C; }
.status-finished { background: #67C23A; }

/* 系统健康监控 */
.health-indicator .health-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.health-status.good {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.health-metrics {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.health-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.health-label {
  font-size: 14px;
  color: #606266;
  flex: 0 0 80px;
}

.health-progress {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 15px;
}

.health-progress .progress-bar {
  flex: 1;
  height: 8px;
  background: #F2F3F5;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill.cpu { background: linear-gradient(90deg, #67C23A, #95D475); }
.progress-fill.memory { background: linear-gradient(90deg, #E6A23C, #F7CD8A); }
.progress-fill.disk { background: linear-gradient(90deg, #409EFF, #79BBFF); }

.progress-text {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
  min-width: 40px;
  text-align: right;
}

.health-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chart-row {
    grid-template-columns: 1fr;
  }

  .metrics-row {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .enhanced-dashboard {
    padding: 10px;
  }

  .dashboard-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .header-right {
    flex-direction: column;
    gap: 10px;
  }

  .metrics-row {
    grid-template-columns: 1fr;
  }

  .chart-element {
    height: 250px;
  }

  .real-time-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .item-left,
  .item-center,
  .item-right {
    flex: none;
    width: 100%;
  }

  .item-right {
    text-align: left;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>