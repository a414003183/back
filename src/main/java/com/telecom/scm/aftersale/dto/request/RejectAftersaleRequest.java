package com.telecom.scm.aftersale.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "拒绝售后请求")
public record RejectAftersaleRequest(@Schema(description = "备注") String remark) {}
