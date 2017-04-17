
package com.road.tool.util;

import java.util.Map;


public class InterfaceCodeUtil
{
    public static String generateInterfaceCode(String entityName, String tableName, Map<String, Boolean> methods)
    {
        StringBuffer result = new StringBuffer();
        /* 所属包 */
        result.append("package " + CommonUtil.default_dao_interfacePackage).append(";\n\n");

        /* 导入框架内部引用 */
        result.append(CommonUtil.default_dao_interfaceImport).append("\n");

        /* 导入上层引用 */
        result.append(generateImport(entityName, tableName)).append(";\n\n");

        /* 生成接口名 */
        result.append(generateInterfaceName(entityName, tableName)).append("\n");
        result.append("{\n");

        if (methods.get("getEntityByUserID") != null && methods.get("getEntityByUserID") == true)
        {
            result.append(generateGetEntityByUserID(entityName, tableName)).append("\n");
        }
        result.append("}\n");
        return result.toString();
    }

    private static String generateGetEntityByUserID(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("\t").append("List<").append(CommonUtil.CreateEntityName(entityName, tableName)).append("> ");
        result.append("get" + CommonUtil.CreateEntityName(entityName, tableName) + "ByUserID(long userID);");
        return result.toString();
    }

    private static String generateImport(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("import "
                + CommonUtil.generateDaoEntityPackage(tableName) + "."
                + CommonUtil.CreateEntityName(entityName, tableName));
        return result.toString();
    }

    private static String generateInterfaceName(String entityName,
            String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("public interface "
                + CommonUtil.CreateInterfaceName(entityName, tableName)
                + " extends IBaseDao<"
                + CommonUtil.CreateEntityName(entityName, tableName) + ">");
        return result.toString();
    }
}
