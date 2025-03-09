package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Dishes manage
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "Dishes Manage Interface")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * new a dish
     * @param dishDto
     * @return
     */
    @PostMapping
    @ApiOperation("new a dish")
    public Result save(@RequestBody DishDTO dishDto) {
        log.info("new a dish: {}", dishDto);
        dishService.saveWithFlavor(dishDto);

        //清理缓存数据
        String key = "dish_:" + dishDto.getCategoryId();
        cleanCache(key);

        return Result.success();
    }

    /**
     * Dish paging query
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("Dish paging query")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("page: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * Deleted dishes in batches
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("Deleted dishes in batches")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("delete: {}", ids);
        dishService.deleteBatch(ids);

        //清理所有dish缓存数据
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * Query dish by ID
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("Query dish by ID")
    public Result<DishVO> getById(@PathVariable("id") Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * update dish information
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("update dish information")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * set dish status
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("set dish status")
    public Result setStatus(@PathVariable Integer status, @RequestParam Long id) {
        dishService.setStatus(status, id);

        //清理所有dish缓存数据
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * query dish by categoryId
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("query dish by categoryId")
    public Result<List<Dish>> queryByCategoryId(@RequestParam Long categoryId) {
        List<Dish> dishs = dishService.queryWithCategory(categoryId);
        return Result.success(dishs);
    }

    /**
     * clean cache data
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
