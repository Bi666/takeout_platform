package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * Insert taste data in batches
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * delete taste data by dishId
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * query flavor data by dishId
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dish_id}")
    List<DishFlavor> getByDishId(Long dishId);
}
