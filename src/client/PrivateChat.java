package client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

import entity.Request;
import entity.User;

public class PrivateChat extends Frame {

	private static final long serialVersionUID = 22407757454605097L;

	private int privateChatWidth = 300;
	private int privateChatHignt = 300;

	private String title;
	private TextArea ta = null;
	private Panel panel = null;
	private TextField tf= null;
	private Button bt = null;
	private ObjectOutputStream oos = null;
	private User user;


	public PrivateChat(String name, ObjectOutputStream oos, User user) throws HeadlessException {	
		super();
		title = name;
		this.oos = oos;
		this.user = user;
		init();		
	}

	private void init() {
		//不能随意更改界面大小
		setResizable(false);
		setTitle("与" + title + "对话中");
		setVisible(true);
		setBounds(300, 300, privateChatWidth, privateChatHignt);
		ta = new TextArea("", 0, 0, TextArea.SCROLLBARS_NONE);
		ta.setBounds(0, 0, 300, 250);
		panel = new Panel();
		bt = new Button("发送");
		tf= new TextField();
		panel.setLayout(null);
		panel.setSize(400, 45);
		tf.setBounds(0, 0, 390, 50);
		bt.setBounds(390, 0, 50, 50);
		panel.add(tf);
		panel.add(bt);
		add(ta,BorderLayout.NORTH);
		add(panel,BorderLayout.SOUTH);
		pack();
		addListener();
	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});	
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = tf.getText()+"\r\n";
				Request req = createRequest("chat", "toOne", msg);
				ta.setText(ta.getText()+msg);
				try {
					oos.writeObject(req);
					System.out.println(req.getMsg());
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				tf.setText("");
			}
		});


	}

	private Request createRequest(String modular, String function, Object msg) {
		Request req = new Request();
		req.setId(user.getId());
		req.setName(user.getUname());
		req.setModular(modular);
		req.setFunction(function);
		req.setMsg("悄悄对" + title +"说:"+ msg + "\r\n");
		return req;
	}


}
