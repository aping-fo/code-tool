
package com.road.tool.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DaoCodeUtil
{

    /**
     * Excel表头
     * 
     * @param className
     * @return
     */
    public static String generateDaoImplCode(String name, Map<String, FieldInfo> fieldMap)
    {
        StringBuffer result = new StringBuffer();

        // Map 转化为List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        Collection<FieldInfo> c = fieldMap.values();
        Iterator<FieldInfo> it = c.iterator();
        while (it.hasNext())
        {
            FieldInfo field = (FieldInfo) it.next();
            fieldList.add(field);
        }
        System.out.println("表名:" + name);

        // // 生成Set
        // result.append("生成DAOImpl类中：getTemplate()\n");
        // String temp = name.substring(0, 1).toUpperCase()
        // + name.substring(1, name.length());
        // result.append("\t\t" + temp + "Info info = new " + temp +
        // "Info();\n");
        //
        // for (int i = 0; i < fieldList.size(); i++)
        // {
        // result.append(makeSet("info", fieldList.get(i).getName(), fieldList
        // .get(i).getJavaType()));
        // }
        // result.append("\n\n\n");
        //
        // // 生成put
        // result.append("生成DAOImpl类中：生成sql参数\n");
        // for (int i = 0; i < fieldList.size(); i++)
        // {
        // result.append(makePut("params", fieldList.get(i).getName(),
        // fieldList.get(i).getSqlType()));
        // }
        // result.append("\n\n\n");
        //
        // // 生成模板xml
        // result.append("下发客户端模板xml\n");
        // for (int i = 0; i < fieldList.size(); i++)
        // {
        // result.append(makeXml(fieldList.get(i).getName(), fieldList.get(i)
        // .getJavaType()));
        // }
        // result.append("\n\n\n");

        // 生成excel导出格式
        result.append("生成数值策划Excel表头：\n\n");

        // 字段行
        for (int i = 0; i < fieldList.size(); i++)
        {
            result.append(fieldList.get(i).getName() + "\t");
        }
        result.append("\n");

        // 注释行
        for (int i = 0; i < fieldList.size(); i++)
        {
            result.append(fieldList.get(i).getComment() + "\t");
        }
        return result.toString();
    }

    public static String generateDaoImplCode(String entityName, String tableName, List<FieldInfo> fields,
            Map<String, Boolean> methods)
    {
        StringBuffer result = new StringBuffer();

        /* 所属包 */
        result.append("package " + CommonUtil.default_dao_implPackage).append(";\n\n");

        /* 导入默认引用 */
        // result.append("import java.sql.Connection;").append("\n");
        result.append(CommonUtil.default_dao_implImport).append("\n");

        /* 导入上层类引用 */
        result.append(generateImport(entityName, tableName)).append("\n");

        // if (methods.get("getEntityByUserID") != null
        // && methods.get("getEntityByUserID") == true)
        // {
        // result.append("import java.sql.Types;").append("\n");
        // result.append("import java.util.List;").append("\n");
        // result.append("import com.road.pitaya.database.DBParamWrapper;")
        // .append("\n");
        // result.append("import com.road.pitaya.entity.data.SeqData;")
        // .append("\n");
        // }

        /* 生成类名 */
        result.append(generateImplementName(entityName, tableName)).append("\n");
        /* { */
        result.append("{").append("\n");
        /* 生产日志器 */
        // result.append("\t").append(generateLogger(entityName,
        // tableName)).append("\n\n");
        /* 生成构造函数 */
        result.append(generateConstructor(entityName, tableName)).append("\n\n");
        /* add方法 */
        result.append(generateAddMethod(entityName, tableName, fields)).append("\n\n");
        /* update方法 */
        result.append(generateUpdateMethod(entityName, tableName, fields)).append("\n\n");
        /* delete方法 */
        result.append(generateDeleteMethod(entityName, tableName, fields)).append("\n\n");
        /* addOrUpdate方法 */
        result.append(generateAddOrUpdateMethod(entityName, tableName, fields)).append("\n\n");
        /* DeleteByKey方法 */
        result.append(genrerateDeleteByKeymethod(entityName, tableName, fields)).append("\n\n");
        /* GetByKey方法 */
        result.append(generateGetByKeyMethod(entityName, tableName, fields)).append("\n\n");
        /* ListAll方法 */
        result.append(generateListAllMethod(entityName, tableName, fields)).append("\n");
        /* addOrUpdateBatch方法 */
        result.append(generateaddOrUpdateBatchMethod(entityName, tableName, fields)).append("\n").append("\n");

        // 2014.08.11
        // result.append(
        // generateAddBatchMethod(entityName, tableName, fields))
        // .append("\n").append("\n");
        // result.append(generateUpdateBatchMethod(entityName, tableName, fields))
        // .append("\n");

        // result.append(
        // generateUpdateBatchMethod(entityName, tableName, fields))
        // .append("\n");

        /* deleteBatch方法 */
        result.append(generatedeleteBatchMethod(entityName, tableName, fields)).append("\n");
        /* rsToEntity方法 */
        result.append(generatersToEntityMethod(entityName, tableName, fields)).append("\n");
        /* } */

        if (methods.get("getEntityByUserID") != null && methods.get("getEntityByUserID") == true)
        {
            result.append(generateGetEntityByUserID(entityName, tableName)).append("\n");
        }

        result.append("}");
        return result.toString();
    }

    private static String generateGetEntityByUserID(String entityName,
            String tableName)
    {

        StringBuffer result = new StringBuffer();
        result.append("\t").append("@Override").append("\n");
        result.append("\t").append("public List<").append(CommonUtil.CreateEntityName(entityName, tableName)).append(
                "> ").append(
                        "get" + CommonUtil.CreateEntityName(entityName, tableName) + "ByUserID(long userID)").append(
                                "\n");
        ;
        result.append("\t").append("{").append("\n");
        result.append("\t\t").append("String sql = \"select * from " + tableName + " where `userID` = ?;\";").append(
                "\n");
        result.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        result.append("\t\t").append("params.put(Types.BIGINT, userID);").append("\n");
        result.append("\t\t").append("List<" + CommonUtil.CreateEntityName(entityName, tableName) + "> "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + " = queryList(sql,params);").append("\n");
        result.append("\t\t").append("return "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + ";").append("\n");
        result.append("\t").append("}").append("\n");
        return result.toString();
    }

    /**
     * @param entityName
     * @param tableName
     * @param fields
     * @return
     */
    private static String generatersToEntityMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = CommonUtil.CreateEntityName(entityName, tableName);
        String methodName = "rsToEntity";
        String params = "ResultSet rs";
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)
                + " throws SQLException").append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append(CommonUtil.CreateEntityName(entityName, tableName)
                + " "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + " = new "
                + CommonUtil.CreateEntityName(entityName, tableName)
                + "();").append("\n");
        for (int i = 0; i < fields.size(); i++)
        {
            sb.append("\t\t").append(
                    createSet(CommonUtil.CreateEntityParameterName(entityName,
                            tableName), fields.get(i).getName(), fields.get(i).getJavaType()));
        }

        // 将DataOption置none
        if (fields.get(0).isFlag())
        {
            sb.append("\t\t").append(CommonUtil.CreateEntityParameterName(entityName,
                    tableName) + ".setOp(DataOption.NONE);\n");
        }

        sb.append("\t\t").append("return "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)).append(";\n");

        sb.append("\t").append("}").append("\n");
        return sb.toString();
    }

    /**
     * @param entityName
     * @param tableName
     * @param fields
     * @return
     */
    private static String generatedeleteBatchMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "int[]";
        String methodName = "deleteBatch";
        String params = "List<"
                + CommonUtil.CreateEntityName(entityName, tableName) + "> "
                + CommonUtil.CreateEntityPluralParaName(entityName, tableName);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("String sql = " + createDeleteSql(tableName, fields)
                + ";").append("\n");

        sb.append("\t\t").append("int[] effectedRows = getDBHelper().sqlBatch(sql, "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ", new DataExecutor<int[]>()").append("\n");
        sb.append("\t\t").append("{").append("\n");
        sb.append("\t\t\t").append("@Override").append("\n");
        sb.append("\t\t").append(
                "public int[] execute(PreparedStatement statement, Object... objects) throws Exception").append("\n");
        sb.append("\t\t").append("{").append("\n");
        sb.append("\t\t\t").append("").append("\n");
        sb.append("\t\t\t").append("@SuppressWarnings(\"unchecked\")").append("\n");
        sb.append("\t\t\t").append("List<"
                + CommonUtil.CreateEntityName(entityName, tableName)
                + ">"
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + " = (List<"
                + CommonUtil.CreateEntityName(entityName, tableName)
                + ">)objects[0];").append("\n");

        sb.append("\t\t\t").append("for ("
                + CommonUtil.CreateEntityName(entityName, tableName)
                + " "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + " : "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ")").append("\n");
        sb.append("\t\t\t").append("{").append("\n");
        sb.append("\t\t\t\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");

        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sb.append("\t\t\t\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
        }

        sb.append("\t\t\t\t\t").append(
                "statement = getDBHelper().prepareCommand(statement,params.getParams());").append("\n");
        sb.append("\t\t\t\t\t").append("statement.addBatch();").append("\n");
        sb.append("\t\t\t\t").append("}").append("\n");
        sb.append("\t\t\t\t").append("return statement.executeBatch();").append("\n");
        sb.append("\t\t\t").append("}").append("\n");
        sb.append("\t\t").append("});").append("\n");
        sb.append("\t\t").append("return effectedRows;").append("\n");
        sb.append("\t").append("}");

        return sb.toString();
    }

    /**
     * @param entityName
     * @param tableName
     * @param fields
     * @return
     */
    private static String generateaddOrUpdateBatchMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "int[]";
        String methodName = "addOrUpdateBatch";
        String params = "List<"
                + CommonUtil.CreateEntityName(entityName, tableName) + "> "
                + CommonUtil.CreateEntityPluralParaName(entityName, tableName);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("String sql = "
                + createaddOrUpdateSql(tableName, fields) + ";").append("\n");
        sb.append("\t\t").append("int[] effectedRows = getDBHelper().sqlBatch(sql, "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ", new DataExecutor<int[]>()").append("\n");
        sb.append("\t\t\t").append("{").append("\n");
        sb.append("\t\t\t\t").append("@Override").append("\n");
        sb.append("\t\t\t\t").append(
                "public int[] execute(PreparedStatement statement, Object... objects) throws Exception").append("\n");
        sb.append("\t\t\t\t").append("{").append("\n");
        sb.append("\t\t\t\t\t").append("@SuppressWarnings(\"unchecked\")").append("\n");
        sb.append("\t\t\t\t\t").append("List<"
                + CommonUtil.CreateEntityName(entityName, tableName)
                + ">"
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + " = (List<"
                + CommonUtil.CreateEntityName(entityName, tableName)
                + ">)objects[0];").append("\n");

        sb.append("\t\t\t\t\t").append("for ("
                + CommonUtil.CreateEntityName(entityName, tableName)
                + " "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + " : "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ")").append("\n");
        sb.append("\t\t\t\t\t").append("{").append("\n");

        // if (e.getOp() == DataOption.NONE)
        // continue
        if (fields.get(0).isFlag())
            sb.append("\t\t\t\t\t\tif(" + CommonUtil.CreateEntityParameterName(entityName,
                    tableName) + ".getOp() == DataOption.NONE)\n");
        sb.append("\t\t\t\t\t\t\tcontinue;\n");

        sb.append("\t\t\t\t\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        // int tag = 1;
        for (int i = 0; i < fields.size(); i++)
        {
            // if (!fields.get(i).isAotuIncreamte())
            // {
            sb.append("\t\t\t\t\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
            // tag++;
            // }
        }
        for (int i = 0; i < fields.size(); i++)
        {
            if (fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sb.append("\t\t\t\t\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
            // tag++;
        }
        sb.append("\t\t\t\t\t\t").append(
                "statement = getDBHelper().prepareCommand(statement,params.getParams());").append("\n");
        sb.append("\t\t\t\t\t\t").append("statement.addBatch();").append("\n");
        sb.append("\t\t\t\t\t").append("}").append("\n");
        sb.append("\t\t\t\t\t").append("return statement.executeBatch();").append("\n");
        sb.append("\t\t\t\t").append("}").append("\n");
        sb.append("\t\t\t").append("});").append("\n");

        if (fields.get(0).isFlag())
            sb.append("\t\t\tfor(int i=0; i<effectedRows.length; ++i)\n").append("\t\t\t{\n").append(
                    "\t\t\t\tif (effectedRows[i] > -1)\n").append(
                            "\t\t\t\t\t" + CommonUtil.CreateEntityParameterName(entityName,
                                    tableName) + "s.get(i).setOp(DataOption.NONE);\n").append("\t\t\t}\n");

        sb.append("\t\t").append("return effectedRows;").append("\n");
        sb.append("\t").append("}");
        return sb.toString();

    }

    @SuppressWarnings("unused")
    private static String generateAddBatchMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "void";
        String methodName = "addBatch";
        String params = "List<"
                + CommonUtil.CreateEntityName(entityName, tableName) + "> "
                + CommonUtil.CreateEntityPluralParaName(entityName, tableName)
                + ", Connection conn";
        String sql = createInsertSql(tableName, fields);
        sql = sql.substring(0, sql.length() - 2);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append(
                " throws SQLException").append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("String sql = " + sql).append("\";\n");
        sb.append("\t\t").append(
                "PreparedStatement statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);\n");

        sb.append("\t\t").append("for ("
                + CommonUtil.CreateEntityName(entityName, tableName)
                + " "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + " : "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ")").append("\n");
        sb.append("\t\t").append("{").append("\n");

        if (fields.get(0).isFlag())
            sb.append("\t\t\t\t\t\tif(" + CommonUtil.CreateEntityParameterName(entityName,
                    tableName) + ".getOp() == DataOption.NONE)\n");
        sb.append("\t\t\t\t\\t\tcontinue;\n");

        sb.append("\t\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        // int tag = 1;
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isAotuIncreamte())
            {
                sb.append("\t\t\t").append(createSetParams("params", fields.get(i).getName(),
                        fields.get(i).getSqlType(), entityName, tableName)).append("\n");
                // tag++;
            }
        }

        sb.append("\t\t\t").append("statement = getDBHelper().prepareCommand(statement,params.getParams());").append(
                "\n");
        sb.append("\t\t\t").append("statement.addBatch();").append("\n");
        sb.append("\t\t").append("}").append("\n");
        sb.append("\t\t").append("statement.executeBatch();").append("\n");
        sb.append("\t\t").append("super.close(statement);").append("\n");
        sb.append("\t").append("}").append("\n");
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private static String generateUpdateBatchMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "void";
        String methodName = "updateBatch";
        String params = "List<"
                + CommonUtil.CreateEntityName(entityName, tableName) + "> "
                + CommonUtil.CreateEntityPluralParaName(entityName, tableName)
                + ", Connection conn";
        String sql = createUpdateSql(tableName, fields);
        sql = sql.substring(0, sql.length() - 2);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append(
                " throws SQLException").append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("String sql = " + sql).append("\";\n");
        sb.append("\t\t").append(
                "PreparedStatement statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);\n");

        sb.append("\t\t").append("for ("
                + CommonUtil.CreateEntityName(entityName, tableName)
                + " "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + " : "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ")").append("\n");
        sb.append("\t\t").append("{").append("\n");

        if (fields.get(0).isFlag())
            sb.append("\t\t\t\t\t\tif(" + CommonUtil.CreateEntityParameterName(entityName,
                    tableName) + ".getOp() == DataOption.NONE)\n");
        sb.append("\t\t\t\t\t\tcontinue;\n");

        sb.append("\t\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");

        List<FieldInfo> keys = new ArrayList<FieldInfo>();
        // int tag = 1;
        for (int i = 0; i < fields.size(); i++)
        {
            if (fields.get(i).isPrimaryKey())
            {
                keys.add(fields.get(i));
                continue;
            }
            sb.append("\t\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
            // tag++;
        }
        for (FieldInfo fieldInfo : keys)
        {
            sb.append("\t\t\t").append(createSetParams("params", fieldInfo.getName(),
                    fieldInfo.getSqlType(), entityName, tableName)).append("\n");
            // tag++;
        }

        sb.append("\t\t\t").append("statement = getDBHelper().prepareCommand(statement,params.getParams());").append(
                "\n");
        sb.append("\t\t\t").append("statement.addBatch();").append("\n");
        sb.append("\t\t").append("}").append("\n");
        sb.append("\t\t").append("statement.executeBatch();").append("\n");
        sb.append("\t\t").append("super.close(statement);").append("\n");
        sb.append("\t").append("}").append("\n");
        return sb.toString();
    }

    /**
     * @param entityName
     * @param tableName
     * @param fields
     * @return
     */
    private static Object genrerateDeleteByKeymethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "boolean";
        String methodName = "deleteByKey";
        String params = "Object... ids";
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("boolean result = false;").append("\n");
        sb.append("\t\t").append("String sql = " + createDeleteSql(tableName, fields)
                + ";").append("\n");
        sb.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sb.append("\t\t").append(createSetParams("params", "ids", fields.get(i).getSqlType(), i)).append("\n");
        }
        sb.append("\t\t").append("result = getDBHelper().execNoneQuery(sql, params) > -1 ? true : false;").append("\n");

        sb.append("\t\t").append("return result;").append("\n");
        sb.append("\t").append("}");
        return sb.toString();
    }

    private static String generateGetByKeyMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = CommonUtil.CreateEntityName(entityName, tableName);
        String methodName = "getByKey";
        String params = "Object... ids";

        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("String sql = " + createGetByIDSql(tableName, fields)
                + ";").append("\n");
        sb.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sb.append("\t\t").append(createSetParams("params", "ids", fields.get(i).getSqlType(), i)).append("\n");
        }
        sb.append("\t\t").append(
                CommonUtil.CreateEntityName(entityName, tableName)
                        + " "
                        + CommonUtil.CreateEntityParameterName(entityName,
                                tableName)
                        + " = query(sql, params);");
        // sb.append("\t\t").append(CommonUtil.CreateEntityName(entityName,
        // tableName) + " " + CommonUtil.CreateEntityParameterName(entityName,
        // tableName) +
        // " = getDBHelper().executeQuery(sql, para, new DataReader<" +
        // CommonUtil.CreateEntityName(entityName, tableName) +
        // ">()").append("\n");
        // sb.append("\t\t").append("{").append("\n");
        // sb.append("\t\t\t").append("@Override").append("\n");
        // sb.append("\t\t\t").append("public " +
        // CommonUtil.CreateEntityName(entityName, tableName) +
        // " readData(ResultSet rs,Object... objects) throws Exception").append("\n");
        // sb.append("\t\t\t").append("{").append("\n");
        // sb.append("\t\t\t\t").append("if (rs.last())").append("\n");
        // sb.append("\t\t\t\t").append("{").append("\n");
        // sb.append("\t\t\t\t\t").append("return " +
        // CommonUtil.CreateImplName(entityName, tableName) +
        // ".this.rsToEntity(rs);").append("\n");
        // sb.append("\t\t\t\t").append("}").append("\n");
        // sb.append("\t\t\t\t").append("return null;").append("\n");
        // sb.append("\t\t\t").append("}").append("\n");
        // sb.append("\t\t").append("});").append("\n");
        sb.append("\t\t").append("return "
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + ";").append("\n");
        sb.append("\t").append("}").append("\n");
        return sb.toString();
    }

    /**
     * @return
     */
    private static String generateAddOrUpdateMethod(String entityName, String tableName, List<FieldInfo> fields)
    {

        String scope = "public";
        String _return = "boolean";
        String methodName = "addOrUpdate";
        String params = generateParam(entityName, tableName);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("boolean result = false;").append("\n");
        sb.append("\t\t").append("String sql = "
                + createaddOrUpdateSql(tableName, fields) + ";").append("\n");

        if (fields.get(0).isFlag())
            sb.append("\t\tif(" + CommonUtil.CreateEntityParameterName(entityName,
                    tableName) + ".getOp() == DataOption.NONE)\n");
        sb.append("\t\t\treturn true;\n");

        sb.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        // int tag = 1;
        for (int i = 0; i < fields.size(); i++)
        {
            // if (!fields.get(i).isAotuIncreamte())
            // {
            sb.append("\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
            // tag++;
            // }
        }
        for (int i = 0; i < fields.size(); i++)
        {
            if (fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sb.append("\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
            // tag++;
        }
        sb.append("\t\t").append("result = getDBHelper().execNoneQuery(sql, params) > -1 ? true : false;").append("\n");

        // dataOption置0
        if (fields.get(0).isFlag())
            sb.append(generateOpNoneCode(entityName, tableName));

        sb.append("\t\t").append("return result;").append("\n");
        sb.append("\t").append("}");
        return sb.toString();
    }

    private static String generateListAllMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "List<"
                + CommonUtil.CreateEntityName(entityName, tableName) + ">";
        String methodName = "listAll";
        String params = "";

        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("String sql = \"select * from " + tableName + ";\"").append(";\n");
        sb.append("\t\t").append(
                "List<"
                        + CommonUtil.CreateEntityName(entityName, tableName)
                        + "> "
                        + CommonUtil.CreateEntityPluralParaName(entityName,
                                tableName)
                        + " = queryList(sql);");
        // sb.append("\t\t").append("List<"+
        // CommonUtil.CreateEntityName(entityName, tableName) + "> "+
        // CommonUtil.CreateEntityPluralParaName(entityName,tableName) +
        // " = getDBHelper().executeQuery(sql, new DataReader<List<" +
        // CommonUtil.CreateEntityName(entityName, tableName)
        // +">>()").append("\n");
        // sb.append("\t\t").append("{").append("\n");
        // sb.append("\t\t\t").append("@Override").append("\n");
        // sb.append("\t\t\t").append("public List<" +
        // CommonUtil.CreateEntityName(entityName, tableName) +
        // "> readData(ResultSet rs,Object... objects) throws Exception").append("\n");
        // sb.append("\t\t\t").append("{").append("\n");
        // sb.append("\t\t\t\t").append("return " +
        // CommonUtil.CreateImplName(entityName, tableName) +
        // ".this.rsToEntityList(rs);").append("\n");
        // sb.append("\t\t\t").append("}").append("\n");
        // sb.append("\t\t").append("});").append("\n");
        sb.append("\t\t").append("return "
                + CommonUtil.CreateEntityPluralParaName(entityName,
                        tableName)
                + ";").append("\n");
        sb.append("\t").append("}").append("\n");

        return sb.toString();
    }

    private static String generateDeleteMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "boolean";
        String methodName = "delete";
        String params = generateParam(entityName, tableName);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("boolean result = false;").append("\n");
        sb.append("\t\t").append("String sql = " + createDeleteSql(tableName, fields)
                + ";").append("\n");
        sb.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sb.append("\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
        }
        sb.append("\t\t").append("result = getDBHelper().execNoneQuery(sql, params) > -1 ? true : false;").append("\n");

        sb.append("\t\t").append("return result;").append("\n");
        sb.append("\t").append("}");
        return sb.toString();
    }

    private static String generateUpdateMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "boolean";
        String methodName = "update";
        String params = generateParam(entityName, tableName);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        
        if (fields.get(0).isFlag())
            sb.append("\t\tif(" + CommonUtil.CreateEntityParameterName(entityName,
                    tableName) + ".getOp() == DataOption.NONE)\n");
        sb.append("\t\t\treturn true;\n\n");
        
        sb.append("\t\t").append("boolean result = false;").append("\n");
        sb.append("\t\t").append("String sql = " + createUpdateSql(tableName, fields)
                + ";").append("\n");
        sb.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        List<FieldInfo> keys = new ArrayList<FieldInfo>();
        // int tag = 1;
        for (int i = 0; i < fields.size(); i++)
        {
            if (fields.get(i).isPrimaryKey())
            {
                keys.add(fields.get(i));
                continue;
            }
            sb.append("\t\t").append(createSetParams("params", fields.get(i).getName(),
                    fields.get(i).getSqlType(), entityName, tableName)).append("\n");
            // tag++;
        }
        for (FieldInfo fieldInfo : keys)
        {
            sb.append("\t\t").append(createSetParams("params", fieldInfo.getName(),
                    fieldInfo.getSqlType(), entityName, tableName)).append("\n");
            // tag++;
        }
        sb.append("\t\t").append("result = getDBHelper().execNoneQuery(sql, params) > -1 ? true : false;").append("\n");

        if (fields.get(0).isFlag())
            sb.append(generateOpNoneCode(entityName, tableName));

        sb.append("\t\t").append("return result;").append("\n");
        sb.append("\t").append("}");
        return sb.toString();
    }

    private static String generateAddMethod(String entityName, String tableName, List<FieldInfo> fields)
    {
        String scope = "public";
        String _return = "boolean";
        String methodName = "add";
        String params = generateParam(entityName, tableName);
        StringBuffer sb = new StringBuffer();
        sb.append("\t").append("@Override").append("\n");
        sb.append("\t").append(generateMethod(scope, _return, methodName, params)).append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t\t").append("boolean result = false;").append("\n");
        sb.append("\t\t").append("String sql = " + createInsertSql(tableName, fields)
                + ";").append("\n");
        sb.append("\t\t").append("DBParamWrapper params = new DBParamWrapper();").append("\n");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isAotuIncreamte())
            {
                sb.append("\t\t").append(createSetParams("params", fields.get(i).getName(), fields.get(i).getSqlType(),
                        entityName, tableName)).append("\n");
            }
        }
        sb.append("\t\t").append("result = getDBHelper().execNoneQuery(sql, params) > -1 ? true : false;").append("\n");

        if (fields.get(0).isFlag())
            sb.append(generateOpNoneCode(entityName, tableName));

        sb.append("\t\t").append("return result;").append("\n");
        sb.append("\t").append("}");
        return sb.toString();
    }

    /**
     * 生成设置op.none方法
     * 
     * @param entityName
     * @param tableName
     * @return
     */
    private static StringBuffer generateOpNoneCode(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("\t\tif (result) \n").append("\t\t{\n").append(
                "\t\t\t" + CommonUtil.CreateEntityParameterName(entityName,
                        tableName) + ".setOp(DataOption.NONE);\n").append("\t\t}\n");
        return result;
    }

    private static String generateConstructor(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("\t").append("public "
                + CommonUtil.CreateImplName(entityName, tableName)
                + "(DBHelper helper)").append("\n");
        result.append("\t").append("{").append("\n");
        result.append("\t\t").append("super(helper);").append("\n");
        result.append("\t").append("}").append("\n");
        return result.toString();
    }

    // /**
    // * @param entityName
    // * @return
    // */
    // private static String generateLogger(String entityName, String tableName)
    // {
    // StringBuffer result = new StringBuffer();
    // result.append(
    // "private static final Logger LOGGER = LoggerFactory.getLogger("
    // + CommonUtil.CreateImplName(entityName, tableName)
    // + ".class)").append(";");
    // return result.toString();
    // }

    private static String generateImplementName(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append("public class "
                + CommonUtil.CreateImplName(entityName, tableName)
                + " extends BaseDao<"
                + CommonUtil.CreateEntityName(entityName, tableName)
                + "> implements "
                + CommonUtil.CreateInterfaceName(entityName, tableName));
        return result.toString();
    }

    private static String generateImport(String entityName, String tableName)
    {
        StringBuffer result = new StringBuffer();
        result.append(
                "import " + CommonUtil.default_dao_interfacePackage + "."
                        + CommonUtil.CreateInterfaceName(entityName, tableName)).append(";\n");
        result.append(
                "import " + CommonUtil.generateDaoEntityPackage(tableName)
                        + "."
                        + CommonUtil.CreateEntityName(entityName, tableName)).append(";\n\n");
        return result.toString();

    }

    private static String generateParam(String entityName, String tableName)
    {
        return CommonUtil.CreateEntityName(entityName, tableName) + " "
                + CommonUtil.CreateEntityParameterName(entityName, tableName);
    }

    private static String generateMethod(String scope, String _return, String methodName, String params)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(scope + " " + _return + " " + methodName + "(" + params + ")");
        return sb.toString();
    }

    private static String createInsertSql(String tableName, List<FieldInfo> fields)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("\"insert into " + tableName);
        sql.append("(");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isAotuIncreamte())
            {
                sql.append("`").append(fields.get(i).getName()).append("`, ");
            }
        }
        sql.delete(sql.length() - 2, sql.length());

        sql.append(") values(");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isAotuIncreamte())
            {
                sql.append("?, ");
            }
        }
        sql.delete(sql.length() - 2, sql.length());

        sql.append(");\"");
        return sql.toString();
    }

    private static String createUpdateSql(String tableName, List<FieldInfo> fields)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("\"update " + tableName + " set ");
        for (int i = 0; i < fields.size(); i++)
        {
            if (fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sql.append("`").append(fields.get(i).getName()).append("`=?, ");
        }
        sql.delete(sql.length() - 2, sql.length());

        sql.append(" where ");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sql.append("`").append(fields.get(i).getName()).append("`=?");
            sql.append(" and ");
        }
        if (sql.lastIndexOf(" and ") > 0)
        {
            sql.delete(sql.length() - 5, sql.length());
        }

        sql.append(";\"");
        return sql.toString();
    }

    private static String createaddOrUpdateSql(String tableName, List<FieldInfo> fields)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("\"insert into " + tableName);
        sql.append("(");
        for (int i = 0; i < fields.size(); i++)
        {
            // if (!fields.get(i).isAotuIncreamte())
            // {
            sql.append("`").append(fields.get(i).getName()).append("`, ");
            // }

        }
        sql.delete(sql.length() - 2, sql.length());

        sql.append(") values(");
        for (int i = 0; i < fields.size(); i++)
        {
            // if (!fields.get(i).isAotuIncreamte())
            // {
            sql.append("?, ");
            // }
        }
        sql.delete(sql.length() - 2, sql.length());

        sql.append(") on DUPLICATE KEY update ");

        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                sql.append("`" + fields.get(i).getName() + "`=?,");
            }
        }
        return sql.substring(0, sql.length() - 1) + ";\"";
    }

    private static String createDeleteSql(String tableName, List<FieldInfo> fields)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("\"delete from " + tableName);
        sql.append(" where ");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sql.append("`").append(fields.get(i).getName()).append("`=?");
            sql.append(" and ");
        }
        if (sql.lastIndexOf(" and ") > 0)
        {
            sql.delete(sql.length() - 5, sql.length());
        }

        sql.append(";\"");
        return sql.toString();
    }

    private static String createGetByIDSql(String tableName, List<FieldInfo> fields)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("\"select * from " + tableName);
        sql.append(" where ");
        for (int i = 0; i < fields.size(); i++)
        {
            if (!fields.get(i).isPrimaryKey())
            {
                continue;
            }
            sql.append("`").append(fields.get(i).getName()).append("`=?");
            sql.append(" and ");
        }
        if (sql.lastIndexOf(" and ") > 0)
        {
            sql.delete(sql.length() - 5, sql.length());
        }

        sql.append(";\"");
        return sql.toString();
    }

    private static String createSetParams(String paramName, String var, String type, String entityName,
            String tableName)
    {
        if (type.equals("INT"))
            type = "INTEGER";
        if (type.equals("DATETIME"))
            type = "TIMESTAMP";
        StringBuffer result = new StringBuffer();
        result.append(paramName).append(".put(").append("Types.").append(type).append(","
                + CommonUtil.CreateEntityParameterName(entityName,
                        tableName)
                + ".");
        result.append("get" + var.substring(0, 1).toUpperCase()
                + var.substring(1) + "());");
        return result.toString();
    }

    private static String createSetParams(String paramName, String var, String type, int index)
    {
        if (type.equals("INT"))
            type = "INTEGER";
        if (type.equals("DATETIME"))
            type = "TIMESTAMP";
        StringBuffer result = new StringBuffer();
        result.append(paramName).append(".put(").append("Types.").append(type).append(",ids[" + index + "]);");
        return result.toString();
    }

    private static StringBuffer createSet(String objName, String var, String type)
    {
        if (type.equals("int"))
            type = "Int";
        if (type.equals("float"))
            type = "Float";
        if (type.equals("long"))
            type = "Long";
        if (type.equals("boolean"))
            type = "Boolean";
        if (type.equals("Date"))
            type = "Timestamp";
        if (type.equals("java.math.BigDecimal"))
        {
            type = "BigDecimal";
        }
        StringBuffer result = new StringBuffer();
        result.append(objName).append(".");
        result.append("set" + var.substring(0, 1).toUpperCase()
                + var.substring(1) + "(rs.get" + type + "(");
        result.append("\"").append(var).append("\"").append("));\n");
        return result;
    }
}
