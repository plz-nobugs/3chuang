package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableField;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 快递柜状态
 * 数据库通用操作实体类（普通增删改查）
 */
@TableName("lockerstatus")
public class LockerstatusEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;


    public LockerstatusEntity() {

    }

    public LockerstatusEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 主键id
     */
    @TableId
    private Long id;

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
     * 添加时间
     * 标记该字段在数据库表中不存在
     */
    @TableField(exist = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Date getAddtime() {
        return addtime;
    }
    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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