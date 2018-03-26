package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import static java.awt.Color.*;

public class MainFrame extends JFrame {
    MainFrame() {
        rb = Main.getStringResource();
        this.setIconImage(new ImageIcon(getClass().getResource("/image/icon.png")).getImage());
        // 关闭窗口
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!isDoing) {
                    int a = JOptionPane.showConfirmDialog(null,
                            MessageFormat.format(rb.getString("confirm_"), rb.getString("exit_program")),
                            rb.getString("confirm"),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (a == JOptionPane.OK_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });


        //ResourceBundle.get

        // 窗口参数
        this.setSize(Tools.HighResolution(586), Tools.HighResolution(300));
        this.setTitle(rb.getString("appName"));
        this.setResizable(true);

        //窗口位置
        Tools.moveToCenter(this, true);
        this.add(new MyPanel());
    }

    private ResourceBundle rb;

    /**
     * 正在加解密标识
     */
    private boolean isDoing = false;

    private JButton encryptButton, decryptButton, importButton, removeButton, clearButton, refreshButton;
    private JTable table;

    private class MyPanel extends JPanel implements ActionListener {

        MyPanel() {
            super();
            this.setBackground(Color.white);
            encryptButton = new JButton(rb.getString("encrypt_file"));
            decryptButton = new JButton(rb.getString("decrypt_file"));
            importButton = new JButton(rb.getString("import"));
            removeButton = new JButton(rb.getString("remove"));
            clearButton = new JButton(rb.getString("clear_all"));
            refreshButton = new JButton(rb.getString("refresh"));
            table = new JTable();
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            Tools.setIcon(getClass().getResource("/image/lock.png"), encryptButton);
            Tools.setIcon(getClass().getResource("/image/unlock.png"), decryptButton);
            Tools.setIcon(getClass().getResource("/image/import.png"), importButton);
            Tools.setIcon(getClass().getResource("/image/remove.png"), removeButton);
            Tools.setIcon(getClass().getResource("/image/clear.png"), clearButton);
            Tools.setIcon(getClass().getResource("/image/refresh.png"), refreshButton);

            this.setLayout(new BorderLayout());
            JPanel topPanel = new JPanel(new BorderLayout());
            this.add(topPanel, BorderLayout.SOUTH);
            this.add(scrollPane, BorderLayout.CENTER);
            JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            topPanel.add(topLeftPanel, BorderLayout.WEST);
            topPanel.add(topRightPanel, BorderLayout.EAST);
            topLeftPanel.add(importButton);
            topLeftPanel.add(encryptButton);
            topLeftPanel.add(decryptButton);
            topRightPanel.add(refreshButton);
            topRightPanel.add(removeButton);
            topRightPanel.add(clearButton);
            table.setBackground(Color.white);

            importButton.addActionListener(this);
            clearButton.addActionListener(this);
            removeButton.addActionListener(this);
            encryptButton.addActionListener(this);
            decryptButton.addActionListener(this);
            refreshButton.addActionListener(this);

            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            table.setModel(model);
            JTableHeader header = table.getTableHeader();
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    int colu = table.convertColumnIndexToModel(column);
                    String result = "";
                    switch (colu) {
                        case 0:
                            result = rb.getString("id");
                            break;
                        case 1:
                            result = rb.getString("status");
                            break;
                        case 2:
                            result = rb.getString("file_name");
                            break;
                        case 3:
                            result = rb.getString("path");
                            break;
                    }
                    return super.getTableCellRendererComponent(table, result, isSelected, hasFocus, row, column);
                }
            });
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    int colu = table.convertColumnIndexToModel(column);
                    int ro = table.convertRowIndexToModel(row);
                    if (colu == 1) {
                        OneFile.Status s = data.get(ro).status;
                        Color c;
                        switch (s) {
                            case ERROR:
                                c = red;
                                break;
                            case NORMAL:
                                c = gray;
                                break;
                            case DES:
                            case AES:
                                c = new Color(0x008000);
                                break;
                            case PROCESSING:
                                c = new Color(0x000080);
                                break;
                            default:
                                c = black;
                        }
                        this.setForeground(c);

                    } else {
                        this.setForeground(black);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumnModel tableColumnModel = table.getColumnModel();
//            tableColumnModel.getColumn(0).setPreferredWidth(Tools.HighResolution(40));
//            tableColumnModel.getColumn(1).setPreferredWidth(Tools.HighResolution(100));
//            tableColumnModel.getColumn(2).setPreferredWidth(Tools.HighResolution(198));
//            tableColumnModel.getColumn(3).setPreferredWidth(Tools.HighResolution(198));
            tableColumnModel.getColumn(0).setMinWidth(Tools.HighResolution(20));
            tableColumnModel.getColumn(1).setMinWidth(Tools.HighResolution(20));
            tableColumnModel.getColumn(2).setMinWidth(Tools.HighResolution(50));
            tableColumnModel.getColumn(3).setMinWidth(Tools.HighResolution(50));
            table.setRowHeight(Tools.HighResolution(table.getRowHeight()));
            table.setAutoCreateRowSorter(true);
            table.setDragEnabled(true);
            table.setDropMode(DropMode.INSERT_ROWS);
            table.setFillsViewportHeight(true);
            table.setTransferHandler(new TransferHandler() {
                @Override
                public boolean canImport(TransferSupport support) {
                    DataFlavor[] flavors = support.getDataFlavors();
                    if (flavors.length > 0) {
                        for (DataFlavor i : flavors) {
                            if (i.isFlavorJavaFileListType()) {
                                support.setDropAction(TransferHandler.LINK);
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean importData(TransferSupport support) {
                    try {
                        DropLocation location = support.getDropLocation();
                        int dropRow = -1;
                        if (location instanceof JTable.DropLocation) {
                            dropRow = ((JTable.DropLocation) location).getRow();
                        }
                        boolean add = false;
                        List c = (List) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        for (Object i : c) {
                            if (i instanceof File) {
                                File f = (File) i;
                                if (f.isFile() && !OneFile.hasFile(f, data)) {
                                    OneFile newFile = new OneFile(f);
                                    if (dropRow < 0) {
                                        data.add(newFile);
                                    } else {
                                        data.add(dropRow, newFile);
                                        dropRow++;
                                    }
                                    add = true;
                                }
                            }
                        }
                        if (add) {
                            model.fireTableDataChanged();
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                    return super.importData(support);
                }
            });

            Tools.setIcon(getClass().getResource("/image/lock.png"), encryptItem);
            Tools.setIcon(getClass().getResource("/image/unlock.png"), decryptItem);
            Tools.setIcon(getClass().getResource("/image/remove.png"), removeItem);

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                menu.add(openPathItem);
                openPathItem.addActionListener(this);
            }
            menu.add(encryptItem);
            menu.add(decryptItem);
            menu.add(removeItem);

            encryptItem.addActionListener(this);
            decryptItem.addActionListener(this);
            removeItem.addActionListener(this);

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3 && !isDoing) {
                        int rowIndex = table.rowAtPoint(e.getPoint());
                        if (rowIndex != -1) {
                            int[] rows = table.getSelectedRows();
                            boolean flag = false;
                            for (int i : rows) {
                                if (i == rowIndex) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                table.setRowSelectionInterval(rowIndex, rowIndex);
                            }
                            rightClickedFileName = data.get(
                                    table.convertRowIndexToModel(rowIndex)).canonicalPath;
                            menu.show(table, e.getX(), e.getY());
                        }
                    }
                }
            });

        }

        String rightClickedFileName = null;

        JPopupMenu menu = new JPopupMenu();
        JMenuItem openPathItem = new JMenuItem(rb.getString("open_path")),
                encryptItem = new JMenuItem(rb.getString("encrypt_file")),
                decryptItem = new JMenuItem(rb.getString("decrypt_file")),
                removeItem = new JMenuItem(rb.getString("remove"));

        MyTableModel model = new MyTableModel();
        List<OneFile> data = new ArrayList<>();
        JFileChooser fileChooser = new JFileChooser();
        QueryPasswordDialog dialog = new QueryPasswordDialog(MainFrame.this);

        @Override
        public void actionPerformed(ActionEvent e) {
            Object s = e.getSource();
            if (s == importButton) {
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    for (File f : files) {
                        if (!OneFile.hasFile(f, data)) {
                            OneFile oneFile = new OneFile(f);
                            data.add(oneFile);
                        }
                    }
                    model.fireTableDataChanged();
                }
            }
            if (s == clearButton) {
                data.clear();
                model.fireTableDataChanged();
            }
            if (s == removeButton || s == removeItem) {
                int[] selections = table.getSelectedRows();
                for (int i = 0; i < selections.length; i++) {
                    selections[i] = table.convertRowIndexToModel(selections[i]);
                }
                Arrays.sort(selections);
                for (int i = selections.length - 1; i >= 0; i--) {
                    data.remove(selections[i]);
                }
                model.fireTableDataChanged();
            }
            if (s == refreshButton) {
                refresh(false);
            }
            if (s == encryptButton || s == encryptItem) {
                encryptFiles(true);
            }
            if (s == decryptButton || s == decryptItem) {
                encryptFiles(false);
            }
            if (s == openPathItem) {
                if (rightClickedFileName != null) {
                    Runtime r = Runtime.getRuntime();
                    try {
                        r.exec("explorer /select, " + rightClickedFileName);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        private void refresh(boolean keepSelected) {
            //获取model中被选中的列
            Set<Integer> selectedRows = new HashSet<>();
            for (int i : table.getSelectedRows()) {
                selectedRows.add(table.convertRowIndexToModel(i));
            }

            //将是否选中的状态，转换为与data类似的结构，这样可以与data一起进行remove
            List<Boolean> fileList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                fileList.add(selectedRows.contains(i));
            }

            //遍历data，判断文件是否还存在，如果不存在，就删除，并刷新文件的状态（未加密或是已加密）
            int i = data.size() - 1;
            for (; i >= 0; i--) {
                OneFile f = data.get(i);
                File file = new File(f.canonicalPath);
                if (!file.exists()) {
                    data.remove(i);
                    fileList.remove(i);
                } else {
                    f.status = MyNativeMethods.isFileEncrypted(f.canonicalPath);
                }
            }

            //刷新表的内容
            model.fireTableDataChanged();


            if (keepSelected && data.size() > 0) {
                //先把所有行选中，然后挨个检查是否在上次的记录中记录为不选中，将不选中的操作一下
                table.setRowSelectionInterval(0, data.size() - 1);
                for (i = 0; i < fileList.size(); i++) {
                    if (!fileList.get(i)) {
                        int viewRowId = table.convertRowIndexToView(i);
                        table.removeRowSelectionInterval(viewRowId, viewRowId);
                    }
                }
            }
        }

        private void encryptFiles(final boolean encrypt) {
            if (data.size() == 0) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        rb.getString("please_import_some_files_first"),
                        rb.getString("tips"), JOptionPane.INFORMATION_MESSAGE);
            } else {
                int oldSelectedCount = table.getSelectedRows().length;
                refresh(true);
                final List<OneFile> paths = new ArrayList<>();
                Set<Integer> selectedRows = new HashSet<>();
                if (oldSelectedCount == 0) {
                    for (int i = 0; i < data.size(); i++) {
                        selectedRows.add(i);
                    }
                } else {
                    for (int i : table.getSelectedRows()) {
                        selectedRows.add(table.convertRowIndexToModel(i));
                    }
                }
                for (int i = 0; i < data.size(); i++) {
                    if (selectedRows.contains(i)) {
                        OneFile f = data.get(i);
                        if (encrypt) {
                            if (f.status == OneFile.Status.NORMAL) {
                                paths.add(f);
                            }
                        } else {
                            if (f.status == OneFile.Status.DES || f.status == OneFile.Status.AES) {
                                paths.add(f);
                            }
                        }
                    }
                }

                if (paths.size() == 0) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            MessageFormat.format(rb.getString("no_file_needs_to_be_"), rb.getString(encrypt ? "encrypted" : "decrypted")),
                            rb.getString("tips"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    dialog.setProperties(encrypt, encrypt ? rb.getString("set_password_and_algorithm_for_encryption") : rb.getString("need_password"));
                    final QueryPasswordDialog.Data returnData = dialog.getReturnData();
                    if (returnData != null) {
                        StringBuilder sb = new StringBuilder(MessageFormat.format(rb.getString("confirm_"),
                                (encrypt ? rb.getString("encrypt") : rb.getString("decrypt")) + " " +
                                        rb.getString("these_files")));
                        sb.append(System.lineSeparator());
                        for (OneFile s : paths) {
                            sb.append(s.canonicalPath);
                            sb.append(System.lineSeparator());
                        }
                        int a = JOptionPane.showConfirmDialog(MainFrame.this, sb.toString(), rb.getString("confirm"),
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (a == JOptionPane.OK_OPTION) {
                            enableAll(false);
                            isDoing = true;
                            final boolean isAes = returnData.isAES;
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] pass = new byte[0];
                                    try {
                                        pass = returnData.password.getBytes("UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    final StringBuilder errs = new StringBuilder();
                                    errs.append(System.lineSeparator());
                                    int successCount = 0;
                                    for (OneFile f : paths) {
                                        if (f.status == OneFile.Status.NORMAL && encrypt || !encrypt && (f.status == OneFile.Status.AES || f.status == OneFile.Status.DES)) {
                                            f.status = OneFile.Status.PROCESSING;
                                            try {
                                                SwingUtilities.invokeAndWait(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                model.fireTableDataChanged();
                                                            }
                                                        });
                                            } catch (InterruptedException | InvocationTargetException e) {
                                                e.printStackTrace();
                                            }
                                            String newFile = f.canonicalPath + "." + System.currentTimeMillis();

                                            String message = MyNativeMethods.encryptFile(f.canonicalPath, newFile, pass, isAes, encrypt);
                                            if (message != null) {
                                                f.status = OneFile.Status.ERROR;
                                            } else {
                                                File oldFiles = new File(f.canonicalPath);
                                                File newFiles = new File(newFile);
                                                try {
                                                    if (oldFiles.delete()) {
                                                        if (newFiles.renameTo(oldFiles)) {
                                                            f.status = encrypt ? (isAes ? OneFile.Status.AES : OneFile.Status.DES) : OneFile.Status.NORMAL;
                                                        } else {
                                                            message = MessageFormat.format(rb.getString("failed_to_rename_file_"), newFile);
                                                            f.status = OneFile.Status.ERROR;
                                                        }
                                                    } else {
                                                        message = MessageFormat.format(rb.getString("failed_to_delete_temporary file_"), f.canonicalPath);
                                                        f.status = OneFile.Status.ERROR;
                                                    }
                                                } catch (Exception e) {
                                                    f.status = OneFile.Status.ERROR;
                                                    message = e.getMessage();
                                                }
                                            }
                                            if (message == null) {
                                                successCount++;
                                            } else {
                                                errs.append(f.canonicalPath);
                                                errs.append(' ');
                                                errs.append(message);
                                                errs.append(System.lineSeparator());
                                            }
                                        }

                                    }
                                    errs.append(MessageFormat.format(rb.getString("success_count"), successCount));
                                    try {
                                        SwingUtilities.invokeAndWait(new Runnable() {
                                            @Override
                                            public void run() {
                                                JOptionPane.showMessageDialog(MainFrame.this, errs.toString(), rb.getString("logs"), JOptionPane.INFORMATION_MESSAGE);
                                                enableAll(true);
                                            }
                                        });
                                    } catch (InterruptedException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                    isDoing = false;
                                }
                            });
                            thread.start();
                        }
                    }
                }
            }
        }

        /**
         * 启用或关闭所有控件
         *
         * @param enable 控制
         */
        private void enableAll(boolean enable) {
            table.setEnabled(enable);
            importButton.setEnabled(enable);
            removeButton.setEnabled(enable);
            clearButton.setEnabled(enable);
            refreshButton.setEnabled(enable);
            encryptButton.setEnabled(enable);
            decryptButton.setEnabled(enable);
        }

        private class MyTableModel extends AbstractTableModel {

            @Override
            public int getRowCount() {
                return data.size();
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                String result = "";
                OneFile f = data.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        result = rowIndex + 1 + "";
                        break;
                    case 1:
                        result = f.status.toString();
                        break;
                    case 2:
                        result = f.fileName;
                        break;
                    case 3:
                        result = f.filePath;
                        break;
                }
                return result;
            }
        }

    }

}
