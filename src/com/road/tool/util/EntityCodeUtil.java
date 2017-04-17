package com.road.tool.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 实体代码生成
 */
public class EntityCodeUtil
{

    /**
     * 生成实体代码
     * 
     * @param packageName
     * @param entityName
     * @param tableName
     * @param fieldMap
     * @param methods
     * @return
     */
    public static String generateEntityCode(String packageName, String entityName, String tableName,
            Map<String, FieldInfo> fieldMap, Map<String, Boolean> methods)
    {
        boolean canCacher = false;
        for (FieldInfo element : fieldMap.values())
        {
            if (element.isFlag())
            {
                canCacher = true;
                break;
            }
        }
        StringBuffer result = new StringBuffer();

        /* 所属包 */
        result.append("package " + CommonUtil.generateDaoEntityPackage(tableName)).append(";\n\n");

        /* 导入类 */
        result.append(generateImport(fieldMap)).append("\n");
        if (canCacher)
        {
            result.append("import com.road.pitaya.database.DataObject;\n");
            result.append("import com.road.pitaya.database.DataOption;\n\n");
        }

        /* Class 开头 */
        if (canCacher)
        {
            result.append("public class " + CommonUtil.CreateEntityName(entityName, tableName)
                    + " extends DataObject \n{\n");
        }
        else
        {
            result.append("public class " + CommonUtil.CreateEntityName(entityName, tableName) + "\n{\n");
        }

        /* 各个私有属性 */
        result.append(generatePropertiesPrivate(fieldMap));

        /* Get Set 方法 */
        result.append(generateMethodGetterAndSetterPublic(fieldMap));

        if (methods.get("clone") != null && methods.get("clone") == true)
        {
            /* clone 方法 */
            result.append(generateMethodClonePublic(entityName, tableName, fieldMap));
        }

        /* Class 结尾 */
        result.append("}");

        return result.toString();
    }

    /**
     * 应该只有 Date 需要另外导入, 其他都是默认包 java.lang...
     * 
     * @param types
     * @return
     */
    private static String generateImport(Map<String, FieldInfo> fieldMap)
    {
        Collection<FieldInfo> c = fieldMap.values();
        Iterator<FieldInfo> it = c.iterator();
        while (it.hasNext())
        {
            FieldInfo field = (FieldInfo) it.next();
            if (field.getJavaType() == "Date") // 非完全匹配
            {
                return "import java.util.Date;\n";
            }
        }
        return "";
    }

    /**
     * 生成私有属性, 属性命名与表字段统一
     * 
     * @param properties
     * @return
     */
    private static StringBuffer generatePropertiesPrivate(Map<String, FieldInfo> fieldMap)
    {
        StringBuffer result = new StringBuffer();

        Collection<FieldInfo> c = fieldMap.values();
        Iterator<FieldInfo> it = c.iterator();
        while (it.hasNext())
        {
            FieldInfo field = (FieldInfo) it.next();
            // 加注释
            result.append(addComment(field.getComment()));
            result.append("\tprivate ");
            result.append(field.getJavaType() + " " + CommonUtil.toLowerName(field.getName()) + ";\n");
            result.append("\n");
        }
        return result.append("\n");
    }

    /**
     * 生成公开的 setter 和 getter 方法
     * 
     * @param properties
     * @return
     */
    private static StringBuffer generateMethodGetterAndSetterPublic(Map<String, FieldInfo> fieldMap)
    {
        StringBuffer result = new StringBuffer();

        Collection<FieldInfo> c = fieldMap.values();
        Iterator<FieldInfo> it = c.iterator();
        while (it.hasNext())
        {
            FieldInfo field = (FieldInfo) it.next();
            // 加注释
            result.append(addComment(field.getComment()));

            result.append(makeGetter(CommonUtil.toLowerName(field.getName()), field.getJavaType()));
            result.append("\n");

            // 加注释
            result.append(addComment(field.getComment()));

            result.append(makeSetter(CommonUtil.toLowerName(field.getName()), field.getJavaType(), field.isFlag()));
            result.append("\n");

        }
        return result.append("\n");

    }

    private static StringBuffer makeSetter(String var, String type, boolean flag)
    {
        StringBuffer result = new StringBuffer("\tpublic ");
        result.append("void" + " set" + var.substring(0, 1).toUpperCase() + var.substring(1) + "(" + type + " " + var
                + ")\n");
        result.append("\t{\n");
        if (flag)
        {
            if (type == "String" || type == "Date")
            {
                result.append("\t\tif(" + var + " != null && ! " + var + ".equals(this." + var + "))\n");
            }
            else
            {
                result.append("\t\tif(" + var + " != this." + var + ")\n");
            }

            result.append("\t\t{\n");
            result.append("\t\t\tthis." + var + " = " + var + ";\n");
            result.append("\t\t\tsetOp(DataOption.UPDATE);\n");
            result.append("\t\t}\n");
        }
        else
        {
            result.append("\t\tthis." + var + " = " + var + ";\n");
        }
        result.append("\t}\n");
        return result;
    }

    private static StringBuffer makeGetter(String var, String type)
    {
        StringBuffer result = new StringBuffer("\tpublic ");
        result.append(type + " get" + CommonUtil.toUpperName(var) + "()\n");
        result.append("\t{\n");
        result.append("\t\treturn " + var + ";\n");
        result.append("\t}\n");
        return result;
    }

    /**
     * 添加字段注释
     * 
     * @param value
     * @return
     */
    private static StringBuffer addComment(String value)
    {

        StringBuffer result = new StringBuffer("");
        result.append("\t").append("/**").append("\n");
        result.append("\t").append(" * ").append(value).append("\n");
        result.append("\t").append(" */").append("\n");
        return result;
    }

    /**
     * 生成 clone方法
     * 
     * @return
     */
    private static StringBuffer generateMethodClonePublic(String entityName, String tableName,
            Map<String, FieldInfo> fieldMap)
    {
        String className = CommonUtil.CreateEntityName(entityName, tableName);
        StringBuffer result = new StringBuffer();
        result.append(addComment("x.clone() != x"));
        result.append("\tpublic ").append(className).append(" clone()\n");
        result.append("\t{\n");
        result.append("\t\t").append(className).append(" clone = new ").append(className).append("();\n");
        Collection<FieldInfo> c = fieldMap.values();
        Iterator<FieldInfo> it = c.iterator();
        while (it.hasNext())
        {
            FieldInfo field = (FieldInfo) it.next();
            String fieldName = field.getName();
            result.append("\t\tclone.").append(fieldName).append(" = this.").append(fieldName).append(";\n");
        }
        result.append("\t\treturn clone;\n");
        result.append("\t}\n");
        return result.append("\n");
    }

}
