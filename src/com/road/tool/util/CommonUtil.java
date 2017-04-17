package com.road.tool.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.road.tool.CodeTool;

public class CommonUtil
{
    public static Map<String, String> entitySuffix = new HashMap<String, String>();
    public static Map<String, String> entitySubPath = new HashMap<String, String>();

    public static String rootPath = "";
    public static String entityPath = "";
    public static String interfacePath = "";
    public static String implPath = "";
    public static String protoPath = "";

    public static String default_dao_entityPackage = "com.road.pitaya.entity";
    public static String default_dao_interfacePackage = "com.road.pitaya.dao";
    public static String default_dao_implPackage = "com.road.pitaya.dao.impl";

    public static String default_orm_entityPackage = "com.road.pitaya.entity";
    public static String default_orm_interfacePackage = "com.road.pitaya.dao";
    public static String default_orm_implPackage = "com.road.pitaya.dao.impl";

    public static String default_dao_interfaceImport = "";

    public static String default_dao_implImport = "";

    public static String default_orm_interfaceImport = "";

    public static String default_orm_implImport = "";

    public static String default_orm_entityImport = "";

    public static String default_orm_annotationImport = "import com.road.entity.bean.ICreateBeanAnnotation;";

    static
    {
        default_dao_implImport = "import java.sql.PreparedStatement;\n"
                + "import java.sql.ResultSet;\n"
                + "import java.sql.SQLException;\n"
                + "import java.sql.Types;\n"
                + "import java.util.List;\n\n"
                + "import com.road.pitaya.database.BaseDao;\n"
                + "import com.road.pitaya.database.DBParamWrapper;\n"
                + "import com.road.pitaya.database.DataExecutor;\n"
                + "import com.road.pitaya.database.pool.DBHelper;\n\n"
                + "import com.road.pitaya.database.DataOption;";

        default_dao_interfaceImport = "import com.road.pitaya.database.IBaseDao;\n" +
                "import java.util.List;\n";

        default_orm_implImport = "import com.road.pitaya.orm.BaseOrmDao;\n";

        default_orm_interfaceImport = "import com.road.pitaya.orm.IBaseOrmDao;\n";

        default_orm_entityImport = "import it.biobytes.ammentos.PersistentEntity;\n"
                + "import it.biobytes.ammentos.PersistentField;\n";

        entitySuffix.put("t_s", "Bean");
        entitySuffix.put("t_u", "Info");
        entitySuffix.put("t_p", "Data");
    }

    private static Properties prop = new Properties();

    static
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream(CodeTool.propertiesPath);
            prop.load(in);
            rootPath = prop.getProperty("rootPath");
            entityPath = prop.getProperty("entityPath");
            interfacePath = prop.getProperty("interfacePath");
            implPath = prop.getProperty("implPath");
            protoPath = prop.getProperty("protoPath");

            default_dao_entityPackage = prop.getProperty("dao_entityPackage");
            default_dao_interfacePackage = prop.getProperty("dao_interfacePackage");
            default_dao_implPackage = prop.getProperty("dao_implPackage");

            default_orm_entityPackage = prop.getProperty("orm_entityPackage");
            default_orm_interfacePackage = prop.getProperty("orm_interfacePackage");

            default_orm_implPackage = prop.getProperty("orm_implPackage");

            default_orm_annotationImport = prop.getProperty("orm_annotationImport");

            String suffix = prop.getProperty("entitySuffix");
            String[] _suffixs = suffix.split(",");
            for (String string : _suffixs)
            {
                String[] _suffix = string.split(":");
                entitySuffix.put(_suffix[0], _suffix[1]);
                entitySubPath.put(_suffix[0], _suffix[2]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        finally
        {
            try
            {
                if (null != in)
                    in.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 保存用户设置导出路径，并持久化
     */
    public static void storeProperties()
    {
        OutputStream out = null;
        try
        {
            out = new FileOutputStream(CodeTool.propertiesPath);
            prop.setProperty("rootPath", rootPath);
            prop.store(out, "Last store:" + new Date());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        finally
        {
            try
            {
                if (null != out)
                    out.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    public static String ExchangeEntityName(String entityName)
    {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
    }

    public static String toLowerName(String entityName)
    {
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }

    public static String toUpperName(String entityName)
    {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
    }

    public static String CreateInterfaceName(String entityName, String tableName)
    {
        return "I" + CreateEntityName(entityName, tableName) + "Dao";
    }

    public static String CreateImplName(String entityName, String tableName)
    {
        return CreateEntityName(entityName, tableName) + "DaoImpl";
    }

    public static String CreateEntityName(String entityName, String tableName)
    {
        return ExchangeEntityName(entityName) + getEntitySuffix(tableName);
    }

    public static String CreateProtoName(String entityName, String tableName)
    {
        return "Redis" + CreateEntityName(entityName, tableName) + "Proto";
    }

    public static String CreateEntityParameterName(String entityName, String tableName)
    {
        return toLowerName(entityName) + getEntitySuffix(tableName);
    }

    public static String CreateEntityPluralParaName(String entityName, String tableName)
    {
        return toLowerName(entityName) + getEntitySuffix(tableName) + "s";
    }

    public static String getEntitySuffix(String tableName)
    {
        for (String pref : entitySuffix.keySet())
        {
            if (tableName.startsWith(pref))
            {
                return entitySuffix.get(pref);
            }
        }

        return "";
    }

    public static String getEntitySubPath(String tableName)
    {
        for (String pref : entitySubPath.keySet())
        {
            if (tableName.startsWith(pref))
            {
                return entitySubPath.get(pref);
            }
        }

        return "";
    }

    public static String generateDaoEntityPackage(String tableName)
    {
        String subPath = CommonUtil.getEntitySubPath(tableName);
        String _package = CommonUtil.default_dao_entityPackage + (subPath.equals("") ? "" : "." + subPath);
        return _package;
    }

    public static String generateORMEntityPackage(String tableName)
    {
        String subPath = CommonUtil.getEntitySubPath(tableName);
        String _package = CommonUtil.default_orm_entityPackage + (subPath.equals("") ? "" : "." + subPath);
        return _package;
    }

    public static String generateEntityNameBySourceTable(String tableName)
    {
        String[] sub = tableName.split("_");
        String _tableName = "";
        if (sub.length < 2)
        {
            return tableName;
        }
        for (int i = 2; i < sub.length; i++)
        {
            _tableName += toUpperName(sub[i]);
        }
        return _tableName;
    }

}
