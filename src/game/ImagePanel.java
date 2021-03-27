package game;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private static ImageIcon[] diceIcons = new ImageIcon[6];
    private ImageIcon icon;
    private JLabel l;

    static {
        for (int i = 0; i < 6; i++) {
            ImageIcon imageIcon = new ImageIcon("die" + (i + 1) + ".png");
            diceIcons[i] = scaleImage(imageIcon, 0.5);
        }
    }

    public ImagePanel(int imgId) {
        this.icon = diceIcons[imgId - 1];
        l = new JLabel(this.icon);
        add(l);

        Image img = this.icon.getImage();
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        System.out.println("initialization: setting size to " + size.toString());
    }


    public void setImage(int imgId) {
        this.icon = diceIcons[imgId - 1];
        l.setIcon(this.icon);
        repaint();
    }


    public static ImageIcon scaleImage(ImageIcon imageIcon, double factor) {
        Image img = imageIcon.getImage();
        int height = imageIcon.getIconHeight();
        int width = imageIcon.getIconWidth();
        int newHeight = (int) (height * factor);
        int newWidth = (int) (width * factor);
        System.out.println("scaleImage: new size is  " + newWidth + ", " + newHeight);
        Image resultingImage = img.getScaledInstance(newWidth, newHeight,
                Image.SCALE_DEFAULT);
        return new ImageIcon(resultingImage);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        l.repaint();
    }
}
