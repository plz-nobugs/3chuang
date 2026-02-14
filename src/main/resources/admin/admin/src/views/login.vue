@keyframes ripple {
0% {
transform: scale(0, 0);
opacity: 0.5;
}
20% {
transform: scale(25, 25);
opacity: 0.3;
}
100% {
opacity: 0;
transform: scale(40, 40);
}
}  .code-area {
margin-bottom: 20px;

.code-input-container {
display: flex;
align-items: center;

.code-input {
flex: 1;
}

.verify-code {
width: 100px;
height: 40px;
margin-left: 10px;
background-color: rgba(240, 240, 240, 0.8);
border-radius: 5px;
display: flex;
align-items: center;
justify-content: center;
cursor: pointer;
user-select: none;

span {
display: inline-block;
margin: 0 2px;
font-weight: bold;
}

&:hover {
background-color: rgba(230, 230, 230, 0.9);
}
}
}
}<template>
  <div>
    <div class="container loginIn" style="background-image: linear-gradient(135deg, #1a237e, #283593, #303f9f, #3949ab, #3f51b5)">
      <!-- 左侧动画区域 -->
      <div class="bg animation-area">
        <div class="floating-elements">
          <div class="floating-element e1"></div>
          <div class="floating-element e2"></div>
          <div class="floating-element e3"></div>
          <div class="floating-element e4"></div>
        </div>

        <div class="project-text">
          <h2>智能物流 循环共生</h2>
          <p>易退智联 · 电商资源循环圈</p>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <el-form class="login-form" label-position="left" :label-width="0">
        <div class="h1">
          <div>欢迎使用</div>
          <div>智能物流管理系统</div>
        </div>

        <!-- 用户名输入框 -->
        <el-form-item>
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input placeholder="请输入用户名" name="username" type="text" v-model="rulesForm.username" />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item>
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input placeholder="请输入密码" name="password" type="password" v-model="rulesForm.password" />
        </el-form-item>

        <!-- 角色选择 -->
        <el-form-item>
          <span>角色</span>
          <el-select v-model="rulesForm.role" placeholder="请选择角色" class="role-select">
            <el-option
                v-for="item in menus"
                v-if="item.hasBackLogin=='是'"
                :key="item.roleName"
                :label="item.roleName"
                :value="item.roleName">
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item class="code-area">
          <span>验证码</span>
          <div class="code-input-container">
            <el-input placeholder="请输入验证码" v-model="rulesForm.code" class="code-input" />
            <div class="verify-code" @click="getRandCode(4)">
              <span v-for="(item, index) in codes" :key="index" :style="{color:item.color,transform:item.rotate,fontSize:item.size}">{{ item.num }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-button type="primary" @click="login()" class="btn-login">
          登录
        </el-button>

        <!-- 注册链接 -->
        <div class="register-link">
          <span @click="register('guke')">没有账号？立即注册</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import menu from "@/utils/menu";
export default {
  data() {
    return {
      rulesForm: {
        username: "",
        password: "",
        role: "",
        code: '',
      },
      menus: [],
      tableName: "",
      loading: false,
      codes: [{
        num: 1,
        color: '#000',
        rotate: '10deg',
        size: '16px'
      },{
        num: 2,
        color: '#000',
        rotate: '10deg',
        size: '16px'
      },{
        num: 3,
        color: '#000',
        rotate: '10deg',
        size: '16px'
      },{
        num: 4,
        color: '#000',
        rotate: '10deg',
        size: '16px'
      }],
    };
  },
  mounted() {
    let menus = menu.list();
    this.menus = menus;
  },
  created() {
    this.setInputColor();
    // 初始化生成验证码
    this.getRandCode(4);
  },
  methods: {
    setInputColor(){
      this.$nextTick(()=>{
        document.querySelectorAll('.loginIn .el-input__inner').forEach(el=>{
          el.style.backgroundColor = "rgba(249, 250, 252, 0.8)";
          el.style.color = "#333";
          el.style.height = "40px";
          el.style.borderRadius = "20px";
          el.style.border = "1px solid #e0e0e0";
        });

        document.querySelectorAll('.loginIn .svg-container').forEach(el=>{
          el.style.color = "#3f51b5";
          el.style.position = "absolute";
          el.style.left = "10px";
          el.style.top = "50%";
          el.style.transform = "translateY(-50%)";
          el.style.zIndex = "2";
        });
      });
    },
    register(tableName){
      this.$storage.set("loginTable", tableName);
      this.$router.push({path:'/register'});
    },
    // 登录
    login() {
      this.loading = true;

      // 验证码检查
      let code = '';
      for(let i in this.codes) {
        code += this.codes[i].num;
      }

      if (!this.rulesForm.code) {
        this.$message.error("请输入验证码");
        this.loading = false;
        return;
      }

      if (this.rulesForm.code.toLowerCase() != code.toLowerCase()) {
        this.$message.error("验证码输入有误");
        this.getRandCode();
        this.loading = false;
        return;
      }

      if (!this.rulesForm.username) {
        this.$message.error("请输入用户名");
        this.loading = false;
        return;
      }
      if (!this.rulesForm.password) {
        this.$message.error("请输入密码");
        this.loading = false;
        return;
      }
      if (!this.rulesForm.role) {
        this.$message.error("请选择角色");
        this.loading = false;
        return;
      }

      let menus = this.menus;
      for (let i = 0; i < menus.length; i++) {
        if (menus[i].roleName == this.rulesForm.role) {
          this.tableName = menus[i].tableName;
        }
      }

      this.$http({
        url: `${this.tableName}/login?username=${this.rulesForm.username}&password=${this.rulesForm.password}`,
        method: "post"
      }).then(({ data }) => {
        this.loading = false;
        if (data && data.code === 0) {
          // 显示登录成功提示
          this.$message.success("登录成功");

          this.$storage.set("Token", data.token);
          this.$storage.set("role", this.rulesForm.role);
          this.$storage.set("sessionTable", this.tableName);
          this.$storage.set("adminName", this.rulesForm.username);

          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            this.$router.replace({ path: "/index/" });
          }, 1000);
        } else {
          this.$message.error(data.msg);
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error("登录失败，请检查网络连接");
      });
    },
    getRandCode(len = 4){
      this.randomString(len);
    },
    randomString(len = 4) {
      let chars = [
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
        "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2",
        "3", "4", "5", "6", "7", "8", "9"
      ];
      let colors = ["0", "1", "2","3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"];
      let sizes = ['14', '15', '16', '17', '18'];

      let output = [];
      for (let i = 0; i < len; i++) {
        // 随机验证码
        let key = Math.floor(Math.random()*chars.length);
        this.codes[i].num = chars[key];
        // 随机验证码颜色
        let code = '#';
        for (let j = 0; j < 6; j++) {
          let key = Math.floor(Math.random()*colors.length);
          code += colors[key];
        }
        this.codes[i].color = code;
        // 随机验证码方向
        let rotate = Math.floor(Math.random()*60);
        let plus = Math.floor(Math.random()*2);
        if(plus == 1) rotate = '-'+rotate;
        this.codes[i].rotate = 'rotate('+rotate+'deg)';
        // 随机验证码字体大小
        let size = Math.floor(Math.random()*sizes.length);
        this.codes[i].size = sizes[size]+'px';
      }
    },
  }
};
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
}

