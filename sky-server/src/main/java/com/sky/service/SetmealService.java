package com.sky.service;

import com.sky.dto.SetmealDTO;

public interface SetmealService {

    /**
     * new a setmeal
     * @param setmealDTO
     */
    void saveSetmeal(SetmealDTO setmealDTO);
}
