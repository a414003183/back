# 后端部署详细文档（Ubuntu LTS）

> 本文档针对 `C:\project\school\backend` Spring Boot 后端项目，部署到 Ubuntu 服务器的完整流程。
> 以下命令以 Ubuntu 22.04/24.04 LTS 为例，默认使用云服务商提供的 `ubuntu` 普通用户登录。
> 当前 JAR 已内置 Flyway baseline 脚本，首次启动会自动建表。

---

## 一、环境要求

| 组件 | 版本/说明 |
|------|----------|
| 服务器系统 | Ubuntu 22.04/24.04 LTS |
| JDK | 21+ |
| MySQL | 8.0+ |
| 构建工具 | Maven 3.9+（本地打包） |
| 反向代理 | Nginx（推荐） |
| 本地系统 | Windows 10/11（用于打包和上传） |

---

## 二、连接服务器

### 2.1 获取服务器信息

你需要提前准备：

- 服务器公网 IP（例如：`43.134.181.228`）
- 登录用户名（本文档统一使用 `ubuntu`）
- 登录密码或 SSH 私钥
- 服务器 SSH 端口（默认 `22`）

### 2.2 方式一：PowerShell / CMD 连接

在 Windows 上按 `Win + R`，输入 `powershell`，回车打开 PowerShell。

**密码登录：**

```powershell
ssh ubuntu@43.134.181.228
```

输入密码时不会显示任何字符，输入完成后直接回车。

**密钥登录：**

```powershell
ssh -i C:\Users\你的用户名\.ssh\id_rsa ubuntu@43.134.181.228
```

### 2.3 方式二：MobaXterm（图形化工具，推荐新手）

1. 下载安装 MobaXterm：https://mobaxterm.mobatek.net/
2. 点击左上角 `Session` → `SSH`
3. `Remote host` 填写服务器 IP
4. `Username` 填写 `ubuntu`
5. 点击 `OK`
6. 输入密码登录

### 2.4 方式三：VS Code Remote - SSH

1. 安装 VS Code 插件 `Remote - SSH`
2. 按 `Ctrl + Shift + P`，输入 `Remote-SSH: Connect to Host`
3. 输入 `ubuntu@43.134.181.228`
4. 输入密码连接

---

## 三、服务器基础配置

### 3.1 更新系统

连接成功后，在服务器终端执行：

```bash
sudo apt update && sudo apt upgrade -y
```

> Ubuntu 默认不启用 SELinux，无需像 CentOS/OpenCloudOS 那样额外关闭。

---

## 四、安装 JDK 21

### 4.1 检查是否已安装

```bash
java -version
```

### 4.2 安装 OpenJDK 21

```bash
sudo apt install openjdk-21-jdk -y
```

### 4.3 验证安装

```bash
java -version
```

预期输出：

```text
openjdk version "21.0.x" 202x-xx-xx
OpenJDK Runtime Environment
```

### 4.4 配置 JAVA_HOME（可选）

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' | sudo tee /etc/profile.d/java.sh
source /etc/profile.d/java.sh
```

---

## 五、安装 MySQL 8.0

### 5.1 安装 MySQL 服务端

```bash
sudo apt update
sudo apt install mysql-server-8.0 -y
```

> 如果提示找不到 `mysql-server-8.0`，可改用 `mysql-server`（会安装 Ubuntu 当前默认版本）。

### 5.2 启动并设置开机自启

```bash
sudo systemctl enable mysql
sudo systemctl start mysql
sudo systemctl status mysql
```

看到 `active (running)` 即成功。

### 5.3 设置 root 密码

Ubuntu 下 root 默认使用 `auth_socket` 插件，需要先改成密码登录，才能后续用 `mysql -u root -p` 执行脚本。

```bash
sudo mysql
```

进入 MySQL 后执行：

```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '你的强密码';
FLUSH PRIVILEGES;
EXIT;
```

### 5.4 创建项目数据库

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS telecom_supply_chain CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

> 首次启动应用时，Flyway 会自动在 `telecom_supply_chain` 库中建表，无需手动执行任何 SQL。

---

## 六、本地打包后端项目

### 6.1 进入后端目录

```powershell
cd C:\project\school\backend
```

### 6.2 执行 Maven 打包

```powershell
mvn clean package -DskipTests
```

### 6.3 确认 JAR 生成

打包完成后，检查文件：

```powershell
ls C:\project\school\backend\target\telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

