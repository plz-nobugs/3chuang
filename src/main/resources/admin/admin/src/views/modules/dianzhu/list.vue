<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{ justifyContent: contents.searchBoxPosition=='1' ? 'flex-start' : contents.searchBoxPosition=='2' ? 'center' : 'flex-end' }">
          <el-form-item :label="contents.inputTitle == 1 ? '店主账号' : ''">
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1"
                prefix-icon="el-icon-search"
                v-model="searchForm.dianzhuzhanghao"
                placeholder="店主账号"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2"
                suffix-icon="el-icon-search"
                v-model="searchForm.dianzhuzhanghao"
                placeholder="店主账号"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 0"
                v-model="searchForm.dianzhuzhanghao"
                placeholder="店主账号"
                clearable>
            </el-input>
          </el-form-item>
          <el-form-item :label="contents.inputTitle == 1 ? '店主姓名' : ''">
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1"
                prefix-icon="el-icon-search"
                v-model="searchForm.dianzhuxingming"
                placeholder="店主姓名"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2"
                suffix-icon="el-icon-search"
                v-model="searchForm.dianzhuxingming"
                placeholder="店主姓名"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 0"
                v-model="searchForm.dianzhuxingming"
                placeholder="店主姓名"
                clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button
                v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 1"
                icon="el-icon-search"
                type="success"
                @click="search()">
              {{ contents.searchBtnFont == 1 ? '查询' : '' }}
            </el-button>
            <el-button
                v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 2"
                type="success"
                @click="search()">
              {{ contents.searchBtnFont == 1 ? '查询' : '' }}<i class="el-icon-search el-icon--right"/>
            </el-button>
            <el-button
                v-if="contents.searchBtnIcon == 0"
                type="success"
                @click="search()">
              {{ contents.searchBtnFont == 1 ? '查询' : '' }}
            </el-button>
          </el-form-item>
        </el-row>
        <!-- 操作区：只保留新增，不显示删除 -->
        <el-row class="ad" :style="{ justifyContent: contents.btnAdAllBoxPosition=='1' ? 'flex-start' : contents.btnAdAllBoxPosition=='2' ? 'center' : 'flex-end' }">
          <el-form-item>
            <el-button
                v-if="isAuth('dianzhu','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1"
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()">
              {{ contents.btnAdAllFont == 1 ? '新增' : '' }}
            </el-button>
            <el-button
                v-if="isAuth('dianzhu','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2"
                type="success"
                @click="addOrUpdateHandler()">
              {{ contents.btnAdAllFont == 1 ? '新增' : '' }}<i class="el-icon-plus el-icon--right" />
            </el-button>
            <el-button
                v-if="isAuth('dianzhu','新增') && contents.btnAdAllIcon == 0"
                type="success"
                @click="addOrUpdateHandler()">
              {{ contents.btnAdAllFont == 1 ? '新增' : '' }}
            </el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <div class="card-container">
          <div v-for="(item, index) in dataList" :key="index" class="card">
            <div class="card-header">
              <h3>{{ item.dianzhuxingming }}</h3>
              <p>店主账号: {{ item.dianzhuzhanghao }}</p>
            </div>
            <div class="card-body">
              <p><strong>性别:</strong> {{ item.xingbie }}</p>
              <p><strong>联系电话:</strong> {{ item.lianxidianhua }}</p>
              <p><strong>邮箱:</strong> {{ item.youxiang }}</p>
              <p><strong>身份证:</strong> {{ item.shenfenzheng }}</p>
              <div v-if="item.tupian">
                <img :src="item.tupian.split(',')[0]" width="100" height="100">
              </div>
              <div v-else>无图片</div>
            </div>
            <div class="card-footer">
              <el-button
                  v-if="isAuth('dianzhu','查看')"
                  @click.stop="addOrUpdateHandler(item.id, 'info')"
                  size="mini">
                详情
              </el-button>
              <el-button
                  v-if="isAuth('dianzhu','修改')"
                  @click.stop="addOrUpdateHandler(item.id)"
                  size="mini">
                修改
              </el-button>
              <!-- 卡片内部删除按钮依然保留 -->
              <el-button
                  v-if="isAuth('dianzhu','删除')"
                  @click.stop="deleteHandler(item.id)"
                  size="mini">
                删除
              </el-button>
            </div>
          </div>
        </div>
        <el-pagination
            clsss="pages"
            :layout="layouts"
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="Number(contents.pageEachNum)"
            :total="totalPage"
            :small="contents.pageStyle"
            class="pagination-content"
            :background="contents.pageBtnBG"
            :style="{ textAlign: contents.pagePosition==1 ? 'left' : contents.pagePosition==2 ? 'center' : 'right' }"
        ></el-pagination>
      </div>
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{ justifyContent: contents.searchBoxPosition=='1' ? 'flex-start' : contents.searchBoxPosition=='2' ? 'center' : 'flex-end' }">
          <el-form-item :label="contents.inputTitle == 1 ? '工号' : ''">
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1"
                prefix-icon="el-icon-search"
                v-model="searchForm.gonghao"
                placeholder="工号"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2"
                suffix-icon="el-icon-search"
                v-model="searchForm.gonghao"
                placeholder="工号"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 0"
                v-model="searchForm.gonghao"
                placeholder="工号"
                clearable>
            </el-input>
          </el-form-item>
          <el-form-item :label="contents.inputTitle == 1 ? '店主姓名' : ''">
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1"
                prefix-icon="el-icon-search"
                v-model="searchForm.dianzhuxingming"
                placeholder="店主姓名"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2"
                suffix-icon="el-icon-search"
                v-model="searchForm.dianzhuxingming"
                placeholder="店主姓名"
                clearable>
            </el-input>
            <el-input
                v-if="contents.inputIcon == 0"
                v-model="searchForm.dianzhuxingming"
                placeholder="店主姓名"
                clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button
                v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 1"
                icon="el-icon-search"
                type="success"
                @click="search()">
              {{ contents.searchBtnFont == 1 ? '查询' : '' }}
            </el-button>
            <el-button
                v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 2"
                type="success"
                @click="search()">
              {{ contents.searchBtnFont == 1 ? '查询' : '' }}<i class="el-icon-search el-icon--right"/>
            </el-button>
            <el-button
                v-if="contents.searchBtnIcon == 0"
                type="success"
                @click="search()">
              {{ contents.searchBtnFont == 1 ? '查询' : '' }}
            </el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: { key: "" },
      form: {},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      sfshVisiable: false,
      shForm: {},
      chartVisiable: false,
      addOrUpdateFlag: false,
      contents: {
        "searchBtnFontColor": "rgba(252, 250, 250, 1)",
        "pagePosition": "1",
        "inputFontSize": "14px",
        "inputBorderRadius": "4px",
        "tableBtnDelFontColor": "rgba(255, 255, 255, 1)",
        "tableBtnIconPosition": "1",
        "searchBtnHeight": "40px",
        "inputIconColor": "rgba(7, 82, 232, 1)",
        "searchBtnBorderRadius": "4px",
        "tableStripe": true,
        "tableBtnDelBgColor": "rgba(245, 194, 61, 1)",
        "btnAdAllWarnFontColor": "rgba(252, 252, 252, 1)",
        "searchBtnIcon": "1",
        "tableSize": "medium",
        "searchBtnBorderStyle": "solid",
        "tableSelection": true,
        "searchBtnBorderWidth": "1px",
        "tableContentFontSize": "15px",
        "searchBtnBgColor": "rgba(13, 142, 235, 1)",
        "inputTitleSize": "14px",
        "btnAdAllBorderColor": "#DCDFE6",
        "pageJumper": true,
        "btnAdAllIconPosition": "1",
        "searchBoxPosition": "1",
        "tableBtnDetailFontColor": "rgba(255, 255, 255, 1)",
        "tableBtnHeight": "40px",
        "pagePager": true,
        "searchBtnBorderColor": "#DCDFE6",
        "tableHeaderFontColor": "#909399",
        "inputTitle": "1",
        "tableBtnBorderRadius": "4px",
        "btnAdAllFont": "1",
        "btnAdAllDelFontColor": "rgba(252, 252, 252, 1)",
        "tableBtnIcon": "1",
        "btnAdAllHeight": "40px",
        "btnAdAllWarnBgColor": "rgba(245, 194, 61, 1)",
        "btnAdAllBorderWidth": "0px",
        "tableStripeFontColor": "#606266",
        "tableBtnBorderStyle": "solid",
        "inputHeight": "40px",
        "btnAdAllBorderRadius": "4px",
        "btnAdAllDelBgColor": "rgba(255, 87, 87, 1)",
        "pagePrevNext": true,
        "btnAdAllAddBgColor": "rgba(13, 142, 235, 1)",
        "searchBtnFont": "1",
        "tableIndex": true,
        "btnAdAllIcon": "1",
        "tableSortable": true,
        "pageSizes": true,
        "tableFit": true,
        "pageBtnBG": true,
        "searchBtnFontSize": "16px",
        "tableBtnEditBgColor": "rgba(255, 87, 87, 1)",
        "inputBorderWidth": "1px",
        "inputFontPosition": "1",
        "inputFontColor": "#333",
        "pageEachNum": 10,
        "tableHeaderBgColor": "#fff",
        "inputTitleColor": "#333",
        "btnAdAllBoxPosition": "1",
        "tableBtnDetailBgColor": "rgba(13, 142, 235, 1)",
        "inputIcon": "0",
        "searchBtnIconPosition": "1",
        "btnAdAllFontSize": "15px",
        "inputBorderStyle": "solid",
        "inputBgColor": "rgba(245, 241, 241, 1)",
        "pageStyle": false,
        "pageTotal": true,
        "btnAdAllAddFontColor": "rgba(252, 252, 252, 1)",
        "tableBtnFont": "1",
        "tableContentFontColor": "#606266",
        "inputBorderColor": "#DCDFE6",
        "tableShowHeader": true,
        "tableBtnFontSize": "15px",
        "tableBtnBorderColor": "#DCDFE6",
        "inputIconPosition": "1",
        "tableBorder": true,
        "btnAdAllBorderStyle": "solid",
        "tableBtnBorderWidth": "0px",
        "tableStripeBgColor": "#F5F7FA",
        "tableBtnEditFontColor": "rgba(255, 255, 255, 1)",
        "tableAlign": "center"
      },
      layouts: ''
    };
  },
  created() {
    this.init();
    this.getDataList();
    this.contentStyleChange();
  },
  mounted() {},
  filters: {
    htmlfilter: function(val) {
      return val.replace(/<[^>]*>/g, '').replace(/undefined/g, '');
    }
  },
  components: {
    AddOrUpdate
  },
  methods: {
    contentStyleChange() {
      this.contentSearchStyleChange();
      this.contentBtnAdAllStyleChange();
      this.contentSearchBtnStyleChange();
      this.contentTableBtnStyleChange();
      this.contentPageStyleChange();
    },
    contentSearchStyleChange() {
      this.$nextTick(() => {
        document.querySelectorAll('.form-content .slt .el-input__inner').forEach(el => {
          let textAlign = 'left';
          if (this.contents.inputFontPosition == 2) textAlign = 'center';
          if (this.contents.inputFontPosition == 3) textAlign = 'right';
          el.style.textAlign = textAlign;
          el.style.height = this.contents.inputHeight;
          el.style.lineHeight = this.contents.inputHeight;
          el.style.color = this.contents.inputFontColor;
          el.style.fontSize = this.contents.inputFontSize;
          el.style.borderWidth = this.contents.inputBorderWidth;
          el.style.borderStyle = this.contents.inputBorderStyle;
          el.style.borderColor = this.contents.inputBorderColor;
          el.style.borderRadius = this.contents.inputBorderRadius;
          el.style.backgroundColor = this.contents.inputBgColor;
        });
        if (this.contents.inputTitle) {
          document.querySelectorAll('.form-content .slt .el-form-item__label').forEach(el => {
            el.style.color = this.contents.inputTitleColor;
            el.style.fontSize = this.contents.inputTitleSize;
            el.style.lineHeight = this.contents.inputHeight;
          });
        }
        setTimeout(() => {
          document.querySelectorAll('.form-content .slt .el-input__prefix').forEach(el => {
            el.style.color = this.contents.inputIconColor;
            el.style.lineHeight = this.contents.inputHeight;
          });
          document.querySelectorAll('.form-content .slt .el-input__suffix').forEach(el => {
            el.style.color = this.contents.inputIconColor;
            el.style.lineHeight = this.contents.inputHeight;
          });
          document.querySelectorAll('.form-content .slt .el-input__icon').forEach(el => {
            el.style.lineHeight = this.contents.inputHeight;
          });
        }, 10);
      });
    },
    contentSearchBtnStyleChange() {
      this.$nextTick(() => {
        document.querySelectorAll('.form-content .slt .el-button--success').forEach(el => {
          el.style.height = this.contents.searchBtnHeight;
          el.style.color = this.contents.searchBtnFontColor;
          el.style.fontSize = this.contents.searchBtnFontSize;
          el.style.borderWidth = this.contents.searchBtnBorderWidth;
          el.style.borderStyle = this.contents.searchBtnBorderStyle;
          el.style.borderColor = this.contents.searchBtnBorderColor;
          el.style.borderRadius = this.contents.searchBtnBorderRadius;
          el.style.backgroundColor = this.contents.searchBtnBgColor;
        });
      });
    },
    contentBtnAdAllStyleChange() {
      this.$nextTick(() => {
        document.querySelectorAll('.form-content .ad .el-button--success').forEach(el => {
          el.style.height = this.contents.btnAdAllHeight;
          el.style.color = this.contents.btnAdAllAddFontColor;
          el.style.fontSize = this.contents.btnAdAllFontSize;
          el.style.borderWidth = this.contents.btnAdAllBorderWidth;
          el.style.borderStyle = this.contents.btnAdAllBorderStyle;
          el.style.borderColor = this.contents.btnAdAllBorderColor;
          el.style.borderRadius = this.contents.btnAdAllBorderRadius;
          el.style.backgroundColor = this.contents.btnAdAllAddBgColor;
        });
        document.querySelectorAll('.form-content .ad .el-button--danger').forEach(el => {
          el.style.height = this.contents.btnAdAllHeight;
          el.style.color = this.contents.btnAdAllDelFontColor;
          el.style.fontSize = this.contents.btnAdAllFontSize;
          el.style.borderWidth = this.contents.btnAdAllBorderWidth;
          el.style.borderStyle = this.contents.btnAdAllBorderStyle;
          el.style.borderColor = this.contents.btnAdAllBorderColor;
          el.style.borderRadius = this.contents.btnAdAllBorderRadius;
          el.style.backgroundColor = this.contents.btnAdAllDelBgColor;
        });
        document.querySelectorAll('.form-content .ad .el-button--warning').forEach(el => {
          el.style.height = this.contents.btnAdAllHeight;
          el.style.color = this.contents.btnAdAllWarnFontColor;
          el.style.fontSize = this.contents.btnAdAllFontSize;
          el.style.borderWidth = this.contents.btnAdAllBorderWidth;
          el.style.borderStyle = this.contents.btnAdAllBorderStyle;
          el.style.borderColor = this.contents.btnAdAllBorderColor;
          el.style.borderRadius = this.contents.btnAdAllBorderRadius;
          el.style.backgroundColor = this.contents.btnAdAllWarnBgColor;
        });
      });
    },
    rowStyle({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        if (this.contents.tableStripe) {
          return { color: this.contents.tableStripeFontColor };
        }
      } else {
        return '';
      }
    },
    cellStyle({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        if (this.contents.tableStripe) {
          return { backgroundColor: this.contents.tableStripeBgColor };
        }
      } else {
        return '';
      }
    },
    headerRowStyle({ row, rowIndex }) {
      return { color: this.contents.tableHeaderFontColor };
    },
    headerCellStyle({ row, rowIndex }) {
      return { backgroundColor: this.contents.tableHeaderBgColor };
    },
    contentTableBtnStyleChange() {
      // 省略
    },
    contentPageStyleChange() {
      let arr = [];
      if (this.contents.pageTotal) arr.push('total');
      if (this.contents.pageSizes) arr.push('sizes');
      if (this.contents.pagePrevNext) {
        arr.push('prev');
        if (this.contents.pagePager) arr.push('pager');
        arr.push('next');
      }
      if (this.contents.pageJumper) arr.push('jumper');
      this.layouts = arr.join();
      this.contents.pageEachNum = 10;
    },
    init() {},
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id'
      };
      if (this.searchForm.dianzhuzhanghao != '' && this.searchForm.dianzhuzhanghao != undefined) {
        params['dianzhuzhanghao'] = '%' + this.searchForm.dianzhuzhanghao + '%';
      }
      if (this.searchForm.dianzhuxingming != '' && this.searchForm.dianzhuxingming != undefined) {
        params['dianzhuxingming'] = '%' + this.searchForm.dianzhuxingming + '%';
      }
      this.$http({
        url: "dianzhu/page",
        method: "get",
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list;
          this.totalPage = data.data.total;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },
    addOrUpdateHandler(id, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      this.crossAddOrUpdateFlag = false;
      if (type != 'info') {
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type);
      });
    },
    download(file) {
      window.open(`${file}`);
    },
    deleteHandler(id) {
      var ids = id
          ? [Number(id)]
          : this.dataListSelections.map(item => {
            return Number(item.id);
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "dianzhu/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.search();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    cardClick(item) {
      this.selectedCardId = item.id;
    }
  }
};
</script>

