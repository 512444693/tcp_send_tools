package com.zm.test;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2014/12/25.
 */
public class MyFrame extends JFrame {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        MyFrame frame = new MyFrame();
        //设置观感，"javax.swing.plaf.nimbus.NimbusLookAndFeel"  "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        SwingUtilities.updateComponentTreeUI(frame);
        frame.setVisible(true);


    }
    JTextArea textSend;
    JTextArea textRec;
    JPanel panelRight;

    JPanel panelConf;

    JButton buttonAdd;
    JButton buttonSend;
    JPanel panelOpt;

    JPanel panelLeft;

    JTextField textIP;
    JTextField textPort;
    JPanel panelIP;
    public MyFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        init();

        textSend = new JTextArea("send");
        textRec = new JTextArea("rec");
        panelRight = new JPanel();
        panelRight.setLayout(new GridLayout(2,1,10,10));
        panelRight.add(textSend);
        panelRight.add(textRec);


        panelConf= new JPanel();

        buttonAdd = new JButton("添加数据");
        buttonAdd.addActionListener(new ButtonAddAction());
        buttonSend = new JButton("发送");
        buttonSend.addActionListener(new ButtonSendAction());
        panelOpt = new JPanel();
        panelOpt.add(buttonAdd);
        panelOpt.add(buttonSend);
        panelLeft = new JPanel();
        textIP = new JTextField("127.0.0.1",17);
        textPort = new JTextField("8189",6);
        panelIP = new JPanel();
        panelIP.add(textIP);
        panelIP.add(textPort);

        panelLeft.setLayout(new BorderLayout());
        panelLeft.add(panelConf,BorderLayout.CENTER);
        panelLeft.add(panelOpt,BorderLayout.SOUTH);
        panelLeft.add(panelIP, BorderLayout.NORTH);


        this.setLayout(new GridLayout(1,2,10,10));
        this.add(panelLeft);
        this.add(panelRight);

        panelConf.add(new PanelData(DataType.FOURBYTES,"Protocol","200"));
        panelConf.add(new PanelData(DataType.FOURBYTES,"Sequence","1"));
        panelConf.add(new PanelData(DataType.ONEBYTE,"CmdId","80"));
        panelConf.validate();
    }
    void sendData() throws IllegalArgumentException, UnknownHostException {
        BufferMgr bufferMgr = new BufferMgr();
        Component[] components = panelConf.getComponents();
        for(int i=0; i<components.length; i++)
        {
            JPanel jPanel =(JPanel) components[i];
            Component[] components1 = jPanel.getComponents();
            MyComboBox myComboBox =(MyComboBox) components1[0];
            JTextField jTextField1 = (JTextField) components1[1];
            JTextField jTextField2 = (JTextField) components1[2];
            DataType dataType = dataTypes[myComboBox.getSelectedIndex()];
            String dataName = jTextField1.getText();
            String dataValue = jTextField2.getText();
            bufferMgr.put(new Data(dataType,dataName,dataValue));
        }
        bufferMgr.encode();

        byte[] dataRec = null;
        try {
            if ((dataRec = Tcp.send(textIP.getText(), Integer.parseInt(textPort.getText()), bufferMgr.getBuffer())) != null)
            {
                textSend.setText(ByteUtils.bytes2HexGoodLook(bufferMgr.getBuffer()).toString());
                textRec.setText(ByteUtils.bytes2HexGoodLook(dataRec).toString());
            }
            else
            {
                JOptionPane.showMessageDialog(null,"接收数据失败");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"发送数据出错,服务器可能没启动哦   Ip，端口写对了吗？");
            e.printStackTrace();
        }
    }
    class ButtonSendAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //弹出提示框
            //JOptionPane.showMessageDialog(null,"sorry");
            textSend.setText("");
            textRec.setText("");
            try{
                sendData();
            }catch (Exception e1)
            {
                JOptionPane.showMessageDialog(null,e1.getMessage());
            }

        }
    }
    class ButtonAddAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            panelConf.add(new PanelData());
            panelConf.validate();
        }
    }
    class PanelData extends JPanel{
        private MyComboBox CBDataType;
        private JTextField JTFDataName;
        private JTextField JTFDataValue;
        public PanelData(DataType dataType, String dataName, String dataValue)
        {
            CBDataType = new MyComboBox();
            CBDataType.setSelectedIndex(getDataTypeIndex(dataType));
            JTFDataName = new JTextField(dataName,8);
            JTFDataValue = new JTextField(dataValue,50);
            this.add(CBDataType);
            this.add(JTFDataName);
            this.add(JTFDataValue);
        }
        public PanelData()
        {
            CBDataType = new MyComboBox();
            JTFDataName = new JTextField(8);
            JTFDataValue = new JTextField(50);
            this.add(CBDataType);
            this.add(JTFDataName);
            this.add(JTFDataValue);
        }

        public MyComboBox getCBDataType() {
            return CBDataType;
        }

        public void setCBDataType(MyComboBox CBDataType) {
            this.CBDataType = CBDataType;
        }

        public JTextField getJTFDataName() {
            return JTFDataName;
        }

        public void setJTFDataName(JTextField JTFDataName) {
            this.JTFDataName = JTFDataName;
        }

        public JTextField getJTFDataValue() {
            return JTFDataValue;
        }

        public void setJTFDataValue(JTextField JTFDataValue) {
            this.JTFDataValue = JTFDataValue;
        }
    }
    class MyComboBox extends JComboBox{
        public MyComboBox(){
            for(int i=0; i<dataStringTyes.length; i++)
                addItem(dataStringTyes[i]);
        }
    }

    void init(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        screenHeight= screenSize.height;
        screenWidth= screenSize.width;
        setSize(screenWidth*2 / 3, screenHeight*2/ 3);
        //setLocationByPlatform(true);
        setLocation(screenWidth / 6, screenHeight / 6);
        setTitle("TCP发包工具 v0.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private int screenHeight;
    private int screenWidth;
    private String[] dataStringTyes = {"oneByte","twoBytes","fourBytes",
            "ip","eightBytes","string","hexString",};
    private DataType[] dataTypes = {DataType.ONEBYTE,DataType.TWOBYTES,DataType.FOURBYTES,
            DataType.IP,DataType.EIGHTBYTES,DataType.STRING,DataType.HEXSTRING};
    int getDataTypeIndex(DataType dataType)
    {
        for(int i=0;i<dataTypes.length;i++)
            if(dataTypes[i] == dataType)
                return i;
        return 0;
    }
}
