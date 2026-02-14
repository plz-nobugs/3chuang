<template>
  <div class="main-content">
    <div class="chat-sessions-container">
      <div class="session-title">
        <h3>{{title}}</h3>
        <el-button type="primary" size="small" @click="createNewSession">新建会话</el-button>
      </div>

      <div v-loading="dataListLoading" class="session-list">
        <el-card v-if="dataList.length === 0 && !dataListLoading" class="session-empty">
          <div slot="header">
            <span>暂无会话</span>
          </div>
          <div class="session-empty-content">
            <el-empty description="暂无聊天记录"></el-empty>
            <p>点击下方按钮开始新的对话</p>
            <el-button type="primary" @click="createNewSession">开始新对话</el-button>
          </div>
        </el-card>

        <el-card
            v-for="item in dataList"
            :key="item.id"
            class="session-item"
            :class="{'session-unread': item.unreadCount > 0}"
            @click.native="handleSessionClick(item)">
          <div class="session-header">
            <span class="session-name">{{ getSessionTitle(item) }}</span>
            <el-badge v-if="item.unreadCount > 0" :value="item.unreadCount" class="unread-badge"></el-badge>
          </div>
          <div class="session-content">
            <div class="last-message">{{ item.lastMessage || '暂无消息' }}</div>
            <div class="session-time">{{ formatTime(item.updatetime) }}</div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 新建会话的弹窗 -->
    <el-dialog
        title="选择联系对象"
        :visible.sync="dialogVisible"
        width="40%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :before-close="handleDialogClose">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="联系商家" name="shop">
          <el-form :inline="true" :model="searchForm" class="form-content">
            <el-form-item>
              <el-input v-model="searchForm.keyword" placeholder="请输入商家名称或账号" clearable @keyup.enter.native="searchShops"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchShops">搜索</el-button>
              <el-button type="info" @click="listAllShops">显示全部</el-button>
            </el-form-item>
          </el-form>

          <div v-loading="searchLoading">
            <el-table
                v-if="shops.length > 0"
                :data="shops"
                style="width: 100%"
                :max-height="300">
              <el-table-column prop="dianzhuzhanghao" label="商家账号" min-width="120"></el-table-column>
              <el-table-column prop="dianzhuxingming" label="商家名称" min-width="120" show-overflow-tooltip></el-table-column>
              <el-table-column prop="lianxidianhua" label="联系电话" min-width="120" show-overflow-tooltip></el-table-column>
              <el-table-column label="操作" width="100" fixed="right">
                <template slot-scope="scope">
                  <el-button type="primary" size="mini" @click="createShopSession(scope.row.id)">联系</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div v-if="shops.length === 0 && hasSearched && !searchLoading" class="empty-result">
              <el-empty description="找不到符合条件的商家"></el-empty>
              <p>请尝试其他关键词或联系平台客服</p>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="联系平台" name="admin">
          <div class="admin-contact">
            <p>点击下方按钮联系平台客服</p>
            <el-button type="primary" @click="createAdminSession">联系平台客服</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: true, // 默认为加载中状态
      role: '',
      userId: '',
      shopId: '',
      employeeId: '',
      title: '客服消息',
      dialogVisible: false,
      activeTab: 'shop',
      searchForm: {
        keyword: ''
      },
      shops: [],
      refreshTimer: null,
      hasSearched: false,
      searchLoading: false
    };
  },
  mounted() {
    console.log('=== 组件挂载 ===');
    // 立即执行初始化
    this.immediateInit();
  },
  beforeDestroy() {
    // 清除定时器
    this.clearRefresh();
  },
  methods: {
    immediateInit() {
      console.log('即刻初始化 - 同步获取用户信息并加载数据');
      // 获取当前用户角色和ID
      this.role = this.$storage.get('role');
      console.log('当前角色:', this.role);

      // 根据角色同步获取用户ID
      if (this.role === '顾客' || this.role === 'guke') {
        this.title = '联系客服';

        // 同步获取用户信息
        this.$http({
          url: 'guke/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.userId = data.data.id;
            console.log('用户ID:', this.userId);
            // 立即获取会话列表
            this.fetchDataList();
          } else {
            console.error('获取用户信息失败');
            this.dataListLoading = false;
          }
        }).catch(error => {
          console.error('获取用户信息异常:', error);
          this.dataListLoading = false;
        });
      } else if (this.role === '员工') {
        this.title = '客户消息';
        // 获取员工ID
        this.$http({
          url: 'yuangong/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.employeeId = data.data.id;
            // 立即获取会话列表
            this.fetchDataList();
          } else {
            this.dataListLoading = false;
          }
        }).catch(() => {
          this.dataListLoading = false;
        });
      } else if (this.role === '店主' || this.role === 'dianzhu') {
        this.title = '客户消息';
        // 获取商家ID
        this.$http({
          url: 'dianzhu/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.shopId = data.data.id;
            // 立即获取会话列表
            this.fetchDataList();
          } else {
            this.dataListLoading = false;
          }
        }).catch(() => {
          this.dataListLoading = false;
        });
      } else if (this.role === '管理员' || this.role === 'users') {
        this.title = '客服消息管理';
        // 管理员直接获取会话列表
        this.fetchDataList();
      } else {
        // 默认为用户角色
        this.title = '联系客服';
        // 尝试获取用户信息
        this.$http({
          url: 'guke/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.userId = data.data.id;
            // 立即获取会话列表
            this.fetchDataList();
          } else {
            this.dataListLoading = false;
          }
        }).catch(() => {
          this.dataListLoading = false;
        });
      }

      // 启动定时刷新
      this.startRefresh();
    },
    fetchDataList() {
      console.log('获取会话列表数据 - fetchDataList()');

      let url = '';
      let params = {};

      if (this.role === '顾客' || this.role === 'guke') {
        url = 'chat/user/sessions';
        params = { userId: this.userId };
      } else if (this.role === '员工') {
        url = 'chat/employee/sessions';
        params = { employeeId: this.employeeId };
      } else if (this.role === '店主' || this.role === 'dianzhu') {
        url = 'chat/shop/sessions';
        params = { shopId: this.shopId };
      } else if (this.role === '管理员' || this.role === 'users') {
        url = 'chat/admin/sessions';
      } else {
        // 默认尝试用户会话
        url = 'chat/user/sessions';
        params = { userId: this.userId };
      }

      // 检查参数是否足够
      if (!url || (this.role === '顾客' && !this.userId) ||
          (this.role === '员工' && !this.employeeId) ||
          (this.role === '店主' && !this.shopId)) {
        console.log('参数不足，不进行请求');
        this.dataListLoading = false;
        return;
      }

      console.log('请求会话列表:', url, params);
      this.dataListLoading = true;

      this.$http({
        url: url,
        method: 'get',
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data;
          console.log('获取会话列表成功:', this.dataList);
        } else {
          this.dataList = [];
          console.log('获取会话列表失败或为空');
        }
        this.dataListLoading = false;
      }).catch((error) => {
        console.error('获取会话列表异常:', error);
        this.dataList = [];
        this.dataListLoading = false;
        this.$message.error('获取会话列表失败，请重试');
      });
    },
    startRefresh() {
      console.log('启动定时刷新');
      this.refreshTimer = setInterval(() => {
        if (!this.dialogVisible) { // 对话框打开时不刷新
          console.log('执行定时刷新');
          this.fetchDataList();
        } else {
          console.log('对话框打开中，跳过此次刷新');
        }
      }, 5000);
    },
    clearRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
        this.refreshTimer = null;
      }
    },
    handleDialogClose(done) {
      // 对话框关闭处理
      this.shops = [];
      this.searchForm.keyword = '';
      this.hasSearched = false;
      done();
    },
    handleSessionClick(session) {
      // 导航到聊天详情页
      this.$router.push(`/chat/detail/${session.id}`);
    },
    getSessionTitle(session) {
      // 根据不同角色和会话类型返回合适的标题
      if (this.role === '顾客' || this.role === 'guke') {
        if (session.sessionType === 'USER_SHOP') {
          return `与商家 ${session.shopAccount} 的对话`;
        } else if (session.sessionType === 'USER_ADMIN') {
          return '与平台客服的对话';
        }
      } else if (this.role === '员工' || this.role === '店主' || this.role === 'dianzhu') {
        if (session.sessionType === 'USER_SHOP') {
          return `与用户 ${session.userAccount} 的对话`;
        } else if (session.sessionType === 'SHOP_ADMIN') {
          return '与平台客服的对话';
        }
      } else if (this.role === '管理员' || this.role === 'users') {
        if (session.sessionType === 'USER_ADMIN') {
          return `与用户 ${session.userAccount} 的对话`;
        } else if (session.sessionType === 'SHOP_ADMIN') {
          return `与商家 ${session.shopAccount} 的对话`;
        }
      }
      return '未知对话';
    },
    formatTime(time) {
      if (!time) return '';

      const date = new Date(time);
      const now = new Date();

      // 如果是今天的消息，只显示时间
      if (date.toDateString() === now.toDateString()) {
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      }

      // 如果是昨天的消息
      const yesterday = new Date(now);
      yesterday.setDate(yesterday.getDate() - 1);
      if (date.toDateString() === yesterday.toDateString()) {
        return `昨天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      }

      // 否则显示完整日期
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    createNewSession() {
      console.log('创建新会话');
      this.dialogVisible = true;
      this.shops = []; // 清空之前的搜索结果
      this.searchForm.keyword = ''; // 清空搜索框
      this.hasSearched = false; // 重置搜索状态

      // 预加载所有商家
      this.listAllShops();
    },
    searchShops() {
      console.log('开始搜索商家:', this.searchForm.keyword);

      this.searchLoading = true;
      this.hasSearched = true;

      // 使用直接SQL查询API
      this.$http({
        url: 'chat/direct-search-shops',
        method: 'get',
        params: {
          keyword: this.searchForm.keyword
        }
      }).then(({ data }) => {
        this.searchLoading = false;
        if (data && data.code === 0) {
          console.log('获取到商家列表:', data.data);
          this.shops = data.data || [];

          if (this.shops.length === 0) {
            console.log('未找到匹配的商家');
          }
        } else {
          console.error('获取商家列表失败:', data);
          this.shops = [];
          this.$message.error(data.msg || '获取商家列表失败');
        }
      }).catch(error => {
        this.searchLoading = false;
        console.error('搜索商家请求异常:', error);
        this.$message.error('搜索商家失败，请重试');
      });
    },
    listAllShops() {
      console.log('获取所有商家');
      this.searchLoading = true;
      this.hasSearched = true;

      // 使用直接SQL查询API
      this.$http({
        url: 'chat/direct-search-shops',
        method: 'get'
      }).then(({ data }) => {
        this.searchLoading = false;
        if (data && data.code === 0) {
          console.log('所有商家:', data.data);
          this.shops = data.data || [];

          if (this.shops.length === 0) {
            console.log('系统中没有商家数据');
            this.$message.info('系统中没有商家数据');
          }
        } else {
          console.error('获取商家列表失败:', data);
          this.shops = [];
          this.$message.error(data.msg || '获取商家列表失败');
        }
      }).catch(error => {
        this.searchLoading = false;
        console.error('获取所有商家异常:', error);
        this.$message.error('获取商家列表失败，请重试');
      });
    },
    createShopSession(shopId) {
      console.log('创建与商家的会话:', shopId);
      if (!this.userId) {
        this.$message.error('用户信息不完整，无法创建会话');
        return;
      }

      // 显示加载状态
      const loading = this.$loading({
        lock: true,
        text: '创建会话中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      this.$http({
        url: 'chat/user/create-shop-session',
        method: 'post',
        params: {
          userId: this.userId,
          shopId: shopId
        }
      }).then(({ data }) => {
        loading.close();
        if (data && data.code === 0) {
          this.$message.success('成功创建会话');
          this.dialogVisible = false;
          // 导航到新会话
          this.$router.push(`/chat/detail/${data.data.id}`);
        } else {
          this.$message.error(data.msg || '创建会话失败');
        }
      }).catch((error) => {
        loading.close();
        console.error('创建会话异常:', error);
        this.$message.error('创建会话失败，请重试');
      });
    },
    createAdminSession() {
      console.log('创建与平台的会话');
      if (!this.userId) {
        this.$message.error('用户信息不完整，无法创建会话');
        return;
      }

      // 显示加载状态
      const loading = this.$loading({
        lock: true,
        text: '创建会话中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      this.$http({
        url: 'chat/user/create-admin-session',
        method: 'post',
        params: {
          userId: this.userId
        }
      }).then(({ data }) => {
        loading.close();
        if (data && data.code === 0) {
          this.$message.success('成功创建会话');
          this.dialogVisible = false;
          // 导航到新会话
          this.$router.push(`/chat/detail/${data.data.id}`);
        } else {
          this.$message.error(data.msg || '创建会话失败');
        }
      }).catch((error) => {
        loading.close();
        console.error('创建会话异常:', error);
        this.$message.error('创建会话失败，请重试');
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.chat-sessions-container {
  padding: 20px;

  .session-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
    }
  }

  .session-list {
    min-height: 200px;

    .session-item {
      margin-bottom: 15px;
      cursor: pointer;

      &:hover {
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }

      &.session-unread {
        border-left: 3px solid #409EFF;
      }

      .session-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .session-name {
          font-weight: bold;
          font-size: 16px;
        }
      }

      .session-content {
        display: flex;
        justify-content: space-between;
        color: #999;

        .last-message {
          flex: 1;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

        .session-time {
          margin-left: 10px;
          font-size: 12px;
        }
      }
    }

    .session-empty {
      text-align: center;

      .session-empty-content {
        padding: 20px 0;
      }
    }
  }
}

.admin-contact {
  text-align: center;
  padding: 30px 0;
}

.empty-result {
  margin-top: 20px;
  text-align: center;
  padding: 20px 0;

  p {
    color: #777;
  }
}

.form-content {
  margin-bottom: 15px;
}
</style>