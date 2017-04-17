

package com.road.tool.util;

public class OrmInterfaceCodeUtil
{
    public static String generateInterfaceCode(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        /* 所属包 */
        result.append("package " + CommonUtil.default_orm_interfacePackage).append(";\n\n");

        /* 导入框架内部引用 */
        result.append(CommonUtil.default_orm_interfaceImport).append("\n");

        /* 导入上层引用 */
        result.append(generateImport(entityName, tableName)).append(";\n\n");
        /* 生成接口名 */
        result.append(generateInterfaceName(entityName, tableName)).append("\n");
        result.append("{\n");
        result.append("}\n");
        return result.toString();
    }

    private static String generateImport(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("import "
                + CommonUtil.generateORMEntityPackage(tableName) + "."
                + CommonUtil.CreateEntityName(entityName, tableName));
        return result.toString();
    }

    private static String generateInterfaceName(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("public interface "
                + CommonUtil.CreateInterfaceName(entityName, tableName)
                + " extends IBaseOrmDao<"
                + CommonUtil.CreateEntityName(entityName, tableName) + ">");
        return result.toString();
    }
}
