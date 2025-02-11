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
        Dish dish = new Dish();
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
}