<style lang="scss" scoped>
.slt {
  margin: 0 !important;
  display: flex;
}
.ad {
  margin: 0 !important;
  display: flex;
}
.pages {
  & /deep/ el-pagination__sizes {
    & /deep/ el-input__inner {
      height: 22px;
      line-height: 22px;
    }
  }
}
.el-button + .el-button {
  margin: 0;
}
.tables {
  & /deep/ .el-button--success {
    height: 40px;
    color: rgba(255, 255, 255, 1);
    font-size: 15px;
    border-width: 0px;
    border-style: solid;
    border-color: #DCDFE6;
    border-radius: 4px;
    background-color: rgba(13, 142, 235, 1);
  }
  & /deep/ .el-button--primary {
    height: 40px;
    color: rgba(255, 255, 255, 1);
    font-size: 15px;
    border-width: 0px;
    border-style: solid;
    border-color: #DCDFE6;
    border-radius: 4px;
    background-color: rgba(255, 87, 87, 1);
  }
  & /deep/ .el-button--danger {
    height: 40px;
    color: rgba(255, 255, 255, 1);
    font-size: 15px;
    border-width: 0px;
    border-style: solid;
    border-color: #DCDFE6;
    border-radius: 4px;
    background-color: rgba(245, 194, 61, 1);
  }
  & /deep/ .el-button {
    margin: 4px;
  }
}
/* 新增的卡片样式 */
.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px; /* 设置卡片之间的间距 */
  justify-content: space-between;
  margin: 0 auto; /* 居中对齐 */
}
.card {
  width: 30%; /* 三列布局，每个卡片占 30% 的宽度 */
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #f9f9f9;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.2);
}
.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.card-body {
  margin-top: 10px;
  font-size: 14px;
  color: #555;
}
.card-footer {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  justify-content: center;
}
.card img {
  border-radius: 4px;
}
</style>
