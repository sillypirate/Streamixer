import com.google.gdata.client.*;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.data.*;
import com.google.gdata.data.geo.impl.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.media.mediarss.*;
import com.google.gdata.data.youtube.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.io.File;
import java.net.URL;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class SearchBar extends JPanel
{
    JTextField search;
    JButton go;

    public SearchBar()
    {
        setPreferredSize(new Dimension(300, 80));
        //setBackground(Color.BLACK);
        search = new JTextField("");
        search.setPreferredSize(new Dimension(200, 20));
        add(search);

        go = new JButton("Go");
        go.addActionListener(new GoHandler());
        add(go);
        
        StreamixPlayer.frame.getRootPane().setDefaultButton(go);
    }
    
    private class GoHandler implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		System.out.println(search.getText());
    		SearchResults.getInstance().search(search.getText());
    	}
    }
}