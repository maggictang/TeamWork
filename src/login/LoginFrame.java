package login;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import entity.User;
import client.Client;
import dao.Controller;

public class LoginFrame extends Frame {

	private static final long serialVersionUID = -3779657587146581953L;

	//当前电脑屏幕宽高
	private static	Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
	private static final int width=screen.width;    
	private static final int height=screen.height;

	public LoginFrame() {
		super();
	}

	//登录窗口的宽高
	private int lWidth = 400;
	private int lHeight = 300;

	private static LoginFrame lf = null;
	private Panel panel = new Panel();

	private Label l1 = new Label("账   号：");
	private TextField uname = new TextField();

	private Label l2 = new Label("密   码：");
	private TextField psd = new TextField();

	private Button loginBnt = new Button("登录");


	//初始化界面
	public void initFrame() {
		//不能随意更改界面大小
		setResizable(false);
		setTitle("登录");
		setBounds( (width>>1) - (lWidth>>1), (height>>1) - (lHeight>>1), lWidth, lHeight);
		setVisible(true);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		initPanel();

		add(panel);
	}

	public void initPanel() {

		panel.setBounds(40, 30, 320, 240);
		panel.setLayout(null);

		l1.setBounds(70, 80, 100, 20);
		panel.add(l1);
		uname.setBounds(180, 80, 140, 20);
		panel.add(uname);

		l2.setBounds(70, 130, 100, 20);
		panel.add(l2);
		psd.setBounds(180, 130, 140, 20);
		panel.add(psd);
		psd.setEchoChar('*');

		loginBnt.setBounds(239, 180, 80, 20);
		panel.add(loginBnt);

		//为各种组件添加事件监听
		addListener();

	}

	private void addListener() {
		loginBnt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//获取用户名密码
				String s1 = uname.getText();
				String s2 = psd.getText();
				int id = -1;
				try {
					id = Integer.parseInt(s1);
				} catch (Exception e1) {
					new FailDialog(lf, "登录失败", false,"用户名或密码错误").initFrame();
					return;
				}
				User user = new Controller().login(id, s2);
				if(user != null) {
					new Client(user).initLaunch();
					dispose();
				}
				else {
					new FailDialog(lf, "登录失败", false,"用户名或密码错误").initFrame();
					return;
				}
			}
		});
	}

	public static void main(String[] args) {
		lf = new LoginFrame();
		lf.initFrame();
	}
}
