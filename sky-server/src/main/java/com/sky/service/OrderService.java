package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.vo.*;

import java.util.List;

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

    /**
     * cancel order by id
     * @param orderId
     */
    void cancelOrder(Long orderId);

    /**
     * repeat order
     * @param id
     */
    void repetition(Long id);

    /**
     * search order with condition
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * Order quantity statistics
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * Confirm order
     * @param id
     */
    void confirmOrder(Long id);

    /**
     * Reject order
     * @param id
     * @param rejectionReason
     */
    void RejectOrder(Long id, String rejectionReason);

    /**
     * Cancel order
     * @param id
     * @param cancelReason
     */
    void CancelOrder(Long id, String cancelReason);

    /**
     * Delivery order
     * @param id
     */
    void deliveryOrder(Long id);

    /**
     * Complete order
     * @param id
     */
    void completeOrder(Long id);

    /**
     * Customer reminder
     * @param orderId
     */
    void reminder(Long orderId);
}
