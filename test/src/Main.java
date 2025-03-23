import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;


/*—————————————————————————————————————分割线———————————————————————————————————*/
public class Main extends JFrame
{
    public static void main(String[] args) {
        // 使用SwingUtilities.invokeLater确保GUI创建和更新在事件调度线程上进行
        SwingUtilities.invokeLater(() -> new Main());

    }

    public Main() {
        // 创建主框架
        JFrame frame = new JFrame("学生宿舍管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        setLocationRelativeTo(null);//居中——还要我说几遍
        // 创建面板
        JPanel panel = new JPanel();
        frame.add(panel);
        JPanel subPanel1 = new JPanel(new FlowLayout());
        subPanel1.setBorder(BorderFactory.createTitledBorder("数据库连接情况检测：（请等待数据库连接成功）"));
        // 创建布局管理器
        FlowLayout glayout = new FlowLayout();
        panel.setLayout(glayout);

        // 创建组件
        JLabel label = new JLabel("—————————————学生公寓管理系统———————————————", JLabel.CENTER);
        JTextArea ta = new JTextArea(10, 30);
        JButton button1 = new JButton("学生端");
        JButton button2 = new JButton("管理端");
        setLocationRelativeTo(null);//居中——还要我说几遍
        // 将组件添加到面板
        panel.add(label);
        panel.add(button1);
        panel.add(button2);
        subPanel1.add(new JScrollPane(ta)); // 使用滚动面板来包含文本区域，以便在需要时滚动
        panel.add(subPanel1);
        // 使框架可见

        frame.setVisible(true);
        //setLocationRelativeTo(null);//居中——还要我说几遍
        button1.addActionListener(new ButtonListener());
        button2.addActionListener(new ButtonListener());
        Database data=new Database(ta);

    }
    //加事件监听
    //哦原来监听器还要加到按钮上
    private static class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String buttonText = e.getActionCommand();
            if (buttonText.equals("学生端")) {
                // 在这里处理学生端的逻辑
                //System.out.println("学生端按钮被点击");//为什么没反应
                //新建第二个窗口
                SecondFrame secondFrame = new SecondFrame();
                secondFrame.setVisible(true);
            } else if (buttonText.equals("管理端")) {
                // 在这里处理管理端的逻辑
                //System.out.println("管理端按钮被点击");
                ThirdFrame tFrame = new ThirdFrame();
                tFrame.setVisible(true);
            }
        }
    }


}
class Database
{
    public static Connection dbConn = null;
    public Database(JTextArea ta)
    {
        try {
            ta.append("开始加载驱动...\n");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            ta.append("加载驱动成功...\n\n");
        } catch (Exception var5) {
            ta.append("加载驱动失败...\n\n");
        }

        String url = "jdbc:sqlserver://localhost:1433;databaseName=Dorm;encrypt=true;trustServerCertificate=true";

        try {
            String user = "sa";
            String password = "123456";
            ta.append("开始连接数据库...\n");
            dbConn = DriverManager.getConnection(url, user, password);
            ta.append("连接数据库成功...\n\n");
        } catch (Exception ex) {
            ta.append("连接数据库失败...\n\n");
            ta.append(ex.toString() + "\n");
        }
    }

}
class SecondFrame extends JFrame //这个就算写完了
{
    public SecondFrame() {
        setTitle("学生端管理界面");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // 创建一个已经包含初始文本的文本框
        //String initialText = "请输入宿舍号";
        JTextField textField_num = new JTextField( 20); // 第二个参数是列数
        JTextArea textField_inform = new JTextArea(10, 30);
        JButton button = new JButton("确认");
        button.addActionListener(e -> {
            String dormitoryNumber = textField_num.getText();
            String repairOption =  textField_inform.getText();

            String sql = "UPDATE Dormitory SET 报修信息 = ? WHERE 宿舍名 = ?;";
            try {
                PreparedStatement ps = Database.dbConn.prepareStatement(sql);
                ps.setString(2,dormitoryNumber);
                ps.setString(1,repairOption);
                int rowsUpdated = ps.executeUpdate(); // 执行更新操作
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(SecondFrame.this, "宿舍号: " + dormitoryNumber + "\n报修信息: " + repairOption + "\n提交成功！");
                } else {
                    JOptionPane.showMessageDialog(SecondFrame.this, "没有找到要更新的宿舍号: " + dormitoryNumber);
                }
            } catch (SQLException ex) {
                throw new RuntimeException("更新宿舍信息时发生错误", ex);
            }
        });

