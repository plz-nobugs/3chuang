<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item :label="contents.inputTitle == 1 ? '名称' : ''">
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1" prefix-icon="el-icon-search" v-model="searchForm.name" placeholder="名称" clearable></el-input>
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2" suffix-icon="el-icon-search" v-model="searchForm.name" placeholder="名称" clearable></el-input>
            <el-input v-if="contents.inputIcon == 0" v-model="searchForm.name" placeholder="名称" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 1" icon="el-icon-search" type="success" @click="search()">{{ contents.searchBtnFont == 1?'查询':'' }}</el-button>
            <el-button v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 2" type="success" @click="search()">{{ contents.searchBtnFont == 1?'查询':'' }}<i class="el-icon-search el-icon--right"/></el-button>
            <el-button v-if="contents.searchBtnIcon == 0" type="success" @click="search()">{{ contents.searchBtnFont == 1?'查询':'' }}</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad" :style="{justifyContent:contents.btnAdAllBoxPosition=='1'?'flex-start':contents.btnAdAllBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item>
            <el-button
                v-if="isAuth('lockerstatus','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1"
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >{{ contents.btnAdAllFont == 1?'新增':'' }}</el-button>
            <el-button
                v-if="isAuth('lockerstatus','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2"
                type="success"
                @click="addOrUpdateHandler()"
            >{{ contents.btnAdAllFont == 1?'新增':'' }}<i class="el-icon-plus el-icon--right" /></el-button>
            <el-button
                v-if="isAuth('lockerstatus','新增') && contents.btnAdAllIcon == 0"
                type="success"
                @click="addOrUpdateHandler()"
            >{{ contents.btnAdAllFont == 1?'新增':'' }}</el-button>
            <el-button
                v-if="isAuth('lockerstatus','删除') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1 && contents.tableSelection"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                icon="el-icon-delete"
                @click="deleteHandler()"
            >{{ contents.btnAdAllFont == 1?'删除':'' }}</el-button>
            <el-button
                v-if="isAuth('lockerstatus','删除') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2 && contents.tableSelection"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                @click="deleteHandler()"
            >{{ contents.btnAdAllFont == 1?'删除':'' }}<i class="el-icon-delete el-icon--right" /></el-button>
            <el-button
                v-if="isAuth('lockerstatus','删除') && contents.btnAdAllIcon == 0 && contents.tableSelection"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                @click="deleteHandler()"
            >{{ contents.btnAdAllFont == 1?'删除':'' }}</el-button>
          </el-form-item>
        </el-row>
      </el-form>

      <div class="table-content">
        <!-- 使用flex布局将表格和地图并排显示 -->
        <div style="display: flex; flex-direction: row;">
          <!-- 表格区域 -->
          <div style="flex: 1; margin-right: 20px;">
            <el-table class="tables" :size="contents.tableSize" :show-header="contents.tableShowHeader"
                      :header-row-style="headerRowStyle" :header-cell-style="headerCellStyle"
                      :border="contents.tableBorder"
                      :fit="contents.tableFit"
                      :stripe="contents.tableStripe"
                      :row-style="rowStyle"
                      :cell-style="cellStyle"
                      :style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
                      v-if="isAuth('lockerstatus','查看')"
                      :data="dataList"
                      v-loading="dataListLoading"
                      @selection-change="selectionChangeHandler">
              <el-table-column v-if="contents.tableSelection"
                               type="selection"
                               header-align="center"
                               align="center"
                               width="50">
              </el-table-column>
              <el-table-column label="索引" v-if="contents.tableIndex" type="index" width="50" />
              <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                                prop="name"
                                header-align="center"
                                label="名称">
                <template slot-scope="scope">
                  {{scope.row.name}}
                </template>
              </el-table-column>
              <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                                prop="total"
                                header-align="center"
                                label="总格数">
                <template slot-scope="scope">
                  {{scope.row.total}}
                </template>
              </el-table-column>
              <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                                prop="used"
                                header-align="center"
                                label="已用格数">
                <template slot-scope="scope">
                  {{scope.row.used}}
                </template>
              </el-table-column>
              <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                                prop="description"
                                header-align="center"
                                label="描述">
                <template slot-scope="scope">
                  {{scope.row.description}}
                </template>
              </el-table-column>
              <el-table-column width="260" :align="contents.tableAlign"
                               header-align="center"
                               label="操作">
                <template slot-scope="scope">
                  <el-button v-if="isAuth('lockerstatus','查看') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="success" icon="el-icon-location" size="mini" @click="showOnMap(scope.row)">{{ contents.tableBtnFont == 1?'在地图展示':'' }}</el-button>
                  <el-button v-if="isAuth('lockerstatus','查看') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="success" size="mini" @click="showOnMap(scope.row)">{{ contents.tableBtnFont == 1?'在地图展示':'' }}<i class="el-icon-location el-icon--right" /></el-button>
                  <el-button v-if="isAuth('lockerstatus','查看') && contents.tableBtnIcon == 0" type="success" size="mini" @click="showOnMap(scope.row)">{{ contents.tableBtnFont == 1?'在地图展示':'' }}</el-button>

                  <el-button v-if="isAuth('lockerstatus','修改') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'修改':'' }}</el-button>
                  <el-button v-if="isAuth('lockerstatus','修改') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'修改':'' }}<i class="el-icon-edit el-icon--right" /></el-button>
                  <el-button v-if="isAuth('lockerstatus','修改') && contents.tableBtnIcon == 0" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'修改':'' }}</el-button>

                  <el-button v-if="isAuth('lockerstatus','删除') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'删除':'' }}</el-button>
                  <el-button v-if="isAuth('lockerstatus','删除') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="danger" size="mini" @click="deleteHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'删除':'' }}<i class="el-icon-delete el-icon--right" /></el-button>
                  <el-button v-if="isAuth('lockerstatus','删除') && contents.tableBtnIcon == 0" type="danger" size="mini" @click="deleteHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'删除':'' }}</el-button>
                </template>
              </el-table-column>
            </el-table>

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
                :style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}"
            ></el-pagination>
          </div>

          <!-- 地图显示区域 -->
          <div style="flex: 1; position: relative;">
            <div v-if="currentLockerInfo" id="container" style="width: 100%; height: 400px; margin-top: 10px;"></div>
            <div v-else class="map-placeholder" style="width: 100%; height: 400px; display: flex; justify-content: center; align-items: center; background-color: #f5f5f5; border: 1px dashed #ddd; margin-top: 10px;">
              请选择一个快递柜查看位置信息
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 添加/修改页面  将父组件的search方法传递给子组件-->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>
<script>
import AddOrUpdate from "./add-or-update";

