package com.shxex.bwts.dome.joinEnitty;

import com.shxex.bwts.common.joinUpdate.JoinEntity;
import com.shxex.bwts.common.joinUpdate.JoinField;
import com.shxex.bwts.common.joinUpdate.JoinForeignKey;

@JoinEntity(table = "user_", joinTable = "search")
public class SearchJoinEntity {

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
