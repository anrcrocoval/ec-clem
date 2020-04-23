package plugins.fr.univ_nantes.ec_clem;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileList extends JPanel implements ListSelectionListener {
    private JList<File> list;
    private DefaultListModel<File> listModel;

    private static final String addString = "Add";
    private static final String deleteString = "Delete";
    private JButton addButton;
    private JButton deleteButton;

    public FileList() {
        super(new BorderLayout());
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        addButton = new JButton(addString);
        addButton.setActionCommand(addString);
        addButton.addActionListener(new AddListener());
        addButton.setEnabled(true);

        deleteButton = new JButton(deleteString);
        deleteButton.setActionCommand(deleteString);
        deleteButton.addActionListener(new DeleteListener());
        deleteButton.setEnabled(false);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(deleteButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(addButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            listModel.remove(index);
            int size = listModel.getSize();
            if (size == 0) {
                deleteButton.setEnabled(false);
            } else {
                if (index == listModel.getSize()) {
                    index--;
                }
                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
    }

    class AddListener implements ActionListener {
        private boolean alreadyEnabled = true;

        public void actionPerformed(ActionEvent e) {
           int index = list.getSelectedIndex();
            if (index == -1) {
                index = 0;
            } else {
                index++;
            }
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setMultiSelectionEnabled(true);
            int r = jFileChooser.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION) {
                for(int i = 0; i < jFileChooser.getSelectedFiles().length; i++) {
                    listModel.insertElementAt(jFileChooser.getSelectedFiles()[i], index + i);
                    list.setSelectedIndex(index + i);
                    list.ensureIndexIsVisible(index + i);
                }
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (list.getSelectedIndex() == -1) {
                deleteButton.setEnabled(false);
            } else {
                deleteButton.setEnabled(true);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled && listModel.getSize() > 0);
    }

    public List<File> getFiles() {
        List<File> files = new LinkedList<>();
        for(int i = 0; i < listModel.getSize(); i++) {
            files.add(listModel.get(i));
        }
        return files;
    }
}
