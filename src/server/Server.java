package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.service.imple.TeamWorkServiceImple;
import server.thread.ClientThread;

public class Server {
	
	private boolean opened = false;
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private List<ClientThread> ctList = new ArrayList<>();
	private Map<String,String> fileList = new HashMap<>();
	private TeamWorkServiceImple tsi = new TeamWorkServiceImple();
	
	public void start() {
		opened = true;
		try {
			serverSocket = new ServerSocket(8888);
		} catch (IOException e) {
			System.out.println("启动服务器失败");
			e.printStackTrace();
		}
		while(opened) {
			recive(serverSocket);
		}
	}
	
	private void recive(ServerSocket serverSocket2) {
		try {
			socket = serverSocket2.accept();
			ClientThread c = new ClientThread(socket,ctList,fileList,tsi);
			new Thread(c).start();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().start();
	}
}
