package com.yuyuforest.personalproject2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 存储食品详情的所有数据
class FoodInfo{
    public static final String[][] foodInfo = {
            {"大豆","粮食","蛋白质","colorOrange"},
            {"十字花科蔬菜","蔬菜","维生素C","colorYellow"},
            {"牛奶","饮品","钙","colorPurple"},
            {"海鱼","肉食","蛋白质","colorGreen"},
            {"菌菇类","蔬菜","微量元素","colorOrange"},
            {"绿茶","饮品","无机矿质元素","colorYellow"},
            {"番茄","蔬菜","番茄红素","colorPurple"},
            {"胡萝卜","蔬菜","胡萝卜素","colorGreen"},
            {"荞麦","粮食","膳食纤维","colorOrange"},
            {"鸡蛋","杂","几乎所有营养物质","colorYellow"}
    };
}

// 存储一种食品的详情（包括种类、营养物质、颜色）
class FoodDetail{
    private String foodKind;
    private String foodNutrient;
    private String foodColor;

    public FoodDetail(String k, String n, String c){
        foodKind = k;
        foodNutrient = n;
        foodColor = c;
    }

    public String getFoodKind(){
        return foodKind;
    }

    public String getFoodNutrient() {
        return foodNutrient;
    }

    public String getFoodColor(){
        return foodColor;
    }
}

// 为DetailActivity提供通过食品名称获得详情的查询
// 为CollectionActivity提供获得所有食品名称和种类缩写的查询
public class FoodMap {
    private Map<String, FoodDetail> foodMap;
    private static FoodMap instance;

    protected FoodMap(){
        foodMap = new HashMap<>();
        String[][] info = FoodInfo.foodInfo;
        for(int i = 0; i < info.length; i++){
            foodMap.put(info[i][0], new FoodDetail(info[i][1], info[i][2], info[i][3]));
        }
    }

    public static FoodMap getInstance() {
        if(instance == null){
            instance = new FoodMap();
        }
        return instance;
    }

    public String getKind(String name){
        return foodMap.get(name).getFoodKind();
    }

    public String getNutrient(String name){
        return foodMap.get(name).getFoodNutrient();
    }

    public String getColor(String name){
        return foodMap.get(name).getFoodColor();
    }

    public ArrayList<FoodShort> getSimpleFoodList(){
        ArrayList<FoodShort> list = new ArrayList<>();
        String[][] info = FoodInfo.foodInfo;
        for(int i = 0; i < info.length; i++){
            list.add(new FoodShort(info[i][0], info[i][1].substring(0, 1)));
        }
        return list;
    }
}