package com.xxz.reggie.dto;

import com.xxz.reggie.entity.Setmeal;
import com.xxz.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
