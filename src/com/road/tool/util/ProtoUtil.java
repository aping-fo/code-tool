/**
 * 
 */
package com.road.tool.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * pb生成
 * 
 * @author json.mao
 *         2016年9月20日
 */
public class ProtoUtil
{
    /**
     * 生成proto
     * 
     * @param name
     * @param tableName
     * @param fieldMap
     * @return
     */
    public static String generateProtoCode(String name, String tableName, Map<String, FieldInfo> fieldMap)
    {

        StringBuffer result = new StringBuffer("\n");
        // 增加导入
        // result.append("option java_package = \"com.road.ddt.gen\";").append("\n");
        // result.append("option java_outer_classname = \"" + name + "Msg\";").append("\n");
        // result.append(addComment(tableName));
        /* proto 开头 */
        String tmp = CommonUtil.CreateProtoName(name, tableName);
        result.append("message " + tmp + "\n");

        // 大括号
        result.append("{").append("\n");

        /* 各个私有属性 */
        result.append(generateField(fieldMap, tableName));

        /* Class 结尾 */
        result.append("}");

        return result.toString();

    }

    /**
     * 组装数据类型
     * 
     * @param fieldMap
     * @param tableName
     * @return
     */
    public static String generateField(Map<String, FieldInfo> fieldMap, String tableName)
    {

        StringBuffer result = new StringBuffer();
        Collection<FieldInfo> c = fieldMap.values();
        Iterator<FieldInfo> it = c.iterator();
        int index = 1;
        while (it.hasNext())
        {
            FieldInfo field = (FieldInfo) it.next();
            // 加注释
            result.append("\toptional ");
            result.append(transferJavaType(field.getJavaType()) + " "
                    + CommonUtil.toLowerName(field.getName()) + "= " + index + ";");
            result.append("\t\t\t\t//" + field.getComment().replace("\n", ""));
            result.append("\n");
            index++;
        }
        return result.append("\n").toString();

    }

    /**
     * 增加注释
     * 
     * @param value
     * @return
     */
    public static StringBuffer addComment(String value)
    {
        StringBuffer result = new StringBuffer("\n");
        result.append("/**").append("\n");
        result.append(" * ").append(value).append("\n");
        result.append(" */").append("\n");
        return result;
    }

    private static String transferJavaType(String javaType)
    {

        if (javaType == "boolean")
        {
            javaType = "bool";
        }
        else if (javaType == "int")
        {
            javaType = "int32";
        }
        else if (javaType == "long")
        {
            javaType = "int64";
        }
        else if (javaType == "float")
        {
            javaType = "float";
        }
        else if (javaType == "String" || javaType == "Date")
        {
            javaType = "string";
        }
        return javaType;
    }

}
