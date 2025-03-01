package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "Shop interface")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Set store status
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("Set store status")
    public Result setStatus(@PathVariable Integer status) {
        log.info("Set store status:{}", status == 1 ? "opening" : "closing");
        redisTemplate.opsForValue().set("KEY", status);
        return Result.success();
    }

    /**
     * Get store status
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("Get store status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("KEY");
        log.info("Get store status:{}", status == 1 ? "opening" : "closing");
        return Result.success(status);
    }
}
