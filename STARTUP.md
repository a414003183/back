# 后端启动说明

## 技术栈
- Spring Boot 3.3.10
- Java 21
- MySQL 8.0
- MyBatis-Plus

## 前提条件

启动后端前，必须确保 **MySQL 服务正在运行**（端口 3306）：

```powershell
# 检查 MySQL 是否运行
netstat -ano | findstr ":3306"
# 或者
Get-Service MySQL80
```

如果 MySQL 没启动，去【服务】里手动启动 `MySQL80`。

---

## 方式一：直接运行 JAR（推荐 ⭐）

项目已经编译好了，直接运行现有的 JAR 包即可：

```powershell
cd "C:\Users\41400\Desktop\新建文件夹 (11)\backend"
java -jar target\telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

**默认配置：**
- 端口：`8080`
- 数据库：`jdbc:mysql://127.0.0.1:3306/telecom_supply_chain`
- 用户名：`root`
- 密码：`123456`

---

## 方式二：后台运行（不占用终端）

```powershell
cd "C:\Users\41400\Desktop\新建文件夹 (11)\backend"
java -jar target\telecom-scm-backend-0.0.1-SNAPSHOT.jar > backend.out.log 2> backend.err.log
```

日志会写入 `backend.out.log`，错误写入 `backend.err.log`。

---

## 方式三：从源码重新编译

如果修改了后端代码，需要重新打包：

```powershell
cd "C:\Users\41400\Desktop\新建文件夹 (11)\backend"

# 编译打包（跳过测试）
mvn clean package -DskipTests

# 运行新生成的 JAR
java -jar target\telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

> ⚠️ **注意**：当前项目路径包含中文 `新建文件夹 (11)`，某些构建工具（如 Gradle CMake）对中文路径支持不好。如果打包遇到 `FindFirstFileExA` 错误，请将项目复制到纯英文路径（如 `C:\temp\project`）后再编译。

---

## 验证启动成功

### 1. 看端口
```powershell
netstat -ano | findstr ":8080"
```
应显示 `0.0.0.0:8080 LISTENING`。

### 2. 看日志
```powershell
Get-Content backend.out.log -Tail 20
```
应包含 `Tomcat started on port 8080`。

### 3. 浏览器访问
```
http://192.168.0.27:8080/api/mall/home
```
应返回 JSON 数据。

---

## 停止后端

```powershell
# 查找 Java 进程并结束
taskkill /F /IM java.exe
```

或者只结束后端端口对应的进程：
```powershell
# 找到占用 8080 的 PID
netstat -ano | findstr ":8080"
# 结束该 PID
taskkill /F /PID <PID>
```

---

## 配置文件说明

| 文件 | 作用 |
|------|------|
| `src/main/resources/application.yml` | 主配置（端口、时区、JWT 等） |
| `src/main/resources/application-db.yml` | 数据库连接配置 |
| `src/main/resources/application-dev.yml` | 开发环境配置（如启用） |

数据库连接在 `application-db.yml` 中：
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/telecom_supply_chain?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: 123456
```

---

## 常见问题

### Q: 启动报错 `Communications link failure`
→ MySQL 没启动，去【服务】里启动 `MySQL80`。

### Q: 启动报错 `Port 8080 was already in use`
→ 8080 端口被占用，先结束之前的 Java 进程：
```powershell
taskkill /F /IM java.exe
```

### Q: App 连不上后端，提示超时
→ 检查手机和电脑是否连 **同一个 WiFi**，以及电脑防火墙是否允许 8080 端口。
→ App 中的 API 地址在 `android-mall-app/src/api/client.ts` 中配置，默认是 `http://192.168.0.27:8080/api`。
