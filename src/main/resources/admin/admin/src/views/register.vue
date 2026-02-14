<template>
  <div class="register-page">
    <!-- 动态背景 -->
    <div class="animated-background">
      <div class="gradient-sphere sphere-1"></div>
      <div class="gradient-sphere sphere-2"></div>
      <div class="gradient-sphere sphere-3"></div>
      <div class="gradient-sphere sphere-4"></div>
      <div class="gradient-sphere sphere-5"></div>

      <!-- 网格背景 -->
      <div class="grid-overlay"></div>
    </div>

    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo-container">
          <div class="logo-icon">
            <i class="el-icon-box"></i>
          </div>
          <div class="logo-pulse"></div>
        </div>
        <h1 class="brand-title">易退智联</h1>
        <p class="brand-subtitle">智能退货物流系统</p>
        <div class="feature-list">
          <div class="feature-item">
            <i class="el-icon-check"></i>
            <span>快速注册，轻松上手</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-check"></i>
            <span>智能化退货管理</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-check"></i>
            <span>全程物流追踪</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单 -->
    <div class="form-section">
      <div class="form-container">
        <div class="form-header">
          <h2 class="form-title">创建账户</h2>
          <p class="form-subtitle">加入易退智联，享受智能化服务</p>
        </div>

        <el-form ref="rgsForm" :model="ruleForm" :rules="rules" class="register-form">
          <!-- 角色选择 -->
          <el-form-item prop="role" label="">
            <div class="role-selection">
              <label class="role-label">选择角色</label>
              <el-select
                  v-model="ruleForm.role"
                  placeholder="请选择角色"
                  class="role-select"
                  @change="handleRoleChange"
              >
                <el-option
                    v-for="role in roleOptions"
                    :key="role.value"
                    :label="role.label"
                    :value="role.value"
                    class="role-option"
                >
                  <div class="role-option-content">
                    <i :class="role.icon"></i>
                    <span>{{ role.label }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>
          </el-form-item>

          <!-- 账号输入 -->
          <el-form-item prop="zhanghao" v-if="ruleForm.role">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-user"></i>
              </div>
              <el-input
                  v-model="ruleForm.zhanghao"
                  placeholder="请输入账号"
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item prop="mima" v-if="ruleForm.role">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-lock"></i>
              </div>
              <el-input
                  v-model="ruleForm.mima"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 姓名输入 -->
          <el-form-item prop="gukexingming" v-if="ruleForm.role">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-user-solid"></i>
              </div>
              <el-input
                  v-model="ruleForm.gukexingming"
                  :placeholder="getNamePlaceholder()"
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 手机号输入 -->
          <el-form-item prop="shouji" v-if="ruleForm.role">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-mobile-phone"></i>
              </div>
              <el-input
                  v-model="ruleForm.shouji"
                  placeholder="请输入手机号码"
                  maxlength="11"
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 邮箱输入带验证码 -->
          <el-form-item prop="youxiang" v-if="ruleForm.role">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-message"></i>
              </div>
              <el-input
                  v-model="ruleForm.youxiang"
                  placeholder="请输入邮箱地址"
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 邮箱验证码 -->
          <el-form-item prop="emailCode" v-if="ruleForm.role">
            <div class="input-group verification-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-key"></i>
              </div>
              <el-input
                  v-model="ruleForm.emailCode"
                  placeholder="请输入邮箱验证码"
                  class="custom-input verification-input"
              />
              <el-button
                  class="send-code-btn"
                  :disabled="sendCodeDisabled || !isValidEmail"
                  @click="sendEmailCode"
                  :loading="sendingCode"
              >
                {{ sendCodeText }}
              </el-button>
            </div>
          </el-form-item>

          <!-- 身份证输入 - 仅顾客需要 -->
          <el-form-item prop="shenfenzheng" v-if="ruleForm.role === 'customer'">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-postcard"></i>
              </div>
              <el-input
                  v-model="ruleForm.shenfenzheng"
                  placeholder="请输入身份证号码"
                  maxlength="18"
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 地址输入 -->
          <el-form-item prop="dizhi" v-if="ruleForm.role">
            <div class="input-group">
              <div class="input-icon-wrapper">
                <i class="input-icon el-icon-location-outline"></i>
              </div>
              <el-input
                  v-model="ruleForm.dizhi"
                  placeholder="请输入详细地址"
                  class="custom-input"
              />
            </div>
          </el-form-item>

          <!-- 服务条款 -->
          <el-form-item>
            <el-checkbox v-model="agreed" class="agreement-check">
              我已阅读并同意
              <a href="#" class="link" @click.prevent="showServiceTerms">服务条款</a>
              和
              <a href="#" class="link" @click.prevent="showPrivacyPolicy">隐私政策</a>
            </el-checkbox>
          </el-form-item>

          <!-- 按钮组 -->
          <div class="button-group">
            <el-button
                class="register-btn"
                type="primary"
                @click="register"
                :loading="loading"
                :disabled="!agreed"
            >
              <i class="el-icon-user-solid"></i>
              立即注册
            </el-button>
            <el-button
                class="cancel-btn"
                @click="close"
            >
              <i class="el-icon-back"></i>
              返回登录
            </el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="decoration-elements">
      <div class="float-element element-1"></div>
      <div class="float-element element-2"></div>
      <div class="float-element element-3"></div>
    </div>

    <!-- 服务条款弹窗 -->
    <el-dialog
        title="服务条款"
        :visible.sync="serviceTermsVisible"
        width="70%"
        :before-close="handleCloseTerms"
        class="terms-dialog"
    >
      <div class="terms-content">
        <h3>易退智联服务条款</h3>
        <p>欢迎使用易退智联智能退货物流系统！</p>

        <h4>1. 服务说明</h4>
        <p>易退智联是一个专业的智能退货物流管理平台，为用户提供便捷、高效的退货处理服务。</p>

        <h4>2. 用户义务</h4>
        <p>2.1 用户注册时应提供真实、准确的个人信息。</p>
        <p>2.2 用户应妥善保管账号密码，不得将账号借给他人使用。</p>
        <p>2.3 用户应遵守相关法律法规，不得利用本平台从事违法活动。</p>

        <h4>3. 服务内容</h4>
        <p>3.1 提供退货订单管理功能。</p>
        <p>3.2 提供物流信息查询和跟踪服务。</p>
        <p>3.3 提供智能化的退货处理建议。</p>

        <h4>4. 隐私保护</h4>
        <p>我们严格保护用户隐私，具体内容请参阅隐私政策。</p>

        <h4>5. 服务变更</h4>
        <p>我们保留在必要时修改本服务条款的权利，修改后的条款将在平台上公布。</p>

        <h4>6. 责任限制</h4>
        <p>在法律允许的范围内，易退智联对因使用本服务而产生的任何直接或间接损失不承担责任。</p>

        <p class="update-time">最后更新时间：2024年1月1日</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="serviceTermsVisible = false">关闭</el-button>
        <el-button type="primary" @click="agreeTerms">我已阅读并同意</el-button>
      </span>
    </el-dialog>

    <!-- 隐私政策弹窗 -->
    <el-dialog
        title="隐私政策"
        :visible.sync="privacyPolicyVisible"
        width="70%"
        :before-close="handleClosePolicy"
        class="terms-dialog"
    >
      <div class="terms-content">
        <h3>易退智联隐私政策</h3>
        <p>本隐私政策说明我们如何收集、使用和保护您的个人信息。</p>

        <h4>1. 信息收集</h4>
        <p>1.1 注册信息：包括您的姓名、邮箱、手机号码等。</p>
        <p>1.2 使用信息：包括您的操作记录、访问日志等。</p>
        <p>1.3 设备信息：包括设备型号、操作系统版本等。</p>

        <h4>2. 信息使用</h4>
        <p>2.1 为您提供退货物流服务。</p>
        <p>2.2 改善我们的产品和服务质量。</p>
        <p>2.3 向您发送重要通知和服务更新。</p>

        <h4>3. 信息共享</h4>
        <p>3.1 我们不会向第三方出售、出租或交易您的个人信息。</p>
        <p>3.2 在法律要求或保护权利时，我们可能会披露必要信息。</p>

        <h4>4. 信息安全</h4>
        <p>4.1 我们采用行业标准的安全措施保护您的信息。</p>
        <p>4.2 使用加密技术传输和存储敏感数据。</p>

        <h4>5. Cookie使用</h4>
        <p>我们使用Cookie来改善用户体验，您可以通过浏览器设置控制Cookie的使用。</p>

        <h4>6. 儿童隐私</h4>
        <p>我们不会故意收集13岁以下儿童的个人信息。</p>

        <h4>7. 联系我们</h4>
        <p>如有隐私相关问题，请联系：privacy@yituizhilian.com</p>

        <p class="update-time">最后更新时间：2024年1月1日</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="privacyPolicyVisible = false">关闭</el-button>
        <el-button type="primary" @click="agreePolicy">我已阅读并同意</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      ruleForm: {
        role: '',
        zhanghao: '',
        mima: '',
        gukexingming: '',
        shouji: '',
        youxiang: '',
        emailCode: '',
        shenfenzheng: '',
        dizhi: ''
      },
      roleOptions: [
        {
          value: 'customer',
          label: '顾客',
          icon: 'el-icon-user',
          tableName: 'guke'
        },
        {
          value: 'owner',
          label: '店主',
          icon: 'el-icon-office-building',
          tableName: 'dianzhu'  // 这里原来是 'dianzhang'，需要改为 'dianzhu'
        }
      ],
      tableName: 'guke',
      loading: false,
      agreed: false,
      sendingCode: false,
      sendCodeDisabled: false,
      countdown: 60,
      sendCodeText: '发送验证码',
      serviceTermsVisible: false,
      privacyPolicyVisible: false,
      rules: {
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        zhanghao: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        mima: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        gukexingming: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        shouji: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        youxiang: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        emailCode: [
          { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
          { len: 6, message: '验证码长度为6位', trigger: 'blur' }
        ],
        shenfenzheng: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
        ],
        dizhi: [
          { required: true, message: '请输入地址', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    isValidEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(this.ruleForm.youxiang);
    }
  },
  mounted() {
    this.tableName = this.$storage.get('loginTable') || 'guke';
    // 如果有默认角色，设置它
    const defaultRole = this.$storage.get('defaultRole');
    if (defaultRole) {
      this.ruleForm.role = defaultRole;
      this.handleRoleChange(defaultRole);
    }
  },
  // 在你的register.vue的methods中添加以下方法：

  // 在 register.vue 的 methods 中，修改以下方法：

  methods: {
    // 处理角色变更
    handleRoleChange(role) {
      const selectedRole = this.roleOptions.find(r => r.value === role);
      if (selectedRole) {
        this.tableName = selectedRole.tableName;
        // 根据角色设置不同的验证规则
        this.updateValidationRules(role);
      }
    },

    // 根据角色更新验证规则
    updateValidationRules(role) {
      // 身份证验证规则 - 只有顾客需要
      if (role === 'customer') {
        this.$set(this.rules, 'shenfenzheng', [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
        ]);
      } else {
        this.$delete(this.rules, 'shenfenzheng');
      }
    },

    // 根据角色获取姓名输入框占位符
    getNamePlaceholder() {
      const roleMap = {
        'customer': '请输入真实姓名',
        'owner': '请输入店主姓名'
      };
      return roleMap[this.ruleForm.role] || '请输入姓名';
    },

    // 🔧 修复：发送邮箱验证码方法 - 根据角色调用不同接口
    async sendEmailCode() {
      // 验证邮箱格式
      if (!this.isValidEmail) {
        this.$message.error('请输入正确的邮箱地址');
        return;
      }

      // 检查是否选择了角色
      if (!this.ruleForm.role) {
        this.$message.error('请先选择角色');
        return;
      }

      try {
        this.sendingCode = true;

        console.log('发送验证码到:', this.ruleForm.youxiang);

        // 根据角色选择对应的发送验证码接口
        let sendCodeUrl = '';
        if (this.ruleForm.role === 'customer') {
          sendCodeUrl = '/guke/sendEmailCode';
        } else if (this.ruleForm.role === 'owner') {
          sendCodeUrl = '/dianzhu/sendEmailCode';
        }

        // 调用对应的验证码发送接口
        const response = await this.$http({
          url: sendCodeUrl,
          method: 'post',
          params: {
            email: this.ruleForm.youxiang
          }
        });

        console.log('发送验证码响应:', response.data);

        if (response.data.code === 0) {
          this.$message.success('验证码已发送到您的邮箱，请注意查收');
          this.startCountdown();
        } else {
          this.$message.error(response.data.msg || '验证码发送失败');
        }

      } catch (error) {
        console.error('发送验证码错误:', error);
        this.$message.error(error.response?.data?.msg || '网络错误，请重试');
      } finally {
        this.sendingCode = false;
      }
    },

    // 🔧 修复：注册方法 - 根据角色调用不同的注册接口
    register() {
      this.$refs.rgsForm.validate(async (valid) => {
        if (valid && this.agreed) {
          this.loading = true;

          try {
            // 检查是否选择了角色
            if (!this.ruleForm.role) {
              this.$message.error('请选择角色');
              this.loading = false;
              return;
            }

            let registerData = {};
            let registerUrl = '';

            // 根据角色准备不同的注册数据和接口
            if (this.ruleForm.role === 'customer') {
              // 顾客注册数据
              registerData = {
                zhanghao: this.ruleForm.zhanghao,
                mima: this.ruleForm.mima,
                gukexingming: this.ruleForm.gukexingming,
                shouji: this.ruleForm.shouji,
                youxiang: this.ruleForm.youxiang,
                shenfenzheng: this.ruleForm.shenfenzheng,
                dizhi: this.ruleForm.dizhi,
                emailCode: this.ruleForm.emailCode
              };
              registerUrl = '/guke/register';

            } else if (this.ruleForm.role === 'owner') {
              // 店主注册数据
              registerData = {
                dianzhuzhanghao: this.ruleForm.zhanghao,
                mima: this.ruleForm.mima,
                dianzhuxingming: this.ruleForm.gukexingming,
                lianxidianhua: this.ruleForm.shouji,
                youxiang: this.ruleForm.youxiang,
                emailCode: this.ruleForm.emailCode
              };
              registerUrl = '/dianzhu/register';
            }

            console.log('发送注册数据:', registerData);
            console.log('注册接口:', registerUrl);

            // 调用对应的注册接口
            const response = await this.$http({
              url: registerUrl,
              method: 'post',
              data: registerData,
              params: {
                emailCode: this.ruleForm.emailCode
              }
            });

            console.log('注册响应:', response.data);

            if (response.data.code === 0) {
              this.$message.success('注册成功！');
              // 注册成功后跳转到登录页
              setTimeout(() => {
                this.$router.push('/login');
              }, 1500);
            } else {
              this.$message.error(response.data.msg || '注册失败');
            }

          } catch (error) {
            console.error('注册错误:', error);
            this.$message.error(error.response?.data?.msg || '注册失败，请重试');
          } finally {
            this.loading = false;
          }
        } else {
          this.$message.error('请完整填写表单信息并同意服务条款');
          return false;
        }
      });
    },

    // 开始倒计时
    startCountdown() {
      this.sendCodeDisabled = true;
      this.countdown = 60;

      const timer = setInterval(() => {
        this.countdown--;
        this.sendCodeText = `${this.countdown}秒后重发`;

        if (this.countdown <= 0) {
          clearInterval(timer);
          this.sendCodeDisabled = false;
          this.sendCodeText = '发送验证码';
        }
      }, 1000);
    },

    close() {
      this.$router.push('/login');
    },

    // 显示服务条款
    showServiceTerms() {
      this.serviceTermsVisible = true;
    },

    // 显示隐私政策
    showPrivacyPolicy() {
      this.privacyPolicyVisible = true;
    },

    // 关闭服务条款弹窗
    handleCloseTerms(done) {
      done();
    },

    // 关闭隐私政策弹窗
    handleClosePolicy(done) {
      done();
    },

    // 同意服务条款
    agreeTerms() {
      this.serviceTermsVisible = false;
      this.agreed = true;
      this.$message.success('感谢您同意服务条款');
    },

    // 同意隐私政策
    agreePolicy() {
      this.privacyPolicyVisible = false;
      this.agreed = true;
      this.$message.success('感谢您同意隐私政策');
    }
  }
};
</script>

<style scoped lang="scss">
/* 引入字体 */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

* {
  box-sizing: border-box;
}

/* 主容器 */
.register-page {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 动态背景 */
.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #4169E1 0%, #1E90FF 50%, #87CEEB 100%);
  overflow: hidden;
  z-index: 0;
}

/* 渐变球体 */
.gradient-sphere {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.4;
  animation: float 20s infinite ease-in-out;
}

.sphere-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #00BFFF 0%, #4682B4 100%);
  top: -10%;
  left: -10%;
  animation-delay: 0s;
}

