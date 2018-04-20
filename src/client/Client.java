package client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.ListModel;

import entity.Request;
import entity.Response;
import entity.User;
import entity.io.MyObjectInputStream;
import login.FailDialog;

public class Client extends Frame {

	private static final long serialVersionUID = -2119036556596392671L;

	//当前电脑屏幕宽高
	private static	Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
	private static final int WIDTH=screen.width;    
	private static final int HEIGHT=screen.height;

	/**
	 * 客户端窗口高度
	 */
	private static final int CLIENTHEIGHT = 450;

	/**
	 * 客户端窗口宽度
	 */
	private static final int CLIENTCWEIGHT = 850;

	/**
	 * 登陆的用户
	 */
	private User user = null;

	/**
	 * 与服务器连接的套接字
	 */
	private Socket s = null;

	/**
	 * 输出流
	 */
	private ObjectOutputStream oos = null;

	/**
	 * 输入流
	 */
	private ObjectInputStream ois = null;

	/**
	 * 在线列表组件
	 */
	private UserListPanel ulp = null;

	/**
	 * 聊天组件
	 */
	private ChatPanel cp = null;

	/**
	 * 文件列表组件
	 */
	private FileListPanel flp = null;

	public Client() {
		super();
	}

	public Client(User user) {
		this.user = user;
	}

