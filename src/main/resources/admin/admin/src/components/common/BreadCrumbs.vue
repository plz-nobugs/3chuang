<template>
  <el-breadcrumb
      class="app-breadcrumb"
      :separator="separatorIcon"
      :style="breadcrumbStyle"
  >
    <!-- 过渡动画名：breadcrumb-transition -->
    <transition-group
        name="breadcrumb-transition"
        tag="div"
        class="breadcrumb-inner"
        :style="innerStyle"
    >
      <el-breadcrumb-item
          v-for="(item, index) in levelList"
          :key="item.path"
      >
        <!-- 如果是最后一项或不可跳转，则只显示文字 -->
        <span
            v-if="item.redirect === 'noRedirect' || index === levelList.length - 1"
            class="no-redirect"
        >
          {{ item.name }}
        </span>
        <!-- 否则可点击跳转 -->
        <a
            v-else
            @click.prevent="handleLink(item)"
            class="breadcrumb-link"
        >
          {{ item.name }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import pathToRegexp from 'path-to-regexp'
import { generateTitle } from '@/utils/i18n'

export default {
  data() {
    return {
      levelList: [],       // 面包屑层级
      separatorIcon: '∰',  // 分隔符
      alignment: 'left'    // 对齐方式，可选值：left / center / right
    }
  },
  watch: {
    $route() {
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
    this.breadcrumbStyleChange()
  },
  methods: {
    generateTitle,

    // 计算并生成面包屑
    getBreadcrumb() {
      const route = this.$route
      // 只保留含 meta 的路由
      let matched = route.matched.filter(item => item.meta)
      // 头部加一个“首页”或“index”占位
      matched = [{ path: '/index' }].concat(matched)
      // 过滤掉第一个
      this.levelList = matched.filter(item => item.meta).slice(1)
    },

    // 判断是否为仪表盘页面，可按需使用
    isDashboard(route) {
      const name = route && route.name
      return name && name.trim().toLowerCase() === 'index'
    },

    // 路由编译
    pathCompile(path) {
      const { params } = this.$route
      return pathToRegexp.compile(path)(params)
    },

    // 点击跳转
    handleLink(item) {
      const { redirect, path } = item
      if (redirect) {
        this.$router.push(redirect)
      } else {
        this.$router.push(path)
      }
    },

    // 保留原先 DOM 操作逻辑，进一步定制分隔符/颜色
    breadcrumbStyleChange() {
      this.$nextTick(() => {
        // 改变分隔符样式
        document.querySelectorAll('.app-breadcrumb .el-breadcrumb__separator')
            .forEach(el => {
              el.innerText = this.separatorIcon
              // 让分隔符呈现浅色以区分
              el.style.color = 'rgba(255, 255, 255, 0.7)'
            })
        // 改变可点击的链接样式
        document.querySelectorAll('.app-breadcrumb .el-breadcrumb__inner a')
            .forEach(el => {
              el.style.color = 'rgba(255, 255, 255, 1)'
            })
        // 改变不可点击的链接（最后一项或 noRedirect）样式
        document.querySelectorAll('.app-breadcrumb .el-breadcrumb__inner .no-redirect')
            .forEach(el => {
              el.style.color = 'rgba(255, 255, 255, 1)'
            })

        // 示例：如果布局是 vertical，则做一些偏移
        let layoutMode = 'vertical'
        if (layoutMode === 'vertical') {
          let headHeight = '80px'
          headHeight = parseInt(headHeight, 10) + 10 + 'px'
          document.querySelectorAll('.app-breadcrumb').forEach(el => {
            el.style.marginTop = headHeight
          })
        }
      })
    }
  },
  computed: {
    // 外层 Breadcrumb 的样式
    breadcrumbStyle() {
      return {
        height: '60px',
        // 4 层绿色渐变
        background: 'linear-gradient(45deg, #b0f1c2 0%, #6bd68d 30%, #39b36b 70%, #1f944e 100%)',
        borderRadius: '30px',
        padding: '0 30px',
        // 让外观更立体一些
        boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
        borderWidth: '0px',
        borderStyle: 'none',
        borderColor: 'transparent'
      }
    },
    // 内部容器的对齐方式
    innerStyle() {
      switch (this.alignment) {
        case 'center':
          return { justifyContent: 'center' }
        case 'right':
          return { justifyContent: 'flex-end' }
        default:
          return { justifyContent: 'flex-start' }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb {
  display: block;
  font-size: 14px;
  line-height: 60px;
  color: #fff; // 与绿色背景对比明显

  .breadcrumb-inner {
    display: flex;
    width: 100%;
    height: 100%;
    align-items: center;
    // 给面包屑项之间留出一点间距
    gap: 8px;
  }

  // 最后一项或 noRedirect
  .no-redirect {
    color: rgba(255, 255, 255, 0.9);
    cursor: default;
    font-weight: 500;
  }

  // 可点击的链接
  .breadcrumb-link {
    // 让点击项有“Pill”形状
    background-color: rgba(255, 255, 255, 0.15);
    padding: 4px 12px;
    border-radius: 16px;
    transition: all 0.3s ease;

    &:hover {
      background-color: rgba(255, 255, 255, 0.3);
      transform: scale(1.05);
      color: #fff;
      text-decoration: none;
    }
  }
}

/* 过渡动画：左右滑动淡入淡出 */
.breadcrumb-transition-enter-active,
.breadcrumb-transition-leave-active {
  transition: all .4s ease;
}
.breadcrumb-transition-enter {
  opacity: 0;
  transform: translateX(-20px);
}
.breadcrumb-transition-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
