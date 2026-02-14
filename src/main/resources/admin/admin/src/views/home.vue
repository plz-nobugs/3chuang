<template>
  <div class="home-page">

    <!-- 中间欢迎文字 -->
    <div class="welcome-container">
      <h1 class="welcome-title">
        欢迎使用 {{ this.$project.projectName }}
      </h1>
      <p class="welcome-subtitle">
        “易退智联”智能退货物流系统
      </p>
    </div>
  </div>
</template>

<script>
import router from '@/router/router-static'

export default {
  mounted() {
    this.init()
  },
  methods: {
    init() {
      // 若无 Token，则跳转登录
      if (this.$storage.get('Token')) {
        this.$http({
          url: `${this.$storage.get('sessionTable')}/session`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code !== 0) {
            router.push({ name: 'login' })
          }
        })
      } else {
        router.push({ name: 'login' })
      }
    },
    // 点击“首页”
    goHome() {
      router.push('/')
    },
    // 点击“客服消息”
    goChat() {
      router.push('/chat/sessions')
    }
  }
}
</script>

<style scoped lang="scss">
.home-page {
  width: 100%;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  /*
    多层背景示例：
    1) 径向渐变 (最底层)
    2) 大图氛围图
    3) 顶部波浪 SVG
    4) 底部波浪 SVG
   */
  background:
      /* (1) 径向渐变：从左上到右下，透明度渐变形成层次 */
      radial-gradient(
              circle at top left,
              rgba(173, 255, 47, 0.2),
              rgba(85, 228, 85, 0.4),
              #4a974a
      ),
        /* (2) 大图：可替换为自己喜欢的图片链接 */
      url('https://images.unsplash.com/photo-1591608519196-a9235db18060?auto=format&w=1920&q=80'),
        /* (3) 顶部波浪：可用相同或不同的SVG链接 */
      url('https://res.cloudinary.com/dzwyiggcp/image/upload/v1615213449/wave-top.svg') no-repeat,
        /* (4) 底部波浪 */
      url('https://res.cloudinary.com/dzwyiggcp/image/upload/v1615213449/wave-bottom.svg') no-repeat;

  /* 设置每层的定位与缩放方式 */
  background-size:
      cover,            /* 径向渐变自动覆盖 */
      cover,            /* 大图充满容器 */
      100% auto,        /* 波浪宽度=容器宽度，高度自适应 */
      100% auto;
  background-position:
      center,           /* 渐变居中 */
      center,           /* 大图居中 */
      top center,       /* 顶部波浪贴顶部 */
      bottom center;    /* 底部波浪贴底部 */
  background-repeat: no-repeat, no-repeat, no-repeat, no-repeat;
}

/* 顶部导航条 */
.top-nav {
  margin: 20px;
  display: flex;
  gap: 16px;
}

/* 中间区域：欢迎文字 */
.welcome-container {
  flex: 1;  /* 占据剩余高度，便于垂直居中 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* 水平 & 垂直居中 */
}

/* 主标题 */
.welcome-title {
  font-size: 48px;
  color: #fff;
  font-weight: bold;
  text-shadow: 2px 2px 6px rgba(0, 0, 0, 0.4);
  margin-bottom: 16px;
}

/* 副标题 */
.welcome-subtitle {
  font-size: 24px;
  color: #f0f0f0;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
}
</style>
