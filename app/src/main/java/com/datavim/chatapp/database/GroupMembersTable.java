package com.datavim.chatapp.database;

/**
 * Created by apple on 28/05/15.
 */
public class GroupMembersTable {
    public static final String GROUP_MEMBER_TABLE_NAME = "tblGroupMembers";

    public static final String KEY_MEMEBER_ID = "MemberId";
    public static final String KEY_GROUP_ID = "GroupId";
    public static final String KEY_MEMEBER_JID = "MemberJID";
    public static final String KEY_USER_JID = "UserId";
    public static final String KEY_USER_ROLE = "UserRole";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_MOBLIE_NO = "MobileNo";
    public static final String KEY_MEMEBER_NAME = "MemberName";


    public static final String CREATE_GROUP_MEMBER_TABLE = "create table "
            + GROUP_MEMBER_TABLE_NAME + "("
            + KEY_MEMEBER_ID + " integer primary key, "
            + KEY_GROUP_ID + " integer, "
            + KEY_MEMEBER_JID + " text, "
            + KEY_USER_JID + " text, "
            + KEY_USER_ROLE + " text, "
            + KEY_EMAIL + " text, "
            + KEY_MOBLIE_NO + " text, "
            + KEY_MEMEBER_NAME + " text );";
}