.bg {
  position: absolute;
  top: 0;
  left: 0;
  width: calc(100% - 350px);
  height: 100%;
}

.animation-area {
  overflow: hidden;

  .project-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #ffffff;
    z-index: 10;

    h2 {
      font-size: 38px;
      font-weight: 600;
      margin-bottom: 15px;
      text-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
      animation: fadeInUp 1.2s ease-out;
    }

    p {
      font-size: 22px;
      opacity: 0.9;
      font-weight: 400;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
      animation: fadeInUp 1.2s ease-out 0.3s backwards;
    }
  }

  .floating-elements {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;

    .floating-element {
      position: absolute;
      border-radius: 50%;
      opacity: 0.7;
      box-shadow: 0 0 30px rgba(255, 255, 255, 0.2);
    }

    .e1 {
      width: 160px;
      height: 160px;
      background: radial-gradient(circle at 30% 30%, rgba(140, 158, 255, 0.7), rgba(83, 109, 254, 0.7));
      top: 15%;
      left: 25%;
      animation: float1 15s ease-in-out infinite;
    }

    .e2 {
      width: 100px;
      height: 100px;
      background: radial-gradient(circle at 30% 30%, rgba(130, 177, 255, 0.7), rgba(68, 138, 255, 0.7));
      top: 65%;
      left: 60%;
      animation: float2 18s ease-in-out infinite;
    }

    .e3 {
      width: 80px;
      height: 80px;
      background: radial-gradient(circle at 30% 30%, rgba(179, 136, 255, 0.7), rgba(124, 77, 255, 0.7));
      top: 35%;
      left: 70%;
      animation: float3 12s ease-in-out infinite;
    }

    .e4 {
      width: 120px;
      height: 120px;
      background: radial-gradient(circle at 30% 30%, rgba(148, 159, 244, 0.7), rgba(83, 109, 254, 0.7));
      top: 70%;
      left: 20%;
      animation: float4 20s ease-in-out infinite;
    }
  }
}

