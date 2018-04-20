package server.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

import entity.Request;
import entity.Response;
import entity.io.MyObjectOutputStream;
import server.thread.ClientThread;

public class FriendServlet {

	private ObjectOutputStream oos = null;
	
	public void service(Request req, ClientThread ct) {
		if("login".equals(req.getFunction())) {
			login(req,ct);
		}
		else if("downline".equals(req.getFunction())) {
			downline(ct);
		}
	}

	private void downline(ClientThread ct) {
		if(ct.getCtList().contains(ct)) {
			ct.getCtList().remove(ct);
		}
		updateClientFriendList(ct);
	}

	private void login(Request req, ClientThread ct) {
		updateServer(req, ct);
		updateClientFriendList(ct);
	}

	private void updateServer(Request req, ClientThread ct) {
		ct.setClientId(req.getId());
		ct.setClientName(req.getName());
		ct.getCtList().add(ct);
	}

	private void updateClientFriendList(ClientThread ct) {
		Vector<String> list = createClientNames(ct);
		for(ClientThread client : ct.getCtList()) {
			Response resp = createResponse(ct,list);
			try {
				oos = new MyObjectOutputStream(client.getSocket().getOutputStream());
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				try {
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	private Vector<String> createClientNames(ClientThread ct) {
		Vector<String> list = new Vector<String>();
		for(ClientThread client : ct.getCtList()) {
			list.add(client.getClientName());
		}
		return list;
	}

	private Response createResponse(ClientThread ct, List<String> list) {
		Response resp = new Response();
		resp.setModular("friend");
		resp.setFunction("login");
		resp.setMsg(list);
		return resp;
	}

}
