# telecom-scm-backend

电信供应链管理系统后端服务。

## 技术栈

- Java 21
- Spring Boot 3.3.10
- Spring Security + JWT
- MyBatis-Plus 3.5.7
- MySQL 8.0
- Maven

## 目录结构

```
src/main/java/com/telecom/scm/
├── admin/              # 后台管理模块
├── aftersale/          # 售后模块
├── app/                # App 端接口
├── audit/              # 审计日志
├── common/             # 公共工具、异常、API 封装
├── config/             # 配置类
├── file/               # 文件存储
├── mall/               # 商城模块
├── member/             # 会员/商家/供应商模块
├── order/              # 订单模块
├── points/             # 积分模块
├── pricing/            # 价格/等级模块
└── security/           # 认证授权

src/main/resources/
├── application.yml     # 主配置
├── application-db.yml  # 数据库配置（db profile）
└── mapper/             # MyBatis XML Mapper
```

## 本地启动

### 前提条件

- MySQL 8.0 服务已启动（默认端口 3306）
- 数据库 `telecom_supply_chain` 已创建

### 方式一：直接运行 JAR

```powershell
cd C:/Users/41400/Desktop/毕设/backend
java -jar target/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

### 方式二：从源码编译运行

```powershell
mvn clean package -DskipTests
java -jar target/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

### 默认配置

- 端口：`8080`
- 数据库：`jdbc:mysql://127.0.0.1:3306/telecom_supply_chain`
- 默认用户名/密码：`root` / `123456`

数据库连接配置在 `src/main/resources/application-db.yml`，可通过环境变量覆盖：

```powershell
$env:DB_URL="jdbc:mysql://127.0.0.1:3306/telecom_supply_chain"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="123456"
java -jar target/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

## 开发规范

详见 [AGENTS.md](./AGENTS.md)。

## 测试

```powershell
# 编译
mvn clean compile

# 打包
mvn clean package -DskipTests
```

## 接口文档

启动后访问：

```
http://localhost:8080/api/health
```

## 数据库迁移

数据库 DDL/DML 变更通过 `sql/` 目录下的 Flyway 风格脚本管理（后续计划引入 Flyway/Liquibase）。
