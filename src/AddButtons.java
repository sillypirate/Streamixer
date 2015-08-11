import javax.swing.*;
import javax.swing.filechooser.*;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddButtons extends JPanel
{
    private JButton addFile;
    private JButton addYouTube;
    private JButton newPlaylist;
    private JTextField songName; 
    private JTextField songArtist; 
    private JTextField songAlbum; 
    private JTextField songLocation; 
    private JTextField tf;
    private JFrame dialog;
    
    public AddButtons()
    {
    	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(300, 80));
        //setBackground(Color.BLACK);
        
        ImageIcon imgAddFile = new ImageIcon("image/add.png");
        ImageIcon imgAddYT = new ImageIcon("image/youtube.png");
        ImageIcon imgPlaylist = new ImageIcon("image/playlist.png");
                   
        addFile = new JButton(imgAddFile);
        addYouTube = new JButton(imgAddYT);
        newPlaylist = new JButton(imgPlaylist);
        
        addFile.setToolTipText("Add Media");
        addYouTube.setToolTipText("Add YouTube Video");
        newPlaylist.setToolTipText("Add New Playlist");
        
        addFile.addActionListener(new AddFileHandler());
        newPlaylist.addActionListener(new NewPlaylistHandler());
        add(addFile);
        add(addYouTube);
        add(newPlaylist);
        
        addYouTube.addActionListener(new ActionListener()
        {
        	JFrame frame;
			JPanel panel;
			JTextField songName;
			JTextField url;
			JButton addMe;
			public void actionPerformed(ActionEvent e)
			{
				if(e.getSource() == addYouTube) {
					frame = new JFrame("Add a Song from YouTube");
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					panel = new JPanel();
					songName = new JTextField();
					url = new JTextField();
					addMe = new JButton("Add Song");
					songName.setText("Song name");
					url.setText("Link/URL");
					panel.add(songName);
					panel.add(url);
					panel.add(addMe);
					frame.add(panel);
					frame.setSize(new Dimension(400, 200));
					panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
					frame.setResizable(false);
					frame.setVisible(true);
					//frame.pack();
					frame.getRootPane().setDefaultButton(addMe);
					frame.setLocationRelativeTo(null);
					
					addMe.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							Library.addSong(songName.getText(), " ", " ", url.getText());
							frame.setVisible(false);
						}
					});
				}
			}
        });
    }
    
    private class NewPlaylistHandler implements ActionListener
    {
		public void actionPerformed(ActionEvent e)
		{
			dialog = new JFrame();
			JPanel dialogPanel = new JPanel();
			JLabel label = new JLabel("Playlist name");
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
			dialog.setLocationRelativeTo(null);
			dialog.getRootPane().setDefaultButton(ok);
			
			ok.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (tf.getText().equals(""))
					{
						return;
					}
					
					Library.addPlaylist(tf.getText());
					
					dialog.setVisible(false);
				}
			});
		}
    }
    
    private class AddFileHandler implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		JFileChooser fc = new JFileChooser();
    		
    		try
    		{
    			String userhome = System.getProperty("user.home");
    			fc.setCurrentDirectory(new File(userhome + "\\Music"));
    		}
    		catch (Exception ex)
    		{
    			
    		}
    		
    		FileFilter filter1 = new FileNameExtensionFilter("MP3", "mp3");
    		FileFilter filter2 = new FileNameExtensionFilter("FLAC", "flac");
    		FileFilter filter3 = new FileNameExtensionFilter("AAC", "aac");
    		FileFilter filter4 = new FileNameExtensionFilter("WAV", "wav");
    		fc.addChoosableFileFilter(filter1);
    		fc.addChoosableFileFilter(filter2);
    		fc.addChoosableFileFilter(filter3);
    		fc.addChoosableFileFilter(filter4);
    		
    		int option = fc.showOpenDialog(Content.getInstance());
    		if (option == JFileChooser.APPROVE_OPTION)
    		{
    			String path = fc.getSelectedFile().getAbsolutePath();
    			path = path.replace("\\", "/");
    			path = path.replace(" ", "%20");
    			System.out.println(path);
    			
    			//Frame parentFrame = (Frame)SwingUtilities.windowForComponent(addFile); 
    			dialog = new JFrame();
    			
    			JPanel addPanel = new JPanel();
    			addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.PAGE_AXIS));
    			
    			JLabel songNameLabel = new JLabel("Song name");
    			songName = new JTextField();
    			songName.setPreferredSize(new Dimension(200, 20));
    			addPanel.add(songNameLabel);
    			addPanel.add(songName);
    			
    			JLabel songArtistLabel = new JLabel("Artist");
    			songArtist = new JTextField();
    			songArtist.setPreferredSize(new Dimension(200, 20));
    			addPanel.add(songArtistLabel);
    			addPanel.add(songArtist);
    			
    			JLabel songAlbumLabel = new JLabel("Album");
    			songAlbum = new JTextField();
    			songAlbum.setPreferredSize(new Dimension(200, 20));
    			addPanel.add(songAlbumLabel);
    			addPanel.add(songAlbum);
    			
    			JLabel songLocationLabel = new JLabel("Location");
    			songLocation = new JTextField(path);
    			songLocation.setPreferredSize(new Dimension(200, 20));
    			addPanel.add(songLocationLabel);
    			addPanel.add(songLocation);
    			
    			JButton buttonAdd = new JButton("OK");
    			addPanel.add(buttonAdd);
    			
    			dialog.add(addPanel);
    			dialog.pack();
    			dialog.setVisible(true);
    			dialog.setTitle("Add Song");
    			
    			buttonAdd.addActionListener(new ActionListener()
    			{
					public void actionPerformed(ActionEvent e)
					{
						Library.addSong(songName.getText(), songArtist.getText(), songAlbum.getText(), songLocation.getText());
						dialog.setVisible(false);
					}
    			});
    		}
    	}
    }
}