.sphere-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #87CEFA 0%, #5F9EA0 100%);
  top: 50%;
  right: -5%;
  animation-delay: 5s;
}

.sphere-3 {
  width: 350px;
  height: 350px;
  background: linear-gradient(135deg, #B0E0E6 0%, #4682B4 100%);
  bottom: -10%;
  left: 30%;
  animation-delay: 10s;
}

.sphere-4 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #ADD8E6 0%, #1E90FF 100%);
  top: 20%;
  right: 20%;
  animation-delay: 15s;
}

.sphere-5 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #E0FFFF 0%, #87CEEB 100%);
  bottom: 20%;
  right: 30%;
  animation-delay: 7s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0) scale(1);
  }
  33% {
    transform: translateY(-30px) translateX(30px) scale(1.1);
  }
  66% {
    transform: translateY(30px) translateX(-20px) scale(0.9);
  }
}

/* 网格背景 */
.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: 1;
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 10;
  padding: 40px;
}

.brand-content {
  text-align: center;
  color: white;
  max-width: 500px;
}

/* Logo容器 */
.logo-container {
  position: relative;
  display: inline-block;
  margin-bottom: 30px;
}

.logo-icon {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
  position: relative;
  z-index: 2;
  border: 2px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.logo-pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: pulse 2s ease-out infinite;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0.8;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 0;
  }
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 20px;
  font-weight: 400;
  opacity: 0.9;
  margin-bottom: 50px;
  letter-spacing: 1px;
}

