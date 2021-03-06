/*
 * panel/DebugPanel.java
 * This class is to Define the Debug Panel. You can check the status of the machine in this panel.
 * You can get and set data of the Cisc computer, read and write of the memory, read and write files
 * and interact with the Operation Panel
 */

package main.java.panel;

import main.java.Main;
import main.java.common.CiscComputer;
import main.java.common.Display;
import main.java.common.Initializer;
import main.java.instruction.Instruction;
import main.java.instruction.InstructionDecoder;
import main.java.memory.Address;
import main.java.memory.Cache;
import main.java.memory.Memory;
import main.java.memory.Word;
import main.java.register.InstructionRegister;
import main.java.theme.CustomMaterialDesignOceanic;
import main.java.theme.Theme;
import main.java.util.Utils;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static main.java.common.CiscComputer.TOTAL_PIPE_LINE;

/**
 * @author baot
 * @since 9/10/19
 *
 * Class is to represent and operate CISC computer with all necessary attributes
 */

public class DebugPanel extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 720;
    private int user_width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int user_height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private JPanel MainForm;
    private JPanel Controller;
    private JButton autoRunButton;
    private JButton singleRunButton;
    private JButton reloadButton;
    private JButton stopButton;
    private JButton restartButton;
    private JButton program1Button;
    private JButton program2Button;
    private JButton IPLButton;
    private JTextField R0_textField;
    private JTextField R1_textField;
    private JTextField R2_textField;
    private JPanel Memory;
    private JPanel Indicators;
    private JPanel Index;
    private JPanel Registers;
    private JTextField IX1_textField;
    private JTextField IX2_textField;
    private JTextField IX3_textField;
    private JLabel IX1_label;
    private JLabel IX2_label;
    private JLabel IX3_label;
    private JLabel R0_label;
    private JLabel R1_label;
    private JLabel R2_label;
    private JLabel R3_label;
    private JTextField R3_textField;
    private JPanel MemoryRegister;
    private JPanel BasicIndicators;
    private JTextField MAR_textField;
    private JTextField MBR_textField;
    private JLabel MAR_label;
    private JLabel MBR_label;
    private JTextField PC_textField;
    private JTextField IR_textField;
    private JTextField CC_textField;
    private JLabel PC_label;
    private JLabel IR_label;
    private JLabel CC_label;
    private JList MemoryAddressList;
    private JScrollPane MemoryListScroll;
    private JLabel R0_value;
    private JLabel R1_value;
    private JLabel R2_value;
    private JLabel R3_value;
    private JLabel IX1_value;
    private JLabel IX2_value;
    private JLabel IX3_value;
    private JLabel MAR_value;
    private JLabel PC_value;
    private JLabel CC_value;
    private JButton saveButton;
    private JButton pauseButton;
    private JLabel MFR;
    private JTextField MFR_textField;
    private JLabel MFR_value;
    private JButton R0_Button;
    private JButton IX1_Button;
    private JList MemoryValueList;
    private JList MemoryHexValueList;
    private JList MemoryAssembleCodeList;
    private JLabel MemoryAddressLabel;
    private JLabel MemoryValueLabel;
    private JLabel MemoryHexValue;
    private JLabel MemoryAssembleCode;
    private JButton R1_Button;
    private JButton R2_Button;
    private JButton R3_Button;
    private JButton IX2_Button;
    private JButton IX3_Button;
    private JButton MAR_Button;
    private JButton MBR_Button;
    private JButton PC_Button;
    private JButton IR_Button;
    private JButton CC_Button;
    private JButton MFR_Button;
    private JLabel MBR_value;
    private JLabel IR_value;
    private JPanel MemoryFullList;
    private JTabbedPane tabbedPane;
    private JLabel ClockCycleLabel;
    private CiscComputer ciscComputer;
    private InstructionDecoder instructionDecoder = new InstructionDecoder();
    private InstructionRegister instructionRegister;
    private Memory memory;
    private boolean pause = true;
    private String r0, r1, r2, r3, ix1, ix2, ix3, mar, mbr, pc, ir, cc, mfr, clockCycle;
