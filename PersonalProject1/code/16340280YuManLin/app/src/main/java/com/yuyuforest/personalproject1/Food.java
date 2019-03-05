package com.yuyuforest.personalproject1;

import java.io.Serializable;

// 用于设置食品列表和收藏夹列表的项，只包括食品名称和种类缩写
public class Food implements Serializable {
    private String name;
    private String kind;

    public Food(String _name, String _kind){
        name = _name;
        kind = _kind;
    }

    public String getName(){
        return name;
    }

    public String getKind(){
        return kind;
    }
}
