<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item :label="contents.inputTitle == 1 ? '门店名称' : ''">
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1" prefix-icon="el-icon-search" v-model="searchForm.mendianmingcheng" placeholder="门店名称" clearable></el-input>
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2" suffix-icon="el-icon-search" v-model="searchForm.mendianmingcheng" placeholder="门店名称" clearable></el-input>
            <el-input v-if="contents.inputIcon == 0" v-model="searchForm.mendianmingcheng" placeholder="门店名称" clearable></el-input>
          </el-form-item>
          <el-form-item :label="contents.inputTitle == 1 ? '品名' : ''">
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1" prefix-icon="el-icon-search" v-model="searchForm.pinming" placeholder="品名" clearable></el-input>
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2" suffix-icon="el-icon-search" v-model="searchForm.pinming" placeholder="品名" clearable></el-input>
            <el-input v-if="contents.inputIcon == 0" v-model="searchForm.pinming" placeholder="品名" clearable></el-input>
          </el-form-item>
          <el-form-item :label="contents.inputTitle == 1 ? '顾客姓名' : ''">
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1" prefix-icon="el-icon-search" v-model="searchForm.gukexingming" placeholder="顾客姓名" clearable></el-input>
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2" suffix-icon="el-icon-search" v-model="searchForm.gukexingming" placeholder="顾客姓名" clearable></el-input>
            <el-input v-if="contents.inputIcon == 0" v-model="searchForm.gukexingming" placeholder="顾客姓名" clearable></el-input>
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
                v-if="isAuth('dingdanxinxi','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1"
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >{{ contents.btnAdAllFont == 1?'新增':'' }}</el-button>
            <el-button
                v-if="isAuth('dingdanxinxi','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2"
                type="success"
                @click="addOrUpdateHandler()"
            >{{ contents.btnAdAllFont == 1?'新增':'' }}<i class="el-icon-plus el-icon--right" /></el-button>
            <el-button
                v-if="isAuth('dingdanxinxi','新增') && contents.btnAdAllIcon == 0"
                type="success"
                @click="addOrUpdateHandler()"
            >{{ contents.btnAdAllFont == 1?'新增':'' }}</el-button>
            <el-button
                v-if="isAuth('dingdanxinxi','删除') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1 && contents.tableSelection"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                icon="el-icon-delete"
                @click="deleteHandler()"
            >{{ contents.btnAdAllFont == 1?'删除':'' }}</el-button>
            <el-button
                v-if="isAuth('dingdanxinxi','删除') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2 && contents.tableSelection"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                @click="deleteHandler()"
            >{{ contents.btnAdAllFont == 1?'删除':'' }}<i class="el-icon-delete el-icon--right" /></el-button>
            <el-button
                v-if="isAuth('dingdanxinxi','删除') && contents.btnAdAllIcon == 0 && contents.tableSelection"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                @click="deleteHandler()"
            >{{ contents.btnAdAllFont == 1?'删除':'' }}</el-button>


          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" :size="contents.tableSize" :show-header="contents.tableShowHeader"
                  :header-row-style="headerRowStyle" :header-cell-style="headerCellStyle"
                  :border="contents.tableBorder"
                  :fit="contents.tableFit"
                  :stripe="contents.tableStripe"
                  :row-style="rowStyle"
                  :cell-style="cellStyle"
                  :style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
                  v-if="isAuth('dingdanxinxi','查看')"
                  :data="dataList"
                  v-loading="dataListLoading"
                  @selection-change="selectionChangeHandler">
          <el-table-column  v-if="contents.tableSelection"
                            type="selection"
                            header-align="center"
                            align="center"
                            width="50">
          </el-table-column>
          <el-table-column label="索引" v-if="contents.tableIndex" type="index" width="50" />
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="dingdanbianhao"
                            header-align="center"
                            label="订单编号">
            <template slot-scope="scope">
              {{scope.row.dingdanbianhao}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="mendianmingcheng"
                            header-align="center"
                            label="门店名称">
            <template slot-scope="scope">
              {{scope.row.mendianmingcheng}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="dingdanzhonglei"
                            header-align="center"
                            label="订单种类">
            <template slot-scope="scope">
              {{scope.row.dingdanzhonglei}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="pinming"
                            header-align="center"
                            label="品名">
            <template slot-scope="scope">
              {{scope.row.pinming}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="jingzhong"
                            header-align="center"
                            label="净重">
            <template slot-scope="scope">
              {{scope.row.jingzhong}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="shifadi"
                            header-align="center"
                            label="始发地">
            <template slot-scope="scope">
              {{scope.row.shifadi}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="mudedi"
                            header-align="center"
                            label="目的地">
            <template slot-scope="scope">
              {{scope.row.mudedi}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="zhanghao"
                            header-align="center"
                            label="账号">
            <template slot-scope="scope">
              {{scope.row.zhanghao}}
            </template>
          </el-table-column>
          <el-table-column  :sortable="contents.tableSortable" :align="contents.tableAlign"
                            prop="gukexingming"
                            header-align="center"
                            label="顾客姓名">
            <template slot-scope="scope">
              {{scope.row.gukexingming}}
            </template>
          </el-table-column>
          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                           prop="shhf"
                           header-align="center"
                           label="审核回复">
          </el-table-column>
          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                           prop="sfsh"
                           header-align="center"
                           label="审核状态">
            <template slot-scope="scope">
              <span style="margin-right:10px">{{scope.row.sfsh=='是'?'通过':'未通过'}}</span>
            </template>
          </el-table-column>
          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
                           v-if="isAuth('dingdanxinxi','审核')"
                           prop="sfsh"
                           header-align="center"
                           label="审核">
            <template slot-scope="scope">
              <el-button  type="text" icon="el-icon-edit" size="small" @click="openAuditDialog(scope.row)">审核</el-button>
            </template>
          </el-table-column>
          <el-table-column width="300" :align="contents.tableAlign"
                           header-align="center"
                           label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('dingdanxinxi','查看') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">{{ contents.tableBtnFont == 1?'详情':'' }}</el-button>
              <el-button v-if="isAuth('dingdanxinxi','查看') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="success" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">{{ contents.tableBtnFont == 1?'详情':'' }}<i class="el-icon-tickets el-icon--right" /></el-button>
              <el-button v-if="isAuth('dingdanxinxi','查看') && contents.tableBtnIcon == 0" type="success" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">{{ contents.tableBtnFont == 1?'详情':'' }}</el-button>
              <el-button v-if="isAuth('dingdanxinxi','修改') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'修改':'' }}</el-button>
              <el-button v-if="isAuth('dingdanxinxi','修改') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'修改':'' }}<i class="el-icon-edit el-icon--right" /></el-button>
              <el-button v-if="isAuth('dingdanxinxi','修改') && contents.tableBtnIcon == 0" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'修改':'' }}</el-button>




              <el-button v-if="isAuth('dingdanxinxi','删除') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 1" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'删除':'' }}</el-button>
              <el-button v-if="isAuth('dingdanxinxi','删除') && contents.tableBtnIcon == 1 && contents.tableBtnIconPosition == 2" type="danger" size="mini" @click="deleteHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'删除':'' }}<i class="el-icon-delete el-icon--right" /></el-button>
              <el-button v-if="isAuth('dingdanxinxi','删除') && contents.tableBtnIcon == 0" type="danger" size="mini" @click="deleteHandler(scope.row.id)">{{ contents.tableBtnFont == 1?'删除':'' }}</el-button>
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
    </div>
    <!-- 添加/修改页面  将父组件的search方法传递给子组件-->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>


    <!-- 新的审核对话框 -->
    <el-dialog
        title="订单审核"
        :visible.sync="auditDialogVisible"
        width="95%"
        :close-on-click-modal="false"
        :before-close="handleCloseAudit"
        class="audit-dialog">
      <div class="audit-container">
        <!-- 左侧审核区域 -->
        <div class="audit-left">
          <div class="audit-form-section">
            <h3>审核信息</h3>
            <el-form ref="auditForm" :model="auditForm" label-width="100px">
              <el-form-item label="订单编号:">
                <span class="form-text">{{auditForm.dingdanbianhao}}</span>
              </el-form-item>
              <el-form-item label="门店名称:">
                <span class="form-text">{{auditForm.mendianmingcheng}}</span>
              </el-form-item>
              <el-form-item label="品名:">
                <span class="form-text">{{auditForm.pinming}}</span>
              </el-form-item>
              <el-form-item label="顾客姓名:">
                <span class="form-text">{{auditForm.gukexingming}}</span>
              </el-form-item>
              <el-form-item label="审核状态:" required>
                <el-select v-model="auditForm.sfsh" placeholder="请选择审核状态" style="width: 100%;">
                  <el-option label="通过" value="是"></el-option>
                  <el-option label="不通过" value="否"></el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </div>

          <div class="audit-comment-section">
            <h3>审核批注</h3>
            <el-input
                type="textarea"
                :rows="6"
                v-model="auditForm.shhf"
                placeholder="请输入审核批注..."
                maxlength="500"
                show-word-limit>
            </el-input>
          </div>
        </div>

        <!-- 右侧图片和检测报告区域 -->
        <div class="audit-right">
          <div class="photos-section">
            <h3>快递柜拍摄照片</h3>
            <div class="photos-container">
              <div class="photos-grid">
                <div class="photo-item" @click="previewImage('shangfangzhaopian', '上方视角')">
                  <div class="photo-label">上方视角</div>
                  <img :src="getImageUrl('shangfangzhaopian')" alt="上方照片" @error="handleImageError">
                </div>
                <div class="photo-item" @click="previewImage('xiafangzhaopian', '下方视角')">
                  <div class="photo-label">下方视角</div>
                  <img :src="getImageUrl('xiafangzhaopian')" alt="下方照片" @error="handleImageError">
                </div>
                <div class="photo-item" @click="previewImage('houfangzhaopian', '后方视角')">
                  <div class="photo-label">后方视角</div>
                  <img :src="getImageUrl('houfangzhaopian')" alt="后方照片" @error="handleImageError">
                </div>
                <div class="photo-item" @click="previewImage('zuocezhaopian', '左侧视角')">
                  <div class="photo-label">左侧视角</div>
                  <img :src="getImageUrl('zuocezhaopian')" alt="左侧照片" @error="handleImageError">
                </div>
                <div class="photo-item" @click="previewImage('youcezhaopian', '右侧视角')">
                  <div class="photo-label">右侧视角</div>
                  <img :src="getImageUrl('youcezhaopian')" alt="右侧照片" @error="handleImageError">
                </div>
              </div>
              <p class="photo-tip">点击图片可放大查看</p>
            </div>
          </div>

          <div class="detect-report-section">
            <h3>AI检测报告</h3>
            <div class="report-content" v-if="auditData && auditData.detectReport">
              <div class="report-summary">
                <div class="score-card">
                  <div class="score-title">综合评分</div>
                  <div class="score-value" :class="getScoreClass(auditData.detectReport.overallScore)">
                    {{auditData.detectReport.overallScore}}
                  </div>
                  <div class="score-desc">{{getScoreDesc(auditData.detectReport.overallScore)}}</div>
                </div>
                <div class="report-info">
                  <div class="info-item">
                    <span class="info-label">损坏程度</span>
                    <span class="info-value">{{auditData.detectReport.damageLevel}}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">完整性检查</span>
                    <span class="info-value">{{auditData.detectReport.integrityCheck}}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">质量评估</span>
                    <span class="info-value">{{auditData.detectReport.qualityAssessment}}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">检测时间</span>
                    <span class="info-value">{{auditData.detectReport.detectTime}}</span>
                  </div>
                </div>
              </div>

              <div class="report-details">
                <h4>详细检测结果</h4>
                <div class="details-grid">
                  <div class="detail-item" v-for="detail in auditData.detectReport.details" :key="detail.item">
                    <div class="detail-name">{{detail.item}}</div>
                    <div class="detail-score" :class="getScoreClass(detail.score)">{{detail.score}}</div>
                    <div class="detail-status">{{detail.status}}</div>
                  </div>
                </div>
              </div>

              <div class="report-recommendation">
                <div class="recommendation-title">
                  <i class="el-icon-lightbulb"></i>
                  AI建议
                </div>
                <div class="recommendation-content">{{auditData.detectReport.recommendation}}</div>
              </div>
            </div>

            <!-- 加载状态 -->
            <div v-else-if="auditDataLoading" style="text-align: center; padding: 40px;">
              <i class="el-icon-loading" style="font-size: 24px; color: #409EFF;"></i>
              <p style="color: #666; margin-top: 10px;">正在加载检测报告...</p>
            </div>

            <!-- 加载失败 -->
            <div v-else style="text-align: center; padding: 40px;">
              <i class="el-icon-warning" style="font-size: 24px; color: #E6A23C;"></i>
              <p style="color: #999; margin: 10px 0;">检测报告加载失败</p>
              <el-button size="small" type="primary" @click="loadAuditData">重新加载</el-button>
            </div>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="closeAuditDialog" size="medium">取 消</el-button>
        <el-button type="primary" @click="submitAudit" size="medium" :loading="auditSubmitting">确认审核</el-button>
      </span>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
        :title="previewTitle"
        :visible.sync="imagePreviewVisible"
        width="60%"
        class="image-preview-dialog">
      <div class="image-preview-container">
        <img :src="previewImageUrl" alt="预览图片" class="preview-image">
      </div>
    </el-dialog>

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
      form:{},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,

      // 审核相关数据
      auditDialogVisible: false,
      auditForm: {},
      auditData: null,
      auditDataLoading: false,
      auditSubmitting: false,

      // 图片预览
      imagePreviewVisible: false,
      previewImageUrl: '',
      previewTitle: '',

      chartVisiable: false,
      addOrUpdateFlag:false,
      contents:{"searchBtnFontColor":"rgba(252, 250, 250, 1)","pagePosition":"1","inputFontSize":"14px","inputBorderRadius":"4px","tableBtnDelFontColor":"rgba(255, 255, 255, 1)","tableBtnIconPosition":"1","searchBtnHeight":"40px","inputIconColor":"rgba(7, 82, 232, 1)","searchBtnBorderRadius":"4px","tableStripe":true,"tableBtnDelBgColor":"rgba(245, 194, 61, 1)","btnAdAllWarnFontColor":"rgba(252, 252, 252, 1)","searchBtnIcon":"1","tableSize":"medium","searchBtnBorderStyle":"solid","tableSelection":true,"searchBtnBorderWidth":"1px","tableContentFontSize":"15px","searchBtnBgColor":"rgba(13, 142, 235, 1)","inputTitleSize":"14px","btnAdAllBorderColor":"#DCDFE6","pageJumper":true,"btnAdAllIconPosition":"1","searchBoxPosition":"1","tableBtnDetailFontColor":"rgba(255, 255, 255, 1)","tableBtnHeight":"40px","pagePager":true,"searchBtnBorderColor":"#DCDFE6","tableHeaderFontColor":"#909399","inputTitle":"1","tableBtnBorderRadius":"4px","btnAdAllFont":"1","btnAdAllDelFontColor":"rgba(252, 252, 252, 1)","tableBtnIcon":"1","btnAdAllHeight":"40px","btnAdAllWarnBgColor":"rgba(245, 194, 61, 1)","btnAdAllBorderWidth":"0px","tableStripeFontColor":"#606266","tableBtnBorderStyle":"solid","inputHeight":"40px","btnAdAllBorderRadius":"4px","btnAdAllDelBgColor":"rgba(255, 87, 87, 1)","pagePrevNext":true,"btnAdAllAddBgColor":"rgba(13, 142, 235, 1)","searchBtnFont":"1","tableIndex":true,"btnAdAllIcon":"1","tableSortable":true,"pageSizes":true,"tableFit":true,"pageBtnBG":true,"searchBtnFontSize":"16px","tableBtnEditBgColor":"rgba(255, 87, 87, 1)","inputBorderWidth":"1px","inputFontPosition":"1","inputFontColor":"#333","pageEachNum":10,"tableHeaderBgColor":"#fff","inputTitleColor":"#333","btnAdAllBoxPosition":"1","tableBtnDetailBgColor":"rgba(13, 142, 235, 1)","inputIcon":"0","searchBtnIconPosition":"1","btnAdAllFontSize":"15px","inputBorderStyle":"solid","inputBgColor":"rgba(245, 241, 241, 1)","pageStyle":false,"pageTotal":true,"btnAdAllAddFontColor":"rgba(252, 252, 252, 1)","tableBtnFont":"1","tableContentFontColor":"#606266","inputBorderColor":"#DCDFE6","tableShowHeader":true,"tableBtnFontSize":"15px","tableBtnBorderColor":"#DCDFE6","inputIconPosition":"1","tableBorder":true,"btnAdAllBorderStyle":"solid","tableBtnBorderWidth":"0px","tableStripeBgColor":"#F5F7FA","tableBtnEditFontColor":"rgba(255, 255, 255, 1)","tableAlign":"center"},
      layouts: '',


    };
  },
  created() {
    this.init();
    this.getDataList();
    this.contentStyleChange()
  },
  mounted() {

  },
  filters: {
    htmlfilter: function (val) {
      return val.replace(/<[^>]*>/g).replace(/undefined/g,'');
    }
  },
  components: {
    AddOrUpdate,
  },
  methods: {
    contentStyleChange() {
      this.contentSearchStyleChange()
      this.contentBtnAdAllStyleChange()
      this.contentSearchBtnStyleChange()
      this.contentTableBtnStyleChange()
      this.contentPageStyleChange()
    },
    contentSearchStyleChange() {
      this.$nextTick(()=>{
        document.querySelectorAll('.form-content .slt .el-input__inner').forEach(el=>{
          let textAlign = 'left'
          if(this.contents.inputFontPosition == 2) textAlign = 'center'
          if(this.contents.inputFontPosition == 3) textAlign = 'right'
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
        if(this.contents.inputTitle) {
          document.querySelectorAll('.form-content .slt .el-form-item__label').forEach(el=>{
            el.style.color = this.contents.inputTitleColor
            el.style.fontSize = this.contents.inputTitleSize
            el.style.lineHeight = this.contents.inputHeight
          })
        }
        setTimeout(()=>{
          document.querySelectorAll('.form-content .slt .el-input__prefix').forEach(el=>{
            el.style.color = this.contents.inputIconColor
            el.style.lineHeight = this.contents.inputHeight
          })
          document.querySelectorAll('.form-content .slt .el-input__suffix').forEach(el=>{
            el.style.color = this.contents.inputIconColor
            el.style.lineHeight = this.contents.inputHeight
          })
          document.querySelectorAll('.form-content .slt .el-input__icon').forEach(el=>{
            el.style.lineHeight = this.contents.inputHeight
          })
        },10)

      })
    },
    // 搜索按钮
    contentSearchBtnStyleChange() {
      this.$nextTick(()=>{
        document.querySelectorAll('.form-content .slt .el-button--success').forEach(el=>{
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
      this.$nextTick(()=>{
        document.querySelectorAll('.form-content .ad .el-button--success').forEach(el=>{
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllAddFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllAddBgColor
        })
        document.querySelectorAll('.form-content .ad .el-button--danger').forEach(el=>{
          el.style.height = this.contents.btnAdAllHeight
          el.style.color = this.contents.btnAdAllDelFontColor
          el.style.fontSize = this.contents.btnAdAllFontSize
          el.style.borderWidth = this.contents.btnAdAllBorderWidth
          el.style.borderStyle = this.contents.btnAdAllBorderStyle
          el.style.borderColor = this.contents.btnAdAllBorderColor
          el.style.borderRadius = this.contents.btnAdAllBorderRadius
          el.style.backgroundColor = this.contents.btnAdAllDelBgColor
        })
        document.querySelectorAll('.form-content .ad .el-button--warning').forEach(el=>{
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
    rowStyle({ row, rowIndex}) {
      if (rowIndex % 2 == 1) {
        if(this.contents.tableStripe) {
          return {color:this.contents.tableStripeFontColor}
        }
      } else {
        return ''
      }
    },
    cellStyle({ row, rowIndex}){
      if (rowIndex % 2 == 1) {
        if(this.contents.tableStripe) {
          return {backgroundColor:this.contents.tableStripeBgColor}
        }
      } else {
        return ''
      }
    },
    headerRowStyle({ row, rowIndex}){
      return {color: this.contents.tableHeaderFontColor}
    },
    headerCellStyle({ row, rowIndex}){
      return {backgroundColor: this.contents.tableHeaderBgColor}
    },
    // 表格按钮
    contentTableBtnStyleChange(){
      // this.$nextTick(()=>{
      //   setTimeout(()=>{
      //     document.querySelectorAll('.table-content .tables .el-table__body .el-button--success').forEach(el=>{
      //       el.style.height = this.contents.tableBtnHeight
      //       el.style.color = this.contents.tableBtnDetailFontColor
      //       el.style.fontSize = this.contents.tableBtnFontSize
      //       el.style.borderWidth = this.contents.tableBtnBorderWidth
      //       el.style.borderStyle = this.contents.tableBtnBorderStyle
      //       el.style.borderColor = this.contents.tableBtnBorderColor
      //       el.style.borderRadius = this.contents.tableBtnBorderRadius
      //       el.style.backgroundColor = this.contents.tableBtnDetailBgColor
      //     })
      //     document.querySelectorAll('.table-content .tables .el-table__body .el-button--primary').forEach(el=>{
      //       el.style.height = this.contents.tableBtnHeight
      //       el.style.color = this.contents.tableBtnEditFontColor
      //       el.style.fontSize = this.contents.tableBtnFontSize
      //       el.style.borderWidth = this.contents.tableBtnBorderWidth
      //       el.style.borderStyle = this.contents.tableBtnBorderStyle
      //       el.style.borderColor = this.contents.tableBtnBorderColor
      //       el.style.borderRadius = this.contents.tableBtnBorderRadius
      //       el.style.backgroundColor = this.contents.tableBtnEditBgColor
      //     })
      //     document.querySelectorAll('.table-content .tables .el-table__body .el-button--danger').forEach(el=>{
      //       el.style.height = this.contents.tableBtnHeight
      //       el.style.color = this.contents.tableBtnDelFontColor
      //       el.style.fontSize = this.contents.tableBtnFontSize
      //       el.style.borderWidth = this.contents.tableBtnBorderWidth
      //       el.style.borderStyle = this.contents.tableBtnBorderStyle
      //       el.style.borderColor = this.contents.tableBtnBorderColor
      //       el.style.borderRadius = this.contents.tableBtnBorderRadius
      //       el.style.backgroundColor = this.contents.tableBtnDelBgColor
      //     })

      //   }, 50)
      // })
    },
    // 分页
    contentPageStyleChange(){
      let arr = []

      if(this.contents.pageTotal) arr.push('total')
      if(this.contents.pageSizes) arr.push('sizes')
      if(this.contents.pagePrevNext){
        arr.push('prev')
        if(this.contents.pagePager) arr.push('pager')
        arr.push('next')
      }
      if(this.contents.pageJumper) arr.push('jumper')
      this.layouts = arr.join()
      this.contents.pageEachNum = 10
    },

    init () {
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
        sort: 'id',
      }
      if(this.searchForm.mendianmingcheng!='' && this.searchForm.mendianmingcheng!=undefined){
        params['mendianmingcheng'] = '%' + this.searchForm.mendianmingcheng + '%'
      }
      if(this.searchForm.pinming!='' && this.searchForm.pinming!=undefined){
        params['pinming'] = '%' + this.searchForm.pinming + '%'
      }
      if(this.searchForm.gukexingming!='' && this.searchForm.gukexingming!=undefined){
        params['gukexingming'] = '%' + this.searchForm.gukexingming + '%'
      }
      this.$http({
        url: "dingdanxinxi/page",
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
    },
    // 添加/修改
    addOrUpdateHandler(id,type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      this.crossAddOrUpdateFlag = false;
      if(type!='info'){
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,type);
      });
    },

    // 打开审核对话框
    openAuditDialog(row) {
      console.log('打开审核对话框，行数据:', row);

      this.auditForm = {
        id: row.id,
        dingdanbianhao: row.dingdanbianhao,
        mendianmingcheng: row.mendianmingcheng,
        dingdanzhonglei: row.dingdanzhonglei,
        pinming: row.pinming,
        jingzhong: row.jingzhong,
        shifadi: row.shifadi,
        mudedi: row.mudedi,
        kehubeizhu: row.kehubeizhu,
        zhanghao: row.zhanghao,
        gukexingming: row.gukexingming,
        sfsh: row.sfsh || '否',
        shhf: row.shhf || ''
      };

      this.auditDialogVisible = true;
      this.loadAuditData();
    },

    // 加载审核数据
    loadAuditData() {
      console.log('开始加载审核数据...');
      this.auditDataLoading = true;
      this.auditData = null;

      this.$http({
        url: `dingdanxinxi/auditInfo/${this.auditForm.id}`,
        method: "get"
      }).then(({ data }) => {
        console.log('审核数据响应:', data);
        this.auditDataLoading = false;

        if (data && data.code === 0) {
          this.auditData = data.data;
          console.log('审核数据设置成功:', this.auditData);
        } else {
          this.$message.error('获取审核数据失败: ' + (data.msg || '未知错误'));
          console.error('获取审核数据失败:', data);
        }
      }).catch(error => {
        this.auditDataLoading = false;
        this.$message.error('网络请求失败');
        console.error('请求错误:', error);
      });
    },

    // 处理对话框关闭前的操作
    handleCloseAudit(done) {
      this.closeAuditDialog();
      done();
    },

    // 关闭审核对话框
    closeAuditDialog() {
      this.auditDialogVisible = false;
      this.auditData = null;
      this.auditForm = {};
      this.imagePreviewVisible = false;
      this.auditDataLoading = false;
      this.auditSubmitting = false;
    },

    // 提交审核
    submitAudit() {
      if (!this.auditForm.sfsh) {
        this.$message.error('请选择审核状态');
        return;
      }

      this.$confirm(`确定${this.auditForm.sfsh === '是' ? '通过' : '不通过'}该订单的退货申请吗？`, "审核确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.auditSubmitting = true;

        this.$http({
          url: "dingdanxinxi/update",
          method: "post",
          data: this.auditForm
        }).then(({ data }) => {
          this.auditSubmitting = false;

          if (data && data.code === 0) {
            this.$message({
              message: "审核操作成功",
              type: "success",
              duration: 1500
            });
            this.getDataList();
            this.closeAuditDialog();
          } else {
            this.$message.error(data.msg || '审核失败');
          }
        }).catch(() => {
          this.auditSubmitting = false;
          this.$message.error('网络请求失败');
        });
      });
    },

    // 获取图片URL
    getImageUrl(field) {
      if (this.auditData && this.auditData.orderInfo && this.auditData.orderInfo[field]) {
        let imagePath = this.auditData.orderInfo[field];
        console.log(`获取${field}图片路径:`, imagePath);

        // 如果是完整URL，直接返回
        if (imagePath.startsWith('http') || imagePath.startsWith('data:')) {
          return imagePath;
        }

        // 修改：如果是绝对路径（以/开头），直接返回
        if (imagePath.startsWith('/')) {
          console.log(`使用绝对路径:`, imagePath);
          return imagePath;
        }

        // 如果是相对路径，构建完整路径
        const fullPath = `./${imagePath}`;
        console.log(`构建相对路径:`, fullPath);
        return fullPath;
      }

      // 返回占位图片
      return this.getPlaceholderImage(field);
    },

    // 获取占位图片
    getPlaceholderImage(field) {
      const labels = {
        'shangfangzhaopian': '上方视角',
        'xiafangzhaopian': '下方视角',
        'houfangzhaopian': '后方视角',
        'zuocezhaopian': '左侧视角',
        'youcezhaopian': '右侧视角'
      };

      const label = labels[field] || '示例图片';

      const svg = `
        <svg width="160" height="120" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
              <path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(0,0,0,0.05)" stroke-width="0.5"/>
            </pattern>
          </defs>
          <rect width="160" height="120" fill="#fafafa" stroke="#e8e8e8" stroke-width="1"/>
          <rect width="160" height="120" fill="url(#grid)"/>
          <circle cx="80" cy="45" r="15" fill="#d9d9d9"/>
          <path d="M 70 42 L 90 42 M 75 37 L 85 37 M 80 52 L 80 58" stroke="#999" stroke-width="2" stroke-linecap="round"/>
          <text x="80" y="78" font-family="PingFang SC,Microsoft YaHei,sans-serif" font-size="12" fill="#666" text-anchor="middle">${label}</text>
          <text x="80" y="95" font-family="PingFang SC,Microsoft YaHei,sans-serif" font-size="10" fill="#999" text-anchor="middle">点击放大查看</text>
        </svg>
      `;

      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)));
    },

    // 预览图片
    previewImage(field, title) {
      this.previewTitle = title + ' - 预览';
      this.previewImageUrl = this.getImageUrl(field);
      this.imagePreviewVisible = true;
    },

    // 图片加载错误处理
    handleImageError(e) {
      console.log('图片加载失败:', e.target.src);
      // 如果是网络图片加载失败，尝试占位图
      if (!e.target.src.startsWith('data:image/svg')) {
        e.target.src = this.getPlaceholderImage('default');
      }
    },

    // 评分颜色
    getScoreClass(score) {
      if (score >= 90) return 'score-excellent';
      if (score >= 80) return 'score-good';
      if (score >= 70) return 'score-average';
      return 'score-poor';
    },

    // 评分描述
    getScoreDesc(score) {
      if (score >= 90) return '优秀';
      if (score >= 80) return '良好';
      if (score >= 70) return '一般';
      return '较差';
    },

    // 下载
    download(file){
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
          url: "dingdanxinxi/delete",
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
  & /deep/ el-pagination__sizes{
    & /deep/ el-input__inner {
      height: 22px;
      line-height: 22px;
    }
  }
}


.el-button+.el-button {
  margin:0;
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

// 审核对话框样式
.audit-dialog {
  & /deep/ .el-dialog {
    border-radius: 8px;
  }

  & /deep/ .el-dialog__body {
    padding: 20px;
  }
}

.audit-container {
  display: flex;
  gap: 20px;
  height: 700px;
}

.audit-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.audit-form-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;

  h3 {
    margin: 0 0 15px 0;
    color: #333;
    font-size: 16px;
    font-weight: 600;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }

  .form-text {
    color: #666;
    font-size: 14px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.audit-comment-section {
  flex: 1;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;

  h3 {
    margin: 0 0 15px 0;
    color: #333;
    font-size: 16px;
    font-weight: 600;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.audit-right {
  flex: 1.5;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.photos-section {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  height: 200px;

  h3 {
    margin: 0 0 10px 0;
    color: #333;
    font-size: 15px;
    font-weight: 600;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.photos-container {
  height: calc(100% - 30px);
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  height: 120px;
}

.photo-item {
  position: relative;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #1890ff;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
  }

  .photo-label {
    position: absolute;
    top: 3px;
    left: 3px;
    background: rgba(0,0,0,0.75);
    color: white;
    padding: 1px 4px;
    border-radius: 3px;
    font-size: 10px;
    z-index: 1;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.photo-tip {
  margin: 8px 0 0 0;
  font-size: 11px;
  color: #999;
  text-align: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.detect-report-section {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  height: 480px;
  overflow-y: auto;

  h3 {
    margin: 0 0 15px 0;
    color: #333;
    font-size: 16px;
    font-weight: 600;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.report-content {
  height: 100%;
}

.report-summary {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.score-card {
  flex: 0 0 120px;
  text-align: center;
  padding: 15px;
  background: linear-gradient(135deg, #f6f8fb 0%, #e9ecef 100%);
  border-radius: 8px;
  border: 1px solid #e0e0e0;

  .score-title {
    font-size: 12px;
    color: #666;
    margin-bottom: 8px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }

  .score-value {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 5px;

    &.score-excellent {
      color: #52c41a;
    }

    &.score-good {
      color: #1890ff;
    }

    &.score-average {
      color: #faad14;
    }

    &.score-poor {
      color: #f5222d;
    }
  }

  .score-desc {
    font-size: 11px;
    color: #999;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.report-info {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 4px;

  .info-label {
    font-size: 12px;
    color: #666;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }

  .info-value {
    font-size: 12px;
    color: #333;
    font-weight: 500;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.report-details {
  margin-bottom: 20px;

  h4 {
    margin: 0 0 12px 0;
    color: #333;
    font-size: 14px;
    font-weight: 600;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.detail-item {
  background: #fafbfc;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 12px;
  text-align: center;

  .detail-name {
    font-size: 12px;
    color: #666;
    margin-bottom: 6px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }

  .detail-score {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 4px;

    &.score-excellent {
      color: #52c41a;
    }

    &.score-good {
      color: #1890ff;
    }

    &.score-average {
      color: #faad14;
    }

    &.score-poor {
      color: #f5222d;
    }
  }

  .detail-status {
    font-size: 11px;
    color: #999;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

.report-recommendation {
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
  border: 1px solid #91d5ff;
  border-radius: 8px;
  padding: 15px;

  .recommendation-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 600;
    color: #1890ff;
    margin-bottom: 8px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;

    i {
      font-size: 16px;
    }
  }

  .recommendation-content {
    font-size: 13px;
    color: #595959;
    line-height: 1.5;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  }
}

// 图片预览对话框样式
.image-preview-dialog {
  & /deep/ .el-dialog {
    border-radius: 8px;
  }

  & /deep/ .el-dialog__body {
    padding: 20px;
    text-align: center;
  }
}

.image-preview-container {
  .preview-image {
    max-width: 100%;
    max-height: 70vh;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  }
}

.dialog-footer {
  text-align: right;
  padding-top: 15px;
  border-top: 1px solid #e9ecef;
}

</style>