import javax.swing.*;
import javax.swing.event.*;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Library extends JPanel
{   
    public static JList<String> lib;
    
    private JPopupMenu menu;
    private JDialog dialog;
    private JTextField tf;
    
    private static DefaultListModel<String> model;
    private static List allSongs;
    private static List list;
    
    public Library()
    {
        setPreferredSize(new Dimension(200, 385));
        setBackground(Color.BLACK);
        
        model = new DefaultListModel<String>();
        model.addElement("Now Playing");
        model.addElement("Library");
        lib = new JList<String>(model);
        lib.setPreferredSize(new Dimension(190, 375));
        lib.addListSelectionListener(new LibHandler());
        
        lib.addMouseListener(new MouseAdapter()
        {
        	   public void mousePressed(MouseEvent e)
        	   {
        		   if (e.getButton() == MouseEvent.BUTTON3)
        		   {
        			   int newSelectedIndex = lib.locationToIndex(e.getPoint());
        			   lib.setSelectedIndex(newSelectedIndex);
        		   }
        	   }
        });
        
        lib.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "deleteSelected");
        lib.getActionMap().put("deleteSelected", new AbstractAction()
        {
			public void actionPerformed(ActionEvent e)
			{
				if (lib.getSelectedValue().equals("Now Playing") || lib.getSelectedValue().equals("Library"))
				{
					return;
				}
				
				int choice = JOptionPane.showConfirmDialog(null, "Delete playlist?", "Confirm", JOptionPane.YES_NO_OPTION);
    	        
    			if (choice == JOptionPane.YES_OPTION)
    			{
    				list.remove(lib.getSelectedIndex() - 2);
    				model.removeElementAt(lib.getSelectedIndex());
					Library.saveAll();
    	        }
			}
        });
        
        add(lib);
        setRightClick();
        
    	try
    	{
        	SAXBuilder builder = new SAXBuilder();
        	Document document = (Document)builder.build(new File("data/library.xml"));
        	Element rootNode = document.getRootElement();
    		list = rootNode.getChildren("playlist");
    		
    		for (int i = 0; i < list.size(); i++)
    		{
    			Element node = (Element)list.get(i);
    			model.addElement(node.getAttributeValue("id"));
    		}
    		
    		allSongs = rootNode.getChild("library").getChildren();
    	}
    	catch (Exception e)
    	{
    		
    	}
    	
    	lib.revalidate();
    }
    
    public static List getLib()
    {
    	return allSongs;
    }
    
    public static List getList()
    {
    	return list;
    }
    
    public void setRightClick()
    {
    	lib.setComponentPopupMenu(null);
		menu = new JPopupMenu();
		
		JMenuItem item1 = new JMenuItem("Rename playlist");
 		item1.addActionListener(new ActionListener()
        {
 			 public void actionPerformed(ActionEvent evt)
 			 {
 				if (Content.getInstance().getCurrentPlaylist().equals("Library"))
				 {
					 return;
				 }
 				
 				if (Content.getInstance().getCurrentPlaylist().equals("Now Playing"))
				 {
					 return;
				 }
 				
 				dialog = new JDialog();
 				JPanel dialogPanel = new JPanel();
 				JLabel label = new JLabel("Rename playlist");
 				tf = new JTextField();
 				JButton ok = new JButton("OK");
 				tf.setPreferredSize(new Dimension(200, 20));
 				dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
 				dialogPanel.add(label);
 				dialogPanel.add(tf);
 				dialogPanel.add(ok);
 				dialog.add(dialogPanel);
 				dialog.pack();
 				dialog.setVisible(true);
 				
 				ok.addActionListener(new ActionListener()
    			{
  					public void actionPerformed(ActionEvent ev)
  					{
  						if (tf.getText() == "")
  						{
  							return;
  						}
  						
  						Element selectedPlaylist = (Element)list.get(lib.getSelectedIndex() - 2);
  						selectedPlaylist.removeAttribute("id");
  						selectedPlaylist.setAttribute(new Attribute("id", tf.getText()));
 						dialog.setVisible(false);
 						
 						saveAll();
 						model.setElementAt(tf.getText(), lib.getSelectedIndex());
  					}
    			});
 			 }
        });
 		
 		JMenuItem item2 = new JMenuItem("Delete playlist");
 		item2.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent ev)
			{
				if (lib.getSelectedValue().equals("Now Playing") || lib.getSelectedValue().equals("Library"))
				{
					return;
				}
				
				int choice = JOptionPane.showConfirmDialog(null, "Delete playlist?", "Confirm", JOptionPane.YES_NO_OPTION);
    	        
    			if (choice == JOptionPane.YES_OPTION)
    			{
    				list.remove(lib.getSelectedIndex() - 2);
    				model.removeElementAt(lib.getSelectedIndex());
					Library.saveAll();
    	        }
			}
        });
 		
 		menu.add(item1);
 		menu.add(item2);
 		lib.setComponentPopupMenu(menu);
    }
    
    public static void addPlaylist(String newPlaylistName)
    {
		Element playlistToAdd = new Element("playlist");
		playlistToAdd.setAttribute(new Attribute("id", newPlaylistName));
		list.add(playlistToAdd);
		
		model.addElement(newPlaylistName);
		saveAll();
    }
    
    public static void addSong(String name, String artist, String album, String location)
    {
    	Element elem = new Element("song");
    	elem.setAttribute(new Attribute("type", "y"));
    	elem.setAttribute(new Attribute("id", Math.random() + "i"));
    	elem.addContent(new Element("name").setText(name));
    	elem.addContent(new Element("artist").setText(artist));
    	elem.addContent(new Element("album").setText(album));
    	elem.addContent(new Element("url").setText(location));
    	allSongs.add(elem);
    	
    	try
    	{
    		Content.getInstance().showPlaylist("Library");
    	}
    	catch (Exception e)
    	{
    	}
    	
    	saveAll();
    }
   
    public static void saveAll()
    {
    	try
    	{
    		Element root = new Element("playlists");
    		Document doc = new Document();
    		doc.setRootElement(root);
    		
    		Element libElem = new Element("library");
    		root.addContent(libElem);
    		
    		for (int i = 0; i < getLib().size(); i++)
    		{
    			Element libSong = (Element)getLib().get(i);
    			Element cloneLibSong = new Element("song");
    			cloneLibSong.setAttribute(new Attribute("type", libSong.getAttributeValue("type")));
    			cloneLibSong.setAttribute(new Attribute("id", libSong.getAttributeValue("id")));
    			cloneLibSong.addContent(new Element("name").setText(libSong.getChildText("name")));
    			cloneLibSong.addContent(new Element("artist").setText(libSong.getChildText("artist")));
    			cloneLibSong.addContent(new Element("album").setText(libSong.getChildText("album")));
    			cloneLibSong.addContent(new Element("url").setText(libSong.getChildText("url")));
    			libElem.addContent(cloneLibSong);
    		}
    		
    		for (int i = 0; i < getList().size(); i++)
    		{
    			Element currentPlaylist = (Element)getList().get(i);
    			Element clonePlaylist = new Element("playlist");
    			clonePlaylist.setAttribute(new Attribute("id", currentPlaylist.getAttributeValue("id")));
    			
    			List currentSongs = currentPlaylist.getChildren();
    			for (int j = 0; j < currentSongs.size(); j++)
    			{
    				Element currentSong = (Element)currentSongs.get(j);
    				Element cloneSong = new Element("entry");
    				cloneSong.setText(currentSong.getText());
        			clonePlaylist.addContent(cloneSong);
    			}
    			
    			root.addContent(clonePlaylist);
    		}
    		
    		XMLOutputter xmlOutput = new XMLOutputter();
    		xmlOutput.setFormat(Format.getPrettyFormat());
    		xmlOutput.output(doc, new FileWriter("data/library.xml"));
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(Content.getInstance(), "Error saving");
    	}
    }
    
    private class LibHandler implements ListSelectionListener
    {
    	public void valueChanged(ListSelectionEvent e)
    	{
    		if (!e.getValueIsAdjusting())
    		{
    			try
    			{
    				String selected = (String)lib.getSelectedValue();
    				Content.getInstance().showPlaylist(selected);
    			}
    			catch (Exception ex)
    			{
    			}
    		}
    	}
    }
}