        // 为了更好的用户体验，我们可以为文本框添加一个标签
        JLabel textFieldLabel_num = new JLabel("宿舍号:");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(textFieldLabel_num);
        topPanel.add(textField_num);
        //同理，为输入的保修信息添加标签
        JPanel topPanel_inform = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel textFieldLabel_inform = new JLabel("报修信息:");
        topPanel_inform.add(textFieldLabel_inform);
        topPanel_inform.add(textField_inform);
        add(topPanel, BorderLayout.NORTH);
        add(topPanel_inform, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        setLocationRelativeTo(null); // 居中显示窗口

        //一会在这里把信息写进数据库


    }
}
class ThirdFrame extends JFrame //复制粘贴一下哈
{
    public ThirdFrame()//哇这是哪个天才写出来的程序呀
    {
        // 设置第二个窗口标题
        setTitle("管理端界面");

        // 设置默认关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 设置窗口大小
        setSize(400, 300);
        setLayout(null);
        //这里应该有一个登陆的界面，但是我已经懒得写了，算了还是写了吧
        JTextField usernameField;
        JPasswordField passwordField;
        JButton loginButton;
        JLabel usernameLabel = new JLabel("用户名:(0000)");
        usernameLabel.setBounds(50, 30, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(150, 30, 200, 25);
        add(usernameField);

        // 创建密码标签和输入框
        JLabel passwordLabel = new JLabel("密码:(0000)");
        passwordLabel.setBounds(50, 70, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 70, 200, 25);
        add(passwordField);

        // 创建登录按钮
        loginButton = new JButton("登录");
        loginButton.setBounds(150, 110, 100, 25);
        add(loginButton);
        //创建登录按钮监听器
        loginButton.addActionListener
                (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if ("0000".equals(username) && "0000".equals(password))
                {
                    JOptionPane.showMessageDialog(null, "登录成功！");
                    FourthFrame fFrame = new FourthFrame();
                    fFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "用户名或密码错误！");
                }
            }
        }
                );
            //setLocationRelativeTo(null);

            // 设置窗体可见
            setVisible(true);


        // 设置窗口居中显示
        setLocationRelativeTo(null);
    }
}
//进入第四个窗口
class FourthFrame extends JFrame
{
    //用第二个改一下
    public FourthFrame()
    {
        setTitle("管理宿舍界面");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);//居中——还要我说几遍
        setSize(400, 300);
        setLayout(new BorderLayout());//设置布局方式
        // 管理宿舍，查看人数，增加人数，删除人，添加宿舍，查看报修信息
        JButton manageDormButton = new JButton("清空宿舍");
        JButton addroom = new JButton("添加宿舍");
        JButton viewPeopleButton = new JButton("查看宿舍成员");
        JButton addPeopleButton = new JButton("增加宿舍成员");
        JButton delPeopleButton = new JButton("删除宿舍成员");
        JButton viewmessageButton = new JButton("查看报修信息");
        JTextArea ta_pres = new JTextArea(10, 30);//展示面板
        class ButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buttonText = e.getActionCommand();
                // 根据按钮文本执行不同的操作
                //不是吧这六个按键每个还要对应一个界面啊？
                switch (buttonText)
                {
                    case "清空宿舍":
                        // 实现管理宿舍的逻辑
                        //啊？我昨天晚上为什么要写这个，这有啥管理宿舍的逻辑
                        Frame6_1 f6_1 = new Frame6_1();
                        f6_1.setVisible(true);
                        break;
                    case "查看宿舍成员":
                        // 实现查看人数的逻辑
                        Frame6_2 f6_2 = new Frame6_2();
                        f6_2.setVisible(true);
                        break;
                    case "增加宿舍成员":
                        // 实现增加宿舍成员的逻辑
                        Frame6_3 f6_3 = new Frame6_3();
                        f6_3.setVisible(true);
                        break;
                    case "删除宿舍成员":
                        // 实现删除宿舍成员的逻辑
                        Frame6_4 f6_4 = new Frame6_4();
                        f6_4.setVisible(true);
                        break;
                    case "添加宿舍":
                        // 实现添加宿舍的逻辑
                        Frame6_5 f6_5 = new Frame6_5();
                        f6_5.setVisible(true);
                        break;
                    case "查看报修信息":
                        // 实现查看报修信息的逻辑
                        Frame6_6 f6_6 = new Frame6_6();
                        f6_6.setVisible(true);
                        break;
                }
            }
        }

 //在ThirdFrame的构造方法中，为每个按钮设置这个监听器
        manageDormButton.addActionListener(new ButtonListener());
        viewPeopleButton.addActionListener(new ButtonListener());
        addPeopleButton.addActionListener(new ButtonListener());
        delPeopleButton.addActionListener(new ButtonListener());
        addroom.addActionListener(new ButtonListener());
        viewmessageButton.addActionListener(new ButtonListener());



        //算了先把按钮加到面板里面吧
        JPanel buttonPanel = new JPanel();
        JPanel subPanel2 = new JPanel(new FlowLayout());//专为展示面板研发
        buttonPanel.add(manageDormButton);
        buttonPanel.add(addroom);
        buttonPanel.add(addPeopleButton);
        buttonPanel.add(delPeopleButton);
        buttonPanel.add(viewPeopleButton);
        buttonPanel.add(viewmessageButton);
        subPanel2.add(new JScrollPane(ta_pres));
        buttonPanel.add(subPanel2);
        add(buttonPanel);//将面板添加到框架
    }

}
class Frame6_1 extends JFrame //删除宿舍
{
    public Frame6_1() {
        setTitle("清空宿舍");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        JTextField textField = new JTextField(10);
        JLabel label = new JLabel("请输入要删除的宿舍号：");
        JButton button = new JButton("删除");

        button.addActionListener(e -> {
            String inputText = textField.getText();
            try {
                deleteDormitory(inputText);
                JOptionPane.showMessageDialog(Frame6_1.this, "宿舍名: " + inputText + "\n删除成功！");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(Frame6_1.this, "删除宿舍时发生错误: " + ex.getMessage());
                // 可以选择记录日志或进行其他错误处理
            }
        });

        add(label, BorderLayout.NORTH);
        add(textField, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        setLocationRelativeTo(null); // 居中显示窗口
    }

    private void deleteDormitory(String dormitoryNumber) throws SQLException {
        String sql = "DELETE FROM Dormitory WHERE 宿舍名 = ?";
        try (PreparedStatement ps = Database.dbConn.prepareStatement(sql)) {
            ps.setString(1, dormitoryNumber);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated <= 0) {
                throw new SQLException("没有找到要删除的宿舍号: " + dormitoryNumber);
            }
        }
    }
}
class Frame6_2 extends JFrame //查看人数
{
    public Frame6_2() {
        setTitle("查看宿舍");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        JTextField textField = new JTextField(10);
        JLabel label = new JLabel("请输入要查看的宿舍号：");
        JButton button = new JButton("确定");

        button.addActionListener(e -> {
            String inputText = textField.getText().trim(); // 去除输入文本的前后空格
            if (inputText.isEmpty()) {
                JOptionPane.showMessageDialog(Frame6_2.this, "请输入宿舍号！");
                return;
            }

            String sql = "SELECT DISTINCT 床位号, 姓名, 学号 FROM Dormitory WHERE 宿舍名 = ?";
            try {
                PreparedStatement ps = Database.dbConn.prepareStatement(sql);
                ps.setString(1, inputText); // 设置查询参数
                ResultSet rs = ps.executeQuery();

                // 创建一个临时表模型来存储查询结果
                DefaultTableModel tableModel = new DefaultTableModel(new String[]{"床位号", "姓名", "学号"}, 0);

                // 如果rs.next()返回true，表示至少有一行数据
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    Object[] row = {
                            rs.getString("床位号"),
                            rs.getString("姓名"),
                            rs.getString("学号")
                    };
                    tableModel.addRow(row);
                }

                // 如果找到了数据，则显示在一个新的对话框或窗口中（这里使用新对话框作为示例）
                if (found) {
                    JTable table = new JTable(tableModel);
                    JScrollPane scrollPane = new JScrollPane(table);
                    JDialog dialog = new JDialog(Frame6_2.this, "宿舍信息", true); // 创建一个模态对话框
                    dialog.add(scrollPane, BorderLayout.CENTER);
                    dialog.setSize(400, 300); // 设置对话框大小
                    dialog.setLocationRelativeTo(Frame6_2.this); // 设置对话框位置
                    dialog.setVisible(true); // 显示对话框
                } else {
                    JOptionPane.showMessageDialog(Frame6_2.this, "没有找到宿舍号: " + inputText + " 的信息。");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(Frame6_2.this, "查询宿舍信息时发生错误: " + ex.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
                // 这里不再抛出RuntimeException，而是直接在UI中显示错误信息
            }
        });

        add(label, BorderLayout.NORTH);
        add(textField, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        setLocationRelativeTo(null); // 居中显示窗口
    }

}
class Frame6_3 extends JFrame //增加宿舍成员
{
    public Frame6_3() {
        setTitle("增加宿舍成员");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());
        JPanel mainPanel = (JPanel) this.getContentPane(); // 或者直接创建一个新的JPanel并设置到JFrame中
        mainPanel.setLayout(new BorderLayout()); // 显式设置主面板的布局为BorderLayout

        JPanel subPanel1 = new JPanel(new FlowLayout());
        JPanel subPanel2 = new JPanel(new FlowLayout());
        JPanel combinedPanel = new JPanel(new BorderLayout()); // 用于组合subPanel3和subPanel4
        JPanel combinedPanel2 = new JPanel(new BorderLayout());//太寒碜了，给12也组合一下吧
        JTextField textField_num = new JTextField(10);
        JLabel label_num = new JLabel("请输入宿舍名：");
        JTextField textField_name = new JTextField(10);
        JLabel label_name = new JLabel("请输入要添加的宿舍成员：");
        JTextField textField_bednum = new JTextField(10);
        JLabel label_bednum = new JLabel("请输入分配的床位号：");
        JTextField textField_id = new JTextField(10);
        JLabel label_id = new JLabel("请输入学号：");
        JButton button = new JButton("添加");
// 添加组件到子面板中，使用FlowLayout的默认行为
        subPanel1.add(label_num);
        subPanel1.add(textField_num);
        subPanel2.add(label_name);
        subPanel2.add(textField_name);

// 将subPanel3和subPanel4添加到combinedPanel中，使用BorderLayout
        JPanel subPanel3 = new JPanel(new FlowLayout());
        subPanel3.add(label_bednum);
        subPanel3.add(textField_bednum);

        JPanel subPanel4 = new JPanel(new FlowLayout());
        subPanel4.add(label_id);
        subPanel4.add(textField_id);



        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputText_num = textField_num.getText();
                String inputText_name = textField_name.getText();
                String inputText_bednum = textField_bednum.getText();
                String inputText_id = textField_id.getText();

                boolean dormitoryExists = false;
                String checkSql = "SELECT COUNT(*) FROM Dormitory WHERE 宿舍名 = ?";
                PreparedStatement psCheck = null;
                ResultSet rs = null;
                try {
                    psCheck = Database.dbConn.prepareStatement(checkSql);
                    psCheck.setString(1, inputText_num);
                    rs = psCheck.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        dormitoryExists = (count > 0);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Frame6_3.this, "检查宿舍信息时发生错误");
                    return;
                }
                if (dormitoryExists)
                {
                    String sql = "INSERT INTO Dormitory (宿舍名, 床位号, 姓名, 学号) VALUES (?, ?, ?, ?);";
                    PreparedStatement ps = null;
                    try {
                        ps = Database.dbConn.prepareStatement(sql);
                        ps.setString(1, inputText_num);
                        ps.setString(2, inputText_bednum);
                        ps.setString(3, inputText_name);
                        ps.setString(4, inputText_id);

                        int rowsUpdated = ps.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(Frame6_3.this, "宿舍号: " + inputText_num + "\n添加成功！");
                        } else {
                            JOptionPane.showMessageDialog(Frame6_3.this, "无法添加，没有找到对应的宿舍号: " + inputText_num);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Frame6_3.this, "添加宿舍信息时发生错误");
                    } finally {
                        try {
                            if (ps != null) ps.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(Frame6_3.this, "无法添加，没有找到对应的宿舍号: " + inputText_num);
                    return;
                }
            }
        });

        combinedPanel.add(subPanel3, BorderLayout.NORTH);
        combinedPanel.add(subPanel4, BorderLayout.SOUTH);
        combinedPanel2.add(subPanel1, BorderLayout.NORTH);
        combinedPanel2.add(subPanel2, BorderLayout.CENTER);
