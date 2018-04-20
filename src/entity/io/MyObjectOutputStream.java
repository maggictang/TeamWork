package entity.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MyObjectOutputStream extends ObjectOutputStream {

    protected MyObjectOutputStream() throws IOException, SecurityException {
        super();
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        
    }
    
    public MyObjectOutputStream(OutputStream o) throws IOException{
        super(o);
    }
}