JAR 大小约 60MB，已内置所有 Flyway 迁移脚本。

---

## 七、上传文件到服务器

### 7.1 在服务器创建目录并授权

先 SSH 登录服务器，执行：

```bash
sudo mkdir -p /opt/telecom-scm/uploads
sudo chown -R ubuntu:ubuntu /opt/telecom-scm
```

> 这里把目录临时属主设为 `ubuntu`，是为了让本地 `scp` 能直接上传文件。服务启动前再改回 `telecom` 用户。

### 7.2 方式一：对象存储下载（网络慢时推荐）

如果本地直接上传服务器速度很慢，推荐先把 JAR 包上传到对象存储（如腾讯云 COS），再在服务器上直接下载。

**当前 JAR 对象地址：**

```text
https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

在服务器上下载：

```bash
cd /opt/telecom-scm
wget https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

或者使用 `curl`：

```bash
cd /opt/telecom-scm
curl -O https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

下载完成后检查：

```bash
ls -lh /opt/telecom-scm/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

> 注意：对象存储 URL 必须允许公网读取。如果 URL 有访问有效期，请在过期前重新生成；生产环境建议给对象设置固定公有读权限或使用带签名的私有 URL。

### 7.3 方式二：PowerShell scp 直接上传

网络稳定时使用：

```powershell
scp C:\project\school\backend\target\telecom-scm-backend-0.0.1-SNAPSHOT.jar ubuntu@43.134.181.228:/opt/telecom-scm/
```

> 把 `43.134.181.228` 替换成你的服务器实际 IP。

### 7.4 方式三：分卷上传（网络不稳时推荐）

JAR 约 60MB，如果直接 `scp` 中途断线，可以切成 10MB 分卷逐个传。

**本地切分（已为你准备好）：**

```powershell
cd C:\project\school\backend\target
split -b 10m telecom-scm-backend-0.0.1-SNAPSHOT.jar telecom-scm-backend.part.
```

会生成 6 个文件：

```text
telecom-scm-backend.part.aa  (10 MB)
telecom-scm-backend.part.ab  (10 MB)
telecom-scm-backend.part.ac  (10 MB)
telecom-scm-backend.part.ad  (10 MB)
telecom-scm-backend.part.ae  (10 MB)
telecom-scm-backend.part.af  (9.5 MB)
```

**上传所有分卷：**

```powershell
scp C:\project\school\backend\target\telecom-scm-backend.part.* ubuntu@43.134.181.228:/opt/telecom-scm/
```

如果某个分卷断了，只重传那一个，例如：

```powershell
scp C:\project\school\backend\target\telecom-scm-backend.part.ad ubuntu@43.134.181.228:/opt/telecom-scm/
```

**服务器端合并：**