// 将子面板添加到主面板中
        mainPanel.add(combinedPanel2, BorderLayout.NORTH);
        mainPanel.add(combinedPanel, BorderLayout.CENTER);
        //mainPanel.add(combinedPanel, BorderLayout.SOUTH); // 将combinedPanel添加到SOUTH

// 添加按钮到主面板的EAST区域
        mainPanel.add(button, BorderLayout.SOUTH);
    }
}
class Frame6_4 extends JFrame //删除宿舍成员
{
    public Frame6_4() {
        setTitle("删除宿舍成员");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());
        JLabel label_num = new JLabel("宿舍号:");
        JTextField textField = new JTextField(20);
        JLabel label_name = new JLabel("姓名:");
        JTextField textField_name = new JTextField(20);
        JLabel label_bednum = new JLabel("床位号:");
        JTextField textField_bednum = new JTextField(20);
        JButton button = new JButton("删除");

        // 创建子面板并设置布局为FlowLayout
        JPanel subPanel1 = new JPanel(new FlowLayout());
        subPanel1.add(label_num);
        subPanel1.add(textField);

        JPanel subPanel2 = new JPanel(new FlowLayout());
        subPanel2.add(label_name);
        subPanel2.add(textField_name);

        JPanel subPanel3 = new JPanel(new FlowLayout());
        subPanel3.add(label_bednum);
        subPanel3.add(textField_bednum);
        JPanel combinedPanel2 = new JPanel(new BorderLayout()); // 这行可以注释掉或删除
        combinedPanel2.add(subPanel1, BorderLayout.NORTH);
        combinedPanel2.add(subPanel2, BorderLayout.CENTER);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText_num = textField.getText();
                String inputText_name = textField_name.getText();
                String inputText_bednum = textField_bednum.getText();
                String sql = "DELETE FROM Dormitory WHERE 宿舍名 = ? AND 床位号 = ? AND 姓名 =? ;";
                PreparedStatement ps = null;
                try {
                    ps = Database.dbConn.prepareStatement(sql);
                    ps.setString(1, inputText_num); // 假设 inputText_dormName 是宿舍名的输入变量
                    ps.setString(2, inputText_bednum);
                    ps.setString(3, inputText_name);

                    int rowsUpdated = ps.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(Frame6_4.this, "宿舍信息删除成功！");
                    } else {
                        JOptionPane.showMessageDialog(Frame6_4.this, "无法添加宿舍信息");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Frame6_4.this, "添加宿舍信息时发生错误");
                } finally {
                    try {
                        if (ps != null) ps.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        add(combinedPanel2, BorderLayout.NORTH); // 将subPanel1放在框架的北部
        add(subPanel3, BorderLayout.CENTER); // 将subPanel3放在框架的中部
        add(button, BorderLayout.SOUTH); // 将按钮放在框架的南部

        setLocationRelativeTo(null);//居中——还要我说几遍
    }

}
class Frame6_5 extends JFrame //添加宿舍
{
    public Frame6_5() {
        setTitle("添加宿舍");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());
        //宿舍号，并且提醒
        JTextField textField = new JTextField(10);
        JLabel label = new JLabel("请输入要添加的宿舍号：");
        JButton button = new JButton("添加");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputText = textField.getText();
                // 在这里执行删除宿舍的操作，例如从数据库中删除该宿舍信息
                // 然后显示删除成功
                String sql = "INSERT INTO Dormitory(宿舍名)\n" +
                        "VALUES (?);";
                try {
                    PreparedStatement ps = Database.dbConn.prepareStatement(sql);
                    ps.setString(1,inputText);
                    int rowsUpdated = ps.executeUpdate(); // 执行更新操作
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(Frame6_5.this, "宿舍号: " + inputText + "\n添加成功！");
                    } else {
                        JOptionPane.showMessageDialog(Frame6_5.this, "没有找到要更新的宿舍号: " + inputText);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException("更新宿舍信息时发生错误", ex);
                }
            }
        });

        add(label, BorderLayout.NORTH);
        add(textField, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        setLocationRelativeTo(null);//居中——还要我说几遍
    }
}



