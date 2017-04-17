package com.road.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.road.tool.util.CommonUtil;
import com.road.tool.util.DaoCodeUtil;
import com.road.tool.util.DbUtil;
import com.road.tool.util.EntityCodeUtil;
import com.road.tool.util.FieldInfo;
import com.road.tool.util.InterfaceCodeUtil;
import com.road.tool.util.OrmEntityCodeUtil;
import com.road.tool.util.ProtoUtil;
import com.road.tool.util.RedisUtil;

public class CodeTool
{
    private static Table table;

    private static Table _Table;

    private static Shell codeDialog;
    // private static Table baseDaoTable;
    // private static Table entityDaoTable;
    // private static TextArea console;

    private static Tree tree;

    private static Map<String, FieldInfo> fieldMap;

    private static Map<String, Boolean> selectExtMethod = new HashMap<String, Boolean>();

    private static String currentSelectedTable;

    private static boolean SELECT_ALL = false;

    public static String propertiesPath = "";
    
    private static Properties prop = new Properties();
    
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("请输入配置文件");
            return;
        }

        propertiesPath = args[0];
        
        ConfDialog loginView = new ConfDialog();
        loginView.init(args[0]);
        
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
        
        /**
         * 初始化主窗口
         */
        final Display display = Display.getDefault();
        final Shell shell = new Shell();
        shell.setSize(1000, 600);
        shell.setText("Code-Tool");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        // /**
        // * 导入配置文件
        // */
        // FileDialog dialog = new FileDialog(shell);
        // dialog.setFilterNames(new String[]{"java配置文件(*.properties)"});
        // dialog.setFilterExtensions(new String[]{"*.properties"});
        // dialog.setText("CodeTool 导入配置文件");
        // dialog.setFileName("db.properties");
        // propertiesPath = dialog.open();

        if (propertiesPath == null)
        {
            return;
        }

        /**
         * 初始化一级栏目
         */
        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);
        MenuItem muConn = new MenuItem(menu, SWT.CASCADE);
        muConn.setText("Menu");
        Menu menu_1 = new Menu(muConn);
        muConn.setMenu(menu_1);
        MenuItem muConnNew = new MenuItem(menu_1, SWT.NONE);
        muConnNew.setText("New");
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem muHelp = new MenuItem(menu, SWT.CASCADE);
        muHelp.setText("About");
        Menu menu_2 = new Menu(muHelp);
        muHelp.setMenu(menu_2);
        MenuItem muAbout = new MenuItem(menu_2, SWT.NONE);
        muAbout.setText("New");

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(getGridLayout());

        SashForm sashForm = new SashForm(composite, SWT.BORDER);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite compConnList = new Composite(sashForm, SWT.BORDER);
        compConnList.setLayout(getGridLayout());

        /**
         * 左边竖直窗口
         */
        Composite composite_1 = new Composite(compConnList, SWT.NONE);
        composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        CLabel lblNewLabel = new CLabel(composite_1, SWT.NONE);
        lblNewLabel.setText("Connections");

        /**
         * 右边竖直窗口
         */
        Composite composite_2 = new Composite(compConnList, SWT.NONE);
        composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite_2.setLayout(getGridLayout());

        tree = new Tree(composite_2, SWT.BORDER);
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        List<String> databases = DbUtil.getDatabases();
        for (int i = 0; i < databases.size(); i++)
        {
            TreeItem dbItem = new TreeItem(tree, SWT.NONE);
            dbItem.setData("type", "DB");
            dbItem.setData("db_name", databases.get(i));
            dbItem.setText(databases.get(i));

//            List<String> tables = DbUtil.getTables(databases.get(i));
//            for (int j = 0; j < tables.size(); j++)
//            {
//                TreeItem treeItem = new TreeItem(dbItem, SWT.NONE);
//                treeItem.setText(tables.get(j));
//                treeItem.setData("type", "TABLE");
//            }
        }

        final String[] titles =
        { "Name", "Java Type", "Sql Type", "Max Size", "Comment" };

        /**
         * 添加树节点被点击的监听
         */
        tree.addListener(SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                TreeItem[] selection = tree.getSelection();
                
                if (selection.length < 0)
                    return;
                    
                if (selection[0].getData("type").equals("DB") && selection[0].getItemCount() <= 0)
                {
                    List<String> tables = DbUtil.getTables((String) selection[0].getData("db_name"));
                    for (int j = 0; j < tables.size(); j++)
                    {
                        TreeItem treeItem = new TreeItem(selection[0], SWT.NONE);
                        treeItem.setText(tables.get(j));
                        treeItem.setData("type", "TABLE");
                    }
                }
                
                if (selection[0].getData("type").equals("TABLE"))
                {
                    currentSelectedTable = selection[0].getText();
                    if (fieldMap != null)
                        fieldMap.clear();

                    table.removeAll();

                    SELECT_ALL = false;

                    fieldMap = DbUtil.getTableFieldList(selection[0].getParentItem().getText(), selection[0].getText());

                    Collection<FieldInfo> c = fieldMap.values();
                    Iterator<FieldInfo> it = c.iterator();
                    while (it.hasNext())
                    {
                        FieldInfo field = (FieldInfo) it.next();
                        TableItem t_item = new TableItem(table, SWT.NONE);
                        t_item.setText(0, field.getName());
                        t_item.setText(1, field.getJavaType());
                        t_item.setText(2, field.getSqlType());
                        t_item.setText(3, field.getLen() + "");
                        t_item.setText(4, field.getComment());
                        t_item.setChecked(field.isFlag());
                    }

                    for (int i = 0; i < titles.length; i++)
                    {
                        table.getColumn(i).pack();
                    }
                }
            }
        });

        Composite compContentShow = new Composite(sashForm, SWT.BORDER);
        compContentShow.setLayout(getGridLayout());

        ToolBar codeBar = new ToolBar(compContentShow, SWT.FLAT | SWT.RIGHT);
        codeBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        /**
         * 点击选中当前表中所有字段
         */
        ToolItem tltmSelectall = new ToolItem(codeBar, SWT.NONE);
        tltmSelectall.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                if (currentSelectedTable == null || table == null)
                    return;

                SELECT_ALL = !SELECT_ALL;

                System.err.println("全选：" + SELECT_ALL);

                for (TableItem item : table.getItems())
                {
                    String clickItemName = item.getText();
                    item.setChecked(SELECT_ALL);
                    fieldMap.get(clickItemName).setFlag(SELECT_ALL);
                }
            }
        });
        tltmSelectall.setText("SelectAll");

        /**
         * 点击 查看数值策划Excel表头
         */
        ToolItem codeDao = new ToolItem(codeBar, SWT.NONE);
        codeDao.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                if (currentSelectedTable == null || table == null)
                    return;

                /**
                 * 生成代码预览窗口, 获取焦点
                 */
                final Shell codeDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                Point pt = display.getCursorLocation();
                codeDialog.setLocation(pt);
                codeDialog.setLayout(new FillLayout());
                codeDialog.setText("Excel 表头");

                final Text text = new Text(codeDialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

                System.out.println(currentSelectedTable);

                String[] strList = currentSelectedTable.split("_");
                String name = strList[strList.length - 1];

                String code = DaoCodeUtil.generateDaoImplCode(name, fieldMap);
                text.setText(code);

                codeDialog.setSize(600, 500);
                codeDialog.open();
            }
        });
        codeDao.setText("Excel表头");

        // /**
        // * 生成业务逻辑层代码
        // */
        // ToolItem tltmCodebll = new ToolItem(codeBar, SWT.NONE);
        // tltmCodebll.addSelectionListener(new SelectionAdapter()
        // {
        // @Override
        // public void widgetSelected(SelectionEvent arg0)
        // {
        // if (currentSelectedTable == null || table == null)
        // return;
        // }
        // });
        // tltmCodebll.setText("Code_Bll");

        /**
         * 点击 生成所有代码
         */
        ToolItem codeCreate = new ToolItem(codeBar, SWT.NONE);
        codeCreate.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                if (currentSelectedTable == null || table == null)
                    return;

                /**
                 * 生成代码预览窗口, 获取焦点
                 */
                codeDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

                Point pt = display.getCursorLocation();
                codeDialog.setLocation(pt);
                // codeDialog.setLayout(new FillLayout());
                codeDialog.setLayout(new FillLayout(SWT.VERTICAL));
                codeDialog.setText("提示：");

                // final Text text = new Text(codeDialog, SWT.BORDER | SWT.MULTI
                // | SWT.V_SCROLL | SWT.H_SCROLL);

                // ****************************************

                // 添加一个选择生成方法的table

                final Label _Label = new Label(codeDialog, SWT.BORDER);
                _Label.setText("请选择需要拓展的方法");

                _Table = new Table(codeDialog, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

                TableItem _TableItem = new TableItem(_Table, SWT.NONE);
                _TableItem.setText(0, "getEntityByUserID");
                _TableItem.setChecked(false);

                TableItem _TableItem1 = new TableItem(_Table, SWT.NONE);
                _TableItem1.setText(0, "clone");
                _TableItem1.setChecked(false);

                TableItem _TableItem2 = new TableItem(_Table, SWT.NONE);
                _TableItem2.setText(0, "CreateBeanAnnotation");
                _TableItem2.setChecked(false);

                final Composite _Composite = new Composite(codeDialog, SWT.BOTTOM);
                _Composite.setLayout(new FillLayout());

                final Button _SubmitButton = new Button(_Composite, SWT.BOTTOM);
                _SubmitButton.setText("生成");

                // final Button _CancleButton = new Button(_Composite,
                // SWT.BOTTOM);
                // _CancleButton.setText("取消");
                // ****************************************

                _SubmitButton.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent arg0)
                    {
                        selectExtMethod.clear();

                        for (TableItem item : _Table.getItems())
                        {
                            if (item.getChecked())
                            {
                                selectExtMethod.put(item.getText(0), true);
                            }
                        }

                        String name = CommonUtil.generateEntityNameBySourceTable(currentSelectedTable);

                        List<FieldInfo> temps = new ArrayList<FieldInfo>(fieldMap.values());
                        generateDaoCode(name, currentSelectedTable, fieldMap, temps);

                        codeDialog.dispose();
                        // text.setText("代码已经生成");
                    }
                });

                // String[] strList = currentSelectedTable.split("_");
                // String name = strList[strList.length - 1];
                // _CancleButton.addSelectionListener(new SelectionAdapter() {
                //
                // @Override
                // public void widgetSelected(SelectionEvent arg0) {
                // }
                //
                // });

                codeDialog.setSize(600, 500);
                codeDialog.open();
            }
        });

        codeCreate.setText("输出Dao代码");

        /**
         * 点击 生成所有代码
         */
        ToolItem ormcodeCreate = new ToolItem(codeBar, SWT.NONE);
        ormcodeCreate.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                if (currentSelectedTable == null || table == null)
                    return;

                /**
                 * 生成代码预览窗口, 获取焦点
                 */
                codeDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                Point pt = display.getCursorLocation();
                codeDialog.setLocation(pt);
                codeDialog.setLayout(new FillLayout(SWT.VERTICAL));
                codeDialog.setText("提示：");

                // final Text text = new Text(codeDialog, SWT.BORDER | SWT.MULTI
                // | SWT.V_SCROLL | SWT.H_SCROLL);

                // 添加一个选择生成方法的table

                final Label _Label = new Label(codeDialog, SWT.BORDER);
                _Label.setText("请选择需要拓展的方法");

                _Table = new Table(codeDialog, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

                TableItem _TableItem = new TableItem(_Table, SWT.NONE);
                _TableItem.setText(0, "getEntityByUserID");
                _TableItem.setChecked(false);

                TableItem _TableItem1 = new TableItem(_Table, SWT.NONE);
                _TableItem1.setText(0, "clone");
                _TableItem1.setChecked(false);

                TableItem _TableItem2 = new TableItem(_Table, SWT.NONE);
                _TableItem2.setText(0, "CreateBeanAnnotation");
                _TableItem2.setChecked(false);

                final Composite _Composite = new Composite(codeDialog, SWT.BOTTOM);
                _Composite.setLayout(new FillLayout());

                final Button _SubmitButton = new Button(_Composite, SWT.BOTTOM);
                _SubmitButton.setText("生成");

                // final Button _CancleButton = new Button(_Composite,
                // SWT.BOTTOM);
                // _CancleButton.setText("取消");
                // ****************************************

                _SubmitButton.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent arg0)
                    {
                        selectExtMethod.clear();

                        for (TableItem item : _Table.getItems())
                        {
                            if (item.getChecked())
                            {
                                selectExtMethod.put(item.getText(0), true);
                            }
                        }

                        String name = CommonUtil.generateEntityNameBySourceTable(currentSelectedTable);

                        generateOrmCode(name, currentSelectedTable);

                        codeDialog.dispose();

                        // String[] strList = currentSelectedTable.split("_");
                        // String name = strList[strList.length - 1];
                        // String name =
                        // CommonUtil.generateEntityNameBySourceTable(currentSelectedTable);
                        //
                        // generateAllCode(name, currentSelectedTable);
                        // text.setText("代码已经生成");

                    }
                });

                codeDialog.setSize(600, 500);
                codeDialog.open();
            }
        });
        ormcodeCreate.setText("输出ORM代码");

        ToolItem codeProto = new ToolItem(codeBar, SWT.NONE);
        codeProto.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                if (currentSelectedTable == null || table == null)
                    return;

                codeDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                Point pt = display.getCursorLocation();
                codeDialog.setLocation(pt);
                codeDialog.setLayout(new FillLayout(SWT.VERTICAL));
                codeDialog.setText("说明");

                // final Text text = new Text(codeDialog, SWT.BORDER | SWT.MULTI
                // | SWT.V_SCROLL | SWT.H_SCROLL);

                // 添加一个选择生成方法的table

                final Label _Label = new Label(codeDialog, SWT.BORDER);
                _Label.setText(
                        "把数据库表生成proto文件，long类型转成int64,int转成int32,varchar转成string,DATETIME转成string,SAMLLINT转成int");

                final Composite _Composite = new Composite(codeDialog, SWT.BOTTOM);
                _Composite.setLayout(new FillLayout());

                final Button _SubmitButton = new Button(_Composite, SWT.BOTTOM);
                _SubmitButton.setText("生成pb");

                _SubmitButton.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent arg0)
                    {
                        String name = CommonUtil.generateEntityNameBySourceTable(currentSelectedTable);

                        generateProtoCode(name, currentSelectedTable);

                        codeDialog.dispose();

                    }
                });

                codeDialog.setSize(600, 500);
                codeDialog.open();
            }
        });
        codeProto.setText("输出proto");

        Composite compTable = new Composite(compContentShow, SWT.NONE);
        compTable.setLayout(new FillLayout(SWT.HORIZONTAL));
        compTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        table = new Table(compTable, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        for (int i = 0; i < titles.length; i++)
        {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }

        table.addListener(SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(Event e)
            {
                String clickItemName = ((TableItem) e.item).getText();
                System.err.println("选择：" + clickItemName);
                boolean tmp = ((TableItem) e.item).getChecked();
                ((TableItem) e.item).setChecked(tmp);
                fieldMap.get(clickItemName).setFlag(tmp);
            }
        });

        sashForm.setWeights(new int[]
        { 1, 4 });

        shell.open();
        shell.layout();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    private static GridLayout getGridLayout()
    {
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        return gridLayout;
    }

    private static File entityFilePath;// 实体文件
    private static File interfaceFilePath;// 接口文件
    private static File implFilePath;// 接口实现文件
    private static File protoFilePath; // proto文件地址

    private static void createFilePath(boolean isProto)
    {
        if (isProto)
        {// 创建proto
            String rootPath = CommonUtil.protoPath;
            protoFilePath = new File(rootPath);
            if (!protoFilePath.exists())
            {
                protoFilePath.mkdirs();
            }
        }
        else
        {
            String rootPath = CommonUtil.rootPath;
            File file = new File(rootPath);
            if (!file.exists())
            {
                file.mkdirs();
            }
            String entityPath = rootPath + "//" + CommonUtil.entityPath;
            String subPath = CommonUtil.getEntitySubPath(currentSelectedTable);
            if (!subPath.equals(""))
            {
                entityPath += "//" + subPath;
            }
            entityFilePath = new File(entityPath);
            if (!entityFilePath.exists())
            {
                entityFilePath.mkdirs();
            }

            String interfacePath = rootPath + "//" + CommonUtil.interfacePath;
            interfaceFilePath = new File(interfacePath);
            if (!interfaceFilePath.exists())
            {
                interfaceFilePath.mkdir();
            }

            String implPath = rootPath + "//" + CommonUtil.implPath;
            implFilePath = new File(implPath);
            if (!implFilePath.exists())
            {
                implFilePath.mkdir();
            }
        }
    }

    private static void generateOrmCode(String entityName, String tableName)
    {
        createFilePath(false);// 首先创建目录文件

        String entityFileName = CommonUtil.CreateEntityName(entityName, currentSelectedTable) + ".java";
        String entityContent = OrmEntityCodeUtil.generateEntityCode(entityName, tableName, fieldMap, selectExtMethod);
        generateFile(entityFilePath, entityFileName, entityContent);
    }

    /**
     * 生成proto(往一个文件中不覆盖的写入新文件)
     * 
     * @param entityName
     * @param tableName
     */
    private static void generateProtoCode(String entityName, String tableName)
    {
        createFilePath(true);
        // 生成文件
        String entityContent = ProtoUtil.generateProtoCode(entityName, tableName, fieldMap);
        String protoName = "message " + CommonUtil.CreateProtoName(entityName, tableName);
        // 生成文件
        generateOldFile(entityContent, protoName);
        
        RedisUtil redisUtil = new RedisUtil(prop);
        redisUtil.generateRedisCode(entityName + "Info", fieldMap);
    }

    private static void generateDaoCode(String entityName, String tableName, Map<String, FieldInfo> fieldMap,
            List<FieldInfo> fields)
    {

        createFilePath(false);// 首先创建目录文件
        String entityFileName = CommonUtil.CreateEntityName(entityName,
                currentSelectedTable) + ".java";
        String entityContent = EntityCodeUtil.generateEntityCode(CommonUtil.entityPath,
                entityName, currentSelectedTable, fieldMap, selectExtMethod);
        generateFile(entityFilePath, entityFileName, entityContent);

        String interfaceFileName = CommonUtil.CreateInterfaceName(entityName,
                currentSelectedTable) + ".java";
        String interfaceContent = InterfaceCodeUtil.generateInterfaceCode(
                entityName, currentSelectedTable, selectExtMethod);
        generateFile(interfaceFilePath, interfaceFileName, interfaceContent);

        String implFileName = CommonUtil.CreateImplName(entityName,
                currentSelectedTable) + ".java";
        String implContent = DaoCodeUtil.generateDaoImplCode(entityName,
                tableName, fields, selectExtMethod);
        generateFile(implFilePath, implFileName, implContent);

    }

    private static void generateFile(File rootFile, String fileName, String content)
    {
        File entityFile = new File(rootFile, fileName);
        if (!entityFile.exists())
        {
            try
            {
                entityFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(entityFile), "UTF-8");

            BufferedWriter writer = new BufferedWriter(out);
            writer.write(content);
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void generateOldFile(String content, String protoName)
    {
        if (protoFilePath == null)
        {
            return;
        }
        StringBuffer result = new StringBuffer();
        try
        {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(protoFilePath), "UTF-8"));
            String value = null;
            boolean isFind = false;
            while ((value = bf.readLine()) != null)
            {
                if (value.equals(protoName))
                {
                    isFind = true;
                }

                if (isFind && !value.equals("}"))
                {
                    continue;
                }
                if (!isFind)
                {
                    result.append(value).append("\n");
                }
                isFind = false;
            }

            result.append(content);

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(protoFilePath), "UTF-8");
            BufferedWriter writer = new BufferedWriter(out);

            writer.write(result.toString());
            writer.flush();
            writer.close();
            out.close();
            bf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