```bash
cd /opt/telecom-scm
cat telecom-scm-backend.part.* > telecom-scm-backend-0.0.1-SNAPSHOT.jar
rm telecom-scm-backend.part.*
ls -lh telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

### 7.5 方式四：MobaXterm 上传

1. 用 MobaXterm 连接服务器
2. 左侧会显示文件浏览器
3. 找到 `/opt/telecom-scm` 目录
4. 直接把本地文件拖拽到该目录

### 7.6 方式五：FileZilla（FTP/SFTP 图形化工具，支持断点续传）

1. 下载安装 FileZilla：https://filezilla-project.org/
2. 点击 `文件` → `站点管理器` → `新站点`
3. 协议选择 `SFTP - SSH File Transfer Protocol`
4. 主机填写服务器 IP，端口 `22`
5. 登录类型选择 `正常`，用户名 `ubuntu`，密码填写你的密码
6. 点击 `连接`
7. 左侧本地目录选择 `C:\project\school\backend\target`，右侧服务器目录选择 `/opt/telecom-scm`
8. 拖拽 `telecom-scm-backend-0.0.1-SNAPSHOT.jar` 到右侧

### 7.7 上传已有 uploads 文件（可选）

```powershell
scp -r C:\project\school\uploads\* ubuntu@43.134.181.228:/opt/telecom-scm/uploads/
```

### 7.8 上传后检查

在服务器上执行：

```bash
ls -lh /opt/telecom-scm/
```

---

## 八、启动后端服务

### 8.1 创建运行用户并修正目录权限

```bash
sudo useradd -r -s /bin/false telecom || true
sudo chown -R telecom:telecom /opt/telecom-scm
```

> 上传阶段目录属主是 `ubuntu`，这里改回 `telecom`，确保服务以最小权限运行。

### 8.2 方式一：临时启动（调试用）

```bash
cd /opt/telecom-scm
export DB_URL="jdbc:mysql://127.0.0.1:3306/telecom_supply_chain?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false"
export DB_USERNAME="root"
export DB_PASSWORD="你的强密码"
export JWT_SECRET="生产环境随机强密钥至少32位"
export FILE_STORAGE_DIR="/opt/telecom-scm/uploads"

java -jar telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

看到 `Tomcat started on port 8080` 即成功。按 `Ctrl + C` 停止。

> 第一次启动时，Flyway 会自动建表，ApplicationRunner 会补充剩余表和字段。根据服务器性能可能需要 10~30 秒。

### 8.3 方式二：systemd 服务启动（生产推荐）

创建服务文件：

```bash
sudo tee /etc/systemd/system/telecom-scm.service << 'EOF'
[Unit]
Description=Telecom SCM Backend
After=network.target mysql.service

[Service]
User=telecom
Group=telecom
WorkingDirectory=/opt/telecom-scm
ExecStart=/usr/bin/java -jar /opt/telecom-scm/telecom-scm-backend-0.0.1-SNAPSHOT.jar
Environment="DB_URL=jdbc:mysql://127.0.0.1:3306/telecom_supply_chain?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false"
Environment="DB_USERNAME=root"
Environment="DB_PASSWORD=你的强密码"
Environment="JWT_SECRET=生产环境随机强密钥至少32位"
Environment="FILE_STORAGE_DIR=/opt/telecom-scm/uploads"
Environment="APP_CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:*,http://127.0.0.1:*,http://你的前端域名"
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

重载并启动：

```bash
sudo systemctl daemon-reload
sudo systemctl enable telecom-scm
sudo systemctl start telecom-scm
sudo systemctl status telecom-scm
```

查看实时日志：

```bash
sudo journalctl -u telecom-scm -f
```

### 8.4 常用服务管理命令

```bash
# 启动
sudo systemctl start telecom-scm

# 停止
sudo systemctl stop telecom-scm

# 重启
sudo systemctl restart telecom-scm

# 查看状态
sudo systemctl status telecom-scm

# 查看日志
sudo journalctl -u telecom-scm -n 100
```

---

## 九、安装和配置 Nginx

### 9.1 安装 Nginx

```bash
sudo apt install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx
```

### 9.2 配置反向代理（HTTP 版）

```bash
sudo tee /etc/nginx/sites-available/telecom-scm << 'EOF'
server {
    listen 80;
    server_name api.your-domain.com;

    client_max_body_size 50M;

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        alias /opt/telecom-scm/uploads/;
    }
}
EOF

sudo ln -sf /etc/nginx/sites-available/telecom-scm /etc/nginx/sites-enabled/telecom-scm
```

> 把 `api.your-domain.com` 替换成你的域名或服务器 IP。

### 9.3 测试并重载 Nginx

```bash
sudo nginx -t
sudo systemctl reload nginx
```

---

## 十、防火墙配置

Ubuntu 默认使用 `ufw`（Uncomplicated Firewall）：

```bash
# 允许 SSH，防止把自己锁在外面
sudo ufw allow OpenSSH

# 允许 HTTP/HTTPS
sudo ufw allow 'Nginx Full'

