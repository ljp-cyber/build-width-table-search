package com.shxex.bwts.mybatis;

import com.shxex.bwts.compone.JoinEntity;
import com.shxex.bwts.compone.JoinField;
import com.shxex.bwts.compone.JoinForeignKey;

@JoinEntity(table = "user_", joinTable = "search")
public class UserSearchEntity {

	@JoinField(column = "id",field = "id")
	private Long userId;
	private String userName;

	private HobbySearchEntity hobbySearchEntity;

	@JoinEntity(table = "hobby")
	public static class HobbySearchEntity{

		@JoinForeignKey(foreignKeyField = "userId")
		private Long userId;

		@JoinField(column = "id",field = "id")
		private Long hobbyId;

		private String hobbyName;
	}
}