export default {
  data() {
    return {
      searchForm: {
        key: ""
      },
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
      layouts: '',
      map: null,
      markers: [],
      currentLockerInfo: null // 存储当前选中的快递柜信息
    };
  },
  components: {
    AddOrUpdate
  },
  created() {
    this.init();
    this.getDataList();
    this.contentStyleChange();
  },
  mounted() {

  },
  methods: {
    // 加载高德地图脚本
    loadMapScript() {
      if (window.AMap) {
        this.initMap();
        return;
      }

      // 从后端获取地图配置
      this.$http({
        url: "config/map",
        method: "get"
      }).then(({data}) => {
        if (data && data.code === 0) {
          const config = data.data;
          // 设置安全密钥
          window._AMapSecurityConfig = {
            securityJsCode: config.securityJsCode,
          };

          // 添加高德地图API脚本
          const script = document.createElement('script');
          script.src = `https://webapi.amap.com/maps?v=2.0&key=${config.key}`;
          script.async = true;
          script.onload = () => {
            this.initMap();
          };
          document.head.appendChild(script);
        }
      });
    },

    // 初始化地图
    initMap() {
      this.$nextTick(() => {
        if (!document.getElementById('container')) return;

        this.map = new AMap.Map('container', {
          viewMode: '2D',
          zoom: 13,
          center: [116.397428, 39.90923], // 默认中心点，可以根据实际情况调整
        });

        this.showSelectedLockerOnMap();
      });
    },

    // 在地图上显示所选快递柜
    showOnMap(locker) {
      // 设置当前选中的快递柜
      this.currentLockerInfo = locker;

      // 确保地图已加载，并显示选中的快递柜
      if (this.map) {
        this.showSelectedLockerOnMap();
      } else {
        this.loadMapScript();
      }
    },

    // 在地图上显示选中的快递柜
    showSelectedLockerOnMap() {
      if (!this.map) return;

      // 清除已有标记
      if (this.markers.length > 0) {
        this.map.remove(this.markers);
        this.markers = [];
      }

      // 获取当前选中的快递柜信息
      let lockerInfo = this.currentLockerInfo;

      if (lockerInfo && lockerInfo.longitude && lockerInfo.latitude) {
        const marker = new AMap.Marker({
          position: [lockerInfo.longitude, lockerInfo.latitude],
          title: lockerInfo.name,
          clickable: true,
        });

        // 添加信息窗口
        const infoWindow = new AMap.InfoWindow({
          content: `
            <div style="padding: 10px;">
              <h4>${lockerInfo.name}</h4>
              <p>总格数: ${lockerInfo.total}</p>
              <p>已用格数: ${lockerInfo.used || 0}</p>
              <p>描述: ${lockerInfo.description || '无'}</p>
            </div>
          `,
          offset: new AMap.Pixel(0, -30),
        });

        marker.on('click', () => {
          infoWindow.open(this.map, marker.getPosition());
        });

        this.markers.push(marker);
        this.map.add(marker);

        // 自动打开信息窗口
        infoWindow.open(this.map, marker.getPosition());

        // 设置地图中心点为标记点
        this.map.setCenter([lockerInfo.longitude, lockerInfo.latitude]);
        this.map.setZoom(15); // 放大地图以便查看详情
      }
    },

    contentStyleChange() {
      this.contentSearchStyleChange()
      this.contentBtnAdAllStyleChange()
      this.contentSearchBtnStyleChange()
      this.contentTableBtnStyleChange()
      this.contentPageStyleChange()
    },
    contentSearchStyleChange() {
      this.$nextTick(() => {
        document.querySelectorAll('.form-content .slt .el-input__inner').forEach(el => {
          let textAlign = 'left'
          if (this.contents.inputFontPosition == 2) textAlign = 'center'
          if (this.contents.inputFontPosition == 3) textAlign = 'right'
          el.style.textAlign = textAlign
          el.style.height = this.contents.inputHeight
          el.style.lineHeight = this.contents.inputHeight
          el.style.color = this.contents.inputFontColor
          el.style.fontSize = this.contents.inputFontSize
          el.style.borderWidth = this.contents.inputBorderWidth
          el.style.borderStyle = this.contents.inputBorderStyle
          el.style.borderColor = this.contents.inputBorderColor
          el.style.borderRadius = this.contents.inputBorderRadius
          el.style.backgroundColor = this.contents.inputBgColor
        })
        if (this.contents.inputTitle) {
          document.querySelectorAll('.form-content .slt .el-form-item__label').forEach(el => {
            el.style.color = this.contents.inputTitleColor
            el.style.fontSize = this.contents.inputTitleSize
            el.style.lineHeight = this.contents.inputHeight
          })
        }
        setTimeout(() => {
          document.querySelectorAll('.form-content .slt .el-input__prefix').forEach(el => {
            el.style.color = this.contents.inputIconColor
            el.style.lineHeight = this.contents.inputHeight
          })
          document.querySelectorAll('.form-content .slt .el-input__suffix').forEach(el => {
            el.style.color = this.contents.inputIconColor
            el.style.lineHeight = this.contents.inputHeight
          })
          document.querySelectorAll('.form-content .slt .el-input__icon').forEach(el => {
            el.style.lineHeight = this.contents.inputHeight
          })
        }, 10)

      })
    },
    // 搜索按钮
    contentSearchBtnStyleChange() {
      this.$nextTick(() => {
        document.querySelectorAll('.form-content .slt .el-button--success').forEach(el => {
          el.style.height = this.contents.searchBtnHeight
          el.style.color = this.contents.searchBtnFontColor
          el.style.fontSize = this.contents.searchBtnFontSize
          el.style.borderWidth = this.contents.searchBtnBorderWidth
          el.style.borderStyle = this.contents.searchBtnBorderStyle
          el.style.borderColor = this.contents.searchBtnBorderColor
          el.style.borderRadius = this.contents.searchBtnBorderRadius
          el.style.backgroundColor = this.contents.searchBtnBgColor
        })
      })
    },
    // 新增、批量删除
    contentBtnAdAllStyleChange() {
      this.$nextTick(() => {
        document.querySelectorAll('.form-content .ad .el-button--success').forEach(el => {
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllAddFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllAddBgColor
        })
        document.querySelectorAll('.form-content .ad .el-button--danger').forEach(el => {
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllDelFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllDelBgColor
        })
        document.querySelectorAll('.form-content .ad .el-button--warning').forEach(el => {
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllWarnFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllWarnBgColor
        })
      })
    },
    // 表格
    rowStyle({row, rowIndex}) {
      if (rowIndex % 2 == 1) {
        if (this.contents.tableStripe) {
          return {color: this.contents.tableStripeFontColor}
        }
      } else {
        return ''
      }
    },
    cellStyle({row, rowIndex}) {
      if (rowIndex % 2 == 1) {
        if (this.contents.tableStripe) {
          return {backgroundColor: this.contents.tableStripeBgColor}
        }
      } else {
        return ''
      }
    },
    headerRowStyle({row, rowIndex}) {
      return {color: this.contents.tableHeaderFontColor}
    },
    headerCellStyle({row, rowIndex}) {
      return {backgroundColor: this.contents.tableHeaderBgColor}
    },
    // 表格按钮
    contentTableBtnStyleChange() {
      // 表格按钮样式代码...
    },
    // 分页
    contentPageStyleChange() {
      let arr = []

      if (this.contents.pageTotal) arr.push('total')
      if (this.contents.pageSizes) arr.push('sizes')
      if (this.contents.pagePrevNext) {
        arr.push('prev')
        if (this.contents.pagePager) arr.push('pager')
        arr.push('next')
      }
      if (this.contents.pageJumper) arr.push('jumper')
      this.layouts = arr.join()
      this.contents.pageEachNum = 10
    },

    init() {
    },
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    // 获取数据列表

    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id', // 使用id作为排序字段，而不是index
      }
      if (this.searchForm.name != '' && this.searchForm.name != undefined) {
        params['name'] = '%' + this.searchForm.name + '%'
      }
      this.$http({
        url: "lockerstatus/page",
        method: "get",
        params: params
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list;
          this.totalPage = data.data.total;

          // 数据加载完成后，如果有数据，选择第一项
          if (this.dataList.length > 0) {
            this.currentLockerInfo = this.dataList[0];
            this.$nextTick(() => {
              this.loadMapScript(); // 加载地图
            });
          }
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandler(val) {
      this.dataListSelections = val;
      // 选择改变时，更新地图显示
      if (val.length === 1) {
        this.currentLockerInfo = val[0];
        this.$nextTick(() => {
          if (this.map) {
            this.showSelectedLockerOnMap();
          } else {
            this.loadMapScript();
          }
        });
      }
    },
    // 添加/修改
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
    // 下载
    download(file) {
      window.open(`${file}`)
    },
    // 删除
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
          url: "lockerstatus/delete",
          method: "post",
          data: ids
        }).then(({data}) => {
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
</style>