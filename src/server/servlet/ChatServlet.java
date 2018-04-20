package server.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import dao.service.imple.TeamWorkServiceImple;
import entity.Request;
import entity.Response;
import entity.io.MyObjectOutputStream;
import server.thread.ClientThread;

public class ChatServlet {

	private ObjectOutputStream oos = null;
	private TeamWorkServiceImple tsi = null;


	public ChatServlet(TeamWorkServiceImple tsi) {
		this.tsi = tsi;
	}

	public void service(List<ClientThread> ctList, Request req) {
		tsi.saveChat((String)req.getMsg());
		if(req.getFunction().equals("toAll")) {
			//System.out.println(req);
			chatToAll(ctList,"chatToAll",req);		
		}
		if(req.getFunction().equals("toOne")) {
			chatToOne(req,"chatToOne",ctList);
		}
	}

	private void chatToOne(Request req, String string, List<ClientThread> ctList) {
		String name = (String) req.getMsg();
		name = name.substring(name.indexOf("悄悄对")+3,name.indexOf("说:") );
		ClientThread ct = seachClient(name,ctList);
		if(ct != null) {
			Response resp = createToOneResponse(string,req);
			try {
				oos = new MyObjectOutputStream(ct.getSocket().getOutputStream());
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

	private Response createToOneResponse(String string, Request req) {
		Response resp = new Response();
		resp.setModular("chat");	
		resp.setFunction(string);
		String[] temp = ((String) req.getMsg()).split("说:");
		resp.setMsg(req.getName()+"悄悄对你说:"+temp[1]);
		return resp;
	}

	private ClientThread seachClient(String name, List<ClientThread> ctList) {
		for(ClientThread ct : ctList) {
			if(name.equals(ct.getClientName())) {
				return ct;
			}
		}
		return null;
	}

	private void chatToAll(List<ClientThread> ctList, String function, Request req) {
		for(ClientThread clientThread : ctList) {
			Response resp = createResponse(function,req);
			try {
				oos = new MyObjectOutputStream(clientThread.getSocket().getOutputStream());
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} 

		}

	}

	public Response createResponse(String function, Request req) {
		Response resp = new Response();
		resp.setModular("chat");	
		resp.setFunction(function);
		resp.setMsg(req.getName()+":"+req.getMsg());
		return resp;
	}
}
