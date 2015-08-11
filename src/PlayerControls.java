import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class PlayerControls extends JPanel
{
	private JButton play;
	private JButton stop;
	private JButton back;
	private JButton skip;
    
    public PlayerControls()
    {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(440, 80));
        
        //Play
        play = new JButton("");
        Image playb = new ImageIcon("image/play.png").getImage();
         play.setIcon(new ImageIcon(playb));
         play.addActionListener(new PlayHandler());
         add(play);
         
         //play.setBorderPainted(false); 
         play.setContentAreaFilled(false); 
         play.setFocusPainted(false); 
         play.setOpaque(false);
        
        //Stop
        stop = new JButton("");
        Image stopb = new ImageIcon("image/stop.png").getImage();
        stop.setIcon(new ImageIcon(stopb));
        stop.addActionListener(new PlayHandler());
        add(stop);
        
        //stop.setBorderPainted(false); 
        stop.setContentAreaFilled(false); 
        stop.setFocusPainted(false); 
        stop.setOpaque(false);
        
        //Back
        back = new JButton("");
        Image backb = new ImageIcon("image/back.png").getImage();
		back.setIcon(new ImageIcon(backb));
        back.addActionListener(new PlayHandler());
        add(back);
        
        //back.setBorderPainted(false); 
        back.setContentAreaFilled(false); 
        back.setFocusPainted(false); 
        back.setOpaque(false);
        
        //Skip
        skip = new JButton("");
        Image skipb = new ImageIcon("image/skip.png").getImage();
		skip.setIcon(new ImageIcon(skipb));
        skip.addActionListener(new PlayHandler());
        add(skip);
        
        //skip.setBorderPainted(false); 
        skip.setContentAreaFilled(false); 
        skip.setFocusPainted(false); 
        skip.setOpaque(false);
    }
    
    private class PlayHandler implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		if (e.getSource() == play)
    		{
    			Content.getInstance().togglePlay();
    		}
    		if (e.getSource() == stop)
    		{
    			Content.getInstance().stopMedia();
    		}
    		if (e.getSource() == skip)
    		{
    			Content.getInstance().playNext();
    		}
    		if (e.getSource() == back)
    		{
    			Content.getInstance().playPrevious();
    		}
    		
    		/*
    		if (!Content.getInstance().showing.equals("video"))
    		{
    			Content.getInstance().showVideo("");
    			Content.getInstance().showing = "video";
    		}
    		else
    		{
    			Content.getInstance().hideVideo();
    			Content.getInstance().showing = "";
    		}
    		*/
    	}
    }
}