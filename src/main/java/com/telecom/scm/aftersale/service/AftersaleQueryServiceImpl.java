package com.telecom.scm.aftersale.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.telecom.scm.aftersale.convert.AftersaleConvert;
import com.telecom.scm.aftersale.dto.response.AftersaleSummaryResponse;
import com.telecom.scm.aftersale.mapper.AftersaleMapper;
import com.telecom.scm.common.api.PageResult;

@Service
public class AftersaleQueryServiceImpl implements AftersaleQueryService {

    private final AftersaleMapper aftersaleMapper;

    public AftersaleQueryServiceImpl(AftersaleMapper aftersaleMapper) {
        this.aftersaleMapper = aftersaleMapper;
    }

    @Override
    public PageResult<AftersaleSummaryResponse> currentCustomerAftersales(
            String username, int page, int pageSize) {
        int validatedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int validatedPage = Math.max(page, 1);
        int offset = (validatedPage - 1) * validatedPageSize;
        long total = aftersaleMapper.countCustomerAftersales(username);
        List<AftersaleSummaryResponse> list =
                aftersaleMapper
                        .selectCustomerAftersaleRows(username, offset, validatedPageSize)
                        .stream()
                        .map(AftersaleConvert.INSTANCE::toAftersaleSummaryResponse)
                        .toList();
        return PageResult.of(list, total, validatedPage, validatedPageSize);
    }

    @Override
    public PageResult<AftersaleSummaryResponse> currentMerchantAftersales(
            String username, int page, int pageSize) {
        int validatedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int validatedPage = Math.max(page, 1);
        int offset = (validatedPage - 1) * validatedPageSize;
        long total = aftersaleMapper.countMerchantAftersales(username);
        List<AftersaleSummaryResponse> list =
                aftersaleMapper
                        .selectMerchantAftersaleRows(username, offset, validatedPageSize)
                        .stream()
                        .map(AftersaleConvert.INSTANCE::toAftersaleSummaryResponse)
                        .toList();
        return PageResult.of(list, total, validatedPage, validatedPageSize);
    }
}
