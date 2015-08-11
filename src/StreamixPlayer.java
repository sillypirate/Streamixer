import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.*;

public class StreamixPlayer
{
	public static JFrame frame;
	public static void checkResolution()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	if(screenSize.getWidth() < 1119 || screenSize.getHeight() < 526)
    	{
    		System.out.println("ERROR: Screen resolution is too low to view application");
    		System.exit(1);
    	}
	};
	
    public static void main(String[] args)
    {
    	checkResolution();
    	frame = new JFrame("Streamix Player");
    	frame.setMinimumSize(new Dimension(1119, 526));
        
        frame.getContentPane().add(new TopPanel(), BorderLayout.PAGE_START);
        frame.getContentPane().add(new BottomPanel(), BorderLayout.CENTER);
        //frame.getContentPane().add(new BackgroundPanel(), BorderLayout.CENTER);
        //frame.getContentPane().add(new BackgroundPanel(), BorderLayout.LINE_END);
        
        Image image = new ImageIcon("image/StreamixLogo.png").getImage();
        frame.setIconImage(image);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        
        frame.addWindowListener(new WindowAdapter()
        {
        	public void windowClosing(WindowEvent e)
        	{
        		Library.saveAll();
        		System.exit(0);
        	}
        });
    }
}