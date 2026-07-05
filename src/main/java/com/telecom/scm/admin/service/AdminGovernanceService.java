package com.telecom.scm.admin.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.telecom.scm.admin.dto.response.ImportDataResponse;
import com.telecom.scm.admin.dto.response.ImportExportOverviewResponse;
import com.telecom.scm.admin.dto.response.MessageResponse;
import com.telecom.scm.admin.mapper.ImportExportLogRow;
import com.telecom.scm.admin.mapper.LoginLogRow;
import com.telecom.scm.admin.mapper.MenuRow;
import com.telecom.scm.admin.mapper.OperationLogRow;
import com.telecom.scm.admin.mapper.RoleRow;
import com.telecom.scm.common.api.PageResult;

public interface AdminGovernanceService {
    PageResult<RoleRow> roles(int page, int pageSize);

    MessageResponse updateRoleStatus(Long roleId, String status);

    PageResult<MenuRow> menus(int page, int pageSize);

    MessageResponse updateMenuStatus(Long menuId, String status);

    List<String> roleMenuIds(Long roleId);

    List<String> assignRoleMenus(Long roleId, List<Long> menuIds);

    PageResult<LoginLogRow> loginLogs(int page, int pageSize);

    PageResult<OperationLogRow> operationLogs(int page, int pageSize);

    ImportExportOverviewResponse importExportOverview();

    PageResult<ImportExportLogRow> importExportLogs(String type, int page, int pageSize);

    ImportDataResponse importData(String type, MultipartFile file);

    ResponseEntity<byte[]> exportCsv(String type, boolean templateOnly);

    List<String> userMenuIds(Long userId);

    List<String> assignUserMenus(Long userId, List<Long> menuIds, Long roleId);
}
