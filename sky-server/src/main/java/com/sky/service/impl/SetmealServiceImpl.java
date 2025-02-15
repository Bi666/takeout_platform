package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * new a setmeal
     * @param setmealDTO
     */
    @Transactional
    public void saveSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //保存套餐表信息
        setmealMapper.insert(setmeal);

        //保存dish-set信息
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * Setmeal paging query
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.queryPage(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * delete setmeal in batches
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            //启售中的套餐不能删除
            if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        });

        //删除setmeal表
        setmealMapper.deleteBatch(ids);

        //删除setmeal_dish表
        setmealDishMapper.deleteBatch(ids);
    }

    /**
     * query setmeal by id
     * @param id
     */
    public SetmealVO getSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishMapper.getDishesBySetmealID(id));
        return setmealVO;
    }

    /**
     * edit information of setmeal
     * @param setmealDTO
     */
    @Transactional
    public void editSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //修改setmeal表
        setmealMapper.update(setmeal);

        //修改setmeal_dish表
        //删除原来的关系数据
        Long setmealId = setmealDTO.getId();
        List<Long> ids = new ArrayList<>();
        ids.add(setmealId);
        setmealDishMapper.deleteBatch(ids);

        //添加新的关系数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * start or stop a setmeal
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id){
        //启售套餐时，判断套餐内是否有停售菜品
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            List<Dish> dishes = dishMapper.getBySetmealId(id);
            for (Dish dish : dishes) {
                if (Objects.equals(dish.getStatus(), StatusConstant.DISABLE))
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
