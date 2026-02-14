package com.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.TokenDao;
import com.entity.TokenEntity;
import com.service.TokenService;

@Service("tokenService")
public class TokenServiceImpl extends ServiceImpl<TokenDao, TokenEntity> implements TokenService {

	@Override
	public TokenEntity getTokenEntity(String token) {
		return this.selectOne(new EntityWrapper<TokenEntity>().eq("token", token));
	}

	@Override
	public String generateToken(Long userId, String username, String tablename, String role) {
		// 生成token字符串
		String token = UUID.randomUUID().toString().replaceAll("-", "");

		// 设置过期时间（比如24小时后过期）
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		Date expireTime = calendar.getTime();

		// 先删除该用户之前的token
		this.delete(new EntityWrapper<TokenEntity>()
				.eq("userid", userId)
				.eq("tablename", tablename));

		// 创建新的token实体
		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserid(userId);
		tokenEntity.setUsername(username);
		tokenEntity.setTablename(tablename);
		tokenEntity.setRole(role);
		tokenEntity.setToken(token);
		tokenEntity.setAddtime(new Date());
		tokenEntity.setExpiratedtime(expireTime);

		// 保存到数据库
		this.insert(tokenEntity);

		return token;
	}
}