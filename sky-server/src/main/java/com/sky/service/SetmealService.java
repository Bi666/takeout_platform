package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface SetmealService {

    /**
     * new a setmeal
     * @param setmealDTO
     */
    void saveSetmeal(SetmealDTO setmealDTO);

    /**
     * Setmeal paging query
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * delete setmeal in batches
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
