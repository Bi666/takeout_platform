package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * Dish paging query
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * Deleted dishes in batches
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * query dish and flavor by id
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * update dish information
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * set dish status
     * @param status
     */
    void setStatus(Integer status, Long id);

    /**
     * query dish by categoryId
     * @param categoryId
     * @return
     */
    List<Dish> queryWithCategory(Long categoryId);

    /**
     * query dish and flavor
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
