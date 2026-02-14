package com.entity.view;

import com.entity.LockerstatusEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;


/**
 * 快递柜状态
 * 后端返回视图实体辅助类
 */
@TableName("lockerstatus")
public class LockerstatusView extends LockerstatusEntity<LockerstatusView> implements Serializable {
    private static final long serialVersionUID = 1L;

    public LockerstatusView(){
    }

    public LockerstatusView(LockerstatusEntity<LockerstatusView> lockerstatusEntity){
        try {
            BeanUtils.copyProperties(this, lockerstatusEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}