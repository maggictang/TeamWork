package entity;

import java.io.Serializable;

public class Request implements Serializable {
	
	private static final long serialVersionUID = 7164771867787741878L;
	private int id;
	private String name;
	private String modular;
	private String function;
	private Object msg;
	
	
	@Override
	public String toString() {
		return "Request [id=" + id + ", name=" + name + ", modular=" + modular + ", function=" + function + ", msg="
				+ msg + "]";
	}
	public String getModular() {
		return modular;
	}
	public void setModular(String modular) {
		this.modular = modular;
	}
	public Object getMsg() {
		return msg;
	}
	public void setMsg(Object msg) {
		this.msg = msg;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}

}
