package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * user order
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * Pay order
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * pay success, update order status
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * query history orders
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult queryHistory(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * get order detail by order id
     * @param orderId
     * @return
     */
    OrderVO getOrderById(Long orderId);
}
