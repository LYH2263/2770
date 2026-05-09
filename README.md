# 学生管理系统

## 🛠 技术栈
- **前端**: Vue3 + Vite + Windicss + Element Plus
- **后端**: Spring Boot 3.2.2 + MyBatis Plus + MySQL 8.0
- **数据库**: MySQL 8.0 (utf8mb4字符集)

## 🚀 启动指南 (How to Run)
1. 确保 Docker Desktop 已启动
2. 在根目录执行：
   ```bash
   docker compose up --build
   ```
3. 等待容器启动完成（首次构建约需5-10分钟）
4. 启动成功后，访问前端地址即可使用

## 🔗 服务地址 (Services)
- **前端**: http://localhost:3270
- **后端API**: http://localhost:8270
- **数据库**: localhost:33270
  - 用户名: root
  - 密码: root123456
  - 数据库名: student_db

## 🧪 测试账号
- **用户名**: admin
- **密码**: 123456

## 📋 功能特性
- ✅ 用户登录认证 (JWT)
- ✅ 学生信息管理（增删改查）
- ✅ 分页查询和搜索
- ✅ 表单验证
- ✅ 现代化UI设计
- ✅ 响应式布局
- ✅ 完全容器化部署

## 🎨 界面特色
- 渐变背景设计
- 平滑动画效果
- 卡片阴影和悬停效果
- 现代化表单组件
- UI对话框提示（无原生alert/confirm）

## 📦 项目结构
```
.
├── backend/                 # Spring Boot后端
│   ├── src/                # 源代码
│   ├── pom.xml            # Maven配置
│   ├── settings.xml       # Maven阿里云镜像
│   ├── init.sql           # 数据库初始化脚本
│   └── Dockerfile         # 后端Docker配置
├── frontend/               # Vue3前端
│   ├── src/               # 源代码
│   ├── package.json       # npm配置
│   ├── nginx.conf         # Nginx配置
│   └── Dockerfile         # 前端Docker配置
└── docker-compose.yml     # Docker编排文件
```

## 🐳 Docker 镜像源配置

### 推荐配置
- **Docker镜像**: 使用官方Docker Hub镜像
- **npm依赖**: 配置淘宝镜像源加速
- **Maven依赖**: 配置阿里云镜像源加速

### 端口说明
根据项目目录 `2770`：
- 前端端口: **3270**
- 后端端口: **8270**
- 数据库端口: **3306**

## 📝 技术亮点
1. **完全容器化**: 一键启动，无需本地环境
2. **字符集配置**: 全链路utf8mb4，支持中文
3. **数据初始化**: 自动填充演示数据
4. **健康检查**: 服务依赖和启动顺序控制
5. **多阶段构建**: 优化镜像体积
6. **镜像加速**: npm/Maven使用国内源

## 🔧 开发说明
- 后端采用分层架构：Controller -> Service -> Mapper
- 前端采用组件化设计
- 使用ORM进行数据库操作
- 全局异常处理和错误提示
- JWT身份认证
- 表单验证和数据校验

## 📖 常见问题

**Q: Docker 启动失败？**  
A: 检查 Docker Desktop 是否正常运行，端口 3270/8270/3306 是否被占用

**Q: 前端无法连接后端？**  
A: 等待所有容器启动完成，查看 `docker compose logs` 确认服务状态

**Q: 中文出现乱码？**  
A: 项目已配置 utf8mb4 字符集，如遇问题请检查数据库连接配置

**Q: 如何查看日志？**  
A: 使用 `docker compose logs -f [服务名]` 查看实时日志

## 🛑 停止项目
```bash
docker compose down
```

## 🗑️ 清理数据
```bash
docker compose down -v
```
