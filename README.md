# 企业会议室预订系统

基于 Spring Boot + Vue3 开发的企业内部会议室预订管理系统。

## 技术栈

### 后端
- Java 8
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.2
- Sa-Token 1.34.0（权限认证）
- PostgreSQL 42.6.0
- Lombok
- Hutool

### 前端
- Vue 3.3.4
- Vue Router 4.2.4
- Element Plus 2.3.14
- Axios 1.5.0
- Day.js 1.11.10
- Vite 4.4.9

## 功能特性

1. **会议室管理**
   - 会议室信息维护（名称、容纳人数、设备设施、位置）
   - 会议室状态管理（可用/不可用）

2. **员工预订**
   - 选择会议室和时间段
   - 填写会议主题和用途说明
   - 参会人数设置
   - 预订取消功能

3. **审批流程**
   - 预订提交后默认为待审批状态
   - 部门经理或管理员可审批（批准/拒绝）
   - 审批意见记录

4. **冲突检测**
   - 同一时间段同一会议室不可重复预订
   - 预订时间合法性校验

5. **时间轴展示**
   - 今日所有会议室占用情况可视化展示
   - 不同状态预订使用不同颜色区分
   - 当前时间红线指示

## 项目结构

```
test5/
├── backend/                 # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/meeting/
│   │       │   ├── common/      # 通用类
│   │       │   ├── config/      # 配置类
│   │       │   ├── controller/  # 控制器
│   │       │   ├── entity/      # 实体类
│   │       │   │   ├── dto/     # 数据传输对象
│   │       │   │   └── vo/      # 视图对象
│   │       │   ├── exception/   # 异常处理
│   │       │   ├── mapper/      # 数据访问层
│   │       │   └── service/     # 业务逻辑层
│   │       └── resources/
│   │           ├── mapper/      # MyBatis XML 映射文件
│   │           └── application.yml
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── components/      # 组件
│   │   ├── router/          # 路由
│   │   ├── types/           # 类型定义
│   │   ├── views/           # 页面
│   │   ├── App.vue
│   │   ├── main.js
│   │   └── style.css
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── database/                # 数据库脚本
│   └── init.sql
└── README.md
```

## 快速开始

### 1. 数据库初始化

安装 PostgreSQL 数据库，创建数据库并执行初始化脚本：

```sql
-- 创建数据库
CREATE DATABASE meeting_booking;

-- 执行初始化脚本
\c meeting_booking
\i database/init.sql
```

数据库表结构：
- `t_department` - 部门表
- `t_user` - 用户表（员工表）
- `t_meeting_room` - 会议室表
- `t_booking` - 预订表
- `t_approval` - 审批表

### 2. 后端启动

修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/meeting_booking
    username: postgres
    password: your_password
```

进入后端目录，启动项目：

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务启动后访问：http://localhost:8080/api

### 3. 前端启动

进入前端目录，安装依赖并启动：

```bash
cd frontend
npm install
npm run dev
```

前端服务启动后访问：http://localhost:3000

## 测试账号

初始化脚本中已预置以下测试账号（密码均为 `123456`）：

| 账号 | 姓名 | 角色 | 说明 |
|------|------|------|------|
| admin | 系统管理员 | ADMIN | 管理员，拥有所有权限 |
| zhangsan | 张三 | MANAGER | 技术部经理，可审批预订 |
| lisi | 李四 | MANAGER | 产品部经理，可审批预订 |
| wangwu | 王五 | EMPLOYEE | 普通员工，可提交预订 |
| zhaoliu | 赵六 | EMPLOYEE | 普通员工，可提交预订 |
| qianqi | 钱七 | EMPLOYEE | 普通员工，可提交预订 |

## API 接口

### 认证接口
- `POST /api/user/login` - 用户登录
- `POST /api/user/logout` - 用户登出
- `GET /api/user/info` - 获取当前用户信息

### 会议室接口
- `GET /api/room/list` - 获取会议室列表
- `GET /api/room/{id}` - 获取会议室详情
- `POST /api/room` - 新增会议室
- `PUT /api/room` - 更新会议室
- `DELETE /api/room/{id}` - 删除会议室

### 预订接口
- `POST /api/booking` - 创建预订
- `POST /api/booking/cancel/{id}` - 取消预订
- `GET /api/booking/today` - 获取今日所有预订
- `GET /api/booking/today/room/{roomId}` - 获取指定会议室今日预订
- `GET /api/booking/my` - 获取我的预订
- `GET /api/booking/pending` - 获取待审批预订
- `GET /api/booking/{id}` - 获取预订详情

### 审批接口
- `POST /api/approval` - 审批预订

## 核心业务流程

### 预订流程
1. 员工登录系统
2. 查看今日会议室占用情况时间轴
3. 点击"新建预订"按钮
4. 选择会议室、填写会议信息、选择时间段
5. 提交预订（自动检测时间冲突）
6. 等待部门经理或管理员审批

### 审批流程
1. 部门经理/管理员登录系统
2. 查看待审批预订列表
3. 点击批准或拒绝按钮
4. 预订状态更新

## 冲突检测规则

预订创建时会进行以下冲突检测：
1. 结束时间不能早于开始时间
2. 预订时间不能早于当前时间
3. 会议室必须存在且可用
4. 参会人数不能超过会议室容纳人数
5. 同一会议室在同一时间段不能有重叠的预订（已批准或待审批状态）

## 许可证

MIT License
