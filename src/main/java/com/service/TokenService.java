package com.service;

import com.entity.TokenEntity;

public interface TokenService {
	TokenEntity getTokenEntity(String token);

	/**
	 * 生成token
	 * @param userId 用户ID
	 * @param username 用户名
	 * @param tablename 表名
	 * @param role 角色
	 * @return token字符串
	 */
	String generateToken(Long userId, String username, String tablename, String role);
}