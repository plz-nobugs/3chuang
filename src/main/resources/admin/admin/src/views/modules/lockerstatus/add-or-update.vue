<template>
  <div class="addEdit-block">
    <h3>{{ title }}</h3>
    <el-form
        class="detail-form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="100px"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="名称" prop="name">
            <el-input v-model="ruleForm.name" placeholder="名称" :disabled="isReadOnly"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row v-if="!isReadOnly">
        <el-col :span="24">
          <el-alert
              title="经纬度支持多种格式输入，例如："
              type="info"
              :description="coordinateFormatsDescription"
              show-icon
              :closable="false"
              style="margin-bottom: 20px;"
          />
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="经度" prop="longitudeInput">
            <el-input v-model="ruleForm.longitudeInput" :placeholder="longitudePlaceholder" :disabled="isReadOnly"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="纬度" prop="latitudeInput">
            <el-input v-model="ruleForm.latitudeInput" :placeholder="latitudePlaceholder" :disabled="isReadOnly"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="总格数" prop="total">
            <el-input v-model="ruleForm.total" placeholder="总格数" :disabled="isReadOnly"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="24">
          <el-form-item label="描述" prop="description">
            <el-input type="textarea" v-model="ruleForm.description" placeholder="描述" :disabled="isReadOnly"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="onSubmit" v-if="!isReadOnly">提交</el-button>
        <el-button @click="back">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: '',
      type: '',
      title: '添加快递柜信息',
      isReadOnly: false,
      coordinateFormatsDescription: '1. 十进制度数：116.404, 39.915\n2. 度分秒格式：121°55\'35\", 36°59\'37\"\n3. 方向+数值：E116.404, N39.915 或 东经116.404, 北纬39.915',
      longitudePlaceholder: '支持十进制或度分秒格式，如116.404或121°55\'35"',
      latitudePlaceholder: '支持十进制或度分秒格式，如39.915或36°59\'37"',
      ruleForm: {
        name: '',
        longitude: '',
        latitude: '',
        longitudeInput: '',
        latitudeInput: '',
        total: '',
        used: '0',
        description: ''
      },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        longitudeInput: [
          { required: true, message: '请输入经度', trigger: 'blur' }
        ],
        latitudeInput: [
          { required: true, message: '请输入纬度', trigger: 'blur' }
        ],
        total: [
          { required: true, message: '请输入总格数', trigger: 'blur' },
          { validator: (rule, value, callback) => {
              if (this.isReadOnly) {
                callback();
                return;
              }
              const num = parseInt(value);
              if (isNaN(num) || num <= 0) {
                callback(new Error('总格数必须是大于0的整数'));
              } else {
                callback();
              }
            }, trigger: 'blur' }
        ]
      }
    };
  },
  props: ['parent'],
  methods: {
    // 从各种格式解析经纬度为十进制
    parseCoordinate(input, type) {
      if (!input || typeof input !== 'string') {
        return { valid: false, decimal: null };
      }

      // 移除所有空格
      input = input.replace(/\s/g, '');

      // 尝试直接解析为十进制
      if (/^-?\d+(\.\d+)?$/.test(input)) {
        const decimal = parseFloat(input);
        const limit = type === 'longitude' ? 180 : 90;
        if (decimal >= -limit && decimal <= limit) {
          return { valid: true, decimal: decimal };
        }
      }

      // 检查是否为带方向的格式（E/W/N/S或东西南北）
      let direction = 1;
      let value = input;

      if (/^[EeWw东西]/.test(input) && type === 'longitude') {
        direction = /^[Ee东]/.test(input) ? 1 : -1;
        value = input.substring(1);
      } else if (/^[NnSs南北]/.test(input) && type === 'latitude') {
        direction = /^[Nn北]/.test(input) ? 1 : -1;
        value = input.substring(1);
      }

      // 尝试转换为十进制
      if (/^-?\d+(\.\d+)?$/.test(value)) {
        const decimal = parseFloat(value) * direction;
        return { valid: true, decimal: decimal };
      }

      // 尝试解析度分秒格式 - 支持两种常见格式
      // 格式1: 121°55'35"
      const dmsRegex1 = /^(\d+)°(\d+)'(\d+(?:\.\d+)?)"?$/;
      // 格式2: 121°55'35''
      const dmsRegex2 = /^(\d+)°(\d+)'(\d+(?:\.\d+)?)'?'$/;

      let match = value.match(dmsRegex1);
      if (!match) {
        match = value.match(dmsRegex2);
      }

      if (match) {
        const degrees = parseInt(match[1] || 0);
        const minutes = parseInt(match[2] || 0) / 60;
        const seconds = parseFloat(match[3] || 0) / 3600;

        const decimal = (degrees + minutes + seconds) * direction;
        return { valid: true, decimal: decimal };
      }

      // 如果都不匹配，但允许任意输入
      return { valid: false, decimal: null };
    },

    // 在提交前验证并转换经纬度
    validateCoordinates() {
      const longitudeResult = this.parseCoordinate(this.ruleForm.longitudeInput, 'longitude');
      const latitudeResult = this.parseCoordinate(this.ruleForm.latitudeInput, 'latitude');

      // 更新表单的十进制值
      if (longitudeResult.valid) {
        this.ruleForm.longitude = longitudeResult.decimal;
      } else {
        // 如果无法解析，尝试直接使用
        this.ruleForm.longitude = parseFloat(this.ruleForm.longitudeInput);
      }

      if (latitudeResult.valid) {
        this.ruleForm.latitude = latitudeResult.decimal;
      } else {
        // 如果无法解析，尝试直接使用
        this.ruleForm.latitude = parseFloat(this.ruleForm.latitudeInput);
      }

      // 验证范围
      const longValid = !isNaN(this.ruleForm.longitude) &&
          this.ruleForm.longitude >= -180 &&
          this.ruleForm.longitude <= 180;

      const latValid = !isNaN(this.ruleForm.latitude) &&
          this.ruleForm.latitude >= -90 &&
          this.ruleForm.latitude <= 90;

      return longValid && latValid;
    },

    init(id, type) {
      // 重置表单数据
      this.ruleForm = {
        name: '',
        longitude: '',
        latitude: '',
        longitudeInput: '',
        latitudeInput: '',
        total: '',
        used: '0',
        description: ''
      };

      this.id = id;
      this.type = type;

      // 设置只读状态和标题
      this.isReadOnly = (type === 'info');

      if (id) {
        this.title = this.isReadOnly ? '查看快递柜信息' : '修改快递柜信息';
      } else {
        this.title = '添加快递柜信息';
      }

      // 重置表单验证
      if (this.$refs.ruleForm) {
        this.$refs.ruleForm.clearValidate();
      }

      // 如果有ID，获取数据
      if (id) {
        this.$http({
          url: `lockerstatus/info/${id}`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            // 将后端返回的数据赋值给表单
            const formData = data.data;

            // 必须创建新对象进行赋值，以触发响应式更新
            this.ruleForm = {
              ...formData,
              longitudeInput: formData.longitude ? formData.longitude.toString() : '',
              latitudeInput: formData.latitude ? formData.latitude.toString() : ''
            };

            // 确保验证状态更新
            this.$nextTick(() => {
              if (this.$refs.ruleForm) {
                this.$refs.ruleForm.clearValidate();
              }
            });
          }
        });
      }
    },

    onSubmit() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          // 在提交前验证并转换经纬度
          if (!this.validateCoordinates()) {
            this.$message.error('经纬度格式或范围错误，请检查输入');
            return;
          }

          // 构造提交数据
          const submitData = {
            id: this.id,
            name: this.ruleForm.name,
            longitude: this.ruleForm.longitude,
            latitude: this.ruleForm.latitude,
            total: parseInt(this.ruleForm.total),
            used: this.ruleForm.used || 0,
            description: this.ruleForm.description
          };

          const url = this.id ? 'lockerstatus/update' : 'lockerstatus/save';
          this.$http({
            url,
            method: 'post',
            data: submitData
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success('操作成功');
              this.back();
            } else {
              this.$message.error(data.msg || '操作失败');
            }
          }).catch(error => {
            this.$message.error('提交失败，请检查网络连接');
          });
        }
      });
    },

    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
      this.parent.search();
    }
  }
};
</script>

<style>
.addEdit-block {
  padding: 20px;
}
</style>