/* 特性列表 */
.feature-list {
  text-align: left;
  display: inline-block;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
  opacity: 0.9;

  i {
    margin-right: 12px;
    font-size: 20px;
    color: #87CEEB;
  }
}

/* 右侧表单区域 */
.form-section {
  width: 580px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 10;
  padding: 40px;
}

.form-container {
  width: 100%;
  max-width: 480px;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.98) 100%);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 50px 40px;
  box-shadow:
      0 25px 80px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255, 255, 255, 0.6) inset,
      0 10px 30px rgba(79, 172, 254, 0.1);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.form-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  border-radius: 24px 24px 0 0;
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 25px;
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  text-shadow: none;
}

.form-subtitle {
  font-size: 16px;
  color: #64748b;
  font-weight: 500;
}

/* 表单样式 */
.register-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  .el-form-item__error {
    font-size: 12px;
    padding-top: 4px;
  }
}

/* 角色选择样式 */
.role-selection {
  margin-bottom: 4px;

  .role-label {
    display: block;
    font-size: 16px;
    font-weight: 600;
    color: #374151;
    margin-bottom: 10px;
    text-align: center;
  }

  .role-select {
    width: 100%;

    ::v-deep .el-input__inner {
      height: 50px;
      border-radius: 12px;
      border: 2px solid #e2e8f0;
      padding: 0 16px;
      font-size: 15px;
      transition: all 0.3s ease;
      background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
      color: #374151;
      font-weight: 500;

      &:hover {
        border-color: #cbd5e0;
        background: #ffffff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      }

      &:focus {
        border-color: #667eea;
        background: #ffffff;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1), 0 4px 12px rgba(102, 126, 234, 0.15);
      }
    }

    ::v-deep .el-input__suffix {
      right: 16px;
    }
  }
}

/* 角色选项样式 */
::v-deep .el-select-dropdown__item {
  height: auto;
  padding: 12px 20px;

  &.hover {
    background: linear-gradient(145deg, #f8fafc 0%, #f1f5f9 100%);
  }

  &.selected {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    font-weight: 600;

    .role-option-content i {
      color: white;
    }
  }
}

.role-option-content {
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    font-size: 18px;
    color: #667eea;
    width: 20px;
    text-align: center;
  }

  span {
    font-size: 15px;
    font-weight: 500;
  }
}

/* 输入框组 */
.input-group {
  position: relative;
  display: flex;
  align-items: center;

  .input-icon-wrapper {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px 0 0 12px;
    z-index: 2;
    box-shadow: inset 2px 0 4px rgba(0, 0, 0, 0.1);
  }

  .input-icon {
    font-size: 18px;
    color: white;
  }

  .custom-input {
    width: 100%;

    ::v-deep .el-input__inner {
      height: 50px;
      border-radius: 12px;
      border: 2px solid #e2e8f0;
      padding-left: 60px;
      padding-right: 16px;
      font-size: 15px;
      transition: all 0.3s ease;
      background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
      color: #374151;
      font-weight: 500;

      &:hover {
        border-color: #cbd5e0;
        background: #ffffff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      }

      &:focus {
        border-color: #667eea;
        background: #ffffff;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1), 0 4px 12px rgba(102, 126, 234, 0.15);
      }
    }

    ::v-deep .el-input__suffix {
      right: 16px;
    }
  }

  &.verification-group {
    display: flex;
    gap: 12px;

    .verification-input {
      flex: 1;
    }
  }
}

/* 验证码按钮 */
.send-code-btn {
  height: 50px;
  padding: 0 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  color: white;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 120px;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
  }

  &:disabled {
    background: #d1d5db;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

/* 复选框样式 */
.agreement-check {
  ::v-deep .el-checkbox__label {
    color: #4a5568;
    font-size: 14px;
    font-weight: 500;
  }

  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
    background-color: #667eea;
    border-color: #667eea;
  }
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;

  &:hover {
    color: #5a67d8;
    text-decoration: underline;
  }
}

/* 按钮组 */
.button-group {
  margin-top: 20px;
}

.register-btn {
  width: 100%;
  height: 54px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);

  i {
    font-size: 18px;
  }

  &:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  }

  &:active {
    transform: translateY(-1px);
  }

  &:disabled {
    background: #d1d5db;
    transform: none;
    box-shadow: none;
  }
}

