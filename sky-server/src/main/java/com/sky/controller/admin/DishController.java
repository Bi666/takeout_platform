package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return Result.success();
    }
}
