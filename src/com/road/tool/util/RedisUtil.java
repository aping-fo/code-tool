package com.road.tool.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class RedisUtil
{
    private String entityName;

    private String redisEntityName;

    private Map<String, FieldInfo> fieldMap;

    private Properties properties;
    
    private boolean containDateType = false;

    public RedisUtil(Properties properties)
    {
        this.properties = properties;
    }

    public String generateRedisCode(String entityName, Map<String, FieldInfo> fieldMap)
    {
        this.entityName = entityName;
        this.fieldMap = fieldMap;
        this.redisEntityName = "Redis" + entityName + "Proto";
        
        for (Entry<String, FieldInfo> field : fieldMap.entrySet())
        {
            if (field.getValue().getJavaType().equals("Date")) // 非完全匹配
                containDateType = true;
        }

        String temp = FileUtil.readFile(properties.getProperty("tmplPath") + "/redis_dao_class.txt");
        temp = temp.replace("${NAME1}", entityName);
        temp = temp.replace("${NAME2}", redisEntityName);
        
        if (containDateType)
            temp = temp.replace("${TIMEUTIL}", "import com.road.util.TimeUtil;");
        else
            temp = temp.replace("${TIMEUTIL}", "");

        temp = temp.replace("${READ_METHOD}", generateReadMethod());
        temp = temp.replace("${WRITE_METHOD}", generateWriteMethod());

        String filePath = properties.getProperty("redisCodePath") + "\\Redis" + entityName + "Dao.java";
        FileUtil.wireFile(filePath, temp);
        return temp;
    }

    private String generateWriteMethod()
    {
        String temp = FileUtil.readFile(properties.getProperty("tmplPath") + "/redis_write_method.txt");
        temp = temp.replace("${NAME1}", entityName);
        temp = temp.replace("${NAME2}", redisEntityName);

        StringBuilder temp1 = new StringBuilder();
        for (Entry<String, FieldInfo> field : fieldMap.entrySet())
        {
            String set;
            FieldInfo fieldInfo = field.getValue();
            if (fieldInfo.getJavaType().equals("Date")) // 非完全匹配
                set = "\t\t\tbuilder.set${NAME1}(TimeUtil.toDateTimeString(info.get${NAME1}()));\n";
            else
                set = "\t\t\tbuilder.set${NAME1}(info.get${NAME1}());\n";

            temp1.append(set.replace("${NAME1}", field.getKey()));
        }

        temp = temp.replace("${TEMP1}", temp1);

        return temp;
    }

    private String generateReadMethod()
    {
        String temp = FileUtil.readFile(properties.getProperty("tmplPath") + "/redis_read_method.txt");
        temp = temp.replace("${NAME1}", entityName);
        temp = temp.replace("${NAME2}", redisEntityName);

        StringBuilder temp1 = new StringBuilder();
        for (Entry<String, FieldInfo> field : fieldMap.entrySet())
        {
            String set;
            FieldInfo fieldInfo = field.getValue();
            if (fieldInfo.getJavaType().equals("Date")) // 非完全匹配
                set = "\t\t\tinfo.set${NAME1}(TimeUtil.toDate(proto.get${NAME1}()));\n";
            else
                set = "\t\t\tinfo.set${NAME1}(proto.get${NAME1}());\n";

            temp1.append(set.replace("${NAME1}", field.getKey()));
        }

        temp = temp.replace("${TEMP1}", temp1);

        return temp;
    }

    public static void main(String[] args)
    {
        Properties properties = new Properties();
        properties.setProperty("tmpl", "E:/server_main2/DBCodeTool/config");
        String temp = FileUtil.readFile("E:/server_main2/DBCodeTool/config/redis_read_method.txt");
        String temp1 = temp.replace("NAME1", "aaaa");
        FileUtil.wireFile("c:/1.java", temp1);
        System.err.println(temp);
        System.err.println(temp1);
    }
}
