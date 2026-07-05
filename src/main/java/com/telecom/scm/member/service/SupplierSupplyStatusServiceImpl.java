package com.telecom.scm.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.exception.BusinessException;
import com.telecom.scm.member.mapper.SupplierSupplyStatusMapper;
import com.telecom.scm.member.mapper.row.SupplierContextRow;
import com.telecom.scm.member.mapper.row.SupplyStatusRow;

@Service
public class SupplierSupplyStatusServiceImpl implements SupplierSupplyStatusService {

    private final SupplierSupplyStatusMapper supplyStatusMapper;

    public SupplierSupplyStatusServiceImpl(SupplierSupplyStatusMapper supplyStatusMapper) {
        this.supplyStatusMapper = supplyStatusMapper;
    }

    @Override
    public PageResult<SupplyStatusRow> getSupplyStatus(String username, int page, int pageSize) {
        SupplierContextRow context = requireSupplierContext(username);
        int limit = Math.min(Math.max(pageSize, 1), 200);
        int offset = Math.max(page - 1, 0) * limit;
        Long supplierId = context.getSupplierId();
        long total = supplyStatusMapper.countSupplyStatusBySupplierId(supplierId);
        List<SupplyStatusRow> rows =
                supplyStatusMapper.selectSupplyStatusBySupplierId(supplierId, limit, offset);
        return PageResult.of(rows, total, page, limit);
    }

    private SupplierContextRow requireSupplierContext(String username) {
        SupplierContextRow context = supplyStatusMapper.selectSupplierContextByUsername(username);
        if (context == null || context.getSupplierId() == null) {
            throw new BusinessException(403, "supplier account is unavailable");
        }
        return context;
    }
}
