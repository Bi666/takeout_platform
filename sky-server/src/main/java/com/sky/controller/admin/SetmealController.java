package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("new a setmeal")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.saveSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * Setmeal paging query
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("Setmeal paging query")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * delete setmeal in batches
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("delete setmeal in batches")
    public Result deleteSetmeal(@RequestParam("ids") List<Long> ids) {
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * query setmeal by id
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result getSetmealById(@PathVariable("id") Long id) {
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    /**
     * edit information of setmeal
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("edit information of setmeal")
    public Result editSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.editSetmeal(setmealDTO);
        return Result.success();
    }
}