//    private FileSystemView fsv = FileSystemView.getFileSystemView();

    private String memoryAddress[] = new String[Main.MAX_MEMORY_SIZE];
    private String memoryValue[] = new String[Main.MAX_MEMORY_SIZE];
    private String memoryHexValue[] = new String[Main.MAX_MEMORY_SIZE];
    private String memoryAssembleCode[] = new String[Main.MAX_MEMORY_SIZE];

    // main function of debugPanel
    public DebugPanel() {
        // create windows
        super("CISC Machine Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setSize(WIDTH, HEIGHT);
        getContentPane().add(MainForm);
        setLocation((user_width - 800 - WIDTH) / 2, (user_height - HEIGHT) / 2);
        setTheme();
        MemoryListScroll.getVerticalScrollBar().setUnitIncrement(Main.MAX_MEMORY_SIZE / MemoryListScroll.getVerticalScrollBar().getVisibleAmount());
        setVisible(true);

        // Initialize memory lists
        Arrays.fill(memoryValue, "0000000000000000");
        Arrays.fill(memoryHexValue, "x0000");
        Arrays.fill(memoryAssembleCode, "null");
        for (int i = 0; i < memoryAddress.length; i++) {
            memoryAddress[i] = "x" + Utils.autoFill(Utils.decimalToHex(i).toUpperCase(), 4);
        }

        MemoryAddressList.setListData(memoryAddress);
        MemoryValueList.setListData(memoryValue);
        MemoryHexValueList.setListData(memoryHexValue);
        MemoryAssembleCodeList.setListData(memoryAssembleCode);

        // set register = the value in the register_textfield
        R0_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = R0_textField.getText();
                if (Utils.binaryValid(register)) { // check if the input is binary
                    r0 = Utils.autoFill(register, 16); // auto fill 0 at the beginning of the string
                    R0_textField.setText(r0); // refresh textfield
                    R0_value.setText("x" + Utils.binaryToHex(r0)); // convert binary to hex value
                    getData(); // refresh the back end machine
                } else { // invalid input warning
                    System.err.println("R0 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        R1_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = R1_textField.getText();
                if (Utils.binaryValid(register)) {
                    r1 = Utils.autoFill(register, 16);
                    R1_textField.setText(r1);
                    R1_value.setText("x" + Utils.binaryToHex(r1));
                    getData();
                } else {
                    System.err.println("R1 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        R2_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = R2_textField.getText();
                if (Utils.binaryValid(register)) {
                    r2 = Utils.autoFill(register, 16);
                    R2_textField.setText(r2);
                    R2_value.setText("x" + Utils.binaryToHex(r2));
                    getData();
                } else {
                    System.err.println("R2 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        R3_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = R3_textField.getText();
                if (Utils.binaryValid(register)) {
                    r3 = Utils.autoFill(register, 16);
                    R3_textField.setText(r3);
                    R3_value.setText("x" + Utils.binaryToHex(r3));
                    getData();
                } else {
                    System.err.println("R3 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        IX1_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = IX1_textField.getText();
                if (Utils.binaryValid(register)) {
                    ix1 = Utils.autoFill(register, 16);
                    IX1_textField.setText(ix1);
                    IX1_value.setText("x" + Utils.binaryToHex(ix1));
                    getData();
                } else {
                    System.err.println("IX1 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        IX2_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = IX2_textField.getText();
                if (Utils.binaryValid(register)) {
                    ix2 = Utils.autoFill(register, 16);
                    IX2_textField.setText(ix2);
                    IX2_value.setText("x" + Utils.binaryToHex(ix2));
                    getData();
                } else {
                    System.err.println("IX2 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        IX3_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = IX3_textField.getText();
                if (Utils.binaryValid(register)) {
                    ix3 = Utils.autoFill(register, 16);
                    IX3_textField.setText(ix3);
                    IX3_value.setText("x" + Utils.binaryToHex(ix3));
                    getData();
                } else {
                    System.err.println("IX3 is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        PC_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = PC_textField.getText();
                if (Utils.binaryValid(register)) {
                    pc = Utils.autoFill(register, 12);
                    PC_textField.setText(pc);
                    PC_value.setText("x" + Utils.binaryToHex(pc));
                    getData();
                    syncListSelect(Integer.parseInt(pc, 2), true);
                } else {
                    System.err.println("PC is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        IR_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = IR_textField.getText();
                if (Utils.binaryValid(register)) {
                    ir = Utils.autoFill(register, 16);
                    IR_textField.setText(ir);
                    IR_value.setText("x" + Utils.binaryToHex(ir));
                    getData();
                } else {
                    System.err.println("IR is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        MAR_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = MAR_textField.getText();
                if (Utils.binaryValid(register)) {
                    mar = Utils.autoFill(register, 16);
                    MAR_textField.setText(mar);
                    MAR_value.setText("x" + Utils.binaryToHex(mar));
                    getData();
                } else {
                    System.err.println("MAR is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        MBR_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String register = MBR_textField.getText();
                if (Utils.binaryValid(register)) {
                    mbr = Utils.autoFill(register, 16); // format mbr value
                    MBR_textField.setText(mbr); // refresh mbr textfield
                    MBR_value.setText("x" + Utils.binaryToHex(mbr)); // refresh mbr hex label
                    writeMemory(mar, mbr); // set memory
                    mar = Utils.increment(mar); // mar++
                    mbr = Utils.decimalToUnsignedBinary(0); // clean mbr
                    MAR_textField.setText(mar); // refresh mar textfield
                    MAR_value.setText("x" + Utils.binaryToHex(mar)); // refresh mar hex label
                    MBR_textField.setText(mbr); // refresh mbr textfield
                    MBR_value.setText("x" + Utils.binaryToHex(mbr)); // refresh mbr hex label
                    getData(); // refresh the back end machine
                } else {
                    System.err.println("MBR is not binary");
                    JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                restart();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                main.java.panel.Controller.stop();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pause();
            }
        });

        singleRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                singleRun();
            }
        });

        autoRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pause = false;
                autoRun();
            }
        });

        IPLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ipl();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                save();
            }
        });

        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                reload();
            }
        });

        // synchronize four memory list selected index
        MemoryAddressList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                syncListSelect(MemoryAddressList.getSelectedIndex(), false);
            }
        });

        MemoryValueList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                syncListSelect(MemoryValueList.getSelectedIndex(), false);
            }
        });

        MemoryHexValueList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                syncListSelect(MemoryHexValueList.getSelectedIndex(), false);
            }
        });

        MemoryAssembleCodeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                syncListSelect(MemoryAssembleCodeList.getSelectedIndex(), false);
            }
        });

        MemoryValueList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String data;
                if (!MemoryValueList.isSelectionEmpty() && e.getClickCount() == 2) { // when double click
                    int index = MemoryValueList.getSelectedIndex();
                    data = String.valueOf(JOptionPane.showInputDialog("Input binary value"));
                    if (String.valueOf(data) == "null") {

                    } else if (String.valueOf(data).trim().length() == 0){
                        data = "0";
                        setMemory(index, Utils.autoFill(data, 16)); // set memory
                    } else if (!Utils.binaryValid(data)){
                        JOptionPane.showMessageDialog(null, "Input is not binary", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                    } else {
                        setMemory(index, Utils.autoFill(data, 16)); // set memory
                    }

                    getMemory(); // refresh memory list
                    syncListSelect(index, false);
                }
            }
        });

        MemoryHexValueList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String data;
                if (!MemoryHexValueList.isSelectionEmpty() && e.getClickCount() == 2) {
                    int index = MemoryHexValueList.getSelectedIndex();
                    data = String.valueOf(JOptionPane.showInputDialog("Input Hex value"));
                    if (String.valueOf(data) == "null") {

                    } else if (String.valueOf(data).trim().length() == 0){
                        data = "0";
                        setMemory(index, Utils.autoFill(Utils.hexToBinary(data), 16)); // set memory
                    } else if (!Utils.hexValid(data)){
                        JOptionPane.showMessageDialog(null, "Input is not hex", "Incorrect Input", JOptionPane.ERROR_MESSAGE);
                    } else {
                        setMemory(index, Utils.autoFill(Utils.hexToBinary(data), 16)); // set memory
                    }

                    getMemory(); // refresh memory list
                    syncListSelect(index, false);
                }
            }
        });

        program1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Main.busy){
                    JOptionPane.showMessageDialog(null, "Machine is busy", "Machine is busy, please try it later.", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Main.op.sendCommand("Program 1");
                }
            }
        });

        program2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Main.busy){
                    JOptionPane.showMessageDialog(null, "Machine is busy", "Machine is busy, please try it later.", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Main.op.sendCommand("Program 2");
                }
            }
        });
    }

    // write memory to cisc
    private void writeMemory(String mar, String mbr) { // Load MBR to MEM[MAR]
        setMemory(Utils.binaryToDecimal(mar), mbr);
        getMemory();
    }

    // sync the selection in four lists
    private void syncListSelect(int selectedIndex, boolean jump) { // synchronize four memory list selected index
        if (MemoryAddressList.getSelectedIndex() != selectedIndex)
            MemoryAddressList.setSelectedIndex(selectedIndex);
        if (MemoryValueList.getSelectedIndex() != selectedIndex)
            MemoryValueList.setSelectedIndex(selectedIndex);
        if (MemoryHexValueList.getSelectedIndex() != selectedIndex)
            MemoryHexValueList.setSelectedIndex(selectedIndex);
        if (MemoryAssembleCodeList.getSelectedIndex() != selectedIndex)
            MemoryAssembleCodeList.setSelectedIndex(selectedIndex);

        if (jump) {
            // jump the list to the position of selected index
            JScrollBar place = MemoryListScroll.getVerticalScrollBar();
            int place_value = place.getMaximum() * selectedIndex / Main.MAX_MEMORY_SIZE - 50;
            place.setValue((place_value < 0) ? 0 : place_value);
        }
    }

    // get memory data from cisc
    private void getMemory() { // fetch memory and show on the memory lists
        int index;
        String value;
        clearMemory();
        for (Map.Entry<Integer, Word> entry : memory.memoryMap.entrySet()) {
            index = entry.getKey();
            value = entry.getValue().getValue();
            value = value.replace(" ", "");
            if (value.length() != 0) {
                memoryValue[index] = value;
                memoryHexValue[index] = "x" + Utils.binaryToHex(memoryValue[index]);
                memoryAssembleCode[index] = getSymbolicForm(memoryValue[index]);
                MemoryValueList.setListData(memoryValue);
                MemoryHexValueList.setListData(memoryHexValue);
                MemoryAssembleCodeList.setListData(memoryAssembleCode);
            }

        }
    }

    // get single data from memory
    public String getMemoryAt(int index) {
        setData(ciscComputer);
        if (index < Main.MAX_MEMORY_SIZE) {
            return memoryValue[index];
        } else
            return null;
    }

    // set single data to memory
    void setMemory(int key, String value) { // set MEM[key] = value
        if (memory.memoryMap.containsKey(key))
            memory.memoryMap.replace(key, new Word(value));
        else
            memory.memoryMap.put(key, new Word(value));
    }

    // update data to cisc
    public void setData(CiscComputer ciscComputer) { // update front-end from back-end
        this.ciscComputer = ciscComputer;
        memory = ciscComputer.getMemory();
        Display data = new Display(ciscComputer, true);
        r0 = (String.valueOf(data.getR0()) == "null" || data.getR0().length() == 0) ? "0" : data.getR0();
        r1 = (String.valueOf(data.getR1()) == "null" || data.getR1().length() == 0) ? "0" : data.getR1();
        r2 = (String.valueOf(data.getR2()) == "null" || data.getR2().length() == 0) ? "0" : data.getR2();
        r3 = (String.valueOf(data.getR3()) == "null" || data.getR3().length() == 0) ? "0" : data.getR3();
        ix1 = (String.valueOf(data.getIx1()) == "null" || data.getIx1().length() == 0) ? "0" : data.getIx1();
        ix2 = (String.valueOf(data.getIx2()) == "null" || data.getIx2().length() == 0) ? "0" : data.getIx2();
        ix3 = (String.valueOf(data.getIx3()) == "null" || data.getIx3().length() == 0) ? "0" : data.getIx3();
        pc = (String.valueOf(data.getPc()) == "null" || data.getPc().length() == 0) ? "0" : data.getPc();
        ir = (String.valueOf(data.getIr()) == "null" || data.getIr().length() == 0) ? "0" : data.getIr();
        mar = (String.valueOf(data.getMar()) == "null" || data.getMar().length() == 0) ? "0" : data.getMar();
        mbr = (String.valueOf(data.getMbr()) == "null" || data.getMbr().length() == 0) ? "0" : data.getMbr();
        mfr = (String.valueOf((data.getMfr())) == "null" || data.getMfr().length() == 0) ? "0" : data.getMfr();
        cc = (String.valueOf((data.getCc())) == "null" || data.getCc().length() == 0) ? "0" : data.getCc();
        clockCycle = (String.valueOf((data.getClockCycle())) == "null" || data.getClockCycle().length() == 0) ? "0" : data.getClockCycle();

        R0_textField.setText(Utils.autoFill(r0, 16));
        R1_textField.setText(Utils.autoFill(r1, 16));
        R2_textField.setText(Utils.autoFill(r2, 16));
        R3_textField.setText(Utils.autoFill(r3, 16));
        IX1_textField.setText(Utils.autoFill(ix1, 16));
        IX2_textField.setText(Utils.autoFill(ix2, 16));
        IX3_textField.setText(Utils.autoFill(ix3, 16));
        MAR_textField.setText(Utils.autoFill(mar, 16));
        MBR_textField.setText(Utils.autoFill(mbr, 16));
        MFR_textField.setText(Utils.autoFill(mfr, 4));
        PC_textField.setText(Utils.autoFill(pc, 12));
        IR_textField.setText(Utils.autoFill(ir, 16));
        CC_textField.setText(Utils.autoFill(cc, 4));
        ClockCycleLabel.setText("Clock Cycle: " + clockCycle);
        getMemory();
        binaryToHex();
    }

    // get data from cisc
    private void getData() { // update back-end from front-end
        Display data = new Display(this.ciscComputer, true);
        data.setR0(r0);
        data.setR1(r1);
        data.setR2(r2);
        data.setR3(r3);
        data.setIx1(ix1);
        data.setIx2(ix2);
        data.setIx3(ix3);
        data.setMar(mar);
        data.setMbr(mbr);
        data.setMfr(mfr);
        data.setPc(pc);
        data.setIr(ir);
        data.setCc(cc);
        data.setClockCycle(clockCycle);
    }

    // convert binary data to hex data
    private void binaryToHex() { // convert binary code to hex code
        R0_value.setText("x" + Utils.binaryToHex(Utils.autoFill(r0, 16)));
        R1_value.setText("x" + Utils.binaryToHex(Utils.autoFill(r1, 16)));
        R2_value.setText("x" + Utils.binaryToHex(Utils.autoFill(r2, 16)));
        R3_value.setText("x" + Utils.binaryToHex(Utils.autoFill(r3, 16)));
        IX1_value.setText("x" + Utils.binaryToHex(Utils.autoFill(ix1, 16)));
        IX2_value.setText("x" + Utils.binaryToHex(Utils.autoFill(ix2, 16)));
        IX3_value.setText("x" + Utils.binaryToHex(Utils.autoFill(ix3, 16)));
        PC_value.setText("x" + Utils.binaryToHex(Utils.autoFill(pc, 12)));
        CC_value.setText("x" + Utils.binaryToHex(Utils.autoFill(cc, 4)));
        IR_value.setText("x" + Utils.binaryToHex(Utils.autoFill(ir, 16)));
        MAR_value.setText("x" + Utils.binaryToHex(Utils.autoFill(mar, 16)));
        MBR_value.setText("x" + Utils.binaryToHex(Utils.autoFill(mbr, 16)));
        MFR_value.setText("x" + Utils.binaryToHex(Utils.autoFill(mfr, 4)));
    }

    // restart machine
    void restart() {
        pause = true;
        new Memory().clear();  // clear all memory
        new Memory().loadBackupContent(); // load the memory with backup
        clear();
    }

    // stop the machine
    void stop() {
        pause = true;
    }

    // pause the machine
    void pause() {
        pause = true;
    }

    // run the machine one step
    void singleRun() {
        int address = Integer.parseInt(pc, 2); // get current counter
        if (String.valueOf(memoryAssembleCode[address]) == "null") {
            pause = true;
            JOptionPane.showMessageDialog(null, "Not a supported instruction!", "HALT", JOptionPane.ERROR_MESSAGE);
        } else {
            ciscComputer.incrementClockCycle();

            int initialFetchSize = ciscComputer.fetch.size();
            int initialDecodeSize = ciscComputer.decode.size();
            int initialExecuteSize = ciscComputer.execute.size();
            int initialWriteSize = ciscComputer.write.size();
            boolean fetchUsed = false;

            if (ciscComputer.stallClockCycle + TOTAL_PIPE_LINE  == ciscComputer.getClockCycle()) {
                ciscComputer.stall = false;
            }

            if (initialFetchSize < TOTAL_PIPE_LINE && !ciscComputer.stall) {
                ciscComputer.getProgramCounter().setDecimalValue(address + 1); // set pc
                Word word = Cache.getWord(new Address(address));
                ciscComputer.fetch.add(word);
                fetchUsed = true;

                System.out.println("Fetching from Memory Address: " + (address + 1));

                ciscComputer.getInstructionRegister().setBinaryInstruction(word.getValue());
                Instruction instruction = new InstructionDecoder().decode(ciscComputer);

                if (Utils.stallInstructions.contains(instruction.getType())) {
                    ciscComputer.stall = true;
                    ciscComputer.stallClockCycle = ciscComputer.getClockCycle();
                }
            }

            if (initialDecodeSize < TOTAL_PIPE_LINE) {
                if (initialFetchSize > 0) {
                    Word word = ciscComputer.fetch.remove();
                    System.out.println("Decoding: " + word.getValue());
                    ciscComputer.getInstructionRegister().setBinaryInstruction(word.getValue()); // set ir
                    Instruction instruction = new InstructionDecoder().decode(ciscComputer); // decode instruction
                    ciscComputer.decode.add(instruction);
                }
            }

            if (initialExecuteSize < TOTAL_PIPE_LINE) {
                if (initialDecodeSize > 0) {
                    Instruction instruction = ciscComputer.decode.remove();
                    System.out.println("Executing: " + instruction.symbolicForm());
                    ciscComputer.execute.add(instruction);
                }
            }

            if (initialWriteSize < TOTAL_PIPE_LINE) {
                if (initialExecuteSize > 0) {
                    Instruction instruction = ciscComputer.execute.remove();
                    System.out.println("Writing Result: " + instruction.symbolicForm());
                    instruction.getType().getProcessor().process(ciscComputer, instruction); // execute instruction
                    ciscComputer.write.add(instruction);
                }
            }

            if (initialWriteSize > 0) {
                System.out.println("Exit from Pipeline: " + ciscComputer.write.remove().symbolicForm());
            }

            setData(ciscComputer); // update front-end

            if (fetchUsed) {
                syncListSelect(address + 1, true); // memory selected index jump to the pc
            }

            Main.printValues(ciscComputer);

            // HALT when next Value is 0000000000000000
            if ((address == Main.MAX_MEMORY_SIZE - 1) || (memoryValue[address + 1] == "0000000000000000")) {
                pause = true;
                JOptionPane.showMessageDialog(null, "HALT", "HALT", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // run the machine until HALT
    void autoRun() {
        // multi-thread the back-end from main thread (front-end)
        Thread backEnd = new Thread(new BackEnd());
        backEnd.start();
    }

    // read from card
    void ipl() { // import instructions from file
        JFileChooser cardReader = new JFileChooser();
        cardReader.setFileSelectionMode(JFileChooser.FILES_ONLY);
        File dir = new File("");
        cardReader.setCurrentDirectory(dir.getAbsoluteFile());
        cardReader.setFileFilter(new FileNameExtensionFilter("Card Image (*.card)", "card"));
        cardReader.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        cardReader.addChoosableFileFilter(new FileNameExtensionFilter("Memory Dump (*.dump)", "dump"));
        cardReader.addChoosableFileFilter(new FileNameExtensionFilter("Program Disk (*.program)", "program"));
        cardReader.showDialog(new JLabel("Choose File"), "Insert");
        File card = cardReader.getSelectedFile();
        if (card != null){
            Path path = Paths.get(card.getAbsolutePath());
            ciscComputer.getMemory().loadContent(path);
            getMemory();
        }
    }

    // reload the memory file
    void reload() {
        ciscComputer.getMemory().loadContent(null);
        getMemory();
    }

    // save the memory file
    void save() {
        JFileChooser cardWriter = new JFileChooser();
        cardWriter.setFileSelectionMode(JFileChooser.FILES_ONLY);
        File dir = new File("");
        cardWriter.setCurrentDirectory(dir.getAbsoluteFile());
        cardWriter.setFileFilter(new FileNameExtensionFilter("Card Image (*.card)", "card"));
        cardWriter.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        cardWriter.addChoosableFileFilter(new FileNameExtensionFilter("Memory Dump (*.dump)", "dump"));
        cardWriter.addChoosableFileFilter(new FileNameExtensionFilter("Program Disk (*.program)", "program"));
        cardWriter.showDialog(new JLabel("Choose File"), "Burn");
        File newCard = cardWriter.getSelectedFile();
        if (newCard != null){
            Path path = Paths.get(newCard.getAbsolutePath());
            ciscComputer.getMemory().writeContent(path);
            getMemory();
        }
    }

    // convert binary instruction to assemble code
    private String getSymbolicForm(String binaryInstruction) {
        // format input binary code
        if (String.valueOf(binaryInstruction) == "null" || binaryInstruction.trim().toString() == "")
            binaryInstruction = "0";
        Utils.autoFill(binaryInstruction, 16);
        // Initialize instruction decoder
        instructionRegister = ciscComputer.getInstructionRegister();
        instructionRegister.setBinaryInstruction(binaryInstruction);
        Instruction instruction = instructionDecoder.decode(ciscComputer);

        if (instruction == null) {
            return "null";
        }

        return instruction.symbolicForm();
    }

    // reset machine and front-end (except memory)
    protected void clear() {
        ciscComputer = new Initializer().initialize();
        setData(ciscComputer);
    }

    // reset the memory
    private void clearMemory() {
        memoryAddress = new String[Main.MAX_MEMORY_SIZE];
        memoryValue = new String[Main.MAX_MEMORY_SIZE];
        memoryHexValue = new String[Main.MAX_MEMORY_SIZE];
        memoryAssembleCode = new String[Main.MAX_MEMORY_SIZE];

        Arrays.fill(memoryValue, "0000000000000000");
        Arrays.fill(memoryHexValue, "x0000");
        Arrays.fill(memoryAssembleCode, "null");
        for (int i = 0; i < memoryAddress.length; i++) {
            memoryAddress[i] = "x" + Utils.autoFill(Utils.decimalToHex(i).toUpperCase(), 4);
        }
    }

    // multi-thread of back-end
    class BackEnd extends Thread {
        // delay AUTORUN_DELAY ms after execute a singleRun
        @Override
        public void run() {
            while (!pause) {
                singleRun();
                try {
                    Thread.sleep(Main.AUTORUN_DELAY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // set the theme funtion
    public void setTheme() {
        Theme theme = Main.theme;
//        SwingUtilities.updateComponentTreeUI(MainForm);
//        MainForm.repaint();

        // Set JPanels
//        for (int i = 0; i < 100; i++){
            setTitleColor(Registers, theme);
            setTitleColor(Index, theme);
            setTitleColor(MemoryRegister, theme);
            setTitleColor(BasicIndicators, theme);
            setTitleColor(Memory, theme);
            setTitleColor(Controller, theme);
//        }

        // Set JButtons
//        R0_Button.setForeground(theme.getForegroundColorJButton());
//        R0_Button.setBackground(theme.getBackgroundColorJButton());
//        R1_Button.setForeground(theme.getForegroundColorJButton());
//        R1_Button.setBackground(theme.getBackgroundColorJButton());
//        R2_Button.setForeground(theme.getForegroundColorJButton());
//        R2_Button.setBackground(theme.getBackgroundColorJButton());
//        R3_Button.setForeground(theme.getForegroundColorJButton());
//        R3_Button.setBackground(theme.getBackgroundColorJButton());
//        IX1_Button.setForeground(theme.getForegroundColorJButton());
//        IX1_Button.setBackground(theme.getBackgroundColorJButton());
//        IX2_Button.setForeground(theme.getForegroundColorJButton());
//        IX2_Button.setBackground(theme.getBackgroundColorJButton());
//        IX3_Button.setForeground(theme.getForegroundColorJButton());
//        IX3_Button.setBackground(theme.getBackgroundColorJButton());
//        MAR_Button.setForeground(theme.getForegroundColorJButton());
//        MAR_Button.setBackground(theme.getBackgroundColorJButton());
//        MBR_Button.setForeground(theme.getForegroundColorJButton());
//        MBR_Button.setBackground(theme.getBackgroundColorJButton());
//        PC_Button.setForeground(theme.getForegroundColorJButton());
//        PC_Button.setBackground(theme.getBackgroundColorJButton());
//        IR_Button.setForeground(theme.getForegroundColorJButton());
//        IR_Button.setBackground(theme.getBackgroundColorJButton());
//        CC_Button.setForeground(theme.getForegroundColorJButton());
//        CC_Button.setBackground(theme.getBackgroundColorJButton());
//        MFR.setForeground(theme.getForegroundColorJButton());
//        MFR.setBackground(theme.getBackgroundColorJButton());
//        reloadButton.setForeground(theme.getForegroundColorJButton());
//        reloadButton.setBackground(theme.getBackgroundColorJButton());
//        saveButton.setForeground(theme.getForegroundColorJButton());
//        saveButton.setBackground(theme.getBackgroundColorJButton());
//        IPLButton.setForeground(theme.getForegroundColorJButton());
//        IPLButton.setBackground(theme.getBackgroundColorJButton());
//        program1Button.setForeground(theme.getForegroundColorJButton());
//        program1Button.setBackground(theme.getBackgroundColorJButton());
//        program2Button.setForeground(theme.getForegroundColorJButton());
//        program2Button.setBackground(theme.getBackgroundColorJButton());
//        floatingTestButton.setForeground(theme.getForegroundColorJButton());
//        floatingTestButton.setBackground(theme.getBackgroundColorJButton());
//        vectorTestButton.setForeground(theme.getForegroundColorJButton());
//        vectorTestButton.setBackground(theme.getBackgroundColorJButton());
//        autoRunButton.setForeground(theme.getForegroundColorJButton());
//        autoRunButton.setBackground(theme.getBackgroundColorJButton());
//        singleRunButton.setForeground(theme.getForegroundColorJButton());
//        singleRunButton.setBackground(theme.getBackgroundColorJButton());
//        pauseButton.setForeground(theme.getForegroundColorJButton());
//        pauseButton.setBackground(theme.getBackgroundColorJButton());
//        stopButton.setForeground(theme.getForegroundColorJButton());
//        stopButton.setBackground(theme.getBackgroundColorJButton());
//        restartButton.setForeground(theme.getForegroundColorJButton());
//        restartButton.setBackground(theme.getBackgroundColorJButton());

        // Set JLabels


        // Set JTextFields


        // Set JList
    }

    // set the title color
    private void setTitleColor(JPanel panel, Theme theme) {
        if (panel.getBorder() instanceof TitledBorder) {
            TitledBorder tb = (TitledBorder) panel.getBorder();
            if (theme == CustomMaterialDesignOceanic.getTheme())
                panel.setBorder(BorderFactory.createTitledBorder((Border) null, tb.getTitle(), 4, 0, (Font) null, MaterialColors.WHITE));
            else
                panel.setBorder(BorderFactory.createTitledBorder((Border) null, tb.getTitle(), 4, 0, (Font) null, theme.getForeground()));
        }
    }

    // get the cisc
    public CiscComputer getCiscComputer() {
        return this.ciscComputer;
    }

    // read the file
    public void readFromFile() {
        JFileChooser cardReader = new JFileChooser();
        cardReader.setFileSelectionMode(JFileChooser.FILES_ONLY);
        File dir = new File("");
        cardReader.setCurrentDirectory(dir.getAbsoluteFile());
        cardReader.setFileFilter(new FileNameExtensionFilter("Card Image (*.card)", "card"));
        cardReader.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        cardReader.showDialog(new JLabel("Choose File"), "Insert");
        File card = cardReader.getSelectedFile();
        if (card != null){
            Path path = Paths.get(card.getAbsolutePath());
            try {
                //String str = Files.readString(path);
                StringBuilder sb = new StringBuilder(Word.MAX_SIZE * 2018);
                Files.readAllLines(path).forEach(l -> sb.append(l).append("\n"));
                String str = sb.toString();

                int index = 1500;
                for (int i = 0; i < Math.min(500, str.length()); i++){
                    setMemory(index++, Utils.autoFill(Utils.decimalToBinary(str.charAt(i)), 16));
                }
                setMemory(index++, Utils.autoFill("1111111111111111", 16));
                setMemory(7, Utils.autoFill(Utils.decimalToBinary(1500), 16));
            } catch (IOException e) {
                e.printStackTrace();

                System.err.println("Cannot read file");
            }
        }
        Main.IO.countDown();
    }
}
