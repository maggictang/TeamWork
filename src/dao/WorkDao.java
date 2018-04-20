package dao;

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import entity.User;

public interface WorkDao {
	
	@Select("select * from user where id = #{id} and psd = #{psd}")
	public User getUser(@Param("id")int id,@Param("psd")String psd);
	
	@Insert("insert into chatlog(date,msg) values (#{date},#{msg})")
	public void saveChat(@Param("date")Date date,@Param("msg")String msg);

}
