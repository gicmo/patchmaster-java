package de.bio.lmu.patchmaster;

import de.bio.lmu.patchmaster.fields.DoubleField;
import de.bio.lmu.patchmaster.fields.Field;
import de.bio.lmu.patchmaster.fields.StringField;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BundleViewer extends JDialog implements TreeSelectionListener {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTree tree;
    private JTable table;
    private JButton loadFileButton;
    private JLabel lblField;
    private TableModel tableModel;

    public void valueChanged(TreeSelectionEvent e) {
        Record node =  (Record) tree.getLastSelectedPathComponent();
        System.err.print(node);
        table.setModel(new RecordTabelModel(node));
    }

    class RecordTabelModel extends AbstractTableModel {

        final RecordType type;
        final Record record;
        final ArrayList<Field> fields;

        RecordTabelModel(Record record) {
            this.record = record;
            this.type = record.getType();
            this.fields = type.getFields();
        }

        public int getRowCount() {
            return fields.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Field f = fields.get(rowIndex);
            if (columnIndex == 0) {
                return f.name;
            } else {

                if (f instanceof StringField) {
                    return record.decodeString(f.name);
                } else if (f instanceof DoubleField) {
                    double d = record.decodeDouble(f.name);
                    return String.format("%5.5f", d);
                }

                return "";
            }
        }
    }

    public BundleViewer() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        tree.addTreeSelectionListener(this);

        this.loadFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int ret = chooser.showOpenDialog(BundleViewer.this);
                if (ret != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                try {
                    File file = chooser.getSelectedFile();
                    System.err.println(file.getPath());
                    Bundle b = new Bundle(file.getAbsolutePath());
                    b.readHeader();
                    Record r = b.loadTree(".pul");
                    TreeModel model = new DefaultTreeModel(r);
                    tree.setModel(model);

                    lblField.setText(file.getName());

                } catch (IOException ex) {

                }

            }
        });


        pack();
    }

    private void onOK() {
        dispose();
    }

    public static void main(String[] args) {
        BundleViewer dialog = new BundleViewer();

        dialog.setVisible(true);

        System.exit(0);
    }
}
