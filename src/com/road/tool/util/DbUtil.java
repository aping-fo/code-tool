package com.road.tool.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.road.tool.CodeTool;

public class DbUtil
{
    private static Properties prop = new Properties();

    static
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream(CodeTool.propertiesPath);
            prop.load(in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            try
            {
                if (null != in)
                    in.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            System.exit(1);
        }
    }

    public static Connection getConn() throws ClassNotFoundException, SQLException
    {
        Class.forName(prop.getProperty("driverName"));
        Connection conn = DriverManager.getConnection(prop.getProperty("url"),
                prop.getProperty("userName"), prop.getProperty("password"));
        return conn;
    }

    public static Connection getConn(String dbName) throws ClassNotFoundException, SQLException
    {
        Class.forName(prop.getProperty("driverName"));
        Connection conn = DriverManager.getConnection(prop.getProperty("url")
                + "/" + dbName, prop.getProperty("userName"), prop.getProperty("password"));
        return conn;
    }

    public static void close(ResultSet rs, Statement stmt, Connection conn)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        if (conn != null)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得可连接的所有数据库名称
     */
    public static List<String> getDatabases()
    {
        Connection conn = null;
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        List<String> databases = new ArrayList<String>();
        try
        {
            conn = DbUtil.getConn();
            metaData = conn.getMetaData();
            rs = metaData.getCatalogs();
            while (rs.next())
            {
                String database = rs.getString("TABLE_CAT");
                System.err.println(database);
                databases.add(database);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DbUtil.close(rs, null, conn);
        }
        return databases;
    }

    /**
     * 获得当前数据库所有表名
     */
    public static List<String> getTables(String dbName)
    {
        Connection conn = null;
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        List<String> tables = new ArrayList<String>();
        try
        {
            conn = DbUtil.getConn(dbName);
            metaData = conn.getMetaData();
            rs = metaData.getTables(null, null, null, new String[]
            { "TABLE" });

            while (rs.next())
            {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DbUtil.close(rs, null, conn);
        }
        return tables;
    }

    /**
     * 获得数据表具体信息
     */
    @SuppressWarnings("resource")
    public static Map<String, FieldInfo> getTableFieldList(String dbName, String tableName)
    {
        Map<String, FieldInfo> fieldMap = new LinkedHashMap<String, FieldInfo>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        // ParameterMetaData paramMetaData = null;
        ResultSetMetaData rsMetaData = null;
        try
        {
            // 获得数据表各个字段的注释
            conn = DbUtil.getConn("information_schema");
            String sql = "SELECT * FROM COLUMNS WHERE TABLE_SCHEMA='" + dbName
                    + "' AND TABLE_NAME='" + tableName + "'";
            pstm = conn.prepareStatement(sql);
            // paramMetaData = pstm.getParameterMetaData();
            rs = pstm.executeQuery();

            while (rs.next())
            {
                System.out.printf(" %1$s\t\t", rs.getString("column_name"));
                System.out.printf(rs.getString("column_comment").isEmpty() ? "无"
                        : rs.getString("column_comment"));
                System.out.println();
                if (!rs.getString("column_comment").contains("无用"))
                {
                    FieldInfo field = new FieldInfo();
                    field.setName(rs.getString("column_name"));
                    field.setComment(rs.getString("column_comment"));
                    fieldMap.put(rs.getString("column_name"), field);
                }
            }

            conn = DbUtil.getConn(dbName);
            sql = "select * from " + tableName;
            pstm = conn.prepareStatement(sql);
            // paramMetaData = pstm.getParameterMetaData();
            rs = pstm.executeQuery();
            rsMetaData = rs.getMetaData();
            pstm.getParameterMetaData();

            int cols = rsMetaData.getColumnCount(); // 查询获取结果的列数

            for (int i = 1; i <= cols; i++)
            {
                // 字段名
                String name = rsMetaData.getColumnName(i);

                // 字段Java中值类型
                String javaType = rsMetaData.getColumnClassName(i);
                String SqlType = rsMetaData.getColumnTypeName(i);

                if (SqlType.contains("UNSIGNED"))
                {
                    SqlType = SqlType.replace("UNSIGNED", "").trim();
                }

                if (javaType == "[B")
                {
                    javaType = "byte[]";
                }
                else if (javaType == "java.lang.Boolean")
                {
                    javaType = "boolean";
                }
                else if (javaType == "java.lang.Integer")
                {
                    javaType = "int";
                }
                else if (javaType == "java.lang.Long")
                {
                    javaType = "long";
                }
                else if (javaType == "java.lang.Float")
                {
                    javaType = "float";
                }
                else if (javaType == "java.lang.String")
                {
                    javaType = "String";
                }
                else if (javaType == "java.sql.Timestamp")
                {
                    javaType = "Date";
                }
                else if (javaType == "java.math.BigInteger")
                {
                    javaType = "long";
                }

                if (fieldMap.get(name) != null)
                {
                    fieldMap.get(name).setJavaType(javaType);

                    // 字段 SQL Type
                    fieldMap.get(name).setSqlType(SqlType);

                    // 字段长度
                    fieldMap.get(name).setLen(
                            (rsMetaData.getColumnDisplaySize(i)));
                }

                // TODO 获取key字段
                ResultSet pkRSet = conn.getMetaData().getPrimaryKeys(null, dbName, tableName);
                while (pkRSet.next())
                {
                    System.err.println("****** Comment ******");
                    System.err.println("TABLE_CAT : " + pkRSet.getObject(1));
                    System.err.println("TABLE_SCHEM: " + pkRSet.getObject(2));
                    System.err.println("TABLE_NAME : " + pkRSet.getObject(3));
                    System.err.println("COLUMN_NAME: " + pkRSet.getObject(4));
                    System.err.println("KEY_SEQ : " + pkRSet.getObject(5));
                    System.err.println("PK_NAME : " + pkRSet.getObject(6));
                    System.err.println("****** ******* ******");

                    FieldInfo key = fieldMap.get(pkRSet.getObject(4));
                    if (key != null)
                    {
                        key.setPrimaryKey(true);
                    }
                }

            }

            Set<Entry<String, FieldInfo>> set = fieldMap.entrySet();
            for (Entry<String, FieldInfo> entry : set)
            {
                FieldInfo fieldInfo = entry.getValue();
                if (!fieldInfo.isPrimaryKey())
                {
                    continue;
                }
                String clmName = fieldInfo.getName();
                ResultSet clmSet = conn.getMetaData().getColumns(null, dbName,
                        tableName, clmName);
                while (clmSet.next())
                {
                    String flag = (String) clmSet.getObject("IS_AUTOINCREMENT");
                    if (flag.equals("YES"))
                    {
                        fieldInfo.setAotuIncreamte(true);
                    }

                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DbUtil.close(rs, pstm, conn);
        }
        return fieldMap;
    }

}