.cancel-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: transparent;
  color: #667eea;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  i {
    font-size: 16px;
  }

  &:hover {
    background: linear-gradient(145deg, #f8fafc 0%, #f1f5f9 100%);
    border-color: #667eea;
    color: #5a67d8;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  }
}

/* 装饰元素 */
.decoration-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 5;
}

.float-element {
  position: absolute;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(5px);
  border-radius: 50%;
  animation: floatElement 15s infinite ease-in-out;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.element-1 {
  width: 60px;
  height: 60px;
  top: 10%;
  right: 10%;
  animation-delay: 0s;
}

.element-2 {
  width: 40px;
  height: 40px;
  bottom: 15%;
  left: 5%;
  animation-delay: 5s;
}

.element-3 {
  width: 80px;
  height: 80px;
  top: 60%;
  left: 10%;
  animation-delay: 10s;
}

@keyframes floatElement {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 条款弹窗样式 */
::v-deep .terms-dialog {
  .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);

    .el-dialog__header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 20px 24px;
      border-radius: 16px 16px 0 0;

      .el-dialog__title {
        font-size: 20px;
        font-weight: 600;
      }

      .el-dialog__close {
        color: white;
        font-size: 20px;

        &:hover {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }

    .el-dialog__body {
      padding: 30px 24px;
      max-height: 60vh;
      overflow-y: auto;
    }

    .el-dialog__footer {
      padding: 20px 24px;
      border-top: 1px solid #e2e8f0;
      background: #f8fafc;
      border-radius: 0 0 16px 16px;
    }
  }
}

.terms-content {
  line-height: 1.6;
  color: #374151;

  h3 {
    font-size: 24px;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 20px;
    text-align: center;
  }

  h4 {
    font-size: 18px;
    font-weight: 600;
    color: #374151;
    margin: 24px 0 12px 0;
    padding-left: 12px;
    border-left: 4px solid #667eea;
  }

  p {
    margin-bottom: 12px;
    text-align: justify;

    &.update-time {
      margin-top: 30px;
      text-align: right;
      font-style: italic;
      color: #6b7280;
      border-top: 1px solid #e5e7eb;
      padding-top: 20px;
    }
  }
}

.dialog-footer {
  text-align: right;

  .el-button {
    border-radius: 8px;
    padding: 10px 20px;
    font-weight: 600;

    &.el-button--primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .brand-section {
    display: none;
  }

  .form-section {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .form-section {
    padding: 20px;
  }

  .form-container {
    padding: 30px 20px;
  }

  .form-title {
    font-size: 24px;
  }

  .brand-title {
    font-size: 36px;
  }

  .input-group {
    &.verification-group {
      flex-direction: column;
      gap: 16px;

      .verification-input {
        width: 100%;
      }

      .send-code-btn {
        width: 100%;
        min-width: auto;
      }
    }
  }
}
</style>