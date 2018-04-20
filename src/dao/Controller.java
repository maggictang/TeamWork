package dao;

import dao.service.imple.TeamWorkServiceImple;
import entity.User;

public class Controller {

	private TeamWorkServiceImple tsi = new TeamWorkServiceImple();

	public User login(int id,String psd) {
		try {
			User user = tsi.getUser(id, psd);
			if(id==user.getId() && psd.equals(user.getPsd())) {
				return user;
			}
			return null;
		} catch(Exception e) {
			return null;
		}
	}
}
