import javax.swing.*;
import java.awt.*;

public class RegistrationForm extends JFrame {

    JTextField title, supervisor;
    JTextArea abstractTxt;
    JComboBox<String> type;
    JButton submit;

    public RegistrationForm() {
        setLayout(new GridLayout(5,2));

        title = new JTextField();
        abstractTxt = new JTextArea();
        supervisor = new JTextField();
        type = new JComboBox<>(new String[]{"Oral","Poster"});
        submit = new JButton("Submit");

        add(new JLabel("Title")); add(title);
        add(new JLabel("Abstract")); add(abstractTxt);
        add(new JLabel("Supervisor")); add(supervisor);
        add(new JLabel("Type")); add(type);
        add(submit);

        submit.addActionListener(e -> {
            Student s = new Student("S001","Ali","123");
            s.register(title.getText(),
                       abstractTxt.getText(),
                       supervisor.getText(),
                       type.getSelectedItem().toString());

            new StudentController().saveStudent(s);
            JOptionPane.showMessageDialog(this,"Registered!");
        });

        setSize(400,300);
        setVisible(true);
    }
}
