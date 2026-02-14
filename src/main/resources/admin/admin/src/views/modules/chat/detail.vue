<template>
  <div class="chat-detail-container">
    <div class="chat-header">
      <div class="chat-back">
        <el-button icon="el-icon-arrow-left" @click="backToSessions">返回</el-button>
      </div>
      <div class="chat-title">{{ chatTitle }}</div>
      <div class="chat-actions">
        <el-button type="danger" size="small" @click="exitSession">退出对话</el-button>
      </div>
    </div>

    <div class="chat-body" ref="chatBody">
      <div v-if="messages.length === 0" class="empty-messages">
        <el-empty description="暂无消息"></el-empty>
        <p>发送第一条消息开始对话吧！</p>
      </div>

      <template v-else>
        <div v-for="(message, index) in messages" :key="index" class="message-container" :class="getMessageClass(message)">
          <div class="message-info">
            <span class="sender-name">{{ message.senderName }}</span>
            <span class="message-time">{{ formatTime(message.addtime) }}</span>
          </div>
          <div class="message-bubble">{{ message.content }}</div>
        </div>
      </template>
    </div>

    <div class="chat-input">
      <el-input
          type="textarea"
          :rows="3"
          placeholder="输入消息内容..."
          v-model="inputMessage"
          @keyup.ctrl.enter.native="sendMessage"
          resize="none">
      </el-input>
      <div class="input-actions">
        <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      sessionId: null,
      chatTitle: '',
      messages: [],
      inputMessage: '',
      role: '',
      userId: null,
      shopId: null,
      employeeId: null,
      adminId: null,
      sessionInfo: null,
      timer: null,
      pageSize: 50,  // 一次加载的消息数量
      page: 1        // 当前页码
    };
  },
  created() {
    this.sessionId = this.$route.params.id;
    this.init();
  },
  mounted() {
    // 启动定时刷新
    this.startRefreshTimer();
  },
  beforeDestroy() {
    // 销毁定时器
    this.clearRefreshTimer();
  },
  methods: {
    init() {
      // 获取当前用户角色和ID
      this.role = this.$storage.get('role');

      // 获取会话信息
      this.getSessionInfo();

      if (this.role === '顾客') {
        // 获取用户ID
        this.$http({
          url: 'guke/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.userId = data.data.id;
            // 标记消息为已读
            this.markMessagesAsRead();
            // 获取消息列表
            this.getMessages();
          }
        });
      } else if (this.role === '员工') {
        // 获取员工ID
        this.$http({
          url: 'yuangong/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.employeeId = data.data.id;
            // 标记消息为已读
            this.markMessagesAsRead();
            // 获取消息列表
            this.getMessages();
          }
        });
      } else if (this.role === '店主') {
        // 获取商家ID
        this.$http({
          url: 'dianzhu/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.shopId = data.data.id;
            // 标记消息为已读
            this.markMessagesAsRead();
            // 获取消息列表
            this.getMessages();
          }
        });
      } else if (this.role === '管理员') {
        // 获取管理员ID
        this.$http({
          url: 'users/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.adminId = data.data.id;
            // 标记消息为已读
            this.markMessagesAsRead();
            // 获取消息列表
            this.getMessages();
          }
        });
      }
    },
    getSessionInfo() {
      this.$http({
        url: `chat/sessions`,
        method: 'get',
        params: {
          sessionId: this.sessionId
        }
      }).then(({ data }) => {
        if (data && data.code === 0 && data.data.length > 0) {
          this.sessionInfo = data.data.find(item => item.id == this.sessionId);
          this.setChatTitle();
        }
      });
    },
    setChatTitle() {
      if (!this.sessionInfo) return;

      if (this.role === '顾客') {
        if (this.sessionInfo.sessionType === 'USER_SHOP') {
          this.chatTitle = `与商家 ${this.sessionInfo.shopAccount} 的对话`;
        } else if (this.sessionInfo.sessionType === 'USER_ADMIN') {
          this.chatTitle = '与平台客服的对话';
        }
      } else if (this.role === '员工' || this.role === '店主') {
        if (this.sessionInfo.sessionType === 'USER_SHOP') {
          this.chatTitle = `与用户 ${this.sessionInfo.userAccount} 的对话`;
        } else if (this.sessionInfo.sessionType === 'SHOP_ADMIN') {
          this.chatTitle = '与平台客服的对话';
        }
      } else if (this.role === '管理员') {
        if (this.sessionInfo.sessionType === 'USER_ADMIN') {
          this.chatTitle = `与用户 ${this.sessionInfo.userAccount} 的对话`;
        } else if (this.sessionInfo.sessionType === 'SHOP_ADMIN') {
          this.chatTitle = `与商家 ${this.sessionInfo.shopAccount} 的对话`;
        }
      }
    },
    getMessages() {
      this.$http({
        url: 'chat/messages',
        method: 'get',
        params: {
          sessionId: this.sessionId,
          page: this.page,
          limit: this.pageSize
        }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.messages = data.data.list;
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        }
      });
    },
    markMessagesAsRead() {
      let url = '';
      let params = { sessionId: this.sessionId };

      if (this.role === '顾客') {
        url = 'chat/user/read';
        params.userId = this.userId;
      } else if (this.role === '员工') {
        url = 'chat/employee/read';
        params.employeeId = this.employeeId;
      } else if (this.role === '店主') {
        url = 'chat/shop/read';
        params.shopId = this.shopId;
      } else if (this.role === '管理员') {
        url = 'chat/admin/read';
        params.adminId = this.adminId;
      }

      this.$http({
        url: url,
        method: 'post',
        params: params
      });
    },
    sendMessage() {
      if (!this.inputMessage.trim()) {
        return;
      }

      let url = '';
      let params = {
        sessionId: this.sessionId,
        content: this.inputMessage
      };

      if (this.role === '顾客') {
        url = 'chat/user/send';
        params.userId = this.userId;
      } else if (this.role === '员工') {
        url = 'chat/employee/send';
        params.employeeId = this.employeeId;
      } else if (this.role === '店主') {
        url = 'chat/shop/send';
        params.shopId = this.shopId;
      } else if (this.role === '管理员') {
        url = 'chat/admin/send';
        params.adminId = this.adminId;
      }

      this.$http({
        url: url,
        method: 'post',
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.inputMessage = '';
          // 重新获取消息
          this.getMessages();
        } else {
          this.$message.error(data.msg || '发送失败');
        }
      });
    },
    getMessageClass(message) {
      let senderType = '';

      if (this.role === '顾客' && message.senderType === 'USER') {
        senderType = 'self';
      } else if ((this.role === '员工' && message.senderType === 'EMPLOYEE') ||
          (this.role === '店主' && message.senderType === 'SHOP')) {
        senderType = 'self';
      } else if (this.role === '管理员' && message.senderType === 'ADMIN') {
        senderType = 'self';
      } else {
        senderType = 'other';
      }

      return senderType;
    },
    formatTime(time) {
      if (!time) return '';

      const date = new Date(time);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    scrollToBottom() {
      const container = this.$refs.chatBody;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },
    startRefreshTimer() {
      // 每5秒刷新一次消息
      this.timer = setInterval(() => {
        this.getMessages();
        this.markMessagesAsRead();
      }, 5000);
    },
    clearRefreshTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    backToSessions() {
      this.$router.push('/chat/sessions');
    },
    exitSession() {
      this.$confirm('确定要退出此对话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 实际上我们不会真的删除会话，只是返回会话列表
        this.$router.push('/chat/sessions');
      }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.chat-detail-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);

  .chat-header {
    display: flex;
    align-items: center;
    padding: 10px 20px;
    border-bottom: 1px solid #eee;

    .chat-back {
      margin-right: 20px;
    }

    .chat-title {
      flex: 1;
      font-size: 18px;
      font-weight: bold;
    }
  }

  .chat-body {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background-color: #f5f5f5;

    .empty-messages {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;

      p {
        margin-top: 20px;
        color: #999;
      }
    }

    .message-container {
      margin-bottom: 20px;
      max-width: 70%;

      &.self {
        margin-left: auto;

        .message-info {
          text-align: right;
        }

        .message-bubble {
          background-color: #409EFF;
          color: white;
          border-radius: 10px 10px 0 10px;
        }
      }

      &.other {
        margin-right: auto;

        .message-bubble {
          background-color: white;
          border-radius: 10px 10px 10px 0;
        }
      }

      .message-info {
        margin-bottom: 5px;

        .sender-name {
          font-weight: bold;
          margin-right: 10px;
        }

        .message-time {
          font-size: 12px;
          color: #999;
        }
      }

      .message-bubble {
        padding: 10px 15px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        word-break: break-all;
      }
    }
  }

  .chat-input {
    padding: 15px;
    border-top: 1px solid #eee;
    background-color: white;

    .input-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 10px;
    }
  }
}
</style>