# 允许直接访问后端端口（调试时用，生产建议只通过 Nginx 暴露 80/443）
sudo ufw allow 8080/tcp

# 启用防火墙
sudo ufw enable

# 查看状态
sudo ufw status
```

---

## 十一、给 App 用：必须配置 HTTPS

App（Android/iOS）通常禁止明文 HTTP 请求，生产环境必须配置 HTTPS。

### 11.1 域名与证书

1. 准备一个域名，解析到服务器公网 IP（如 `api.your-domain.com`）。
2. 使用 Let's Encrypt 免费证书：

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d api.your-domain.com
```

按提示操作，certbot 会自动修改 Nginx 配置并续期。

### 11.2 Nginx HTTPS 配置

如果 certbot 没有自动配置，可手动写入：

```bash
sudo tee /etc/nginx/sites-available/telecom-scm << 'EOF'
server {
    listen 80;
    server_name api.your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.your-domain.com;

    ssl_certificate /etc/letsencrypt/live/api.your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.your-domain.com/privkey.pem;

    client_max_body_size 50M;

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        alias /opt/telecom-scm/uploads/;
    }
}
EOF

sudo ln -sf /etc/nginx/sites-available/telecom-scm /etc/nginx/sites-enabled/telecom-scm
sudo nginx -t
sudo systemctl reload nginx
```

### 11.3 后端 CORS 配置

修改 `/etc/systemd/system/telecom-scm.service` 中的 `APP_CORS_ALLOWED_ORIGIN_PATTERNS`，把 App/Web 域名加进去：

```ini
Environment="APP_CORS_ALLOWED_ORIGIN_PATTERNS=https://your-domain.com,https://app.your-domain.com,http://localhost:*,http://127.0.0.1:*"
```

然后重启：

```bash
sudo systemctl daemon-reload
sudo systemctl restart telecom-scm
```

### 11.4 App 端接口说明

项目已提供 App 端接口，前缀都是 `/api/app/`：

| 功能 | 路径 |
|------|------|
| 首页/商品 | `/api/app/mall/home`、`/api/app/mall/products`、`/api/app/mall/products/{id}` |
| 购物车 | `/api/app/mall/cart` |
| 客户资料/订单 | `/api/app/customer/profile`、`/api/app/customer/orders` |

App 登录后拿到 JWT Token，请求头里带上：

```http
Authorization: Bearer <token>
```

### 11.5 生产安全加固

1. **JWT Secret**：必须改成随机强密钥（至少 32 位），不要用默认值。
2. **数据库用户**：不要给后端用 `root`，单独创建：

   ```sql
   CREATE USER 'telecom_app'@'localhost' IDENTIFIED WITH mysql_native_password BY '强密码';
   GRANT SELECT, INSERT, UPDATE, DELETE ON telecom_supply_chain.* TO 'telecom_app'@'localhost';
   FLUSH PRIVILEGES;
   ```

   然后 `DB_USERNAME` 改为 `telecom_app`。
3. **防火墙**：生产环境只开放 80/443，关闭 8080：

   ```bash
   sudo ufw delete allow 8080/tcp
   ```

---

## 十二、验证部署

### 12.1 本地访问后端端口

```bash
curl http://127.0.0.1:8080/api/health
```

### 12.2 通过 Nginx 域名访问

```bash
curl https://api.your-domain.com/api/health
```

或在浏览器/App 里访问：

```text
https://api.your-domain.com/api/app/mall/home
```

---

## 十三、完整命令速查

### 服务器端

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 JDK
sudo apt install openjdk-21-jdk -y

# 安装 MySQL
sudo apt install mysql-server-8.0 -y
sudo systemctl enable mysql && sudo systemctl start mysql

# 设置 MySQL root 密码并创建数据库
sudo mysql <<'SQL'
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '你的强密码';
CREATE DATABASE IF NOT EXISTS telecom_supply_chain CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
FLUSH PRIVILEGES;
SQL

# 创建项目目录并授权给 ubuntu 用户用于上传
sudo mkdir -p /opt/telecom-scm/uploads
sudo chown -R ubuntu:ubuntu /opt/telecom-scm

