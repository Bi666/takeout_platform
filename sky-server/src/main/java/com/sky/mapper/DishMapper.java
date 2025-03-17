package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * count dishes by category id
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * insert dish
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * Dish paging query
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * query dish by id
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * delete dish by ids in batches
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * update dish
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * query dish by categoryId
     * @param dish
     * @return
     */
    List<Dish> listDish(Dish dish);

    /**
     * get dishes by setmeal id
     * @param setmeal_id
     * @return
     */
    @Select("select * from dish d left join setmeal_dish s on d.id = s.dish_id where s.setmeal_id = #{setmeal_id}")
    List<Dish> getBySetmealId(Long setmeal_id);

    /**
     * Count dishes number with conditions
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
