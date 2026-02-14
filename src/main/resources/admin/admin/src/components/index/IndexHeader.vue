<template>
  <div class="navbar" :style="{backgroundColor: heads.headBgColor, height: heads.headHeight, boxShadow: heads.headBoxShadow, lineHeight: heads.headHeight}">
    <div class="title-menu" :style="{justifyContent: heads.headTitleStyle === '1' ? 'flex-start' : 'center'}">
      <el-image v-if="heads.headTitleImg" class="title-img" :style="{width: heads.headTitleImgWidth, height: heads.headTitleImgHeight, boxShadow: heads.headTitleImgBoxShadow, borderRadius: heads.headTitleImgBorderRadius}" :src="heads.headTitleImgUrl" fit="cover"></el-image>
      <div class="title-name" :style="{fontSize: heads.headFontSize}">
        <span class="gradient-text">“易退智联”智能退货物流系统</span>
      </div>
    </div>
    <div class="right-menu">
      <div class="user-info" :style="{color: heads.headUserInfoFontColor, fontSize: heads.headUserInfoFontSize}">{{ this.$storage.get('role') }} {{ this.$storage.get('adminName') }}</div>
      <div class="logout" :style="{color: heads.headLogoutFontColor, fontSize: heads.headLogoutFontSize}" @click="onLogout">退出登录</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: false,
      ruleForm: {},
      user: {},
      heads: {
        "headLogoutFontHoverColor": "#fff",
        "headFontSize": "20px",
        "headUserInfoFontColor": "rgba(255, 255, 255, 1)",
        "headBoxShadow": "0 4px 8px rgba(13, 142, 235, 1)",
        "headTitleImgHeight": "44px",
        "headLogoutFontHoverBgColor": "rgba(213, 17, 17, 1)",
        "headFontColor": "rgba(255, 255, 255, 1)",
        "headTitleImg": false,
        "headHeight": "80px",
        "headTitleImgBorderRadius": "22px",
        "headTitleImgUrl": "",
        "headBgColor": "#A1D9B1", // Adjusted lighter green
        "headTitleImgBoxShadow": "0 1px 6px #444",
        "headLogoutFontColor": "rgba(255, 255, 255, 1)",
        "headUserInfoFontSize": "16px",
        "headTitleImgWidth": "44px",
        "headTitleStyle": "1",
        "headLogoutFontSize": "16px",
      }
    };
  },
  created() {
    this.setHeaderStyle();
  },
  mounted() {
    let sessionTable = this.$storage.get("sessionTable");
    this.$http({
      url: sessionTable + '/session',
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.user = data.data;
      } else {
        let message = this.$message;
        message.error(data.msg);
      }
    });
  },
  methods: {
    onLogout() {
      let storage = this.$storage;
      let router = this.$router;
      storage.clear();
      router.replace({
        name: "login"
      });
    },
    onIndexTap() {
      window.location.href = `${this.$base.indexUrl}`;
    },
    setHeaderStyle() {
      this.$nextTick(() => {
        document.querySelectorAll('.navbar .right-menu .logout').forEach(el => {
          el.addEventListener("mouseenter", e => {
            e.stopPropagation();
            el.style.backgroundColor = this.heads.headLogoutFontHoverBgColor;
            el.style.color = this.heads.headLogoutFontHoverColor;
          });
          el.addEventListener("mouseleave", e => {
            e.stopPropagation();
            el.style.backgroundColor = "transparent";
            el.style.color = this.heads.headLogoutFontColor;
          });
        });
      });
    },
  }
};
</script>

<style lang="scss" scoped>
.navbar {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  background-color: #A1D9B1; /* Adjusted lighter green */
  position: relative;
  z-index: 111;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.right-menu {
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .user-info {
    color: rgba(255, 255, 255, 1);
    font-size: 16px;
    padding-right: 12px;
  }

  .logout {
    color: rgba(255, 255, 255, 1);
    font-size: 16px;
    padding: 0 12px;
    cursor: pointer;
    transition: background-color 0.3s, color 0.3s;
  }
}

.title-menu {
  display: flex;
  align-items: center;

  .title-img {
    width: 44px;
    height: 44px;
    border-radius: 22px;
    box-shadow: 0 1px 6px #444;
    margin-right: 16px;
  }

  .title-name {
    font-size: 22px;
    font-weight: 700;
  }

  .gradient-text {
    background: linear-gradient(90deg, rgba(255, 0, 0, 1), rgb(191, 0, 255), rgb(255, 234, 0));
    -webkit-background-clip: text;
    color: transparent;
  }
}
</style>