	/**
	 * 初始化客户端界面
	 */
	public void initLaunch() {
		//连接服务器
		linkServer();
		//设置可见性
		setVisible(true);
		setLayout(null);
		//设置位置和大小
		setBounds((WIDTH>>1) - (CLIENTCWEIGHT>>1), (HEIGHT>>1) - (CLIENTHEIGHT>>1), CLIENTCWEIGHT, CLIENTHEIGHT);
		//设置标题
		try {
			setTitle("协同办公----"+user.getUname());
		} catch(NullPointerException e) {
			new FailDialog(this, "错误", false,"用户尚未登录").initFrame();
			System.exit(-1);
		}
		//不能随意更改界面大小
		setResizable(false);
		//添加关闭窗口事件
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				Request req = createRequest("friend","downline",new Date());
				try {
					oos.writeObject(req);
					oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		ulp = new UserListPanel();
		add(ulp);
		cp = new ChatPanel();
		add(cp);
		flp = new FileListPanel(this);
		add(flp);
		new Thread(new ReciveResponse()).start();
	}

	/**
	 * 接收线程
	 */
	class ReciveResponse implements Runnable {

		@Override
		public void run() {
			try {
				ois = new MyObjectInputStream(s.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Response resp = null;		
			try {
				while((resp=(Response) ois.readObject())!=null) {
					handlerResponse(resp);
					resp =null;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		@SuppressWarnings("unchecked")
		private void handlerResponse(Response resp) {
			if("friend".equals(resp.getModular())) {
				ulp.uList = (Vector<String>) resp.getMsg();
				ulp.list.setListData(ulp.uList);
			}
			else if("chat".equals(resp.getModular())) {
				cp.ta.setText(cp.ta.getText() + resp.getMsg());
			}
			else if("file".equals(resp.getModular())) {
				switch(resp.getFunction()) {
				case "flush" : 
				case "upload" : 
				case "delete" : flushFileList(resp); break;
				case "download" : downLoadFile(resp); break;
				}
			}
		}

	}


	/**
	 * 连接服务器
	 */
	private void linkServer() {
		try {
			s = new Socket("127.0.0.1",8888);
			oos = new ObjectOutputStream(s.getOutputStream());
			Request req = createRequest("friend","login",new Date());
			oos.writeObject(req);
			oos.flush();
		} catch (UnknownHostException|ConnectException e) {
			new FailDialog(this, "错误", false,"连不上服务器").initFrame();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	public void downLoadFile(Response resp) {
		if(resp.getMsg()!=null) {
			FileDialog fd = new FileDialog(this, "下载文件", FileDialog.SAVE);
			fd.setFile(flp.list.getSelectedValue());
			fd.setVisible(true);
			new Thread(new DownloadFile(resp,fd)).start();
		}	
	}

	class DownloadFile implements Runnable {

		private Response resp = null;
		private FileDialog fd;
		//总大小
		private double totalSize;
		//已完成
		private double completed;
		private JProgressBar bar = new JProgressBar();

		public DownloadFile(Response resp, FileDialog fd) {
			Frame frame = new Frame();
			frame.setBounds((WIDTH>>1)-(400>>1),(HEIGHT>>1)-(80>>1), 400,80);
			frame.setVisible(true);
			frame.add(bar);
			bar.setVisible(true);
			this.resp = resp;
			this.fd = fd;
			frame.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					frame.dispose();
				}
				
			});
		}

		@Override
		public void run() {
			File srcPath = new File((String) resp.getMsg());
			File targetPath = new File(fd.getDirectory()+flp.list.getSelectedValue());
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(srcPath);
				totalSize = srcPath.length();
				FileOutputStream fos = new FileOutputStream(targetPath);			
				int hasRead = 0;
				byte[] buf = new byte[1024];
				while((hasRead = fis.read(buf))!=-1) {
					completed += hasRead;
					updateProcess();
					fos.write(buf, 0, hasRead);	
				}
				fis.close();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void updateProcess() {
			int a = (int) ((completed/totalSize)*100);
			bar.setValue(a);
			bar.setStringPainted(true);
		}

	}

	@SuppressWarnings("unchecked")
	public void flushFileList(Response resp) {
		flp.fList = (Vector<String>) resp.getMsg();
		flp.list.setListData(flp.fList);
	}

	/**
	 * 创建请求
	 */
	private Request createRequest(String modular, String function, Object msg) {
		Request req = new Request();
		req.setId(user.getId());
		req.setName(user.getUname());
		req.setModular(modular);
		req.setFunction(function);
		req.setMsg(msg);
		return req;
	}

	/**
	 * 成员列表类
	 */
	class UserListPanel extends Panel {

		private static final long serialVersionUID = -8160210594241818255L;

		/**
		 * 成员列表区域宽度-----120
		 */
		private static final int USERLISTWEIGHT = 120;
		/**
		 * 在线列表
		 */
		private Vector<String> uList = new Vector<String>();

		private JList<String> list = new JList<String>();

		private Label l = new Label("在线成员");
		//搜索框
		private TextField search = new TextField(14);
		private PopupMenu popupMenu = new PopupMenu();
		private MenuItem toOne = new MenuItem("私聊");
		

		public UserListPanel() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 1, 0));
			setBackground(new Color(247,247,247));
			setLocation(3, 26);
			setSize(USERLISTWEIGHT,CLIENTHEIGHT);
			list.setBackground(new Color(247,247,247));
			add(l);
			add(search);
			add(list);
			list.add(popupMenu);
			popupMenu.add(toOne);
			toOne.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = list.getSelectedValue();
					new PrivateChat(name,oos,user);
				}
			});
			list.addMouseListener(new MouseAdapter() {	
				@Override
				public void mouseReleased(MouseEvent e) {
					//如果释放的是鼠标右键
					if(e.isPopupTrigger()) {
						popupMenu.show(list,e.getX(),e.getY());
					}
				}
			});
			search.addTextListener(new TextListener() {
				@Override
				public void textValueChanged(TextEvent e) {
					//遍历列表元素
					ListModel<String> lm = list.getModel();
					for(int i = lm.getSize()-1; i >= 0; i--) {
						if(lm.getElementAt(i).equals(search.getText())) {
							list.setSelectedIndex(i);
						}				
					}
					if(search.getText()==null||search.getText().equals("")) {
						list.setSelectedIndices(new int[] {});
					}
				}
			});

		}
	}

	/**
	 * 聊天模块
	 */
	class ChatPanel extends Panel {

		private static final long serialVersionUID = -7018777217488253713L;
		/**
		 * 聊天区域宽度-----500
		 */
		private static final int CHATWEIGHT = 500;
		public TextArea ta = null;
		

		public ChatPanel() {
			setLayout(null);
			setSize(CHATWEIGHT,CLIENTHEIGHT);
			//初始化位置
			setLocation(UserListPanel.USERLISTWEIGHT+3, 0);
			//聊天显示框
			ta = new TextArea("", 0, 0, TextArea.SCROLLBARS_NONE);
			ta.setBounds(0, 26, 500, 373);
			add(ta);	
			//输入区域模块
			add(new EnterPanel());
			

		}

