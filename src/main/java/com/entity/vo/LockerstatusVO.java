package com.entity.vo;

import com.entity.LockerstatusEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;


/**
 * 快递柜状态
 * 手机端接口返回实体辅助类
 */
public class LockerstatusVO  implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 名称
     */

    private String name;

    /**
     * 经度
     */

    private Float longitude;

    /**
     * 纬度
     */

    private Float latitude;

    /**
     * 总格数
     */

    private Integer total;

    /**
     * 已用格数
     */

    private Integer used;

    /**
     * 描述
     */

    private String description;


    /**
     * 设置：名称
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取：名称
     */
    public String getName() {
        return name;
    }


    /**
     * 设置：经度
     */

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /**
     * 获取：经度
     */
    public Float getLongitude() {
        return longitude;
    }


    /**
     * 设置：纬度
     */

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取：纬度
     */
    public Float getLatitude() {
        return latitude;
    }


    /**
     * 设置：总格数
     */

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 获取：总格数
     */
    public Integer getTotal() {
        return total;
    }


    /**
     * 设置：已用格数
     */

    public void setUsed(Integer used) {
        this.used = used;
    }

    /**
     * 获取：已用格数
     */
    public Integer getUsed() {
        return used;
    }


    /**
     * 设置：描述
     */

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取：描述
     */
    public String getDescription() {
        return description;
    }

}