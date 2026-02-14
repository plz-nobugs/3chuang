# application.yml已被移除！

# application.yml has been removed!


# 三创赛作品说明

## 1. 参考用户
- **管理员**：abo abo
- **用户**：user user
- **店主**：seller seller
- **员工**：worker worker

## 2. 配置环境
- **JetBrains IntelliJ IDEA**  
  （Java 开发工具，可通过学生认证免费获取）
- **MySQL 8.0.37 及以上版本**  
  （请确保版本 ≥ 8.0.37）
- **Navicat**  
  （数据库客户端工具，可通过学生认证免费获取）
- **Node.js 14**  
  （前端构建或服务器端使用，安装并确保 `npm` 正常工作）
- **Visual Studio 2017 / 2019 / 2022**  
  （安装 C++ 桌面开发和 Node.js 相关组件）
- **Python 2.x 系列**  
  （注意和 Python 3 的区分）

## 3. 使用说明
1. **确保数据库账户密码正确**  
   在 `src/main/resources/application.yml` 中查看并确认对应的数据库账户、密码、IP 等信息。
2. **数据库账户密码、IP 等关键信息必须严格保密**  
   切勿在公共场合或公共仓库公开。
3. **项目本身不要随意对外传播**  
   以保密为主，谨慎分享。
4. 修改 **Nginx.txt** 文件时  
   - 请在 **commit message** 中说明修改原因及修改内容。
5. **禁止进行压力测试**  
   程序目前可能存在比功能更多的 BUG，压力测试可能导致不可预料的问题。

---

## 4. Q & A

### Q1: GitHub 提交时提示「空仓库」
**解决方案**：
1. 进入项目根目录，右键选择 **Git Bash Here**（或打开命令行并切换到项目根目录）。
2. 执行以下命令：
   ```bash
   git commit -m "install commit!"


### Q2: 路径名过长问题

**症状**：在执行 Git 操作时，可能会出现类似「filename too long」或「pathspec too long」的错误。

**解决方案**：

1. **启用 Windows 系统的长路径支持**
    - 打开「注册表编辑器（regedit）」，导航至：  
        `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem`
    - 查找或新建一个名为 `LongPathsEnabled` 的 `DWORD (32-bit)` 值。
    - 将 `LongPathsEnabled` 的值设置为 `1`。
    - 如果有提示，重启电脑使修改生效。
2. **在 Git 中允许长路径**
    - 打开「命令提示符」或「Git Bash」，以管理员权限运行：
        
        ```bash
        git config --system core.longpaths true
        ```
        

### Q3: npm 无法 build

**前提**：请先确保 Node.js、npm 等环境安装正确。

**步骤**：

1. 执行：
    
    ```bash
    npm install
    ```
    
2. 等待依赖安装完成后，执行：
    
    ```bash
    npm run build
    ```
    
3. 若此过程无报错，则表示前端部分可以正常打包。

---

> **注意：**
> 
> - 如需修改配置或其他敏感信息，切勿在公开环境（如公共仓库）暴露相关内容。
> - 对于修改 Nginx 配置、Docker 配置或其他关键性脚本，一定要做好日志记录或在 commit message 中注明修改原因。

若有其他问题，可在对应文件处添加注释说明或另行更新至本文档。
