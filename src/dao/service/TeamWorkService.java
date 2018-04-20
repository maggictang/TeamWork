package dao.service;

import entity.User;

public interface TeamWorkService {
	public User getUser(int id,String psd);
	public void saveChat(String msg);
}
