package z2;


import javax.swing.*;
import java.awt.*;

public class GFrame extends JFrame{
    private GPanel gPanel;
    private JMenuBar jMenuBar;
    private JMenu gameMenu, optionsMenu, helpMenu;
    private JMenuItem newGameItem, exitItem, imageModeItem, signModeItem, aboutProgramm;

    public  GFrame() {
        gPanel = new GPanel(this);

        add(gPanel);

        UIManager.put("OptionPane.yesButtonText", "��");
        UIManager.put("OptionPane.noButtonText", "���");
        UIManager.put("OptionPane.cancelButtonText", "������");
        UIManager.put("OptionPane.okButtonText", "������");

        jMenuBar = new JMenuBar();

        createJMenus();

        createJMenuItems();

        setJMenuBar(jMenuBar);

        setPreferredSize(new Dimension(gPanel.getGPanelSize(), gPanel.getGPanelSize() + 52));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    private void createJMenus() {
        gameMenu = new JMenu("����");
        optionsMenu = new JMenu("�����");
        helpMenu = new JMenu("������");

        jMenuBar.add(gameMenu);
        jMenuBar.add(optionsMenu);
        jMenuBar.add(helpMenu);
    }

    private void createJMenuItems() {
        newGameItem = new JMenuItem("�����");
        gameMenu.add(newGameItem);

        exitItem = new JMenuItem("�����");
        gameMenu.add(exitItem);

        exitItem.addActionListener(e -> System.exit(0));

        imageModeItem = new JRadioButtonMenuItem("�����������", true);
        optionsMenu.add(imageModeItem);

        signModeItem = new JRadioButtonMenuItem("�����");
        optionsMenu.add(signModeItem);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(imageModeItem);
        buttonGroup.add(signModeItem);

        imageModeItem.addActionListener(e -> {
            gPanel.setGameModeAndRepaint(1);
        });
        signModeItem.addActionListener(e -> {
            gPanel.setGameModeAndRepaint(2);
        });

        newGameItem.addActionListener(e -> {
            gPanel.startNewGame();
            setRadioEnabled(true);
        });

        aboutProgramm = new JMenuItem("� ���������");
        helpMenu.add(aboutProgramm);

        aboutProgramm.addActionListener(e -> showProgramInfo());
    }

    void setRadioEnabled(boolean enabled) {
        imageModeItem.setEnabled(enabled);
        signModeItem.setEnabled(enabled);
    }

    private void showProgramInfo() {
        JOptionPane.showMessageDialog(this, "��������� ������� � ������ ����� \"���������� ����������������\"");
    }
}
