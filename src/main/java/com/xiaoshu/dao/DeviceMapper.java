package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.DeviceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeviceMapper extends BaseMapper<Device> {
    long countByExample(DeviceExample example);

    int deleteByExample(DeviceExample example);

    List<Device> selectByExample(DeviceExample example);
    
    List<Device> selectDeviceByExample(Device device);

    int updateByExampleSelective(@Param("record") Device record, @Param("example") DeviceExample example);

    int updateByExample(@Param("record") Device record, @Param("example") DeviceExample example);

	List<Device> getEcharts();
}