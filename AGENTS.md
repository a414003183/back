# telecom-scm-backend 开发规范

> 本文档用于指导本项目的代码结构、命名、分层和工程实践。

---

## 1. 项目定位

- **类型**：Spring Boot 单体应用（学习/毕业设计用途，但按企业级标准组织）。
- **JDK**：Java 21
- **Spring Boot**：3.3.x
- **ORM**：MyBatis-Plus 3.5.x
- **构建工具**：Maven

---

## 2. 分层规范

### 2.1 包结构

```
com.telecom.scm
├── common/                 # 公共基础设施
│   ├── api/               # 统一返回结果（ApiResponse、PageResult）
│   ├── base/              # 基础实体、自动填充、当前用户上下文
│   ├── constants/         # 常量
│   ├── enums/             # 枚举
│   ├── exception/         # 业务异常、全局异常处理
│   └── utils/             # 工具类
├── config/                # 框架级配置（Security、MyBatis-Plus、WebMvc 等）
├── modules/               # 业务模块（可选）
│   └── order/
│       ├── controller/    # 控制器
│       ├── service/       # 服务接口 + 实现
│       ├── mapper/        # MyBatis Mapper 接口
│       ├── entity/        # 数据库实体（DO）
│       ├── dto/           # 数据传输对象
│       │   ├── request/   # 入参
│       │   └── response/  # 出参
│       └── convert/       # MapStruct 转换器
├── security/              # 认证授权
├── file/                  # 文件存储
└── audit/                 # 审计日志
```

### 2.2 分层职责

| 层级 | 职责 | 禁止做的事 |
|---|---|---|
| Controller | 接收请求、参数校验、调用 Service、返回响应 | 直接写 SQL、处理业务逻辑 |
| Service | 业务逻辑编排、事务控制 | 直接返回 DO 给前端、跨模块直接调用 Mapper |
| Mapper | 数据访问 | 写业务判断 |
| Entity（DO） | 映射数据库表 | 包含业务方法、返回给前端 |
| DTO/Request/Response | 接口入参出参 | 包含持久化逻辑 |
| Convert | DO ↔ DTO 转换 | 包含业务逻辑 |

---

## 3. 实体类规范（重点）

### 3.1 必须继承 `BaseEntity`

所有数据库实体必须继承 `com.telecom.scm.common.base.BaseEntity`：

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_info")
public class OrderInfoEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // 只声明业务字段
}
```

`BaseEntity` 已包含：
- `id`：主键
- `createdBy` / `updatedBy`：操作人（自动填充）
- `createdTime` / `updatedTime`：时间（自动填充）
- `deleted`：逻辑删除标志
- `version`：乐观锁版本号
- `remark`：备注

### 3.2 必须使用 Lombok

禁止使用手写 getter/setter。统一使用：

```java
@Data
@EqualsAndHashCode(callSuper = true)
```

### 3.3 必须使用 MyBatis-Plus 注解

```java
@TableName("order_info")                 // 表名
public class OrderInfoEntity extends BaseEntity {
    // id 已在 BaseEntity 中通过 @TableId 声明
}
```

### 3.4 同表只保留一个实体

**不允许**在不同模块里重复定义同一张表的实体（例如 `admin/entity/SupplierInfoEntity` 和 `security/entity/SupplierInfoEntity`）。

正确做法：
1. 把实体放到最接近数据来源的模块（如 `member/entity`）。
2. 其他模块如果需要不同字段视图，使用 **DTO / BO / Row** 投影，不要新建 Entity。
3. 使用 MapStruct 做转换。

### 3.5 实体字段必须使用 `@Schema` 注解

便于生成 OpenAPI 文档：

```java
@Schema(description = "订单编号")
private String orderNo;
```

---

## 4. 转换规范：使用 MapStruct

禁止在 Service 里手写大量 `setXxx()` 做对象转换。

```java
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConvert {
    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    @Mapping(target = "id", expression = "java(String.valueOf(entity.getId()))")
    OrderCreateResponse toOrderCreateResponse(OrderInfoEntity entity);
}
```

---

## 5. Service 规范

### 5.1 接口与实现分离

```java
public interface OrderCommandService { ... }

