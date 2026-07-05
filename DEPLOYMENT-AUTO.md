# aixiaoya.site 自动部署文档

> 域名：`aixiaoya.site`  
> 服务器：`43.134.181.228`  
> 代码仓库：前端 `https://github.com/a414003183/apps`，后端 `https://github.com/a414003183/back`

本文档描述使用 **GitHub Actions + 压缩包上传** 的自动部署方案，适合小服务器（不在服务器上构建）。

---

## 一、整体架构

```
本地 push 代码
    ├── 前端仓库 apps → GitHub Actions 构建 admin + mall → web.tar.gz → 服务器
    └── 后端仓库 back → GitHub Actions 构建 jar → backend.tar.gz → 服务器

服务器 Nginx 统一入口：
    https://aixiaoya.site/        → admin 管理后台
    https://aixiaoya.site/mall/   → mall 商城前端
    https://aixiaoya.site/api/    → Spring Boot 后端（端口 8080）
    https://aixiaoya.site/uploads/ → 上传文件目录（持久化）
```

---

## 二、服务器目录结构

```
/opt/app/
├── releases/              # GitHub Actions 上传的压缩包
│   ├── web-xxxx.tar.gz
│   └── backend-xxxx.tar.gz
├── web/
│   ├── current/           # 软链，指向当前前端版本
│   └── backup/            # 历史版本备份
├── backend/
│   ├── current/           # 软链，指向当前后端版本
│   └── backup/            # 历史版本备份
├── uploads/               # 上传文件持久化目录
└── scripts/               # 可选：公共脚本
```

---

## 三、首次服务器初始化

### 3.1 基础环境安装

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 JDK 21
sudo apt install openjdk-21-jdk -y

# 安装 Nginx
sudo apt install nginx -y

# 安装 MySQL 8.0（如未安装）
sudo apt install mysql-server-8.0 -y

# 安装 Certbot（申请免费 SSL 证书）
sudo apt install certbot python3-certbot-nginx -y
```

### 3.2 创建应用用户和目录

```bash
sudo useradd -r -s /bin/false app || true
sudo mkdir -p /opt/app/{releases,web/current,backend/current,uploads,scripts}
sudo chown -R app:app /opt/app
```

### 3.3 配置数据库

```bash
sudo mysql -u root -p
```

```sql
CREATE DATABASE IF NOT EXISTS telecom_supply_chain
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 强烈建议创建独立用户，不要给后端用 root
CREATE USER 'scm_user'@'127.0.0.1' IDENTIFIED BY '你的强密码';
GRANT ALL PRIVILEGES ON telecom_supply_chain.* TO 'scm_user'@'127.0.0.1';
FLUSH PRIVILEGES;
```

### 3.4 配置 Nginx

首次部署时，`deploy-backend.sh` 脚本会自动把 Nginx 配置安装到 `/etc/nginx/sites-available/aixiaoya.site` 并启用。你不需要手动复制。

如果你不确定是否已经放到服务器，可以 SSH 登录后检查：

```bash
# 查看 Nginx 站点配置是否存在
ls -la /etc/nginx/sites-available/aixiaoya.site
ls -la /etc/nginx/sites-enabled/aixiaoya.site

# 查看后端部署包里是否包含 Nginx 配置
ls -la /opt/app/backend/current/nginx/aixiaoya.site.conf

# 查看 Nginx 配置语法是否正确
sudo nginx -t

# 查看 Nginx 运行状态
sudo systemctl status nginx
```

如果因为某些原因自动安装失败（比如 Nginx 没装），可以手动复制：

```bash
sudo cp /opt/app/backend/current/nginx/aixiaoya.site.conf /etc/nginx/sites-available/aixiaoya.site
sudo ln -sf /etc/nginx/sites-available/aixiaoya.site /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx
```

### 3.5 申请 SSL 证书

```bash
sudo certbot --nginx -d aixiaoya.site -d www.aixiaoya.site
```

按提示操作，certbot 会自动修改 Nginx 配置并添加证书路径。建议设置自动续期：

```bash
sudo systemctl enable certbot-renew.timer
sudo certbot renew --dry-run
```

### 3.6 配置后端 systemd 服务

首次部署时，部署脚本会自动安装 `telecom-scm-backend.service`。也可以手动提前安装：

```bash
sudo cp /path/to/backend.service /etc/systemd/system/telecom-scm-backend.service
sudo systemctl daemon-reload
sudo systemctl enable telecom-scm-backend
```

**注意**：必须编辑 `/etc/systemd/system/telecom-scm-backend.service`，把 `DB_PASSWORD` 和 `JWT_SECRET` 改成你自己的强密码：

```ini
Environment="DB_PASSWORD=你的强密码"
Environment="JWT_SECRET=一个很长的随机字符串"
```

然后重载：

```bash
sudo systemctl daemon-reload
```

---

## 四、GitHub 仓库配置

### 4.1 把本地代码 push 到对应仓库

```bash
# 前端仓库（apps/ 目录）
cd apps
git init
git remote add origin https://github.com/a414003183/apps.git
git add .
git commit -m "init: add deploy config"
git push -u origin main

