package com.telecom.scm.admin.service;

import com.telecom.scm.admin.dto.request.SaveBrandRequest;
import com.telecom.scm.admin.dto.request.SaveCategoryRequest;
import com.telecom.scm.admin.dto.response.MessageResponse;
import com.telecom.scm.admin.mapper.BrandRow;
import com.telecom.scm.admin.mapper.CategoryRow;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;

public interface AdminProductService {
    PageResult<BrandRow> listBrands(int page, int pageSize);

    PageResult<CategoryRow> listCategories(int page, int pageSize);

    MessageResponse saveBrand(String username, SaveBrandRequest request);

    MessageResponse updateBrand(String username, Long brandId, SaveBrandRequest request);

    MessageResponse deleteBrand(Long brandId);

    MessageResponse updateBrandStatus(Long brandId, AccountStatusEnum status);

    MessageResponse saveCategory(String username, SaveCategoryRequest request);

    MessageResponse updateCategory(String username, Long categoryId, SaveCategoryRequest request);

    MessageResponse deleteCategory(Long categoryId);

    MessageResponse updateCategoryStatus(Long categoryId, AccountStatusEnum status);
}
