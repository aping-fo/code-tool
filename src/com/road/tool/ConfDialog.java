package com.road.tool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * @author fei.wang
 *
 */
public class ConfDialog
{
    private Shell shell;
    private Text urltext;
    private Text usernametext;
    private Text passwordtext;
    private Text rootPathText;
    private Button button;
    private Properties prop;
    private String propFilepath;

    public ConfDialog()
    {
    }

    public void init(String string)
    {
        propFilepath = string;
        FileInputStream in;
        prop = new Properties();
        try
        {
            in = new FileInputStream(string);
            prop.load(in);
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Display display = new Display();
        
        shell = new Shell(display);
        shell.setText("ConfDialog");
        shell.setSize(400, 300);
        
        GridLayout layout = new GridLayout(2, false);
        layout.marginRight = 5;
        layout.marginLeft = 10;
//        layout.marginTop = 0;
        shell.setLayout(layout);
        
        Label urlLabel = new Label(shell, SWT.NONE);
        urlLabel.setText("url:");
        urlLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        
        urltext = new Text(shell, SWT.BORDER);
        urltext.setText(prop.getProperty("url"));
        urltext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        
        Label usrnameLabel = new Label(shell, SWT.NONE);
        usrnameLabel.setText("usrname:");
        
        usernametext = new Text(shell, SWT.BORDER);
        usernametext.setText(prop.getProperty("userName"));
        usernametext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        
        Label password = new Label(shell, SWT.NONE);
        password.setText("password:");
        
        passwordtext = new Text(shell, SWT.BORDER);
        passwordtext.setText(prop.getProperty("password"));
        passwordtext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        
        Label generatepath = new Label(shell, SWT.NONE);
        generatepath.setText(" generatePath:");
        
        rootPathText = new Text(shell, SWT.BORDER);
        rootPathText.setText(prop.getProperty("rootPath"));
        rootPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        
        button = new Button(shell, SWT.BOTTOM);
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                2, 1));
        button.setText("确定");

        button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                String url = urltext.getText();
                String usrname = usernametext.getText();
                String password = passwordtext.getText();

                prop.setProperty("url", url);
                prop.setProperty("userName", usrname);
                prop.setProperty("password", password);
                prop.setProperty("rootPath", rootPathText.getText());
                System.err.println(url);
                System.err.println(usrname);
                System.err.println(password);

                try
                {
                    FileOutputStream out = new FileOutputStream(propFilepath);
                    prop.store(out, "Last store:" + new Date());
                    out.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                shell.dispose();
            }
        });

        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    public static void main(String[] args)
    {
        ConfDialog loginView = new ConfDialog();
        loginView.init("D:/ddt/workspace/server/DBCodeTool/config/db.properties");
    }
}