class Frame6_6 extends JFrame {
    // ... 构造函数和其他成员变量 ...

    public Frame6_6() {
        setTitle("查看报修信息");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // 居中显示窗口

        // 使用SwingWorker在后台线程中执行数据库查询
        SwingWorker<Void, List<Object[]>> worker = new SwingWorker<Void, List<Object[]>>() {
            @Override
            protected Void doInBackground() throws Exception {
                String sqlQuery = "SELECT DISTINCT 宿舍名, 报修信息 FROM Dormitory WHERE 报修信息 IS NOT NULL";
                List<Object[]> rows = new ArrayList<>();
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    conn = Database.dbConn;
                    ps = conn.prepareStatement(sqlQuery);
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        Object[] row = {
                                rs.getString("宿舍名"),
                                rs.getString("报修信息"),
                                false // 添加一个布尔值来表示是否选中删除
                        };
                        rows.add(row);
                    }
                } catch (SQLException ex) {
                    // 在EDT中显示错误信息
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(Frame6_6.this, "查询报修信息时发生错误: " + ex.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
                    });
                }
                // 发布查询结果到EDT
                publish(rows);
                return null;
            }

            @Override
            protected void process(List<List<Object[]>> chunks) {
                List<Object[]> rows = chunks.get(0);

                // 在EDT中更新UI
                if (!rows.isEmpty()) {
                    DefaultTableModel tableModel = new DefaultTableModel(new String[]{"宿舍名", "报修信息", "删除"}, 0);
                    for (Object[] row : rows) {
                        tableModel.addRow(row);
                    }

                    JTable table = new JTable(tableModel) {
                        @Override
                        public TableCellEditor getCellEditor(int row, int column) {
                            if (column == 2) { // 如果是删除列，则返回JCheckBox的编辑器
                                JCheckBox checkBox = new JCheckBox();
                                checkBox.setSelected((Boolean) getValueAt(row, column));
                                return new DefaultCellEditor(checkBox);
                            }
                            return super.getCellEditor(row, column);
                        }

                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return column == 2; // 只有删除列是可编辑的
                        }
                    };

                    JScrollPane scrollPane = new JScrollPane(table);
                    JButton deleteButton = new JButton("删除选中的报修信息");

                    deleteButton.addActionListener(e -> {
                        // 在后台线程中执行删除操作
                        new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                for (int i = 0; i < table.getRowCount(); i++) {
                                    if ((Boolean) table.getValueAt(i, 2)) { // 如果选中删除
                                        String dormName = (String) table.getValueAt(i, 0);
                                        executeUpdate(dormName);
                                    }
                                }
                                return null;
                            }

                            @Override
                            protected void done() {
                                try {
                                    get(); // 确保没有异常发生
                                    JOptionPane.showMessageDialog(Frame6_6.this, "删除成功");
                                    // 这里可以重新查询数据库以更新表格，或者关闭对话框等
                                } catch (InterruptedException | ExecutionException ex) {
                                    // 处理异常
                                    JOptionPane.showMessageDialog(Frame6_6.this, "删除失败: " + ex.getCause().getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                                }
                            }

                            private void executeUpdate(String dormName) throws SQLException {
                                String sqlUpdate = "UPDATE Dormitory SET 报修信息 = NULL WHERE 宿舍名 = ?";
                                Connection conn = Database.dbConn;
                                PreparedStatement ps = null;
                                try {
                                    ps = conn.prepareStatement(sqlUpdate);
                                    ps.setString(1, dormName);
                                    ps.executeUpdate();
                                } catch (SQLException ex) {
                                    // 处理异常
                                    throw ex;
                                }
                            }
                        }.execute();
                    });

                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(scrollPane, BorderLayout.CENTER);
                    panel.add(deleteButton, BorderLayout.SOUTH);

                    JDialog dialog = new JDialog(Frame6_6.this, "宿舍报修信息", true);
                    dialog.add(panel);
                    dialog.setSize(500, 400); // 可能需要调整大小以适应内容
                    dialog.setLocationRelativeTo(Frame6_6.this);
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(Frame6_6.this, "暂无报修信息");
                }
            }
        };

        // 启动SwingWorker
        worker.execute();
    }
}