.login-form {
  position: absolute;
  top: 0;
  right: 0;
  width: 350px;
  height: 100%;
  background: #ffffff;
  padding: 0 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);

  .h1 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 20px;
    font-weight: bold;
    color: #3f51b5;

    div:first-child {
      margin-bottom: 5px;
    }
  }

  .el-form-item {
    position: relative;
    margin-bottom: 20px;
  }

  .el-input {
    width: 100%;

    /deep/ input {
      padding-left: 35px;
    }
  }

  .role-select {
    width: 100%;
  }

  .btn-login {
    width: 100%;
    height: 40px;
    margin-top: 20px;
    border-radius: 20px;
    background: #3f51b5;
    border: none;
    font-size: 16px;
    position: relative;
    overflow: hidden;

    &:hover {
      background: #303f9f;
    }

    &:after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 5px;
      height: 5px;
      background: rgba(255, 255, 255, 0.5);
      opacity: 0;
      border-radius: 100%;
      transform: scale(1, 1) translate(-50%);
      transform-origin: 50% 50%;
    }

    &:focus:not(:active)::after {
      animation: ripple 1s ease-out;
    }
  }

  .register-link {
    text-align: center;
    margin-top: 15px;
    font-size: 14px;
    color: #3f51b5;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* 动画定义 */
@keyframes float1 {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(-30px, 15px) rotate(5deg); }
  50% { transform: translate(-20px, 25px) rotate(0deg); }
  75% { transform: translate(10px, 10px) rotate(-5deg); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(25px, -25px) scale(1.1); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(-20px, -10px) rotate(10deg); }
  66% { transform: translate(10px, -15px) rotate(-10deg); }
}

@keyframes float4 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(15px, 15px) scale(0.9); }
  50% { transform: translate(30px, 0px) scale(1); }
  75% { transform: translate(15px, -15px) scale(1.1); }
}

@keyframes fadeInUp {
  0% { opacity: 0; transform: translateY(20px); }
  100% { opacity: 1; transform: translateY(0); }
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .bg {
    display: none;
  }

  .login-form {
    width: 90%;
    max-width: 350px;
    height: auto;
    position: relative;
    margin: 30px auto;
    padding: 30px 20px;
    border-radius: 10px;
  }
}
</style>