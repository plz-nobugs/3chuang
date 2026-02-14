<template>
  <el-aside class="index-aside" height="100vh" width="260px">
    <!-- 整个侧边栏容器 -->
    <div class="index-aside-inner menulist" style="height:100%">
      <!-- 循环角色对应的菜单 -->
      <div
          v-for="item in menuList"
          :key="item.roleName"
          v-if="role == item.roleName"
          class="menulist-item"
          style="height:100%;border:0;"
      >
        <!-- 移除原先嵌套的“background-color:#01f14e” div -->

        <!-- 菜单组件：将背景色设为 transparent，让 SCSS 渐变生效 -->
        <el-menu
            mode="vertical"
            :unique-opened="true"
            class="el-menu-demo"
            style="height:100%;"
            background-color="transparent"
            text-color="#000000"
            active-text-color="#000000"
            default-active="0"
        >
          <!-- 首页 -->
          <el-menu-item
              index="0"
              :style="menulistBorderBottom"
              @click="menuHandler('')"
          >
            <i v-if="true" class="el-icon-s-home" />
            首页
          </el-menu-item>

          <!-- 个人中心 -->
          <el-submenu :index="1+''" :style="menulistBorderBottom">
            <template slot="title">
              <i v-if="true" class="el-icon-user-solid" />
              <span>个人中心</span>
            </template>
            <el-menu-item :index="1-1" @click="menuHandler('updatePassword')">
              修改密码
            </el-menu-item>
            <el-menu-item :index="1-2" @click="menuHandler('center')">
              个人信息
            </el-menu-item>
          </el-submenu>

          <!-- 动态菜单 -->
          <el-submenu
              v-for="(menu, index) in item.backMenu"
              :key="menu.menu"
              :index="(index + 2) + ''"
              :style="menulistBorderBottom"
          >
            <template slot="title">
              <i v-if="true" :class="icons[index]" />
              <span>{{ menu.menu }}</span>
            </template>
            <el-menu-item
                v-for="(child, sort) in menu.child"
                :key="sort"
                :index="(index + 2) + '-' + sort"
                @click="menuHandler(child.tableName)"
            >
              {{ child.menu }}
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </div>
    </div>
  </el-aside>
</template>

<script>
import menu from '@/utils/menu'

