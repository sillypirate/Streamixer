import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BottomPanel extends JPanel
{
    public BottomPanel()
    {
    	Color transparent = new Color(0, true);
        setBackground(transparent);
        add(new Library());
        add(Content.getInstance());
        add(SearchResults.getInstance());
        JScrollPane scroller = new JScrollPane(SearchResults.getInstance(),
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(new Dimension(230, 385));
        add(scroller, BorderLayout.CENTER);
    }
    
  
}