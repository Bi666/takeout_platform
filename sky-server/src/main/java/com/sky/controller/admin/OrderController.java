package com.sky.controller.admin;

import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "admin order interface")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * search order with condition
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("search order with condition")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * Order quantity statistics
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("Order quantity statistics")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * Search for detail of order
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("Search for detail of order")
    public Result<OrderVO> queryDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }

    /**
     * Confirm order
     * @param id
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("Confirm order")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        Long id = ordersConfirmDTO.getId();
        orderService.confirmOrder(id);
        return Result.success();
    }

    /**
     * Reject order
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("Reject order")
    public Result RejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();
        String rejectionReason = ordersRejectionDTO.getRejectionReason();
        orderService.RejectOrder(id, rejectionReason);
        return Result.success();
    }

    /**
     * Cancel order
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("Cancel order")
    public Result CancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        Long id = ordersCancelDTO.getId();
        String cancelReason = ordersCancelDTO.getCancelReason();
        orderService.CancelOrder(id, cancelReason);
        return Result.success();
    }

    /**
     * Delivery order
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("Delivery order")
    public Result delivery(@PathVariable Long id) {
        orderService.deliveryOrder(id);
        return Result.success();
    }

    /**
     * Complete order
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("Complete order")
    public Result completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success();
    }
}
