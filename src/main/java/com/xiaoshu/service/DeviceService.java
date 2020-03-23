package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.DeviceMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.DeviceExample;
import com.xiaoshu.entity.DeviceExample.Criteria;
@Service
public class DeviceService {

	@Autowired
	UserMapper userMapper;
	
	@Autowired
	DeviceMapper deviceMapper;
	
	@Autowired
	RedisTemplate redisTemplate;

	// 查询所有
	public List<Device> findUser(Device t) throws Exception {
		return deviceMapper.selectDeviceByExample(t);
	};
//
//	// 数量
//	public int countUser(User t) throws Exception {
//		return userMapper.selectCount(t);
//	};
//
//	// 通过ID查询
//	public User findOneUser(Integer id) throws Exception {
//		return userMapper.selectByPrimaryKey(id);
//	};
//
	// 新增
	public void addUser(Device t) throws Exception {
		deviceMapper.insert(t);
		redisTemplate.boundValueOps("Device").set(t);
		redisTemplate.boundHashOps("device1").put("device", t);
	};

	// 修改
	public void updateUser(Device t) throws Exception {
		deviceMapper.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		deviceMapper.deleteByPrimaryKey(id);
	};
//
//	// 登录
//	public User loginUser(User user) throws Exception {
//		UserExample example = new UserExample();
//		Criteria criteria = example.createCriteria();
//		criteria.andPasswordEqualTo(user.getPassword()).andUsernameEqualTo(user.getUsername());
//		List<User> userList = userMapper.selectByExample(example);
//		return userList.isEmpty()?null:userList.get(0);
//	};
//
	// 通过用户名判断是否存在，（新增时不能重名）
	public Device existUserWithUserName(String userName) throws Exception {
		DeviceExample example = new DeviceExample();
		Criteria criteria = example.createCriteria();
		criteria.andDevicenameEqualTo(userName);
		List<Device> userList = deviceMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};
//
//	// 通过角色判断是否存在
//	public User existUserWithRoleId(Integer roleId) throws Exception {
//		UserExample example = new UserExample();
//		Criteria criteria = example.createCriteria();
//		criteria.andRoleidEqualTo(roleId);
//		List<User> userList = userMapper.selectByExample(example);
//		return userList.isEmpty()?null:userList.get(0);
//	}

	public PageInfo<Device> findUserPage(Device device, int pageNum, int pageSize, String ordername, String order) {
		PageHelper.startPage(pageNum, pageSize);
		List<Device> userList = deviceMapper.selectDeviceByExample(device);
		PageInfo<Device> pageInfo = new PageInfo<Device>(userList);
		return pageInfo;
	}
	public List<Device> getEcharts() {
		// TODO Auto-generated method stub
		return deviceMapper.getEcharts();
	}


}
