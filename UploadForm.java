import javax.swing.*;
import java.awt.*;

public class UploadForm extends JFrame {

    JButton choose;

    public UploadForm() {
        choose = new JButton("Choose File");
        add(choose);

        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int r = fc.showOpenDialog(this);
            if(r == JFileChooser.APPROVE_OPTION){
                String path = fc.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this,"Uploaded: " + path);
            }
        });

        setSize(300,100);
        setVisible(true);
    }
}