# 安装 Nginx
sudo apt install nginx -y
sudo systemctl enable nginx && sudo systemctl start nginx
```

### 本地 PowerShell

```powershell
# 打包
cd C:\project\school\backend
mvn clean package -DskipTests

# 上传 JAR（网络稳定时）
scp C:\project\school\backend\target\telecom-scm-backend-0.0.1-SNAPSHOT.jar ubuntu@43.134.181.228:/opt/telecom-scm/

# 或上传分卷（网络不稳时）
# 先切分（已准备好）
cd C:\project\school\backend\target
split -b 10m telecom-scm-backend-0.0.1-SNAPSHOT.jar telecom-scm-backend.part.
# 再上传
scp C:\project\school\backend\target\telecom-scm-backend.part.* ubuntu@43.134.181.228:/opt/telecom-scm/

# 上传已有 uploads 文件
scp -r C:\project\school\uploads\* ubuntu@43.134.181.228:/opt/telecom-scm/uploads/
```

### 服务器端继续

```bash
# 方式 A：从对象存储直接下载 JAR（网络慢时推荐）
cd /opt/telecom-scm
wget https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar
# 或者：curl -O https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar

# 方式 B：如果是分卷上传，先合并
cd /opt/telecom-scm
cat telecom-scm-backend.part.* > telecom-scm-backend-0.0.1-SNAPSHOT.jar 2>/dev/null
rm -f telecom-scm-backend.part.*

# 改回服务运行用户权限
sudo useradd -r -s /bin/false telecom || true
sudo chown -R telecom:telecom /opt/telecom-scm

# 启动后端
sudo systemctl enable telecom-scm
sudo systemctl start telecom-scm

# 查看日志
sudo journalctl -u telecom-scm -f
```

---

## 十四、常见问题

### Q1：ssh 连接不上服务器？

- 检查服务器 IP、用户名、密码是否正确
- 检查服务器安全组是否放行 22 端口
- 检查服务器是否开启 SSH 服务：`sudo systemctl status ssh`

### Q2：MySQL 启动失败？

```bash
sudo systemctl status mysql
sudo tail -n 50 /var/log/mysql/error.log
```

### Q3：后端启动报数据库连接失败？

- 确认 MySQL 已启动：`sudo systemctl status mysql`
- 确认数据库已创建
- 确认 `DB_PASSWORD` 正确
- 确认 root 用户允许从 `localhost` 登录

### Q4：后端启动报 `Flyway migration checksum error`？

说明有人修改过已执行的 Flyway 脚本。解决：

```bash
# 清空 Flyway 历史，让应用重新执行 baseline（会丢失数据，仅用于空库开发）
mysql -u root -p -e "DROP TABLE IF EXISTS telecom_supply_chain.flyway_schema_history;"
```

### Q5：端口 8080 被占用？

```bash
sudo ss -tlnp | grep 8080
sudo lsof -i :8080
```

### Q6：前端/App 访问后端报 CORS 错误？

修改 `/etc/systemd/system/telecom-scm.service` 中的 `APP_CORS_ALLOWED_ORIGIN_PATTERNS`，添加前端/App 域名，然后重启：

```bash
sudo systemctl daemon-reload
sudo systemctl restart telecom-scm
```

### Q7：App 访问 HTTP 接口报错 / 无响应？

App 必须使用 HTTPS。配置域名 + Let's Encrypt 证书，参考第十一节。

### Q8：scp 上传中途断开或速度太慢？

推荐改用对象存储：把 JAR 上传到腾讯云 COS 等对象存储，再在服务器上通过 `wget`/`curl` 直接下载，参考第 7.2 节。

也可以继续使用分卷上传或 FileZilla，参考第七节。

---

## 十五、安全建议

1. **修改默认 JWT 密钥**，不要使用默认值
2. **设置强密码**，不要使用 `123456`
3. **不要直接暴露 8080 端口到公网**，通过 Nginx 反向代理
4. **生产必须配置 HTTPS**，使用 Let's Encrypt 免费证书
5. **数据库使用独立用户**，不要给后端用 `root`
6. **定期备份数据库**：

```bash
mysqldump -u root -p telecom_supply_chain > backup_$(date +%Y%m%d).sql
```

---

## 十六、更新/重新部署（已有服务时）

如果你已经部署过一次，只需要把新 JAR 替换到服务器并重启服务即可。以下是推荐流程：

### 16.1 停止当前服务

```bash
sudo systemctl stop telecom-scm
```

### 16.2 备份旧 JAR（可选但推荐）

```bash
cd /opt/telecom-scm
mv telecom-scm-backend-0.0.1-SNAPSHOT.jar telecom-scm-backend-0.0.1-SNAPSHOT.jar.bak.$(date +%Y%m%d%H%M%S)
```

### 16.3 从对象存储下载新 JAR

如果你已经把新 JAR 上传到 COS，在服务器上直接下载：

```bash
cd /opt/telecom-scm
wget -O telecom-scm-backend-0.0.1-SNAPSHOT.jar https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