export default {
  data() {
    return {
      menuList: [],
      dynamicMenuRoutes: [],
      role: '',
      icons: [
        'el-icon-s-cooperation',
        'el-icon-s-order',
        'el-icon-s-platform',
        'el-icon-s-fold',
        'el-icon-s-unfold',
        'el-icon-s-operation',
        'el-icon-s-promotion',
        'el-icon-s-release',
        'el-icon-s-ticket',
        'el-icon-s-management',
        'el-icon-s-open',
        'el-icon-s-shop',
        'el-icon-s-marketing',
        'el-icon-s-flag',
        'el-icon-s-comment',
        'el-icon-s-finance',
        'el-icon-s-claim',
        'el-icon-s-custom',
        'el-icon-s-opportunity',
        'el-icon-s-data',
        'el-icon-s-check',
        'el-icon-s-grid',
        'el-icon-menu',
        'el-icon-chat-dot-square',
        'el-icon-message',
        'el-icon-postcard',
        'el-icon-position',
        'el-icon-microphone',
        'el-icon-close-notification',
        'el-icon-bangzhu',
        'el-icon-time',
        'el-icon-odometer',
        'el-icon-crop',
        'el-icon-aim',
        'el-icon-switch-button',
        'el-icon-full-screen',
        'el-icon-copy-document',
        'el-icon-mic',
        'el-icon-stopwatch',
      ],
      menulistStyle: 'vertical',
      menulistBorderBottom: {},
    }
  },
  mounted() {
    // 初始化菜单
    const menus = menu.list()
    this.menuList = menus
    this.role = this.$storage.get('role')
  },
  created() {
    setTimeout(() => {
      this.menulistStyleChange()
    }, 10)
    this.icons.sort(() => 0.5 - Math.random())
    this.lineBorder()
  },
  methods: {
    // 设置边框(如果需要)
    lineBorder() {
      let style = 'vertical'
      let w = '0'
      let s = 'solid'
      let c = '${template.back.menulist.menulistLineColor}'
      if (style === 'vertical') {
        this.menulistBorderBottom = {
          borderBottomWidth: w,
          borderBottomStyle: s,
          borderBottomColor: c,
        }
      } else {
        this.menulistBorderBottom = {
          borderRightWidth: w,
          borderRightStyle: s,
          borderRightColor: c,
        }
      }
    },
    // 点击菜单跳转
    menuHandler(name) {
      let router = this.$router
      // 特殊处理联系客服
      if (name === 'chat') {
        router.push('/chat/sessions')
        return
      }
      name = '/' + name
      router.push(name)
    },

    // 原本用来鼠标悬停改颜色的函数 —— 注释掉以免覆盖我们的新样式
    // setMenulistHoverColor() {
    //   ...
    // },

    setMenulistIconColor() {
      this.$nextTick(() => {
        document.querySelectorAll('.menulist .el-submenu__title .el-submenu__icon-arrow').forEach((el) => {
          el.style.color = 'rgba(153, 153, 153, 1)'
        })
      })
    },

    menulistStyleChange() {
      this.setMenulistIconColor()
      // this.setMenulistHoverColor() // 注释掉
      this.setMenulistStyleHeightChange()
      let str = 'vertical'
      if ('horizontal' === str) {
        this.$nextTick(() => {
          document.querySelectorAll('.el-container .el-container').forEach((el) => {
            el.style.display = 'block'
            el.style.paddingTop = '80px' // header 高度
          })
          document.querySelectorAll('.el-aside').forEach((el) => {
            el.style.width = '100%'
            el.style.height = '60px'
            el.style.paddingTop = '0'
          })
          document.querySelectorAll('.index-aside .index-aside-inner').forEach((el) => {
            el.style.paddingTop = '0'
          })
        })
      }
      if ('vertical' === str) {
        this.$nextTick(() => {
          document.querySelectorAll('.index-aside .index-aside-inner').forEach((el) => {
            el.style.paddingTop = '80px'
          })
        })
      }
    },

    setMenulistStyleHeightChange() {
      this.$nextTick(() => {
        document
            .querySelectorAll('.menulist-item>.el-menu--horizontal>.el-menu-item')
            .forEach((el) => {
              el.style.height = '60px'
              el.style.lineHeight = '60px'
            })
        document
            .querySelectorAll('.menulist-item>.el-menu--horizontal>.el-submenu>.el-submenu__title')
            .forEach((el) => {
              el.style.height = '60px'
              el.style.lineHeight = '60px'
            })
      })
    },
  },
}
</script>

<style lang="scss" scoped>
/* 侧边栏最外层：浅绿色渐变 */
.index-aside {
  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, #d4f7d4 0%, #50df50 100%);
  color: #000; /* 整体文字黑色 */

  .index-aside-inner {
    height: 100%;
    margin-right: -17px;
    margin-bottom: -17px;
    overflow-y: auto;
    overflow-x: hidden !important;
    padding-top: 60px;
    box-sizing: border-box;

    /* el-menu 背景透明，让父级渐变透出来 */
    .el-menu {
      border: none;
      background-color: transparent;

      /* 每个菜单项自身再使用另一套渐变绿，让选项更明显 */
      .el-menu-item,
      .el-submenu__title {
        background: linear-gradient(180deg, #baf7ba 0%, #027e02 100%);
        color: #000;
        margin: 4px 0;
        transition: background 0.2s, transform 0.2s;

        &:hover,
        &.is-active {
          /* 悬停 / 激活时，加深一些 */
          background: linear-gradient(180deg, #a0e0a0 0%, #045c04 100%);
          transform: translateX(8px);
        }
      }
    }
  }
}

/* 滚动条美化 */
.index-aside-inner::-webkit-scrollbar {
  width: 6px;
}
.index-aside-inner::-webkit-scrollbar-thumb {
  background-color: rgba(74, 127, 74, 0.4);
  border-radius: 3px;
}
.index-aside-inner::-webkit-scrollbar-track {
  background: transparent;
}
</style>