		/**
		 * 输入区域模块
		 */
		class EnterPanel extends Panel {

			private static final long serialVersionUID = 2000773679662557499L;
			public EnterPanel() {
				setLayout(null);
				//EnterPanel的位置
				setBounds(0, 400, CHATWEIGHT, 47);
				//输入框
				TextArea ta1 = new TextArea("", 0, 0, TextArea.SCROLLBARS_NONE);
				ta1.setBounds(0, 0, 450, 47);
				add(ta1);
				//发送按钮
				Button enter = new Button("发送");
				enter.setBounds(450, 0, 50, 47);
				add(enter);
				enter.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String msg = ta1.getText()+"\r\n";
						Request req = createRequest("chat", "toAll", msg);
						try {
							oos.writeObject(req);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						ta1.setText("");
					}
				});

			}
		}
	}


	/**
	 * 文件列表模块
	 */
	class FileListPanel extends Panel {

		private static final long serialVersionUID = 9042096678878853740L;

		/**
		 * 文件列表宽度------180
		 */
		private static final int FWEIGHT = 230;
		private Vector<String> fList = new Vector<String>();
		private JList<String> list = new JList<String>();
		private PopupMenu popupMenu = new PopupMenu();
		private MenuItem download = new MenuItem("下载");
		private MenuItem delete = new MenuItem("删除");
		/**
		 * 文本标签
		 */
		private Label l = new Label("文件列表",Label.CENTER);
		private Button open = new Button("上传文件");
		private Button flush = new Button("刷新列表");
		private Client client = null;

		public FileListPanel(Client client) {
			this.client = client;
			setBackground(new Color(247,247,247));
			//指定位置
			setLocation(ChatPanel.CHATWEIGHT +UserListPanel.USERLISTWEIGHT+3, 21);
			setSize(FWEIGHT,CLIENTHEIGHT);
			setLayout(new FlowLayout(FlowLayout.CENTER));
			l.setBackground(new Color(247,247,247));
			list.setBackground(new Color(247,247,247));
			add(open);
			add(l);
			add(flush);
			add(list);
			list.add(popupMenu);
			popupMenu.add(download);
			popupMenu.add(delete);
			addListener();

		}
		/**
		 * 为组件添加监听器
		 */
		public void addListener() {
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Request req = createRequest("file", "delete", list.getSelectedValue());
					try {
						oos.writeObject(req);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});

			download.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Request req = createRequest("file", "download", list.getSelectedValue());
					try {
						oos.writeObject(req);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});

			list.addMouseListener(new MouseAdapter() {	
				@Override
				public void mouseReleased(MouseEvent e) {
					//如果释放的是鼠标右键
					if(e.isPopupTrigger()) {
						popupMenu.show(list,e.getX(),e.getY());
					}
				}
			});

			flush.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Request req = createRequest("file", "flush", new Date());
					try {
						oos.writeObject(req);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}			
				}
			});


			//当上传按钮按下时弹出选择文件对话框
			open.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					InetAddress ia = null;
					String str = "";
					try {
						//获得内网ip
						ia = InetAddress.getLocalHost();
					} catch (UnknownHostException e2) {
						// TODO 自动生成的 catch 块
						e2.printStackTrace();
					}

					try {		
						FileDialog fd = new FileDialog(client, "选择文件", FileDialog.LOAD);
						fd.setVisible(true);
						String directory = fd.getDirectory().replace("\\", "/");
						directory = directory.substring(directory.indexOf("/"), directory.length());
						String ip = ia.toString();
						ip = "/" + ip.substring(ip.indexOf("/"), ip.length());
						String fileName = fd.getFile();
						str=ip+directory+fileName;
						Request req = createRequest("file", "upload", str);
						try {
							oos.writeObject(req);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}		
						System.out.println(str);

					} catch(NullPointerException e1) {
						System.out.println("没有读取到文件");
					} 

				}
			});
		}
	}
}