package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.service.EmailService;
import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.GukeEntity;
import com.entity.view.GukeView;

import com.service.GukeService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;

/**
 * 顾客
 * 后端接口
 * @author
 * @email
 * @date 2021-04-27 11:11:19
 */
@RestController
@RequestMapping("/guke")
public class GukeController {
	@Autowired
	private GukeService gukeService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private EmailService emailService;

	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		GukeEntity user = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("zhanghao", username));
		if(user==null || !user.getMima().equals(password)) {
			return R.error("账号或密码不正确");
		}

		String token = tokenService.generateToken(user.getId(), username,"guke",  "顾客" );
		return R.ok().put("token", token);
	}

	/**
	 * 注册 - 修复版本
	 */
	@IgnoreAuth
	@RequestMapping("/register")
	public R register(@RequestBody GukeEntity guke, HttpServletRequest request){
		try {
			// 1. 基本参数验证
			if (StringUtils.isBlank(guke.getZhanghao())) {
				return R.error("账号不能为空");
			}
			if (StringUtils.isBlank(guke.getMima())) {
				return R.error("密码不能为空");
			}
			if (StringUtils.isBlank(guke.getYouxiang())) {
				return R.error("邮箱不能为空");
			}

			// 2. 验证邮箱格式
			if (!guke.getYouxiang().matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
				return R.error("邮箱格式不正确");
			}

			// 3. 检查账号是否已存在
			GukeEntity existUser = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("zhanghao", guke.getZhanghao()));
			if(existUser != null) {
				return R.error("账号已存在，请更换账号");
			}

			// 4. 检查邮箱是否已存在
			GukeEntity emailUser = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("youxiang", guke.getYouxiang()));
			if(emailUser != null) {
				return R.error("该邮箱已被注册，请使用其他邮箱");
			}

			// 5. 获取前端传来的验证码参数
			String emailCode = request.getParameter("emailCode");
			System.out.println("顾客注册 - 前端传来的验证码: " + emailCode);

			// 6. 强制验证邮箱验证码（必须验证）
			if (StringUtils.isBlank(emailCode)) {
				return R.error("请输入邮箱验证码");
			}

			// 7. 验证码校验
			HttpSession session = request.getSession();
			String sessionCode = (String) session.getAttribute("emailCode_" + guke.getYouxiang());
			Long codeTime = (Long) session.getAttribute("emailCodeTime_" + guke.getYouxiang());

			System.out.println("顾客注册 - Session中的验证码: " + sessionCode);
			System.out.println("顾客注册 - 验证码时间: " + codeTime);

			if (StringUtils.isBlank(sessionCode) || codeTime == null) {
				return R.error("验证码已过期或不存在，请重新获取验证码");
			}

			// 检查验证码是否过期（5分钟）
			if (System.currentTimeMillis() - codeTime > 5 * 60 * 1000) {
				session.removeAttribute("emailCode_" + guke.getYouxiang());
				session.removeAttribute("emailCodeTime_" + guke.getYouxiang());
				return R.error("验证码已过期，请重新获取");
			}

			if (!sessionCode.equals(emailCode.trim())) {
				return R.error("验证码错误，请检查后重新输入");
			}

			// 8. 验证码验证成功，清除验证码
			session.removeAttribute("emailCode_" + guke.getYouxiang());
			session.removeAttribute("emailCodeTime_" + guke.getYouxiang());

			// 9. 设置用户ID和创建时间
			Long uId = new Date().getTime();
			guke.setId(uId);
			guke.setAddtime(new Date());  // 设置创建时间

			// 10. 打印调试信息
			System.out.println("准备插入的顾客信息:");
			System.out.println("ID: " + guke.getId());
			System.out.println("账号: " + guke.getZhanghao());
			System.out.println("邮箱: " + guke.getYouxiang());
			System.out.println("姓名: " + guke.getGukexingming());

			// 11. 插入数据库
			boolean insertResult = gukeService.insert(guke);
			System.out.println("顾客数据库插入结果: " + insertResult);

			if (insertResult) {
				return R.ok("顾客注册成功！");
			} else {
				return R.error("注册失败，请重试");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("顾客注册过程中发生异常: " + e.getMessage());
			return R.error("注册失败: " + e.getMessage());
		}
	}

	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}

	/**
	 * 获取用户的session用户信息
	 */
	@RequestMapping("/session")
	public R getCurrUser(HttpServletRequest request){
		Long id = (Long)request.getSession().getAttribute("userId");
		GukeEntity user = gukeService.selectById(id);
		return R.ok().put("data", user);
	}

	/**
	 * 密码重置
	 */
	@IgnoreAuth
	@RequestMapping(value = "/resetPass")
	public R resetPass(String username, HttpServletRequest request){
		GukeEntity user = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("zhanghao", username));
		if(user==null) {
			return R.error("账号不存在");
		}
		user.setMima("123456");
		gukeService.updateById(user);
		return R.ok("密码已重置为：123456");
	}

	/**
	 * 后端列表
	 */
	@RequestMapping("/page")
	public R page(@RequestParam Map<String, Object> params,GukeEntity guke,
				  HttpServletRequest request){
		EntityWrapper<GukeEntity> ew = new EntityWrapper<GukeEntity>();
		PageUtils page = gukeService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, guke), params), params));

		return R.ok().put("data", page);
	}

	/**
	 * 前端列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params,GukeEntity guke,
				  HttpServletRequest request){
		EntityWrapper<GukeEntity> ew = new EntityWrapper<GukeEntity>();
		PageUtils page = gukeService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, guke), params), params));
		return R.ok().put("data", page);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/lists")
	public R list( GukeEntity guke){
		EntityWrapper<GukeEntity> ew = new EntityWrapper<GukeEntity>();
		ew.allEq(MPUtil.allEQMapPre( guke, "guke"));
		return R.ok().put("data", gukeService.selectListView(ew));
	}

	/**
	 * 查询
	 */
	@RequestMapping("/query")
	public R query(GukeEntity guke){
		EntityWrapper< GukeEntity> ew = new EntityWrapper< GukeEntity>();
		ew.allEq(MPUtil.allEQMapPre( guke, "guke"));
		GukeView gukeView =  gukeService.selectView(ew);
		return R.ok("查询顾客成功").put("data", gukeView);
	}

	/**
	 * 后端详情
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		GukeEntity guke = gukeService.selectById(id);
		return R.ok().put("data", guke);
	}

	/**
	 * 前端详情
	 */
	@RequestMapping("/detail/{id}")
	public R detail(@PathVariable("id") Long id){
		GukeEntity guke = gukeService.selectById(id);
		return R.ok().put("data", guke);
	}

	/**
	 * 后端保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody GukeEntity guke, HttpServletRequest request){
		guke.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
		//ValidatorUtils.validateEntity(guke);
		GukeEntity user = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("zhanghao", guke.getZhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		guke.setId(new Date().getTime());
		gukeService.insert(guke);
		return R.ok();
	}

	/**
	 * 前端保存
	 */
	@RequestMapping("/add")
	public R add(@RequestBody GukeEntity guke, HttpServletRequest request){
		guke.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
		//ValidatorUtils.validateEntity(guke);
		GukeEntity user = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("zhanghao", guke.getZhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		guke.setId(new Date().getTime());
		gukeService.insert(guke);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody GukeEntity guke, HttpServletRequest request){
		//ValidatorUtils.validateEntity(guke);
		gukeService.updateById(guke);//全部更新
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		gukeService.deleteBatchIds(Arrays.asList(ids));
		return R.ok();
	}

	/**
	 * 提醒接口
	 */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request,
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);

		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}

		Wrapper<GukeEntity> wrapper = new EntityWrapper<GukeEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		int count = gukeService.selectCount(wrapper);
		return R.ok().put("count", count);
	}

	/**
	 * 发送邮箱验证码
	 */
	@PostMapping("/sendEmailCode")
	@IgnoreAuth
	public R sendEmailCode(@RequestParam("email") String email, HttpServletRequest request) {
		try {
			// 验证邮箱格式
			if (StringUtils.isBlank(email) || !email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
				return R.error("邮箱格式不正确");
			}

			// 检查邮箱是否已被注册
			GukeEntity existUser = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("youxiang", email));
			if (existUser != null) {
				return R.error("该邮箱已被注册，请使用其他邮箱");
			}

			// 检查发送频率限制（防止频繁发送）
			HttpSession session = request.getSession();
			Long lastSendTime = (Long) session.getAttribute("lastSendTime_" + email);
			long currentTime = System.currentTimeMillis();

			if (lastSendTime != null && (currentTime - lastSendTime) < 60000) { // 1分钟内不能重复发送
				long waitTime = 60 - (currentTime - lastSendTime) / 1000;
				return R.error("发送过于频繁，请" + waitTime + "秒后再试");
			}

			// 生成6位随机验证码
			String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));

			// 发送邮件
			boolean success = emailService.sendVerificationCode(email, code);

			if (success) {
				// 将验证码存储到session中，设置5分钟过期
				session.setAttribute("emailCode_" + email, code);
				session.setAttribute("emailCodeTime_" + email, currentTime);
				session.setAttribute("lastSendTime_" + email, currentTime);

				return R.ok("验证码已发送到您的邮箱，请查收邮件。验证码5分钟内有效。");
			} else {
				return R.error("验证码发送失败，请检查邮箱地址或稍后重试");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return R.error("系统繁忙，请稍后重试");
		}
	}

	/**
	 * 验证邮箱验证码（公开接口）
	 */
	@PostMapping("/verifyEmailCode")
	@IgnoreAuth
	public R verifyEmailCode(@RequestParam("email") String email,
							 @RequestParam("code") String code,
							 HttpServletRequest request) {
		return verifyEmailCodeInternal(email, code, request);
	}

	/**
	 * 验证邮箱验证码（内部方法）
	 */
	private R verifyEmailCodeInternal(String email, String code, HttpServletRequest request) {
		try {
			if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
				return R.error("邮箱和验证码不能为空");
			}

			HttpSession session = request.getSession();
			String sessionCode = (String) session.getAttribute("emailCode_" + email);
			Long codeTime = (Long) session.getAttribute("emailCodeTime_" + email);

			if (StringUtils.isBlank(sessionCode) || codeTime == null) {
				return R.error("验证码已过期或不存在，请重新获取");
			}

			// 检查验证码是否过期（5分钟）
			if (System.currentTimeMillis() - codeTime > 5 * 60 * 1000) {
				// 清除过期的验证码
				session.removeAttribute("emailCode_" + email);
				session.removeAttribute("emailCodeTime_" + email);
				return R.error("验证码已过期，请重新获取");
			}

			if (sessionCode.equals(code.trim())) {
				// 验证成功，清除验证码
				session.removeAttribute("emailCode_" + email);
				session.removeAttribute("emailCodeTime_" + email);
				return R.ok("验证码验证成功");
			} else {
				return R.error("验证码错误，请检查后重新输入");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return R.error("验证过程中发生错误，请重试");
		}
	}

	/**
	 * 检查邮箱是否可用
	 */
	@GetMapping("/checkEmail")
	@IgnoreAuth
	public R checkEmail(@RequestParam("email") String email) {
		try {
			if (StringUtils.isBlank(email)) {
				return R.error("邮箱不能为空");
			}

			// 验证邮箱格式
			if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
				return R.error("邮箱格式不正确");
			}

			// 检查邮箱是否已被注册
			GukeEntity existUser = gukeService.selectOne(new EntityWrapper<GukeEntity>().eq("youxiang", email));
			if (existUser != null) {
				return R.error("该邮箱已被注册");
			}

			return R.ok("邮箱可用");

		} catch (Exception e) {
			e.printStackTrace();
			return R.error("检查邮箱时发生错误");
		}
	}
}