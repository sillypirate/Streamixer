import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel
{
    public TopPanel()
    {
    	Color transparent = new Color(0, true);
        setBackground(transparent);
        add(new AddButtons());
        add(new PlayerControls());
        add(new SearchBar());
    }
}