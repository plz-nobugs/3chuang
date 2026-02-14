<style lang="scss" scoped>
.index-aside {
  margin-top: 80px;
  background: linear-gradient(180deg, #d4f7d4 0%, #a8e6a8 100%);
  color: #4a7f4a;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;

  .index-aside-inner {
    width: 100%;
    height: calc(100vh - 100px);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    padding-right: 4px;
  }

  .el-menu {
    background-color: transparent;
    border: none;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .el-menu-item,
    .el-submenu__title {
      font-size: 16px;
      font-weight: 500;
      padding: 12px 20px;
      border-radius: 12px;
      transition: transform .2s ease, background .2s ease, box-shadow .2s ease;
      position: relative;
      display: flex;
      align-items: center;
      color: inherit;

      i { margin-right: 12px; font-size: 20px; }

      &:hover,
      &.is-active {
        background-color: rgba(168, 230, 168, 0.3);
        box-shadow: 0 4px 12px rgba(168, 230, 168, 0.4);
        transform: translateX(8px);
      }

      &::before {
        content: "";
        position: absolute;
        left: 0;
        width: 4px;
        height: 100%;
        background: #4a7f4a;
        border-radius: 4px;
        opacity: 0;
        transition: opacity .2s ease;
      }

      &:hover::before,
      &.is-active::before {
        opacity: 1;
      }
    }

    .el-submenu__popper {
      background: #bff4bf !important;
      border-radius: 12px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06) !important;
    }

    .el-menu-item.is-active,
    .el-submenu__title.is-active {
      color: #2e8b57;
      font-weight: 600;
    }
  }
}

/* 滚动条美化 */
.index-aside-inner::-webkit-scrollbar { width: 6px; }
.index-aside-inner::-webkit-scrollbar-thumb {
  background-color: rgba(46, 74, 43, 0.3);
  border-radius: 3px;
}
.index-aside-inner::-webkit-scrollbar-track { background: transparent; }
</style>
