import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import javafx.embed.swing.*;
import javafx.scene.*;
import javafx.scene.media.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.awt.*; 
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.net.URI;

//import com.webrenderer.swing.*;

public class Content extends JPanel
{   
	private static Content instance;
	
	private DefaultTableModel model;
	private JList<String> allLists;
    private JTable playlist;
    private JDialog dialog;
    private JDialog listsDialog;
    private Element songClicked;
    //private IBrowserCanvas browser;
    private String currentPlaylist;
    private JFXPanel fxPanel;
    private MediaPlayer mediaPlayer;
    private JPopupMenu menu;
    private boolean eventEnabled = true;
    private JTextField songName;
    private JTextField songArtist;
    private JTextField songAlbum;
    private JTextField songLocation;
    private int nowPlayingIndex;
    private JLabel nowPlayingSong;
    private JLabel nowPlayingArtist;
    private JLabel nowPlayingAlbum;
    private JPanel nowPlayingInfo;
    private boolean dragEnabled = false;
    
    public String showing;
    
    public Content()
    {
    	showing = "";
    	
        setPreferredSize(new Dimension(640, 385));
        //setBackground(Color.BLACK);
        
        model = new DefaultTableModel(new Object[] { "Song", "Artist", "Album" }, 0)
        {
        	public boolean isCellEditable(int row, int column)
            {
        		return false;
            }
        };
        
        playlist = new JTable(model);
        playlist.getSelectionModel().addListSelectionListener(new PlaylistHandler());
        playlist.getColumnModel().getColumn(0).setCellRenderer(new ColRenderer());
        playlist.setPreferredSize(new Dimension(640, 385));
        playlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //playlist.setDragEnabled(true);
        //playlist.setDropMode(DropMode.ON);
        
        playlist.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
        playlist.getActionMap().put("deleteRow", new AbstractAction()
        {
			public void actionPerformed(ActionEvent e)
			{
				if (!currentPlaylist.equals("Library"))
     		    {
     			   return;
     		    }
				
				int choice = JOptionPane.showConfirmDialog(null, "Delete track from library?", "Confirm", JOptionPane.YES_NO_OPTION);
    	        
    			if (choice == JOptionPane.YES_OPTION)
    			{
    				Library.getLib().remove(playlist.getSelectedRow());
    				
    				try
			    	{
			    		Content.getInstance().showPlaylist("Library");
			    	}
			    	catch (Exception ex)
			    	{
			    	}
					
					Library.saveAll();
    	        }
			}
        });
        
        playlist.addMouseListener(new PlaylistMouseAdapter());
        playlist.addMouseMotionListener(new PlaylistMouseAdapter());
        
        add(playlist);
        
        //BrowserFactory.setLicenseData("30dtrial", "0M4RT8DQHHKFH358HAT31M6GFMTBJFMS");
        //browser = BrowserFactory.spawnMozilla();
        //browser.setPreferredSize(640, 385);
        //add((JPanel)browser);
        //((JPanel)browser).setVisible(false);
        
        fxPanel = new JFXPanel();
        //Scene scene = createScene();
        //fxPanel.setScene(scene);
        //String bip = "file:///C:/Users/User/Music/Downloaded/The_Smashing_Pumpkins-Glynis.mp3";
        //Media hit = new Media(bip);
        //mediaPlayer = new MediaPlayer(hit);
        add(fxPanel);
        //add(jj);
        
    }
    
