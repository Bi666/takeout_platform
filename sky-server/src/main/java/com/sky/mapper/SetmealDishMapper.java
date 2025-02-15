package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * save setmeal_dish relationship
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * query setmeal id by dish id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    /**
     * count setmeals by category id
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * delete setmeal in batches
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * get dishes by setmeal id
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishesBySetmealID(Long id);
}
