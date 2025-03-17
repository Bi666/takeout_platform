package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * insert order information
     * @param orders
     */
    void insert(Orders orders);

    /**
     * query order by number
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * query order by id
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * update order information
     * @param orders
     */
    void update(Orders orders);

    /**
     * update pay status
     * @param orderStatus
     * @param orderPaidStatus
     * @param check_out_time
     * @param orderNumber
     */
    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{check_out_time} " +
            "where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, String orderNumber);

    /**
     * query history order by page
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> queryHistory(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * search order with condition
     * @param ordersPageQueryDTO
     * @return
     */
    List<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * Order quantity statistics
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * query by status and order time
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatus(Integer status, LocalDateTime orderTime);


    /**
     * Sum turnover by begin & end & status
     * @param map
     * @return
     */
    Double sumByMap(Map map);
}