    public void setRightClick(boolean isLibrary)
    {
    	if (isLibrary)
    	{
    		playlist.setComponentPopupMenu(null);
    		menu = new JPopupMenu();
  		  
  		  JMenuItem item1 = new JMenuItem("Edit Track");
  		  item1.addActionListener(new ActionListener()
          {
  			  public void actionPerformed(ActionEvent evt)
  			  {
  				  int row = playlist.getSelectedRow();
    	    	  if (row == -1)
    	    	  {
    	    		  return;
    	    	  }
    	    	  
    	    	  songClicked = (Element)Library.getLib().get(row);
    	         
    	    	  Frame parentFrame = (Frame)SwingUtilities.windowForComponent(playlist); 
     			  dialog = new JDialog(parentFrame);
     			
     			  JPanel editPanel = new JPanel();
     			  editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.PAGE_AXIS));
     			
     			  JLabel songNameLabel = new JLabel("Song name");
     			  songName = new JTextField(songClicked.getChildText("name"));
     			  songName.setPreferredSize(new Dimension(200, 20));
     			  editPanel.add(songNameLabel);
     			 editPanel.add(songName);
     			
     			  JLabel songArtistLabel = new JLabel("Artist");
     			  songArtist = new JTextField(songClicked.getChildText("artist"));
     			  songArtist.setPreferredSize(new Dimension(200, 20));
     			 editPanel.add(songArtistLabel);
     			editPanel.add(songArtist);
     			
     			  JLabel songAlbumLabel = new JLabel("Album");
     			  songAlbum = new JTextField(songClicked.getChildText("album"));
     			  songAlbum.setPreferredSize(new Dimension(200, 20));
     			 editPanel.add(songAlbumLabel);
     			editPanel.add(songAlbum);
     			
     			  JLabel songLocationLabel = new JLabel("Location");
     			  songLocation = new JTextField(songClicked.getChildText("url"));
     			  songLocation.setPreferredSize(new Dimension(200, 20));
     			 editPanel.add(songLocationLabel);
     			editPanel.add(songLocation);
     			
     			JPanel buttons = new JPanel();
     			
     			  JButton buttonEdit = new JButton("Save");
     			 buttons.add(buttonEdit);
     			 
     			 editPanel.add(buttonEdit);
     			 
     			buttonEdit.addActionListener(new ActionListener()
    			{
  					public void actionPerformed(ActionEvent ev)
  					{
  						// edit song
  						songClicked.removeChild("name");
  						songClicked.removeChild("artist");
  						songClicked.removeChild("album");
  						songClicked.removeChild("url");
  						songClicked.addContent(new Element("name").setText(songName.getText()));
  						songClicked.addContent(new Element("artist").setText(songArtist.getText()));
  						songClicked.addContent(new Element("album").setText(songAlbum.getText()));
  						songClicked.addContent(new Element("url").setText(songLocation.getText()));
  						dialog.setVisible(false);
  						
  						try
  				    	{
  				    		Content.getInstance().showPlaylist("Library");
  				    	}
  				    	catch (Exception ex)
  				    	{
  				    	}
  						
  						Library.saveAll();
  					}
    			});
     			
     			
     			  dialog.add(editPanel);
     			  dialog.pack();
     			  dialog.setVisible(true);
     			  dialog.setTitle("Edit Song");
  			  }
          });
  		  
  		  JMenuItem item2 = new JMenuItem("Delete Track");
  		  item2.addActionListener(new ActionListener()
          {
  			  public void actionPerformed(ActionEvent evt)
  			  {
  				  if (playlist.getSelectedRow() == -1)
  				  {
  					  return;
  				  }
  				  
  				  int choice = JOptionPane.showConfirmDialog(null, "Delete track from library?", "Confirm", JOptionPane.YES_NO_OPTION);
  	    	        
  	    			if (choice == JOptionPane.YES_OPTION)
  	    			{
  	    				Library.getLib().remove(playlist.getSelectedRow());
  	    				
  	    				try
  				    	{
  				    		Content.getInstance().showPlaylist("Library");
  				    	}
  				    	catch (Exception ex)
  				    	{
  				    	}
  						
  						Library.saveAll();
  	    	        }
  			  }
          });
  		  
  		  JMenuItem item3 = new JMenuItem("Add to Playlist...");
  		  item3.addActionListener(new ActionListener()
          {
  			  public void actionPerformed(ActionEvent evt)
  			  {
  				  if (playlist.getSelectedRow() == -1)
  				  {
  					  return;
  				  }
  				  
  				  JPanel lists = new JPanel();
  				  lists.setLayout(new BoxLayout(lists, BoxLayout.PAGE_AXIS));
  				  
  				  Frame parentFrame = (Frame)SwingUtilities.windowForComponent(playlist); 
  				  listsDialog = new JDialog(parentFrame);
  				  
  				  DefaultListModel<String> newModel = new DefaultListModel<String>();
  				  allLists = new JList<String>(newModel);
  				  allLists.setSize(new Dimension(150, 500));
  				  
  				  for (int i = 0; i < Library.getList().size(); i++)
  				  {
  					  String curPlaylistName = ((Element)Library.getList().get(i)).getAttributeValue("id");
  					  newModel.addElement(curPlaylistName);
  				  }
  				  
  				  JButton addOK = new JButton("Add");
  				  lists.add(new JLabel("Add to which playlist?"));
  				  lists.add(allLists);
  				  lists.add(addOK);
  				  listsDialog.setTitle("Add song to playlist...");
  				  listsDialog.add(lists);
  				  listsDialog.pack();
  				  listsDialog.setVisible(true);
  				  
  				  addOK.addActionListener(new ActionListener()
  				  {
  					  public void actionPerformed(ActionEvent evvent)
  					  {
  						  if (allLists.getSelectedIndex() != -1)
  						  {
  							  String selectedId = ((Element)Library.getLib().get(playlist.getSelectedRow())).getAttributeValue("id");
  							  Element selectedPlaylist = (Element)(Library.getList().get(allLists.getSelectedIndex()));
  							  selectedPlaylist.addContent(new Element("entry").setText(selectedId));
  							  listsDialog.setVisible(false);
  						  }
  					  }
  				  });
  			  }
          });
  		  
