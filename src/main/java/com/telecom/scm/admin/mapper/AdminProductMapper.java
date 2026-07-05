package com.telecom.scm.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.telecom.scm.admin.entity.BrandEntity;
import com.telecom.scm.admin.entity.CategoryEntity;

@Mapper
public interface AdminProductMapper {

    // 品牌管理
    List<BrandRow> selectBrands(@Param("offset") int offset, @Param("limit") int limit);

    long countBrands();

    BrandRow selectBrandById(@Param("brandId") Long brandId);

    Integer selectNextBrandNumber();

    int insertBrand(BrandEntity payload);

    int updateBrand(BrandEntity payload);

    int deleteBrand(@Param("brandId") Long brandId);

    int updateBrandStatus(@Param("brandId") Long brandId, @Param("status") String status);

    @Select("SELECT COUNT(1) FROM product_spu WHERE deleted = 0 AND brand_id = #{brandId}")
    Integer countProductsByBrand(@Param("brandId") Long brandId);

    // 分类管理
    List<CategoryRow> selectCategories(@Param("offset") int offset, @Param("limit") int limit);

    long countCategories();

    CategoryRow selectCategoryById(@Param("categoryId") Long categoryId);

    Integer selectNextCategoryNumber();

    int insertCategory(CategoryEntity payload);

    int updateCategory(CategoryEntity payload);

    int deleteCategory(@Param("categoryId") Long categoryId);

    int updateCategoryStatus(@Param("categoryId") Long categoryId, @Param("status") String status);

    @Select("SELECT COUNT(1) FROM product_category WHERE deleted = 0 AND parent_id = #{categoryId}")
    Integer countChildCategories(@Param("categoryId") Long categoryId);

    @Select("SELECT COUNT(1) FROM product_spu WHERE deleted = 0 AND category_id = #{categoryId}")
    Integer countProductsByCategory(@Param("categoryId") Long categoryId);
}
