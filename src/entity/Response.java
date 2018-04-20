package entity;

import java.io.Serializable;

public class Response implements Serializable {
	
	private static final long serialVersionUID = -3458069143675918034L;
	
	private String modular;
	private String function;
	private Object msg;
	
	public String getModular() {
		return modular;
	}
	public void setModular(String modular) {
		this.modular = modular;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public Object getMsg() {
		return msg;
	}
	public void setMsg(Object msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "Response [modular=" + modular + ", function=" + function + ", msg=" + msg + "]";
	}

	
}
