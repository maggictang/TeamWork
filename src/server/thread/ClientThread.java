package server.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import dao.service.imple.TeamWorkServiceImple;
import entity.Request;
import server.servlet.ChatServlet;
import server.servlet.FileServlet;
import server.servlet.FriendServlet;

public class ClientThread implements Runnable {
	private int clientId;
	private String clientName;
	private Socket socket = null;
	private ObjectInputStream ois = null;
	private List<ClientThread> ctList  = null;
	private Map<String,String> fileList = null;
	private TeamWorkServiceImple tsi = null;

	public ClientThread(Socket socket, List<ClientThread> ctList, Map<String, String> fileList, TeamWorkServiceImple tsi) {
		this.socket = socket;	
		this.setCtList(ctList);
		this.fileList = fileList;
		this.tsi = tsi;
	}

	@Override
	public void run() {
		new Thread(new ReciveRequest(this)).start();

	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public List<ClientThread> getCtList() {
		return ctList;
	}

	public void setCtList(List<ClientThread> ctList) {
		this.ctList = ctList;
	}

	class ReciveRequest implements Runnable {

		private ClientThread ct;

		public ReciveRequest(ClientThread clientThread) {
			this.ct = clientThread;
		}

		private boolean opened = false;
		@Override
		public void run() {
			try {
				opened = true;
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {		
				e.printStackTrace();
			}
			while(opened) {
				try {
					Request req = null;
					synchronized(this) {                //p操作
						if(req == null) {
							req = (Request) ois.readObject();
						}
					}			
					parseRequest(req);	             //临界区
					req = null;						//v操作	
				} catch(SocketException e) {
					opened = false;
					System.out.println("客户端断开连接");			
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		private void parseRequest(Request req) {	
			if("chat".equals(req.getModular())) {
				new ChatServlet(tsi).service(getCtList(),req);	
			}
			else if("friend".equals(req.getModular())) {
				new FriendServlet().service(req,ct);
				
			}
			else if("file".equals(req.getModular())) {
				new FileServlet().service(getCtList(),fileList,req,ct);
			}
		}

	}

}
