package com.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.annotation.IgnoreAuth;

import com.entity.DianzhuEntity;
import com.entity.view.DianzhuView;

import com.service.DianzhuService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 店主
 * 后端接口
 * @author
 * @email
 * @date 2021-04-27 11:11:19
 */
@RestController
@RequestMapping("/dianzhu")
public class DianzhuController {
	@Autowired
	private DianzhuService dianzhuService;

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
		DianzhuEntity user = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("dianzhuzhanghao", username));
		if(user==null || !user.getMima().equals(password)) {
			return R.error("账号或密码不正确");
		}

		String token = tokenService.generateToken(user.getId(), username,"dianzhu",  "店主" );
		return R.ok().put("token", token);
	}

	/**
	 * 注册 - 店主专用
	 */
	@IgnoreAuth
	@RequestMapping("/register")
	public R register(@RequestBody DianzhuEntity dianzhu, HttpServletRequest request){
		try {
			// 1. 基本参数验证
			if (StringUtils.isBlank(dianzhu.getDianzhuzhanghao())) {
				return R.error("账号不能为空");
			}
			if (StringUtils.isBlank(dianzhu.getMima())) {
				return R.error("密码不能为空");
			}
			if (StringUtils.isBlank(dianzhu.getYouxiang())) {
				return R.error("邮箱不能为空");
			}

			// 2. 验证邮箱格式
			if (!dianzhu.getYouxiang().matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
				return R.error("邮箱格式不正确");
			}

			// 3. 检查账号是否已存在
			DianzhuEntity existUser = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("dianzhuzhanghao", dianzhu.getDianzhuzhanghao()));
			if(existUser != null) {
				return R.error("账号已存在，请更换账号");
			}

			// 4. 检查邮箱是否已存在
			DianzhuEntity emailUser = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("youxiang", dianzhu.getYouxiang()));
			if(emailUser != null) {
				return R.error("该邮箱已被注册，请使用其他邮箱");
			}

			// 5. 获取前端传来的验证码参数
			String emailCode = request.getParameter("emailCode");
			System.out.println("店主注册 - 前端传来的验证码: " + emailCode);

			// 6. 强制验证邮箱验证码（必须验证）
			if (StringUtils.isBlank(emailCode)) {
				return R.error("请输入邮箱验证码");
			}

			// 7. 验证码校验
			HttpSession session = request.getSession();
			String sessionCode = (String) session.getAttribute("emailCode_" + dianzhu.getYouxiang());
			Long codeTime = (Long) session.getAttribute("emailCodeTime_" + dianzhu.getYouxiang());

			System.out.println("店主注册 - Session中的验证码: " + sessionCode);
			System.out.println("店主注册 - 验证码时间: " + codeTime);

			if (StringUtils.isBlank(sessionCode) || codeTime == null) {
				return R.error("验证码已过期或不存在，请重新获取验证码");
			}

			// 检查验证码是否过期（5分钟）
			if (System.currentTimeMillis() - codeTime > 5 * 60 * 1000) {
				session.removeAttribute("emailCode_" + dianzhu.getYouxiang());
				session.removeAttribute("emailCodeTime_" + dianzhu.getYouxiang());
				return R.error("验证码已过期，请重新获取");
			}

			if (!sessionCode.equals(emailCode.trim())) {
				return R.error("验证码错误，请检查后重新输入");
			}

			// 8. 验证码验证成功，清除验证码
			session.removeAttribute("emailCode_" + dianzhu.getYouxiang());
			session.removeAttribute("emailCodeTime_" + dianzhu.getYouxiang());

			// 9. 设置用户ID和创建时间
			Long uId = new Date().getTime();
			dianzhu.setId(uId);
			dianzhu.setAddtime(new Date());

			// 10. 打印调试信息
			System.out.println("准备插入的店主信息:");
			System.out.println("ID: " + dianzhu.getId());
			System.out.println("账号: " + dianzhu.getDianzhuzhanghao());
			System.out.println("邮箱: " + dianzhu.getYouxiang());
			System.out.println("姓名: " + dianzhu.getDianzhuxingming());

			// 11. 插入数据库
			boolean insertResult = dianzhuService.insert(dianzhu);
			System.out.println("店主数据库插入结果: " + insertResult);

			if (insertResult) {
				return R.ok("店主注册成功！");
			} else {
				return R.error("注册失败，请重试");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("店主注册过程中发生异常: " + e.getMessage());
			return R.error("注册失败: " + e.getMessage());
		}
	}

	/**
	 * 发送邮箱验证码 - 店主专用
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
			DianzhuEntity existUser = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("youxiang", email));
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
	 * 验证邮箱验证码 - 店主专用
	 */
	@PostMapping("/verifyEmailCode")
	@IgnoreAuth
	public R verifyEmailCode(@RequestParam("email") String email,
							 @RequestParam("code") String code,
							 HttpServletRequest request) {
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
	 * 检查邮箱是否可用 - 店主专用
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
			DianzhuEntity existUser = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("youxiang", email));
			if (existUser != null) {
				return R.error("该邮箱已被注册");
			}

			return R.ok("邮箱可用");

		} catch (Exception e) {
			e.printStackTrace();
			return R.error("检查邮箱时发生错误");
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
		DianzhuEntity user = dianzhuService.selectById(id);
		return R.ok().put("data", user);
	}

	/**
	 * 后端列表
	 */
	@RequestMapping("/page")
	public R page(@RequestParam Map<String, Object> params,DianzhuEntity dianzhu,
				  HttpServletRequest request){
		EntityWrapper<DianzhuEntity> ew = new EntityWrapper<DianzhuEntity>();
		PageUtils page = dianzhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, dianzhu), params), params));
		return R.ok().put("data", page);
	}

	/**
	 * 前端列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params,DianzhuEntity dianzhu,
				  HttpServletRequest request){
		EntityWrapper<DianzhuEntity> ew = new EntityWrapper<DianzhuEntity>();
		PageUtils page = dianzhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, dianzhu), params), params));
		return R.ok().put("data", page);
	}

	/**
	 * 后端详情
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id){
		DianzhuEntity dianzhu = dianzhuService.selectById(id);
		return R.ok().put("data", dianzhu);
	}

	/**
	 * 前端详情
	 */
	@RequestMapping("/detail/{id}")
	public R detail(@PathVariable("id") Long id){
		DianzhuEntity dianzhu = dianzhuService.selectById(id);
		return R.ok().put("data", dianzhu);
	}

	/**
	 * 后端保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody DianzhuEntity dianzhu, HttpServletRequest request){
		dianzhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
		DianzhuEntity user = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("dianzhuzhanghao", dianzhu.getDianzhuzhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		dianzhu.setId(new Date().getTime());
		dianzhuService.insert(dianzhu);
		return R.ok();
	}

	/**
	 * 前端保存
	 */
	@RequestMapping("/add")
	public R add(@RequestBody DianzhuEntity dianzhu, HttpServletRequest request){
		dianzhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
		DianzhuEntity user = dianzhuService.selectOne(new EntityWrapper<DianzhuEntity>().eq("dianzhuzhanghao", dianzhu.getDianzhuzhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		dianzhu.setId(new Date().getTime());
		dianzhuService.insert(dianzhu);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody DianzhuEntity dianzhu, HttpServletRequest request){
		dianzhuService.updateById(dianzhu);//全部更新
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		dianzhuService.deleteBatchIds(Arrays.asList(ids));
		return R.ok();
	}
}