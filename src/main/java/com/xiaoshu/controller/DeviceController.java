package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Attachment;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.Devicetype;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.DeviceService;
import com.xiaoshu.service.DevicetypeService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("device")
public class DeviceController extends LogController{
	static Logger logger = Logger.getLogger(DeviceController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DevicetypeService devicetypeService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	
	@RequestMapping("deviceIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		//查询副表部门
		List<Devicetype> roleList = devicetypeService.findUser(new Devicetype());
		
		
		
		
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "device";
	}
	
	
	@RequestMapping(value="deviceList",method=RequestMethod.POST)
	public void userList(Device device,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
//			User user = new User();
			String devicename1 = request.getParameter("devicename1");
			String devicetypeid1 = request.getParameter("devicetypeid1");
//			String usertype = request.getParameter("usertype");
			String order = request.getParameter("order");
			String ordername = request.getParameter("ordername");
//			if (StringUtil.isNotEmpty(username)) {
//				user.setUsername(username);
//			}
//			if (StringUtil.isNotEmpty(roleid) && !"0".equals(roleid)) {
//				user.setRoleid(Integer.parseInt(roleid));
//			}
//			if (StringUtil.isNotEmpty(usertype)) {
//				user.setUsertype(usertype.getBytes()[0]);
//			}
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			
			
			
			
			//查询主表
			PageInfo<Device> userList= deviceService.findUserPage(device,pageNum,pageSize,ordername,order);
			
			
			
			
			request.setAttribute("devicename1", devicename1);
			request.setAttribute("devicetypeid1", devicetypeid1);
//			request.setAttribute("usertype", usertype);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	@Autowired
	private ActiveMQQueue amq;
	
	@Autowired
	private JmsTemplate jms;
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(HttpServletRequest request,final Device user,HttpServletResponse response){
		Integer userId = user.getDeviceid();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // Id不为空 说明是修改     为空   说明是添加
				Device userName = deviceService.existUserWithUserName(user.getDevicename());//判断name有没有重复	 没有重复可以修改
				if(userName != null){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}else{
					
					
					
					
					//修改的时候获取它的id
					user.setDeviceid(userId);
					//直接把当前时间时间添加到数据库
					user.setCreatetime(new Date());
					//修改
					deviceService.updateUser(user);
					result.put("success", true);
					
					
					
					
				}
				
			}else {   // 添加
				if(deviceService.existUserWithUserName(user.getDevicename())==null){  //判断name有没有重复	 没有重复可以添加
					
					
					
					
					//添加的时候直接添加当前时间
					user.setCreatetime(new Date());
					//添加
					deviceService.addUser(user);
					
					jms.send(amq,new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							return session.createTextMessage(user.toString());
						}
					});
					result.put("success", true);
					
					
					
					
					
					
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				
				
				
				//调用删除方法
				deviceService.deleteUser(Integer.parseInt(id));
			
			
			
			
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("getEcharts")
	public void getEcharts(HttpServletRequest request,HttpServletResponse response) throws IOException{
		List<Device> list = deviceService.getEcharts();
		Object json = JSONObject.toJSON(list);
		response.getWriter().write(json.toString());
	}
	
	@RequestMapping("daochu")
	public void backup(HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "导出"+time;
			Device device = new Device();
			List<Device> list = deviceService.findUser(device);
			String[] handers = {"编号","设备名称","设备类型名称","设备内存","设备颜色","设备价格","设备状态","创建时间"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list, excelName);
			// 2导出的位置放入attachment表
//			Attachment attachment = new Attachment();
//			attachment.setAttachmentname(excelName+".xls");
//			attachment.setAttachmentpath("logs/backup");
//			attachment.setAttachmenttime(new Date());
//			attachmentService.insertAttachment(attachment);
//			// 3删除log表
//			logService.truncateLog();
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("", "对不起，备份失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	
	// 导出到硬盘
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request,
			String[] handers, List<Device> list, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
			    //获取第一行的每一个单元格
			    HSSFCell cell = rowFirst.createCell(i);
			    //往单元格里面写入值
			    cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list.size(); i++) {
			    //获取list里面存在是数据集对象
			    Device log = list.get(i);
			    //创建数据行
			    HSSFRow row = sheet.createRow(i+1);
			    //设置对应单元格的值
			    row.setHeight((short)400);   // 设置每行的高度
			    //"编号","设备名称","设备类型名称","设备内存","设备颜色","设备价格","设备状态","创建时间"
			    row.createCell(0).setCellValue(i+1);
			    row.createCell(1).setCellValue(log.getDevicename());
			    row.createCell(2).setCellValue(log.getDevicetype().getTypename());
			    row.createCell(3).setCellValue(log.getDeviceram());
			    row.createCell(4).setCellValue(log.getColor());
			    row.createCell(5).setCellValue(log.getPrice());
			    row.createCell(6).setCellValue(log.getStatus()==1?"启用":"禁用");
			    row.createCell(7).setCellValue(TimeUtil.formatTime(log.getCreatetime(), "yyyy-MM-dd HH:mm:ss"));
			}
			//写出文件（path为文件路径含文件名）
				OutputStream os;
				File file = new File("E:"+File.separator+excleName+".xls");
				
				if (!file.exists()){//若此目录不存在，则创建之  
					file.createNewFile();  
					logger.debug("创建文件夹路径为："+ file.getPath());  
	            } 
				os = new FileOutputStream(file);
				wb.write(os);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}

	
	
	
	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
