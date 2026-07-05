#!/bin/bash
set -e

# ============================================================
# 后端部署脚本
# 由 GitHub Actions 调用，也可在服务器上手动执行
# 用法：./deploy-backend.sh /opt/app/releases/backend-xxx.tar.gz
# ============================================================

APP_DIR="/opt/app"
RELEASES_DIR="$APP_DIR/releases"
BACKEND_DIR="$APP_DIR/backend"
CURRENT_DIR="$BACKEND_DIR/current"
BACKUP_DIR="$BACKEND_DIR/backup/$(date +%Y%m%d-%H%M%S)"
SERVICE_NAME="telecom-scm-backend"

TAR_FILE="${1:-}"
if [ -z "$TAR_FILE" ]; then
    # 如果没有传参数，默认使用 releases 目录最新的 backend 包
    TAR_FILE=$(ls -t "$RELEASES_DIR"/backend-*.tar.gz 2>/dev/null | head -n 1)
    if [ -z "$TAR_FILE" ]; then
        echo "错误：未找到 backend 压缩包，请传入路径或确保 $RELEASES_DIR 目录存在 backend-*.tar.gz"
        exit 1
    fi
fi

if [ ! -f "$TAR_FILE" ]; then
    echo "错误：压缩包不存在：$TAR_FILE"
    exit 1
fi

VERSION=$(basename "$TAR_FILE" .tar.gz)
EXTRACT_DIR="$RELEASES_DIR/$VERSION"

echo "========================================"
echo "开始部署后端：$VERSION"
echo "========================================"

# 1. 解压新版本
mkdir -p "$EXTRACT_DIR"
tar -xzf "$TAR_FILE" -C "$EXTRACT_DIR"

# 2. 备份当前版本
if [ -d "$CURRENT_DIR" ]; then
    echo "备份当前版本到 $BACKUP_DIR"
    mkdir -p "$BACKUP_DIR"
    cp -a "$CURRENT_DIR"/. "$BACKUP_DIR"/ || true
fi

# 3. 切换版本
mkdir -p "$BACKEND_DIR"
rm -rf "$CURRENT_DIR"
ln -sfn "$EXTRACT_DIR" "$CURRENT_DIR"

# 4. 重命名 jar 为固定文件名（方便 systemd 配置）
JAR_FILE=$(find "$EXTRACT_DIR" -maxdepth 1 -name "telecom-scm-backend*.jar" ! -name "original-*" | head -n 1)
if [ -n "$JAR_FILE" ]; then
    mv "$JAR_FILE" "$CURRENT_DIR/telecom-scm-backend.jar"
else
    echo "错误：压缩包中未找到 jar 文件"
    exit 1
fi

# 5. 确定应用运行用户（取 /opt/app 目录的属主，确保和 SSH 部署用户一致）
APP_OWNER=$(stat -c '%U' "$APP_DIR" 2>/dev/null || echo "root")
APP_GROUP=$(stat -c '%G' "$APP_DIR" 2>/dev/null || echo "root")
if [ "$APP_OWNER" = "root" ]; then
    echo "警告：/opt/app 属主是 root，建议改为普通用户"
fi

# 6. 确保上传目录存在并设置权限
mkdir -p "$APP_DIR/uploads"
chown -R "$APP_OWNER:$APP_GROUP" "$APP_DIR/uploads" "$CURRENT_DIR"

# 7. 安装/更新 Nginx 站点配置
NGINX_CONF_SRC="$CURRENT_DIR/nginx/aixiaoya.site.conf"
NGINX_CONF_DST="/etc/nginx/sites-available/aixiaoya.site"
if [ -f "$NGINX_CONF_SRC" ] && command -v nginx >/dev/null 2>&1; then
    echo "更新 Nginx 站点配置..."
    sudo cp "$NGINX_CONF_SRC" "$NGINX_CONF_DST"
    sudo ln -sf "$NGINX_CONF_DST" /etc/nginx/sites-enabled/aixiaoya.site
    sudo rm -f /etc/nginx/sites-enabled/default
    sudo nginx -t && sudo systemctl reload nginx
else
    echo "警告：未检测到 Nginx，跳过 Nginx 配置更新"
fi

# 8. 安装/更新 systemd 服务
if [ -f "$CURRENT_DIR/systemd/backend.service" ]; then
    # 根据 /opt/app 属主自动替换服务文件中的运行用户
    sed -e "s/APP_RUN_USER/$APP_OWNER/g" -e "s/APP_RUN_GROUP/$APP_GROUP/g" \
        "$CURRENT_DIR/systemd/backend.service" > "/tmp/$SERVICE_NAME.service"
    sudo cp "/tmp/$SERVICE_NAME.service" "/etc/systemd/system/$SERVICE_NAME.service"
    rm -f "/tmp/$SERVICE_NAME.service"
    sudo systemctl daemon-reload
fi

# 9. 启动/重启服务
if systemctl is-active --quiet "$SERVICE_NAME"; then
    echo "重启后端服务..."
    sudo systemctl restart "$SERVICE_NAME"
else
    echo "启动后端服务..."
    sudo systemctl enable "$SERVICE_NAME"
    sudo systemctl start "$SERVICE_NAME"
fi

# 10. 健康检查
sleep 5
if curl -fsS "http://127.0.0.1:8080/api/health" >/dev/null 2>&1; then
    echo "后端健康检查通过"
else
    echo "警告：后端健康检查未通过，请手动查看日志：journalctl -u $SERVICE_NAME -n 50"
    exit 1
fi

echo "========================================"
echo "后端部署完成：$VERSION"
echo "========================================"
