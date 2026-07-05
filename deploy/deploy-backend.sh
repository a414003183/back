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

# 1. 创建应用用户（如果不存在）
if ! id -u app >/dev/null 2>&1; then
    echo "创建 app 用户..."
    sudo useradd -r -s /bin/false app || true
fi

# 2. 解压新版本
mkdir -p "$EXTRACT_DIR"
tar -xzf "$TAR_FILE" -C "$EXTRACT_DIR" --strip-components=1

# 3. 备份当前版本
if [ -d "$CURRENT_DIR" ]; then
    echo "备份当前版本到 $BACKUP_DIR"
    mkdir -p "$BACKUP_DIR"
    cp -a "$CURRENT_DIR"/. "$BACKUP_DIR"/ || true
fi

# 4. 切换版本
mkdir -p "$BACKEND_DIR"
rm -rf "$CURRENT_DIR"
ln -sfn "$EXTRACT_DIR" "$CURRENT_DIR"

# 5. 重命名 jar 为固定文件名（方便 systemd 配置）
JAR_FILE=$(find "$CURRENT_DIR" -maxdepth 1 -name "telecom-scm-backend*.jar" ! -name "original-*" | head -n 1)
if [ -n "$JAR_FILE" ]; then
    mv "$JAR_FILE" "$CURRENT_DIR/telecom-scm-backend.jar"
else
    echo "错误：压缩包中未找到 jar 文件"
    exit 1
fi

# 6. 确保上传目录存在并设置权限
mkdir -p "$APP_DIR/uploads"
chown -R app:app "$APP_DIR/uploads" "$CURRENT_DIR"

# 7. 安装/更新 systemd 服务
if [ -f "$CURRENT_DIR/systemd/backend.service" ]; then
    sudo cp "$CURRENT_DIR/systemd/backend.service" "/etc/systemd/system/$SERVICE_NAME.service"
    sudo systemctl daemon-reload
fi

# 8. 启动/重启服务
if systemctl is-active --quiet "$SERVICE_NAME"; then
    echo "重启后端服务..."
    sudo systemctl restart "$SERVICE_NAME"
else
    echo "启动后端服务..."
    sudo systemctl enable "$SERVICE_NAME"
    sudo systemctl start "$SERVICE_NAME"
fi

# 9. 健康检查
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
