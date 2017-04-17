package com.road.tool.util;


public class FieldInfo
{
    private boolean isAotuIncreamte = false;
    private boolean isPrimaryKey = false;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段Java类型
     */
    private String javaType;

    /**
     * 字段数据库类型
     */
    private String sqlType;

    /**
     * 字段值长度
     */
    private int len;

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 标志是否加更改说明
     */
    private boolean flag = false;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getJavaType()
    {
        return javaType;
    }

    public void setJavaType(String javaType)
    {
        this.javaType = javaType;
    }

    public String getSqlType()
    {
        return sqlType;
    }

    public void setSqlType(String sqlType)
    {
        this.sqlType = sqlType;
    }

    public int getLen()
    {
        return len;
    }

    public void setLen(int len)
    {
        this.len = len;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public boolean isPrimaryKey()
    {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey)
    {
        this.isPrimaryKey = isPrimaryKey;
    }

    public boolean isAotuIncreamte()
    {
        return isAotuIncreamte;
    }

    public void setAotuIncreamte(boolean isAotuIncreamte)
    {
        this.isAotuIncreamte = isAotuIncreamte;
    }

}
