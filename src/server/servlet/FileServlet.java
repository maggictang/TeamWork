package server.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import entity.*;
import entity.io.MyObjectOutputStream;
import server.thread.ClientThread;

public class FileServlet {
	
	private ObjectOutputStream oos = null;

	public void service(List<ClientThread> ctList, Map<String, String> fileList2, Request req, ClientThread ct) {
		if("upload".equals(req.getFunction())) {
			uploadFile(fileList2,req);
			updateClientFileList(ctList,fileList2,"upload");	
		}
		else if("delete".equals(req.getFunction())) {
			deleteFile(fileList2,req);
			updateClientFileList(ctList,fileList2,"delete");	
		}
		else if("flush".equals(req.getFunction())) {
			updateClientFileList(ctList,fileList2,"flush");	
		}
		else if("download".equals(req.getFunction())) {
			downloadFile(fileList2,req,ct);
		}
	}
	
	private void downloadFile(Map<String, String> fileList2, Request req, ClientThread ct) {
		String fileName = (String) req.getMsg();
		String fileMsg = fileList2.get(fileName);
		Response resp = new Response();
		resp = createResponse(resp, fileMsg, "download");
		try {
			oos = new MyObjectOutputStream(ct.getSocket().getOutputStream());
			oos.writeObject(resp);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteFile(Map<String, String> fileList2, Request req) {
		String fileName = (String) req.getMsg();
		fileList2.remove(fileName);		
	}

	public void uploadFile(Map<String, String> fileList2, Request req) {
		String fileMsg = (String) req.getMsg();
		String fileName = fileMsg.substring(fileMsg.lastIndexOf("/")+1);
		fileList2.put(fileName, fileMsg);
	}
	
	public void updateClientFileList(List<ClientThread> ctList, Map<String, String> fileList2,String function) {
		Response resp = new Response();
		Vector<String> clientFileName = createClientFileName(fileList2);
		for(ClientThread client : ctList) {
			resp = createResponse(resp,clientFileName,function);
			try {
				oos = new MyObjectOutputStream(client.getSocket().getOutputStream());
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}

	private Vector<String> createClientFileName(Map<String, String> fileList2) {
		Vector<String> list = new Vector<>();
		for(String name : fileList2.keySet()) {
			list.add(name);
		}
		return list;
	}

	private Response createResponse(Response resp, Object msg, String function) {	
		resp.setModular("file");
		resp.setFunction(function);
		resp.setMsg(msg);
		return resp;
	}

}
