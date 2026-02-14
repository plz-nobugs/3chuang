package com.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.TokenEntity;
import com.entity.UserEntity;
import com.service.TokenService;
import com.service.UserService;
import com.service.EmailService;
import com.utils.CommonUtil;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.ValidatorUtils;
import com.utils.VerificationCodeCache;

/**
 * 登录相关
 */
@RequestMapping("users")
@RestController
public class UserController{

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private VerificationCodeCache verificationCodeCache;

	/**
	 * 管理员登录方法 - 修复Token返回结构
	 */
	@IgnoreAuth
	@PostMapping(value = "/login")
	public R login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
		try {
			System.out.println("=== 管理员登录调试 ===");
			System.out.println("用户名: " + username);
			System.out.println("密码: " + password);

			// 参数验证
			if (username == null || username.trim().isEmpty()) {
				return R.error("用户名不能为空");
			}
			if (password == null || password.trim().isEmpty()) {
				return R.error("密码不能为空");
			}

			// 查询用户
			UserEntity user = userService.selectOne(new EntityWrapper<UserEntity>().eq("username", username));
			if (user == null) {
				System.out.println("用户不存在");
				return R.error("账号不存在");
			}

			System.out.println("找到用户: " + user.getUsername());
			System.out.println("数据库密码: " + user.getPassword());

			// 验证密码 - 这里根据您的密码存储方式调整
			if (!password.equals(user.getPassword())) {
				System.out.println("密码不匹配");
				return R.error("用户名或密码不正确");
			}

			// 生成token
			String token = tokenService.generateToken(user.getId(), username, "users", "管理员");
			System.out.println("生成的Token: " + token);

			// 存储session信息
			request.getSession().setAttribute("userId", user.getId());
			request.getSession().setAttribute("role", "管理员");
			request.getSession().setAttribute("tableName", "users");
			request.getSession().setAttribute("username", username);

			// **关键修复：直接在顶层返回token，不要嵌套在data中**
			R result = R.ok("登录成功");
			result.put("token", token);
			result.put("role", "管理员");
			result.put("sessionTable", "users");
			result.put("username", username);

			System.out.println("登录成功，返回结果: " + result);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return R.error("登录失败，请稍后重试");
		}
	}

	/**
	 * 发送邮箱验证码
	 */
	@IgnoreAuth
	@PostMapping(value = "/sendCode")
	public R sendVerificationCode(@RequestParam String email) {
		try {
			// 验证邮箱格式
			if (email == null || email.trim().isEmpty()) {
				return R.error("邮箱地址不能为空");
			}

			// 简单邮箱格式验证
			if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
				return R.error("邮箱格式不正确");
			}

			// 检查邮箱是否已注册
			UserEntity existUser = userService.selectOne(new EntityWrapper<UserEntity>().eq("username", email));
			if (existUser != null) {
				return R.error("该邮箱已注册");
			}

			// 检查是否已有有效验证码（防止频繁发送）
			if (verificationCodeCache.hasValidCode(email)) {
				long remainingTime = verificationCodeCache.getRemainingTime(email);
				return R.error("验证码已发送，请" + remainingTime + "秒后再试");
			}

			// 生成验证码
			String code = verificationCodeCache.generateCode();

			// 发送邮件 - 添加详细的异常处理
			boolean sendSuccess = false;
			String errorMessage = "";

			try {
				sendSuccess = emailService.sendVerificationCode(email, code);
			} catch (Exception mailException) {
				// 捕获邮件发送的具体异常
				System.err.println("邮件发送异常详情: " + mailException.getMessage());
				mailException.printStackTrace();

				// 根据异常类型返回更具体的错误信息
				if (mailException.getMessage().contains("Authentication")) {
					errorMessage = "邮件服务器认证失败，请联系管理员";
				} else if (mailException.getMessage().contains("Connection")) {
					errorMessage = "邮件服务器连接失败，请稍后重试";
				} else {
					errorMessage = "邮件发送服务暂时不可用，请稍后重试";
				}
			}

			if (sendSuccess) {
				// 邮件发送成功，存储验证码
				verificationCodeCache.storeCode(email, code);
				return R.ok("验证码已发送到您的邮箱，请注意查收");
			} else {
				// 返回具体的错误信息
				return R.error(errorMessage.isEmpty() ? "邮件发送失败，请稍后重试" : errorMessage);
			}

		} catch (Exception e) {
			// 捕获所有其他异常
			System.err.println("发送验证码总体异常: " + e.getMessage());
			e.printStackTrace();
			return R.error("系统错误，请稍后重试");
		}
	}

	/**
	 * 注册（带邮箱验证码验证）
	 */
	@IgnoreAuth
	@PostMapping(value = "/register")
	public R register(@RequestBody Map<String, Object> params) {
		try {
			// 获取参数
			String username = (String) params.get("username");
			String password = (String) params.get("password");
			String email = (String) params.get("email");
			String verificationCode = (String) params.get("verificationCode");

			// 参数验证
			if (username == null || username.trim().isEmpty()) {
				return R.error("用户名不能为空");
			}
			if (password == null || password.trim().isEmpty()) {
				return R.error("密码不能为空");
			}
			if (email == null || email.trim().isEmpty()) {
				return R.error("邮箱不能为空");
			}
			if (verificationCode == null || verificationCode.trim().isEmpty()) {
				return R.error("验证码不能为空");
			}

			// 验证码验证
			if (!verificationCodeCache.verifyCode(email, verificationCode)) {
				return R.error("验证码错误或已过期");
			}

			// 检查用户名是否已存在
			if (userService.selectOne(new EntityWrapper<UserEntity>().eq("username", username)) != null) {
				return R.error("用户名已存在");
			}

			// 检查邮箱是否已注册
			if (userService.selectOne(new EntityWrapper<UserEntity>().eq("username", email)) != null) {
				return R.error("该邮箱已注册");
			}

			// 创建用户对象
			UserEntity user = new UserEntity();
			user.setUsername(username);
			user.setPassword(password);
			user.setAddtime(new Date());

			// 保存用户
			userService.insert(user);

			return R.ok("注册成功");

		} catch (Exception e) {
			e.printStackTrace();
			return R.error("注册失败：" + e.getMessage());
		}
	}

	/**
	 * 退出
	 */
	@GetMapping(value = "logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}

	/**
	 * 密码重置
	 */
	@IgnoreAuth
	@RequestMapping(value = "/resetPass")
	public R resetPass(String username, HttpServletRequest request){
		UserEntity user = userService.selectOne(new EntityWrapper<UserEntity>().eq("username", username));
		if(user==null) {
			return R.error("账号不存在");
		}
		user.setPassword("123456");
		userService.update(user,null);
		return R.ok("密码已重置为：123456");
	}

	/**
	 * 列表
	 */
	@RequestMapping("/page")
	public R page(@RequestParam Map<String, Object> params,UserEntity user){
		EntityWrapper<UserEntity> ew = new EntityWrapper<UserEntity>();
		PageUtils page = userService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.allLike(ew, user), params), params));
		return R.ok().put("data", page);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list( UserEntity user){
		EntityWrapper<UserEntity> ew = new EntityWrapper<UserEntity>();
		ew.allEq(MPUtil.allEQMapPre( user, "user"));
		return R.ok().put("data", userService.selectListView(ew));
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") String id){
		UserEntity user = userService.selectById(id);
		return R.ok().put("data", user);
	}

	/**
	 * 获取用户的session用户信息
	 */
	@RequestMapping("/session")
	public R getCurrUser(HttpServletRequest request){
		Long id = (Long)request.getSession().getAttribute("userId");
		UserEntity user = userService.selectById(id);
		return R.ok().put("data", user);
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public R save(@RequestBody UserEntity user){
//    	ValidatorUtils.validateEntity(user);
		if(userService.selectOne(new EntityWrapper<UserEntity>().eq("username", user.getUsername())) !=null) {
			return R.error("用户已存在");
		}
		userService.insert(user);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody UserEntity user){
//        ValidatorUtils.validateEntity(user);
		UserEntity u = userService.selectOne(new EntityWrapper<UserEntity>().eq("username", user.getUsername()));
		if(u!=null && u.getId()!=user.getId() && u.getUsername().equals(user.getUsername())) {
			return R.error("用户名已存在。");
		}
		userService.updateById(user);//全部更新
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		userService.deleteBatchIds(Arrays.asList(ids));
		return R.ok();
	}
}