> 使用 `-O` 参数指定输出文件名，确保覆盖旧文件。

或者使用 `curl`：

```bash
cd /opt/telecom-scm
curl -o telecom-scm-backend-0.0.1-SNAPSHOT.jar https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

### 16.4 修正文件权限

```bash
sudo chown telecom:telecom /opt/telecom-scm/telecom-scm-backend-0.0.1-SNAPSHOT.jar
```

### 16.5 启动服务

```bash
sudo systemctl start telecom-scm
```

### 16.6 查看日志确认启动成功

```bash
sudo journalctl -u telecom-scm -f
```

看到 `Started Telecom SCM Backend` 或 `Tomcat started on port 8080` 即表示启动成功。

### 16.7 验证接口

```bash
curl http://127.0.0.1:8080/api/health
```

### 16.8 数据库变更说明

- 如果新 JAR 包含新的 Flyway 迁移脚本，启动时会自动执行，无需手动操作。
- 如果启动报错 `Flyway migration checksum error`，说明有人修改过已执行的脚本，参考第十四节 Q4 处理（**会丢失数据，仅空库开发时使用**）。

### 16.9 完整更新命令速查

```bash
# 1. 停止服务
sudo systemctl stop telecom-scm

# 2. 备份旧 JAR
cd /opt/telecom-scm
mv telecom-scm-backend-0.0.1-SNAPSHOT.jar telecom-scm-backend-0.0.1-SNAPSHOT.jar.bak.$(date +%Y%m%d%H%M%S)

# 3. 下载新 JAR
wget -O telecom-scm-backend-0.0.1-SNAPSHOT.jar https://telecom-scm-backend-1404940047.cos.ap-guangzhou.myqcloud.com/telecom-scm-backend-0.0.1-SNAPSHOT.jar

# 4. 修正权限
sudo chown telecom:telecom telecom-scm-backend-0.0.1-SNAPSHOT.jar

# 5. 启动服务
sudo systemctl start telecom-scm

# 6. 查看日志
sudo journalctl -u telecom-scm -f
```

---

## 十七、GitHub Actions 自动部署（推荐）

> 如果你使用域名 `aixiaoya.site` 和服务器 `43.134.181.228`，推荐采用项目根目录 `DEPLOYMENT.md` 中的自动部署方案。
>
> 该方案把代码拆成两个仓库：
> - 前端：`https://github.com/a414003183/apps`
> - 后端：`https://github.com/a414003183/back`
>
> 每次 push 到 `main`/`master` 分支时，GitHub Actions 会自动在云端构建、打包、上传压缩包到服务器，并执行部署脚本。服务器只做解压和重启，不占构建资源，适合小服务器。
>
> 相关文件：
> - `apps/.github/workflows/deploy.yml`
> - `apps/deploy/deploy-web.sh`
> - `backend/.github/workflows/deploy.yml`
> - `backend/deploy/deploy-backend.sh`
> - `backend/deploy/nginx/aixiaoya.site.conf`
> - `backend/deploy/systemd/backend.service`
>
> 详细步骤请参考项目根目录的 `DEPLOYMENT.md`。
