package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.ResourceBundle;

public class QueryPasswordDialog extends JDialog {
    public QueryPasswordDialog(Frame owner) {
        super(owner, true);
        rb = Main.getStringResource();

        JLabel passwordLabel = new JLabel(rb.getString("password:"));
        JLabel confirmPasswordLabel = new JLabel(rb.getString("confirm_password:"));
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        aesButton = new JRadioButton("AES");
        JRadioButton desButton = new JRadioButton("DES");
        JButton okButton = new JButton(rb.getString("ok"));
        JButton cancelButton = new JButton(rb.getString("cancel"));
        ButtonGroup group = new ButtonGroup();
        group.add(aesButton);
        group.add(desButton);
        aesButton.setSelected(true);

        JPanel rootPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rootS = new GridBagConstraints();
        rootS.weightx = 1;
        rootS.fill = GridBagConstraints.HORIZONTAL;
        rootS.gridwidth = GridBagConstraints.REMAINDER;
        final JPanel passwordPanel = new JPanel(new GridBagLayout());
        aesPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        rootPanel.add(passwordPanel, rootS);
        rootPanel.add(aesPanel, rootS);
        rootPanel.add(buttonPanel, rootS);
        JPanel rootPanelContainer = new JPanel(new BorderLayout());
        rootPanelContainer.add(rootPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(rootPanelContainer));

        GridBagConstraints leftS = new GridBagConstraints();
        leftS.weightx = 0;
        leftS.anchor = GridBagConstraints.WEST;
        leftS.insets = new Insets(Tools.HighResolution(20),
                Tools.HighResolution(20),
                0,
                Tools.HighResolution(5));
        GridBagConstraints rightS = new GridBagConstraints();
        rightS.gridwidth = GridBagConstraints.REMAINDER;
        rightS.fill = GridBagConstraints.HORIZONTAL;
        rightS.weightx = 1;
        rightS.insets = new Insets(Tools.HighResolution(20),
                Tools.HighResolution(5),
                0,
                Tools.HighResolution(20));
        passwordPanel.add(passwordLabel, leftS);
        passwordPanel.add(passwordField, rightS);
        passwordPanel.add(confirmPasswordLabel, leftS);
        passwordPanel.add(confirmPasswordField, rightS);

        final GridBagConstraints aesS = new GridBagConstraints();
        aesS.insets = new Insets(Tools.HighResolution(10), Tools.HighResolution(20), 0, Tools.HighResolution(10));
        aesPanel.add(aesButton, aesS);
        aesPanel.add(desButton, aesS);

        GridBagConstraints buttonS = new GridBagConstraints();
        buttonS.insets = new Insets(Tools.HighResolution(15), Tools.HighResolution(10), Tools.HighResolution(15), Tools.HighResolution(20));
        buttonS.anchor = GridBagConstraints.EAST;
        buttonS.weightx = 1;
        buttonPanel.add(okButton, buttonS);
        buttonS.weightx = 0;
        buttonPanel.add(cancelButton, buttonS);

        this.getRootPane().setDefaultButton(okButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryPasswordDialog.this.setVisible(false);
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] password = passwordField.getPassword();
                char[] password2 = confirmPasswordField.getPassword();

                if (!Arrays.equals(password, password2)) {
                    JOptionPane.showMessageDialog(QueryPasswordDialog.this,
                            rb.getString("password_not_match"),
                            rb.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length == 0) {
                    JOptionPane.showMessageDialog(QueryPasswordDialog.this,
                            rb.getString("password_cant_be_blank"),
                            rb.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                returnData = new Data();
                returnData.isAES = aesButton.isSelected();
                returnData.password = new String(password);
                QueryPasswordDialog.this.setVisible(false);

            }
        });
    }

    public void setProperties(boolean isEncryption, String title) {
        aesPanel.setVisible(isEncryption);
        this.setTitle(title);
    }

    public Data getReturnData() {
        returnData = null;
        this.pack();
        Tools.moveToCenter(this, true);
        this.setVisible(true);
        if (returnData == null) {
            return null;
        }
        Data d = new Data();
        d.isAES = returnData.isAES;
        d.password = returnData.password;
        return d;
    }

    private ResourceBundle rb;
    private JPasswordField passwordField, confirmPasswordField;
    private JRadioButton aesButton;
    private JPanel aesPanel;

    private Data returnData;

    public class Data {
        String password;
        boolean isAES;
    }

}