# 后端仓库（backend/ 目录）
cd backend
git init
git remote add origin https://github.com/a414003183/back.git
git add .
git commit -m "init: add deploy config"
git push -u origin main
```

### 4.2 配置 GitHub Secrets

分别进入两个仓库的 `Settings → Secrets and variables → Actions → Repository secrets`，添加：

| Secret 名 | 说明 |
|---|---|
| `HOST` | `43.134.181.228` |
| `USERNAME` | 服务器登录用户名，例如 `ubuntu` |
| `SSH_KEY` | 服务器私钥内容（`~/.ssh/id_rsa` 的完整内容） |

生成密钥对（如还没有）：

```bash
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions
# 把公钥放到服务器 authorized_keys
cat ~/.ssh/github_actions.pub | ssh ubuntu@43.134.181.228 "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"
```

然后把 `~/.ssh/github_actions` 的**私钥内容**粘贴到 GitHub `SSH_KEY`。

---

## 五、触发部署

配置完成后，每次 push 代码到 `main` 或 `master` 分支，GitHub Actions 会自动：

1. 构建前端 admin + mall 或后端 jar
2. 打成压缩包
3. 上传到服务器 `/opt/app/releases/`
4. 执行服务器端部署脚本
5. 自动重启后端服务 / reload Nginx

也可以手动触发：进入仓库 `Actions → Deploy ... → Run workflow`。

---

## 六、部署后验证

```bash
# 后端健康检查
curl http://127.0.0.1:8080/api/health

# 查看后端日志
sudo journalctl -u telecom-scm-backend -f

# 检查 Nginx
sudo nginx -t
sudo systemctl status nginx

# 检查前端文件
ls -la /opt/app/web/current
ls -la /opt/app/backend/current
```

浏览器访问：

- 后台：`https://aixiaoya.site/`
- 商城：`https://aixiaoya.site/mall/`

---

## 七、常见问题

### Q1：前端显示空白或 404

- admin 是单页应用，Nginx 已配置 `try_files $uri $uri/ /index.html`
- mall 也是单页应用，访问 `/mall/xxx` 时会回退到 `/mall/index.html`

### Q2：接口报 CORS 错误

后端 `application.yml` 已配置 `https://aixiaoya.site` 和 `http://aixiaoya.site` 允许跨域。如果还有问题，检查：

```bash
# 查看后端实际读取到的配置
sudo journalctl -u telecom-scm-backend -n 100 | grep -i cors
```

### Q3：上传图片不显示

- 上传目录 `/opt/app/uploads` 必须持久化，不能被部署覆盖（脚本已处理）
- Nginx `/uploads/` 已指向 `/opt/app/uploads/`
- 后端 `FILE_STORAGE_DIR=/opt/app/uploads` 环境变量必须正确

### Q4：如何回滚

脚本每次部署都会备份旧版本到 `/opt/app/web/backup/` 和 `/opt/app/backend/backup/`。手动回滚：

```bash
# 回滚后端
sudo systemctl stop telecom-scm-backend
sudo rm /opt/app/backend/current
sudo ln -s /opt/app/backend/backup/XXXX /opt/app/backend/current
sudo systemctl start telecom-scm-backend

# 回滚前端
sudo rm /opt/app/web/current
sudo ln -s /opt/app/web/backup/XXXX /opt/app/web/current
sudo nginx -s reload
```

### Q5：GitHub Actions 免费额度够吗？

公开仓库无限制。私有仓库每月 2000 分钟，普通项目完全够用。

### Q6：不想用 GitHub Actions，想本地打包手动上传怎么办？

可以，步骤如下：

```bash
# 前端本地打包
cd apps
pnpm install
pnpm build:admin
pnpm build:mall
mkdir -p deploy-pkg
cp -r projects/admin/dist deploy-pkg/admin
cp -r projects/mall/dist deploy-pkg/mall
cp deploy/deploy-web.sh deploy-pkg/
tar -czf web-manual.tar.gz -C deploy-pkg .

# 上传到服务器并执行部署
scp web-manual.tar.gz ubuntu@43.134.181.228:/opt/app/releases/
ssh ubuntu@43.134.181.228 "mkdir -p /tmp/web-manual && tar -xzf /opt/app/releases/web-manual.tar.gz -C /tmp/web-manual && bash /tmp/web-manual/deploy-web.sh /opt/app/releases/web-manual.tar.gz && rm -rf /tmp/web-manual"

# 后端本地打包
cd backend
mvn clean package -DskipTests
mkdir -p deploy-pkg
cp target/telecom-scm-backend-*.jar deploy-pkg/
cp deploy/deploy-backend.sh deploy-pkg/
cp -r deploy/systemd deploy-pkg/
cp -r deploy/nginx deploy-pkg/
tar -czf backend-manual.tar.gz -C deploy-pkg .

# 上传到服务器并执行部署
scp backend-manual.tar.gz ubuntu@43.134.181.228:/opt/app/releases/
ssh ubuntu@43.134.181.228 "mkdir -p /tmp/backend-manual && tar -xzf /opt/app/releases/backend-manual.tar.gz -C /tmp/backend-manual && bash /tmp/backend-manual/deploy-backend.sh /opt/app/releases/backend-manual.tar.gz && rm -rf /tmp/backend-manual"
```

---

## 八、文件清单

| 文件 | 说明 |
|---|---|
| `apps/.github/workflows/deploy.yml` | 前端 GitHub Actions |
| `apps/deploy/deploy-web.sh` | 前端服务器部署脚本 |
| `backend/.github/workflows/deploy.yml` | 后端 GitHub Actions |
| `backend/deploy/deploy-backend.sh` | 后端服务器部署脚本 |
| `backend/deploy/nginx/aixiaoya.site.conf` | Nginx 站点配置 |
| `backend/deploy/systemd/backend.service` | 后端 systemd 服务模板 |
| `backend/src/main/resources/application.yml` | 后端配置（已包含 aixiaoya.site 跨域） |
