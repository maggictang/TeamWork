package login;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FailDialog extends Dialog {
	
	private static final long serialVersionUID = -248315395575689884L;

	
	public FailDialog(Frame owner, String title, boolean modal,String text) {
		super(owner, title, modal);
		context = new Label(text,Label.CENTER);
	}

	//对话框宽
	private static final int fWidth = 250;

	//对话框高
	private static final int fHeight = 180;
	
	private Label context = null; 

	public void initFrame() {
		
		//当前电脑屏幕宽高
		Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
		final int width=screen.width;    
		final int height=screen.height;
		
		setVisible(true);
		setBounds( (width>>1) - (fWidth>>1), (height>>1) - (fHeight>>1), fWidth, fHeight);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO 自动生成的方法存根
				dispose();
			}
		});
		
		add(context);
		
	}
	
}