  		  menu.add(item1);
  		  menu.add(item2);
  		  menu.add(item3);
  		  playlist.setComponentPopupMenu(menu);
    	}
    	else
    	{
    		playlist.setComponentPopupMenu(null);
    		
    		menu = new JPopupMenu();
			  
			JMenuItem item = new JMenuItem("Remove from playlist");
			menu.add(item);
			  
			item.addMouseListener(new MouseAdapter()
			{
				  public void mousePressed(MouseEvent evt)
				  {
					  int row = playlist.getSelectedRow();
					  if (row == -1)
					  {
						  return;
					  }
      	    	  
					  for (int i = 0; i < Library.getList().size(); i++)
					  {
						  String curPlaylistName = ((Element)Library.getList().get(i)).getAttributeValue("id");
						  if (currentPlaylist.equals(curPlaylistName))
						  {
							  ((Element)Library.getList().get(i)).getChildren().remove(row);
						  }
					  }
					  
					  try
				      {
				    		Content.getInstance().showPlaylist(currentPlaylist);
				      }
				      catch (Exception ex)
				      {
				      }
						
					  Library.saveAll();
				  }
			  });
			  
			  playlist.setComponentPopupMenu(menu);
		  
    	}
    }
    
    public static Content getInstance()
    {
    	if (instance == null)
    	{
    		instance = new Content();
    	}
    	
    	return instance;
    }
    
    public void showPlaylist(String playlistName)
    {
    	if (playlistName.equals("Library"))
    	{
    		setRightClick(true);
    	}
    	else
    	{
    		setRightClick(false);
    	}
    	
    	if (nowPlayingInfo != null)
    	{
    		try
    		{
    			nowPlayingInfo.setVisible(false);
    			remove(nowPlayingInfo);
    		}
    		catch (Exception e)
    		{
    		}
    	}
    	
    	currentPlaylist = playlistName;
    	eventEnabled = false;
    	
    	int count = model.getRowCount();
    	for (int n = 0; n < count; n++)
    	{
    		model.removeRow(0);
    	}
    	
    	if (playlistName.equals("Now Playing"))
    	{
    		playlist.setVisible(false);
    		
    		if (mediaPlayer != null)
    		{
    			try
    			{
    				String loc = mediaPlayer.getMedia().getSource();
    				
    				for (int i = 0; i < Library.getLib().size(); i++)
    				{
    					if (("file:///" + ((Element)Library.getLib().get(i)).getChildText("url")).equals(loc))
    					{
    						Element el = ((Element)Library.getLib().get(i));
    						nowPlayingSong = new JLabel(el.getChildText("name"));
    	    	    		nowPlayingArtist = new JLabel(el.getChildText("artist"));
    	    	    		nowPlayingAlbum = new JLabel(el.getChildText("album"));
    	    	    		nowPlayingInfo = new JPanel();
    	    	    		
    	    	    		nowPlayingInfo.setLayout(new BoxLayout(nowPlayingInfo, BoxLayout.PAGE_AXIS));
    	    	    		nowPlayingInfo.add(nowPlayingSong);
    	    	    		nowPlayingInfo.add(nowPlayingArtist);
    	    	    		nowPlayingInfo.add(nowPlayingAlbum);
    	    	    		nowPlayingInfo.setVisible(true);
    	    	    		add(nowPlayingInfo, BorderLayout.CENTER);
    	    	    		
    	    	    		break;
    					}
    				}
    				
    	    		
    			}
    			catch (Exception e)
    			{
    			}
    		}
    	}
    	else if (playlistName.equals("Library"))
    	{
    		playlist.setVisible(true);
    		List lib = Library.getLib();
    		
    		for (int i = 0; i < lib.size(); i++)
	    	{
	        	Element song = (Element)lib.get(i);
	        			
	        	Vector<String> row = new Vector<String>();
	        	row.add(song.getChildText("name"));
	        	row.add(song.getChildText("artist"));
	        	row.add(song.getChildText("album"));
	        			
	        	model.addRow(row);
	    	}
    	}
    	else
    	{
    		playlist.setVisible(true);
        	List list = Library.getList();
        	
	    	for (int i = 0; i < list.size(); i++)
	    	{
	    		Element node = (Element)list.get(i);
	    		
	    		if (node.getAttributeValue("id") == playlistName)
	    		{
	    			List children = node.getChildren();
	    			
	    			for (int j = 0; j < children.size(); j++)
	            	{
	        			Element entry = (Element)children.get(j);
	        			String entryNum = entry.getText();
	        			
	        			List lib = Library.getLib();
	        			for (int k = 0; k < lib.size(); k++)
	        			{
	        				if (((Element)lib.get(k)).getAttributeValue("id").equals(entryNum))
	        				{
	        					Element song = (Element)lib.get(k);
	        					Vector<String> row = new Vector<String>();
	    	        			row.add(song.getChildText("name"));
	    	        			row.add(song.getChildText("artist"));
	    	        			row.add(song.getChildText("album"));
	    	        			model.addRow(row);
	        				}
	        			}
	            	}
	    		}
	    	}
	    	
	    	playlist.revalidate();
	    	revalidate();
	    	eventEnabled = true;
    	}
    }
    
    public void togglePlay()
    {
    	if (mediaPlayer == null)
    	{
    		playSelected();
    		return;
    	}
    	
    	if (mediaPlayer.getStatus().toString().equals("PLAYING"))
    	{
    		mediaPlayer.pause();
    	}
    	else if (mediaPlayer.getStatus().toString().equals("PAUSED"))
    	{
    		mediaPlayer.play();
    	}
    	else
    	{
    		playSelected();
    	}
    }
    
    public void stopMedia()
    {
    	if (mediaPlayer != null)
    	{
    		mediaPlayer.stop();
    	}
    }
    
    public void playNext()
    {
    	if (mediaPlayer != null)
    	{
    		mediaPlayer.stop();
    	}
    	
    	try
    	{
    		playlist.setRowSelectionInterval(nowPlayingIndex + 1, nowPlayingIndex + 1);
    	}
    	catch (Exception e)
    	{
    		try
    		{
    			playlist.setRowSelectionInterval(0, 0);
    		}
    		catch (Exception ex)
    		{
    		}
    	}
    	
    	playSelected();
    }
    
    public void playPrevious()
    {
    	mediaPlayer.stop();
    	
    	try
    	{
    		playlist.setRowSelectionInterval(playlist.getSelectedRow() - 1, playlist.getSelectedRow() - 1);
    	}
    	catch (Exception e)
    	{
    		try
    		{
    			playlist.setRowSelectionInterval(0, 0);
    		}
    		catch (Exception ex)
    		{
    		}
    	}
    	
    	playSelected();
    }
    
    public String getCurrentPlaylist()
    {
    	return currentPlaylist;
    }
    
    public void showVideo(String videoURL)
    {
        //browser.loadURL(videoURL);
        
        //browser.loadURL("http://www.youtube.com/v/OQeZ_sMfaBA?version=3&f=videos&c=streamix-player-1&app=youtube_gdata");
        //((JPanel)browser).setVisible(true);
    }
    
    public void hideVideo()
    {
    	//((JPanel)browser).setVisible(false);
    }
    
    public void playSelected()
    {
    	if (playlist.getSelectedRow() == -1)
    	{
    		try
    		{
    			playlist.setRowSelectionInterval(0, 0);
    		}
    		catch (Exception e)
    		{
    			return;
    		}
    	}
    	if (currentPlaylist.equals("Library"))
    	{
    		if (mediaPlayer != null)
			{
				mediaPlayer.stop();
			}
    		
    		try
    		{
    			List lib = Library.getLib();
    			Element child = (Element)lib.get(playlist.getSelectedRow());
    			
    			if (child.getChildText("url").indexOf("http://") != -1)
    			{
    				Desktop.getDesktop().browse(new URI(child.getChildText("url")));
    				return;
    			}
    			if (child.getChildText("url").toLowerCase().indexOf("youtube") != -1)
    			{
    				Desktop.getDesktop().browse(new URI(child.getChildText("url")));
    				return;
    			}
    			if (child.getChildText("url").toLowerCase().indexOf(".com") != -1)
    			{
    				Desktop.getDesktop().browse(new URI(child.getChildText("url")));
    				return;
    			}
    			
    			String loc = "file:///" + child.getChildText("url");
    			Media media = new Media(loc);
    			mediaPlayer = new MediaPlayer(media);
    			nowPlayingIndex = playlist.getSelectedRow();
    			mediaPlayer.play();
    			mediaPlayer.setOnEndOfMedia(new Runnable()
    			{
					public void run()
					{
						playNext();
					}
    			});
    		}
    		catch (Exception e)
    		{
    			JOptionPane.showMessageDialog(this, "Error playing media - please check the song location");
    		}
    	}
    	else
    	{
    		if (mediaPlayer != null)
			{
				mediaPlayer.stop();
			}
    		
	    	List list = Library.getList();
			
			for (int i = 0; i < list.size(); i++)
			{
				Element node = (Element)list.get(i);
				
				if (node.getAttributeValue("id").equals(currentPlaylist))
				{
					Element elem = (Element)node.getChildren().get(playlist.getSelectedRow());
					String entryNum = elem.getText();
					
					try
					{
						if (mediaPlayer != null)
						{
							mediaPlayer.stop();
						}
						
						for (int j = 0; j < Library.getLib().size(); j++)
						{
							Element current = (Element)Library.getLib().get(j);
							
							if (current.getAttributeValue("id").equals(entryNum))
							{
								if (current.getChildText("url").indexOf("http://") != -1)
				    			{
				    				Desktop.getDesktop().browse(new URI(current.getChildText("url")));
				    				return;
				    			}
				    			if (current.getChildText("url").toLowerCase().indexOf("youtube") != -1)
				    			{
				    				Desktop.getDesktop().browse(new URI(current.getChildText("url")));
				    				return;
				    			}
				    			if (current.getChildText("url").toLowerCase().indexOf(".com") != -1)
				    			{
				    				Desktop.getDesktop().browse(new URI(current.getChildText("url")));
				    				return;
				    			}
								
								String loc = "file:///" + current.getChildText("url");
								Media media = new Media(loc);
								mediaPlayer = new MediaPlayer(media);
								mediaPlayer.play();
								nowPlayingIndex = playlist.getSelectedRow();
								
								mediaPlayer.setOnEndOfMedia(new Runnable()
				    			{
									public void run()
									{
										playNext();
									}
				    			});
							}
						}
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(this, "Error playing media - please check the song location");
					}
				}
			}
    	}
    }
    
    private class PlaylistHandler implements ListSelectionListener
    {
    	public void valueChanged(ListSelectionEvent e)
    	{
    		if (!e.getValueIsAdjusting() && eventEnabled)
    		{
    			//playSelected();
    		}
    	}
    }
    
    private class ColRenderer extends DefaultTableCellRenderer
    {
    	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    	{
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(new Font("sansserif", Font.BOLD, 12));
            return this;
    	}
    }
    
    private class PlaylistMouseAdapter extends MouseAdapter
    {
 	   public void mouseClicked(MouseEvent e)
 	   {
 		   if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
 		   {
 			   playSelected();
 		   }
 	   }
 	   
 	   public void mousePressed(MouseEvent e)
 	   {
 		   if (e.getButton() == MouseEvent.BUTTON3)
 		   {
 			   int newSelectedIndex = playlist.rowAtPoint(e.getPoint());
 			   playlist.setRowSelectionInterval(newSelectedIndex, newSelectedIndex);
 		   }
 	   }
 	   
 	   public void mouseDragged(MouseEvent e)
 	   {
 		   dragEnabled = true;
 		   playlist.setEnabled(false);
 		   StreamixPlayer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
 	   }
 	   
 	   public void mouseReleased(MouseEvent e)
 	   {
 		   if (e.getButton() == MouseEvent.BUTTON1)
 		   {
 			   if (dragEnabled)
 			   {
 				   Point p = e.getPoint();
 				   // reconfigure the point to make it relative to lib
 				   p.setLocation(p.getX() + 200, p.getY());
 				   
 				   if (Library.lib.contains(p))
 				   {
 					  int index = Library.lib.locationToIndex(e.getPoint());
 					  System.out.println(index);
 					  
 					  if (index > 1 && playlist.getSelectedRow() != -1)
 					  {
 		  				  String selectedId = ((Element)Library.getLib().get(playlist.getSelectedRow())).getAttributeValue("id");
 		  				  System.out.println(selectedId);
 		  				  Element selectedPlaylist = (Element)(Library.getList().get(index - 2));
 		  				  selectedPlaylist.addContent(new Element("entry").setText(selectedId));
 		  				  Library.lib.setSelectedIndex(index);
 					  }
 				   }
 			   }
 		   }
 		  
 		  StreamixPlayer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
 		  dragEnabled = false;
 		  playlist.setEnabled(true);
 	   }
    }
}