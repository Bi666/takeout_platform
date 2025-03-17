package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * Statistical business data by date
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * Query order management data
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * Query dish overview
     * @return
     */
    DishOverViewVO getDishOverView();

    /**
     * Query setmeal overview
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

}
