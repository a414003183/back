package com.telecom.scm.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.scm.admin.dto.request.SaveBrandRequest;
import com.telecom.scm.admin.dto.request.SaveCategoryRequest;
import com.telecom.scm.admin.dto.response.MessageResponse;
import com.telecom.scm.admin.entity.BrandEntity;
import com.telecom.scm.admin.entity.CategoryEntity;
import com.telecom.scm.admin.mapper.AdminProductMapper;
import com.telecom.scm.admin.mapper.BrandRow;
import com.telecom.scm.admin.mapper.CategoryRow;
import com.telecom.scm.common.api.PageResult;
import com.telecom.scm.common.enums.AccountStatusEnum;
import com.telecom.scm.common.exception.BusinessException;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private final AdminProductMapper adminProductMapper;

    public AdminProductServiceImpl(AdminProductMapper adminProductMapper) {
        this.adminProductMapper = adminProductMapper;
    }

    @Override
    public PageResult<BrandRow> listBrands(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<BrandRow> list = adminProductMapper.selectBrands(offset, pageSize);
        long total = adminProductMapper.countBrands();
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public PageResult<CategoryRow> listCategories(int page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (page - 1) * pageSize;
        List<CategoryRow> list = adminProductMapper.selectCategories(offset, pageSize);
        long total = adminProductMapper.countCategories();
        return PageResult.of(list, total, page, pageSize);
    }

    @Transactional
    @Override
    public MessageResponse saveBrand(String username, SaveBrandRequest request) {
        Integer nextNum = adminProductMapper.selectNextBrandNumber();
        BrandEntity param = new BrandEntity();
        param.setNextNum(nextNum);
        param.setBrandName(request.brandName().trim());
        param.setBrandDesc(normalizeText(request.brandDesc()));
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setSortNo(request.sortNo() != null ? request.sortNo() : 0);

        adminProductMapper.insertBrand(param);
        return MessageResponse.of("brand saved");
    }

    @Transactional
    @Override
    public MessageResponse updateBrand(String username, Long brandId, SaveBrandRequest request) {
        BrandRow existing = adminProductMapper.selectBrandById(brandId);
        if (existing == null) {
            throw new BusinessException(404, "brand not found");
        }

        BrandEntity param = new BrandEntity();
        param.setBrandId(brandId);
        param.setBrandName(request.brandName().trim());
        param.setBrandDesc(normalizeText(request.brandDesc()));
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setSortNo(request.sortNo() != null ? request.sortNo() : existing.getSortNo());

        adminProductMapper.updateBrand(param);
        return MessageResponse.of("brand updated");
    }

    @Transactional
    @Override
    public MessageResponse deleteBrand(Long brandId) {
        BrandRow existing = adminProductMapper.selectBrandById(brandId);
        if (existing == null) {
            throw new BusinessException(404, "brand not found");
        }

        // 检查是否有商品使用该品牌
        Integer productCount = adminProductMapper.countProductsByBrand(brandId);
        if (productCount != null && productCount > 0) {
            throw new BusinessException(400, "品牌已被商品使用，无法删除");
        }

        adminProductMapper.deleteBrand(brandId);
        return MessageResponse.of("brand deleted");
    }

    @Transactional
    @Override
    public MessageResponse updateBrandStatus(Long brandId, AccountStatusEnum status) {
        BrandRow existing = adminProductMapper.selectBrandById(brandId);
        if (existing == null) {
            throw new BusinessException(404, "brand not found");
        }
        adminProductMapper.updateBrandStatus(brandId, status.getCode());
        return MessageResponse.of("brand status updated");
    }

    @Transactional
    @Override
    public MessageResponse saveCategory(String username, SaveCategoryRequest request) {
        int levelNo = 1;
        long parentId = 0;

        if (request.parentId() != null && request.parentId() > 0) {
            CategoryRow parent = adminProductMapper.selectCategoryById(request.parentId());
            if (parent == null) {
                throw new BusinessException(404, "parent category not found");
            }
            int parentLevel = parent.getLevelNo();
            if (parentLevel >= 3) {
                throw new BusinessException(400, "最多支持三级分类");
            }
            levelNo = parentLevel + 1;
            parentId = request.parentId();
        }

        Integer nextNum = adminProductMapper.selectNextCategoryNumber();
        CategoryEntity param = new CategoryEntity();
        param.setNextNum(nextNum);
        param.setCategoryName(request.categoryName().trim());
        param.setParentId(parentId);
        param.setLevelNo(levelNo);
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setSortNo(request.sortNo() != null ? request.sortNo() : 0);

        adminProductMapper.insertCategory(param);
        return MessageResponse.of("category saved");
    }

    @Transactional
    @Override
    public MessageResponse updateCategory(
            String username, Long categoryId, SaveCategoryRequest request) {
        CategoryRow existing = adminProductMapper.selectCategoryById(categoryId);
        if (existing == null) {
            throw new BusinessException(404, "category not found");
        }

        if (request.parentId() != null && request.parentId().equals(categoryId)) {
            throw new BusinessException(400, "cannot set self as parent");
        }

        int levelNo = 1;
        long parentId = 0;

        if (request.parentId() != null && request.parentId() > 0) {
            CategoryRow parent = adminProductMapper.selectCategoryById(request.parentId());
            if (parent == null) {
                throw new BusinessException(404, "parent category not found");
            }
            int parentLevel = parent.getLevelNo();
            if (parentLevel >= 3) {
                throw new BusinessException(400, "最多支持三级分类");
            }
            levelNo = parentLevel + 1;
            parentId = request.parentId();
        }

        CategoryEntity param = new CategoryEntity();
        param.setCategoryId(categoryId);
        param.setCategoryName(request.categoryName().trim());
        param.setParentId(parentId);
        param.setLevelNo(levelNo);
        param.setStatus(request.status() != null ? request.status() : AccountStatusEnum.ENABLED);
        param.setSortNo(request.sortNo() != null ? request.sortNo() : existing.getSortNo());

        adminProductMapper.updateCategory(param);
        return MessageResponse.of("category updated");
    }

    @Transactional
    @Override
    public MessageResponse deleteCategory(Long categoryId) {
        CategoryRow existing = adminProductMapper.selectCategoryById(categoryId);
        if (existing == null) {
            throw new BusinessException(404, "category not found");
        }

        // 检查是否有子分类
        Integer childCount = adminProductMapper.countChildCategories(categoryId);
        if (childCount != null && childCount > 0) {
            throw new BusinessException(400, "分类下有子分类，无法删除");
        }

        // 检查是否有商品使用该分类
        Integer productCount = adminProductMapper.countProductsByCategory(categoryId);
        if (productCount != null && productCount > 0) {
            throw new BusinessException(400, "分类已被商品使用，无法删除");
        }

        adminProductMapper.deleteCategory(categoryId);
        return MessageResponse.of("category deleted");
    }

    @Transactional
    @Override
    public MessageResponse updateCategoryStatus(Long categoryId, AccountStatusEnum status) {
        CategoryRow existing = adminProductMapper.selectCategoryById(categoryId);
        if (existing == null) {
            throw new BusinessException(404, "category not found");
        }
        adminProductMapper.updateCategoryStatus(categoryId, status.getCode());
        return MessageResponse.of("category status updated");
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
