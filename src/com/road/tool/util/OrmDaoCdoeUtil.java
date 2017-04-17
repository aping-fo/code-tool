

package com.road.tool.util;


public class OrmDaoCdoeUtil
{
    public static String generateDaoImplCode(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();

        /* 所属包 */
        result.append("package " + CommonUtil.default_orm_implPackage).append(
                ";\n\n");

        /* 导入默认引用 */
        result.append(CommonUtil.default_orm_implImport).append("\n");
        /* 导入上层类引用 */
        result.append(generateImport(entityName, tableName)).append("\n");
        /* 生成类名 */
        result.append(generateImplementName(entityName, tableName))
                .append("\n");
        /* { */
        result.append("{").append("\n");

        result.append("}").append("\n");
        return result.toString();
    }

    private static String generateImport(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result
                .append(
                        "import "
                                + CommonUtil.default_orm_interfacePackage
                                + "."
                                + CommonUtil.CreateInterfaceName(entityName,
                                        tableName)).append(";\n");
        result.append(
                "import " + CommonUtil.generateORMEntityPackage(tableName)
                        + "."
                        + CommonUtil.CreateEntityName(entityName, tableName))
                .append(";\n\n");
        return result.toString();
    }

    private static String generateImplementName(String entityName,
            String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("public class "
                + CommonUtil.CreateImplName(entityName, tableName)
                + " extends BaseOrmDao<"
                + CommonUtil.CreateEntityName(entityName, tableName)
                + "> implements "
                + CommonUtil.CreateInterfaceName(entityName, tableName));
        return result.toString();
    }
}
