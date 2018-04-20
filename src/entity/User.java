package entity;

public class User {
	
	private int id;
	private String uname;
	private String psd;

	
	public User() {
		super();
		// TODO 自动生成的构造函数存根
	}

	public User(int id, String name) {
		super();
		this.id = id;
		this.uname = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", uname=" + uname + "]";
	}

	public String getPsd() {
		return psd;
	}

	public void setPsd(String psd) {
		this.psd = psd;
	}

	
	
}
