package entity.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

public class MyObjectInputStream extends ObjectInputStream{

	protected MyObjectInputStream() throws IOException, SecurityException {
		super();
		// TODO 自动生成的构造函数存根
	}

	public MyObjectInputStream(InputStream inputStream) throws IOException {
		// TODO 自动生成的构造函数存根
		super(inputStream);
	}

	@Override
	protected void readStreamHeader() throws IOException, StreamCorruptedException {

	}
	
	

}