@Service
public class OrderCommandServiceImpl implements OrderCommandService { ... }
```

### 5.2 构造函数注入

禁止使用 `@Autowired` 字段注入。

```java
private final OrderQueryMapper orderQueryMapper;

public OrderCommandServiceImpl(OrderQueryMapper orderQueryMapper) {
    this.orderQueryMapper = orderQueryMapper;
}
```

### 5.3 事务控制

写操作必须加 `@Transactional`。

---

## 6. Controller 规范

### 6.1 路径设计

- 类级别路径表示资源，方法级别路径表示动作。
- 禁止类级别路径和方法级别路径混用。

推荐：

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping("/customer")
    public ApiResponse<PageResult<OrderSummaryResponse>> customerOrders(...) { ... }

    @PostMapping("/{orderId}/payment-register")
    public ApiResponse<OrderActionResponse> registerPayment(...) { ... }
}
```

### 6.2 按角色拆分 Controller

如果一个 Controller 同时处理 customer/merchant/admin 的接口，应拆分为：

- `CustomerOrderController`
- `MerchantOrderController`
- `AdminOrderController`

---

## 7. 数据库规范

### 7.1 所有业务表必须包含公共字段

```sql
CREATE TABLE example_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 业务字段
    created_by BIGINT,
    updated_by BIGINT,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    remark VARCHAR(500),
    INDEX idx_example_created_time (created_time)
);
```

### 7.2 迁移脚本（已使用 Flyway）

- 所有 DDL/DML 变更放在 `src/main/resources/db/migration/` 目录。
- 文件名格式：`VYYYYMMDD_序号__简短描述.sql`（Flyway 命名规范）。
- 原 `sql/` 目录仅保留 `README.md`，说明脚本已迁移到 Flyway。
- 启动应用时 Flyway 会自动执行未应用的迁移。

---

## 8. 测试规范

### 8.1 必须写单元测试

至少为以下场景写测试：
- 复杂业务逻辑（如下单、调价、库存扣减）
- 工具类
- MapStruct 转换器

### 8.2 测试目录

```
src/test/java/com/telecom/scm/order/convert/OrderConvertTest.java
```

### 8.3 测试使用 H2 内存数据库

已引入 `com.h2database:h2`，用于 DAO/Service 集成测试。

---

## 9. 接口文档

已引入 `springdoc-openapi-starter-webmvc-ui`。

启动后访问：

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

---

## 10. 代码风格

### 10.1 格式化

- 每个语句单独一行。
- 禁止一行写完 `if { throw }`。
- 大括号使用 K&R 风格（左大括号不换行）。

### 10.2 日志

- 使用 Slf4j。
- 生产代码禁止保留调试性质的 `=== XXX ===` 日志。
- 日志使用英文，业务异常提示给用户的内容可以用中文。

### 10.3 配置安全

- 密码、JWT Secret 不能硬编码在 `application.yml`。
- 使用环境变量注入：

```yaml
app:
  security:
    jwt:
      secret: ${JWT_SECRET:change-me-in-production}
```

---

## 11. 推荐工具链

| 目的 | 工具 |
|---|---|
| 接口文档 | SpringDoc |
| 数据库迁移 | Flyway |
| 代码格式化 | Spotless（Google Java Format AOSP） |
| 对象转换 | MapStruct |
| 单元测试 | JUnit 5 + Mockito + H2 |
| 静态检查 | SonarLint（IDE 插件）+ SonarQube Maven 插件 |

SonarQube 插件使用方式（需配置 SonarQube 服务器地址、Token 后执行）：

```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN
```

---

## 12. 改进路线（供学习参考）

1. 把所有实体改为继承 `BaseEntity` + Lombok + MP 注解。
2. 合并重复实体（如 `SupplierInfoEntity`、`CustomerInfoEntity`）。
3. 用 MapStruct 替换手写转换。
4. 按角色拆分 `OrderController` 等胖 Controller。
5. 补充单元测试和集成测试。
6. ~~引入 Flyway 管理数据库迁移。~~（已完成）
7. 引入 Spotless 做代码格式化并接入 CI。（已完成）
8. ~~引入 SonarQube Maven 插件，支持 `mvn sonar:sonar` 静态扫描。~~（已完成）
9. 密码和 JWT Secret 走环境变量。
9. 集群环境下改造订单号等唯一 ID 生成策略。
