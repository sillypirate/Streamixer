import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;

import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.net.URI;
import java.net.URL;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class SearchResults extends JPanel
{
	private ArrayList<JPanel> entryPanels;
	private static SearchResults instance;
	private String curURL;
	private JScrollPane scroller;
	
    public SearchResults()
    {
        setPreferredSize(new Dimension(240, 730));
        setBackground(Color.BLACK);
    }
    
    public static SearchResults getInstance()
    {
    	if (instance == null)
    	{
    		instance = new SearchResults();
    	}
    	
    	return instance;
    }
    
    public void search(String keyword)
    {
    	 try
         {
 	        YouTubeService service = new YouTubeService("streamix-player-1");
 	        YouTubeQuery query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
 	        query.setOrderBy(YouTubeQuery.OrderBy.RELEVANCE);
 	        query.setFullTextQuery(keyword);
 	        query.setSafeSearch(YouTubeQuery.SafeSearch.NONE);
 	        VideoFeed videoFeed = service.query(query, VideoFeed.class);
 	        
 	        if (entryPanels != null)
 	        {
 	        	for (JPanel pan : entryPanels)
 	        	{
 	        		remove(pan);
 	        	}
 	        }
 	        
 	        entryPanels = new ArrayList<JPanel>();
 	        
 	        int vidcount = 0;
 	        
 	        for (VideoEntry videoEntry : videoFeed.getEntries())
 	        {
 	        	vidcount++;
 	        	
 	        	if (vidcount >= 5)
 	        	{
 	        		break;
 	        	}
 	        	
 	        	JPanel current = new JPanel();
 	        	current.setPreferredSize(new Dimension(180, 140));
 	        	current.setBackground(Color.DARK_GRAY);
 	        	current.setBorder(BorderFactory.createLineBorder(Color.WHITE));
 	        	entryPanels.add(current);
 	        	
 	        	System.out.println(videoEntry.getTitle().getPlainText());
 	        	System.out.println(videoEntry.isEmbeddable());

 	            YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
 	            
 	            scroller = new JScrollPane();
 	            add(scroller);
 	          
 	            for (MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails())
	        	{
 	            	BufferedImage img = ImageIO.read(new URL(mediaThumbnail.getUrl()));
 	            	JLabel picLabel = new JLabel(new ImageIcon(img));
 	            	current.add(picLabel, BorderLayout.PAGE_START);
 	            	break;
	        	}
 	            
 	            
 	            JLabel title = new JLabel(videoEntry.getTitle().getPlainText());
 	            //title.setEditable(false);
 	            title.setOpaque(false);
 	            title.setBorder(null);
 	            title.setSize(new Dimension(140, 80));
 	            title.setFont(new Font("SansSerif", Font.PLAIN, 10));
 	            title.setForeground(Color.WHITE);
 	            current.add(title, BorderLayout.CENTER);
 	            
 	            for (YouTubeMediaContent mediaContent : mediaGroup.getYouTubeContents())
 	            {
 	            	System.out.println("\t\tMedia Location: "+ mediaContent.getUrl());
 	            	curURL = mediaContent.getUrl();
 	            	break;
 	            }
 	            
 	            add(current);
 	            	            
 	            current.addMouseListener(new MouseAdapter()
 	            {
 	            	public void mouseClicked(MouseEvent e)
 	        	    {
 	        		   if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
 	        		   {
 	        			   try
 	        			   {
 	        				  Desktop.getDesktop().browse(new URI(curURL));
 	        			   }
 	        			   catch(Exception exc)
 	        			   {
 	        			   }
 	        		   }
 	        	    }
 	            });
 	        }
 	        
         }
         catch (Exception e)
         {
         	e.printStackTrace();
         }
    	 
    	 revalidate();
    }
}