package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

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

    /**
     * edit information of setmeal
     * @param setmealDTO
     */
    void editSetmeal(SetmealDTO setmealDTO);

    /**
     * query setmeal by id
     * @param id
     * @return
     */
    SetmealVO getSetmealById(Long id);

    /**
     * start or stop a setmeal
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
