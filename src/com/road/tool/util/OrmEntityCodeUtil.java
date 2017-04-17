

package com.road.tool.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class OrmEntityCodeUtil
{
    /**
     * 生成实体代码
     * 
     * @param packageName
     * @param className
     * @param properties
     * @return
     */
    public static String generateEntityCode(String name, String tableName, Map<String, FieldInfo> fieldMap,
            Map<String, Boolean> methods)
    {
        StringBuffer result = new StringBuffer();

        /* 所属包 */
        result.append(
                "package " + CommonUtil.generateORMEntityPackage(tableName)).append(";\n\n");

        /* 导入类 */
        result.append(generateImport(fieldMap)).append("\n");

        if (methods.get("CreateBeanAnnotation") != null
                && methods.get("CreateBeanAnnotation") == true)
        {
            result.append(CommonUtil.default_orm_annotationImport).append("\n");
        }

        /* 导入框架类 */
        result.append(generateFrameImport(fieldMap)).append("\n");

        /* 配置 */
        if (methods.get("CreateBeanAnnotation") != null
                && methods.get("CreateBeanAnnotation") == true)
        {
            result.append("@ICreateBeanAnnotation(desc = \"").append(tableName).append("\")").append("\n");
        }

        result.append(generateOrmConfig(tableName, fieldMap)).append("\n");
        /* Class 开头 */
        String tmp = CommonUtil.CreateEntityName(name, tableName);
        result.append("public class " + tmp + "\n");

        result.append("{").append("\n");
        /* 各个私有属性 */
        result.append(generatePropertiesPrivate(fieldMap));

        /* Get Set 方法 */
        result.append(generateMethodGetterAndSetterPublic(fieldMap));

        if (methods.get("clone") != null && methods.get("clone") == true)
        {
            /* clone 方法 */
            result.append(generateMethodClonePublic(name, tableName, fieldMap));
        }

        /* Class 结尾 */
        result.append("}");

        return result.toString();
    }

    /**
     * @return
     */
    private static Object generateOrmConfig(String tableName, Map<String, FieldInfo> fieldMap)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("@PersistentEntity(sourceDomain = ").append("\"" + tableName + "\"").append(", targetDomain = ");
        sb.append("\"" + tableName + "\"");

        Set<Entry<String, FieldInfo>> sets = fieldMap.entrySet();
        for (Entry<String, FieldInfo> entry : sets)
        {
            FieldInfo fieldInfo = entry.getValue();
            if (fieldInfo.isPrimaryKey())
            {
                sb.append(", primaryKey = ").append(
                        "\"" + fieldInfo.getName() + "\"");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * @param fieldMap
     * @return
     */
    private static Object generateFrameImport(Map<String, FieldInfo> fieldMap)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(CommonUtil.default_orm_entityImport).append("\n");
        return sb.toString();
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
            result.append("\t").append("@PersistentField").append("\n");
            result.append("\tprivate ");
            result.append(field.getJavaType() + " "
                    + CommonUtil.toLowerName(field.getName()) + ";\n");
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

            result.append(makeGetter(CommonUtil.toLowerName(field.getName()),
                    field.getJavaType()));
            result.append("\n");

            // 加注释
            result.append(addComment(field.getComment()));

            result.append(makeSetter(CommonUtil.toLowerName(field.getName()),
                    field.getJavaType(), field.isFlag()));
            // result.append(makeAddRemove(field.getName(),
            // field.getJavaType()));
            result.append("\n");

        }
        return result.append("\n");

    }

    private static StringBuffer makeSetter(String var, String type, boolean flag)
    {
        StringBuffer result = new StringBuffer("\tpublic ");
        result.append("void" + " set" + CommonUtil.toUpperName(var) + "("
                + type + " " + var + ")\n");
        result.append("\t{\n");
        result.append("\t\tthis." + var + " = " + var + ";\n");
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
