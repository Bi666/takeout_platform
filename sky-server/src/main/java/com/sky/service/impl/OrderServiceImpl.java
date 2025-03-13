package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sky.utils.WeChatPayUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sky.entity.Orders.CANCELLED;
import static com.sky.entity.Orders.PAID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
//    @Autowired
//    private WeChatPayUtil weChatPayUtil;

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu.ak}")
    private String ak;

    /**
     * user order
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理业务异常
        //address empty
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 检查用户的收货地址是否超出配送范围
        checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        //shopping cart empty
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向order里插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setOrderTime(LocalDateTime.now());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        orderMapper.insert(orders);

        //向order detail里插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        //封装VO数据
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * pay order
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "take out order", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("Order has been paid");
//        }

        JSONObject jsonObject =new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; // 支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED; //订单状态，待接单

        //将支付时间 check out属性赋值
        LocalDateTime check_out_time = LocalDateTime.now();
        //获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("Replace wechat Pay and update the database status");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);
        return vo;
    }

    /**
     * pay success, update order status
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * query history orders
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult queryHistory(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.queryHistory(ordersPageQueryDTO);

        //查询order detail并且存入orderVO
        List<OrderVO> orderVOList = new ArrayList<>();
        if (page != null || page.getTotal() > 0) {
            for (OrderVO orderVO : page) {
                List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrder(orderVO.getId());
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * get order detail by order id
     * @param orderId
     * @return
     */
    public OrderVO getOrderById(Long orderId){
        Orders orders = orderMapper.getById(orderId);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailMapper.getDetailByOrder(orderId));
        return orderVO;
    }

    /**
     * cancel order by id
     * @param orderId
     */
    public void cancelOrder(Long orderId){
        Orders orders = orderMapper.getById(orderId);
        // 订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (orders.getStatus() >= 3) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setStatus(CANCELLED);
        newOrder.setCancelReason("User cancel this order");
        newOrder.setOrderTime(LocalDateTime.now());
        if (orders.getPayStatus() == Orders.PAID) {
            //退款
            newOrder.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(newOrder);
    }

    /**
     * repeat order
     * @param id
     */
    public void repetition(Long id) {
        // 根据id查询到原来订单中的菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrder(id);

        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 使用 Stream API 将 OrderDetail 转换为 ShoppingCart
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(x, shoppingCart, "id"); // 复制属性，但忽略 id
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList()); // 收集到 List

        // 批量插入购物车
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * search order with condition
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        Page<OrderVO> orderVOPage = orderMapper.queryHistory(ordersPageQueryDTO);
        List<OrderVO> orderVOList = new ArrayList<>();
        for (OrderVO orderVO : orderVOPage) {
            List<OrderDetail> orderDishesList = orderDetailMapper.getDetailByOrder(orderVO.getId());
            StringBuilder sb = new StringBuilder();
            for (OrderDetail orderDetail : orderDishesList)
                sb.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(";");
            orderVO.setOrderDishes(sb.toString());
            orderVOList.add(orderVO);
        }
        return new PageResult(orderVOPage.getTotal(), orderVOList);
    }

    /**
     * Order quantity statistics
     * @return
     */
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(orderMapper.countStatus(Orders.CONFIRMED));
        orderStatisticsVO.setToBeConfirmed(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS));
        return orderStatisticsVO;
    }

    /**
     * Confirm order
     * @param id
     */
    public void confirmOrder(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    /**
     * Reject order
     * @param id
     * @param rejectionReason
     */
    public void RejectOrder(Long id, String rejectionReason) {
        Orders orders = orderMapper.getById(id);
        //只有TO_BE_CONFIRMED可以Reject
        if (orders == null || !Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //已支付需要REFUND
        Orders newOrder = new Orders();
        newOrder.setId(id);
        if (Objects.equals(orders.getPayStatus(), PAID)) {
            log.info("Reject order and refund pay: {}", id);
            newOrder.setPayStatus(Orders.REFUND);
        }
        newOrder.setStatus(Orders.CANCELLED);
        newOrder.setRejectionReason(rejectionReason);
        orderMapper.update(newOrder);
    }

    /**
     * Cancel order
     * @param id
     * @param cancelReason
     */
    public void CancelOrder(Long id, String cancelReason) {
        Orders orders = orderMapper.getById(id);
        //已支付需要REFUND
        Orders newOrder = new Orders();
        newOrder.setId(id);
        if (Objects.equals(orders.getPayStatus(), PAID)) {
            log.info("Cancel order and refund pay: {}", id);
            newOrder.setPayStatus(Orders.REFUND);
        }
        newOrder.setStatus(Orders.CANCELLED);
        newOrder.setCancelReason(cancelReason);
        newOrder.setCancelTime(LocalDateTime.now());
        orderMapper.update(newOrder);
    }

    /**
     * Delivery order
     * @param id
     */
    public void deliveryOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null || !Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders newOrder = new Orders();
        newOrder.setId(id);
        newOrder.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(newOrder);
    }

    /**
     * Complete order
     * @param id
     */
    public void completeOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null || !Objects.equals(orders.getStatus(), Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders newOrder = new Orders();
        newOrder.setId(id);
        newOrder.setStatus(Orders.COMPLETED);
        newOrder.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(newOrder);
    }

    /**
     * 检查客户的收货地址是否超出配送范围
     *
     * @param address 收货地址
     */
    private void checkOutOfRange(String address) {
        Map<String, String> map = new HashMap<>();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        // 获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        JSONObject jsonObject = JSON.parseObject(shopCoordinate);

        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("店铺地址解析失败");
        }

        // 数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");

        // 店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address", address);

        // 获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        jsonObject = JSON.parseObject(userCoordinate);

        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("收货地址解析失败");
        }

        // 数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");

        // 用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin", shopLngLat);
        map.put("destination", userLngLat);
        map.put("steps_info", "0");

        // 路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);
        jsonObject = JSON.parseObject(json);

        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("配送路线规划失败");
        }

        // 数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = result.getJSONArray("routes");
        Integer distance = jsonArray.getJSONObject(0).getInteger("distance");

        if (distance > 5000) {
            // 配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }

}
