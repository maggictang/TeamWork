package dao.service.imple;

import java.util.Date;

import org.apache.ibatis.session.SqlSession;


import dao.WorkDao;
import dao.service.TeamWorkService;
import entity.User;
import dao.utils.MyBatisUtil;

public class TeamWorkServiceImple implements TeamWorkService {
	
	SqlSession sqlSession = MyBatisUtil.getSqlSession();
	WorkDao dao = sqlSession.getMapper(dao.WorkDao.class);

	@Override
	public User getUser(int id, String psd) {
		User user = dao.getUser(id, psd);
		return user;
	}
	
	@Override
	public void saveChat(String msg) {
		Date date = new Date();
		dao.saveChat(date, msg);
		sqlSession.commit();
	}
	
}
