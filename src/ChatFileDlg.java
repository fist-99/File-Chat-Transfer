import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
 
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;

import org.jnetpcap.PcapIf;

public class ChatFileDlg extends JFrame implements BaseLayer {

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	BaseLayer UnderLayer;
 
	private static LayerManager m_LayerMgr = new LayerManager();
 
	private JTextField ChattingWrite;
 
	Container contentPane;
 
	JTextArea ChattingArea; //ä�� ȭ��
	JTextArea FileArea; //���� �̸� ȭ��
	JTextArea srcMacAddress; //���� Mac �ּ�
	JTextArea dstMacAddress; //���� Mac �ּ�

	JLabel lblsrc;  // Label(�̸�)
	JLabel lbldst;

	JButton Setting_Button; //Port��ȣ(�ּ�)�� �Է¹��� �� �Ϸ��ư����
	JButton Chat_send_Button; //ä��ȭ���� ä�� �Է� �Ϸ� �� data Send��ư
	
	//File GUI
	File selectedFile; //������ ����
	JProgressBar progressBar; //progressBar
	JButton FileFindButton; // ���� ã�� ��ư
	JButton FileTransferButton; // ���� ���� ��ư

	static JComboBox<String> NICComboBox;

	int adapterNumber = 0;

	String Text;

	public static void main(String[] args) {

		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ChatAppLayer("ChatApp"));
		m_LayerMgr.AddLayer(new FileAppLayer("FileApp"));
		m_LayerMgr.AddLayer(new ChatFileDlg("GUI"));

		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ChatApp ( *GUI ) *FileApp ( *GUI ) )");
	}

	public ChatFileDlg(String pName) {
		pLayerName = pName;

		setTitle("Chatting & File Transfer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, 644, 425);
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
 
		JPanel chattingPanel = new JPanel();// chatting panel
		chattingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "chatting",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		chattingPanel.setBounds(10, 5, 360, 276);
		contentPane.add(chattingPanel);
		chattingPanel.setLayout(null);

		JPanel chattingEditorPanel = new JPanel();// chatting write panel
		chattingEditorPanel.setBounds(10, 15, 340, 210);
		chattingPanel.add(chattingEditorPanel);
		chattingEditorPanel.setLayout(null);
 
		ChattingArea = new JTextArea();
		ChattingArea.setEditable(false);
		ChattingArea.setBounds(0, 0, 340, 210);
		chattingEditorPanel.add(ChattingArea);// chatting edit
 
		JPanel chattingInputPanel = new JPanel();// chatting write panel
		chattingInputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		chattingInputPanel.setBounds(10, 230, 250, 20);
		chattingPanel.add(chattingInputPanel);
		chattingInputPanel.setLayout(null);
 
		ChattingWrite = new JTextField();
		ChattingWrite.setBounds(2, 2, 250, 20);// 249
		chattingInputPanel.add(ChattingWrite);
		ChattingWrite.setColumns(10);// writing area
		
		//---------------FIleTransfer GUI----------------
		JPanel FileTransferPanel = new JPanel();// File panel
		FileTransferPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "FileTransfer",
		TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		FileTransferPanel.setBounds(10, 280, 360, 96);
		contentPane.add(FileTransferPanel);
		FileTransferPanel.setLayout(null);
				
		JLabel FileSelectPanel = new JLabel();
		FileSelectPanel.setBounds(10, 25, 270, 23);
		FileTransferPanel.add(FileSelectPanel);
		FileSelectPanel.setLayout(null);
				
		FileArea = new JTextArea();
		FileArea.setEditable(false);
		FileArea.setBounds(0, 0, 270, 23);
		FileSelectPanel.add(FileArea);
				
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(10, 55, 270, 23);
		progressBar.setStringPainted(true);
		FileTransferPanel.add(progressBar);
				
		//-----------------------------------------------
				
		JPanel settingPanel = new JPanel(); //Setting ���� �г�
		settingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "setting",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		settingPanel.setBounds(380, 5, 236, 371);
		contentPane.add(settingPanel);
		settingPanel.setLayout(null);
 
		JPanel sourceAddressPanel = new JPanel();
		sourceAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sourceAddressPanel.setBounds(10, 140, 170, 20);
		settingPanel.add(sourceAddressPanel);
		sourceAddressPanel.setLayout(null);
 
		lblsrc = new JLabel("Source Mac Address");
		lblsrc.setBounds(10, 115, 170, 20); //��ġ ����
		settingPanel.add(lblsrc); //panel �߰�
 
		srcMacAddress = new JTextArea();
		srcMacAddress.setBounds(2, 2, 170, 20); 
		sourceAddressPanel.add(srcMacAddress);// src address
 
		JPanel destinationAddressPanel = new JPanel();
		destinationAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		destinationAddressPanel.setBounds(10, 212, 170, 20);
		settingPanel.add(destinationAddressPanel);
		destinationAddressPanel.setLayout(null);
 
		lbldst = new JLabel("Destination Mac Address");
		lbldst.setBounds(10, 187, 190, 20);
		settingPanel.add(lbldst);
 
		dstMacAddress = new JTextArea();
		dstMacAddress.setBounds(2, 2, 170, 20);
		destinationAddressPanel.add(dstMacAddress);// dst address
 
		JLabel NICLabel = new JLabel("NIC List");
		NICLabel.setBounds(10, 20, 170, 20);
		settingPanel.add(NICLabel);
 
		NICComboBox = new JComboBox();
		NICComboBox.setBounds(10, 49, 170, 20);
		settingPanel.add(NICComboBox);
		
		
		NILayer tempNiLayer = (NILayer) m_LayerMgr.GetLayer("NI"); //�޺��ڽ� ����Ʈ�� �߰��ϱ� ���� �������̽� ��ü
 
		for (int i = 0; i < tempNiLayer.getAdapterList().size(); i++) { //��Ʈ��ũ �������̽��� ����� ��� ����Ʈ�� �����ŭ�� �迭 ����
			//NICComboBox.addItem(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(i).getDescription());
			PcapIf pcapIf = tempNiLayer.GetAdapterObject(i); //
			NICComboBox.addItem(pcapIf.getName()); // NIC ���� â�� ����͸� ������
		}
 
		NICComboBox.addActionListener(new ActionListener() { //combo�ڽ��� ������ ���� ����
 
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//adapterNumber = NICComboBox.getSelectedIndex();
				JComboBox jcombo = (JComboBox) e.getSource();
				adapterNumber = jcombo.getSelectedIndex();
				System.out.println("Index: " + adapterNumber); 
				try {
					srcMacAddress.setText("");
					srcMacAddress.append(get_MacAddress(((NILayer) m_LayerMgr.GetLayer("NI"))
							.GetAdapterObject(adapterNumber).getHardwareAddress()));
 
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
 
		try {// ������ MAC�ּ� ���̰��ϱ�
			srcMacAddress.append(get_MacAddress(
					((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(adapterNumber).getHardwareAddress()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		;
 
		Setting_Button = new JButton("Setting");// setting
		Setting_Button.setBounds(80, 270, 100, 20);
		Setting_Button.addActionListener(new setAddressListener());

		settingPanel.add(Setting_Button);// setting
 
		Chat_send_Button = new JButton("Send");
		Chat_send_Button.setBounds(270, 230, 80, 20);
		Chat_send_Button.addActionListener(new setAddressListener());
		chattingPanel.add(Chat_send_Button);// chatting send button
 
		
		//----------------File Button-----------------
		FileFindButton = new JButton("File..");
		FileFindButton.setBounds(285, 25, 67, 23);
		FileFindButton.addActionListener(new setAddressListener());
		FileFindButton.setEnabled(true);
		FileTransferPanel.add(FileFindButton);
 
		
		FileTransferButton = new JButton("OK");
		FileTransferButton.setBounds(285, 55, 67, 23);
		FileTransferButton.addActionListener(new setAddressListener());
		FileTransferButton.setEnabled(false);
		FileTransferPanel.add(FileTransferButton);
		
		setVisible(true);
 
	}
	
	class setAddressListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
 
			if (e.getSource() == Setting_Button) { //setting ��ư ���� ��
 
				if (Setting_Button.getText() == "Reset") { //reset �������� ���,
					srcMacAddress.setText("");  //�ּ� �������� �ٲ�
					dstMacAddress.setText("");  //�ּ� �������� �ٲ�

					Setting_Button.setText("Setting"); //��ư�� ������, setting���� �ٲ�
					srcMacAddress.setEnabled(true);  //��ư�� Ȱ��ȭ��Ŵ
					dstMacAddress.setEnabled(true);  //��ư�� Ȱ��ȭ��Ŵ
				}  
				else { //�ۼ����ּ� ����
					 
					byte[] srcAddress = new byte[6];
					byte[] dstAddress = new byte[6];
 
					String src = srcMacAddress.getText(); //MAC �ּҸ� String byte�� ��ȯ
					String dst = dstMacAddress.getText();
 
					String[] byte_src = src.split("-"); //Sting MAC �ּҸ�"-"�� ����
					for (int i = 0; i < 6; i++) {
						srcAddress[i] = (byte) Integer.parseInt(byte_src[i], 16); //16��Ʈ (2byte)
					}
 
					String[] byte_dst = dst.split("-");//Sting MAC �ּҸ�"-"�� ����
					for (int i = 0; i < 6; i++) {
						dstAddress[i] = (byte) Integer.parseInt(byte_dst[i], 16);//16비트 (2byte)
					}
 
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetSrcAddress(srcAddress); //�̺κ��� ���� ������ �ּҸ� ���α׷� �� �ҽ��ּҷ� ��밡��
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetDstAddress(dstAddress); //�̺κ��� ���� ������ �ּҸ� ���α׷� �� �������ּҷ� ��밡��
 
					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(adapterNumber);
 
					Setting_Button.setText("Reset"); //setting ��ư ������ �������� �ٲ�
					dstMacAddress.setEnabled(false);  //��ư�� ��Ȱ��ȭ��Ŵ
					srcMacAddress.setEnabled(false);  //��ư�� ��Ȱ��ȭ��Ŵ  
				} 
			}
 
			if (e.getSource() == Chat_send_Button) { //send ��ư ������, 
				if (Setting_Button.getText() == "Reset") { 
					String input = ChattingWrite.getText(); //ä��â�� �Էµ� �ؽ�Ʈ�� ����
					ChattingArea.append("[SEND] : " + input + "\n"); //�����ϸ� �Է°� ���
					byte[] bytes = input.getBytes(); //�Էµ� �޽����� ����Ʈ�� ����
					
					((ChatAppLayer)m_LayerMgr.GetLayer("ChatApp")).Send(bytes, bytes.length);
					//ä��â�� �Էµ� �޽����� chatApplayer�� ����
					ChattingWrite.setText(""); 
					//ä�� �Է¶� �ٽ� �����
				} else {
					JOptionPane.showMessageDialog(null, "Address Setting Error!.");
				}
			}
			
			//-------------------file ��ư ���� -------------------------
			if(e.getSource() == FileFindButton){
				JFileChooser chooser = new JFileChooser();
				int ret = chooser.showOpenDialog(null);
				if(ret == JFileChooser.APPROVE_OPTION){
					progressBar.setValue(0);
					selectedFile = chooser.getSelectedFile();
					FileArea.setText(chooser.getSelectedFile().getPath());
					FileTransferButton.setEnabled(true);
				}
				else{
					selectedFile = null;
					FileArea.setText("");
					FileTransferButton.setEnabled(false);
				}
			}
			
			if(e.getSource() == FileTransferButton){
				//FileAppLayer�� �����ؾ� �� �κ�
				if (Setting_Button.getText() == "Reset") { 
					progressBar.setValue(0);
					((FileAppLayer)m_LayerMgr.GetLayer("FileApp")).setAndStartSendFile();
					//���� ����
				} else {
					JOptionPane.showMessageDialog(null, "Address Setting Error!.");
				}
			}
		}
	}

	public File getFile(){ //FileAppLayer���� ���� ������ ����
		return this.selectedFile;
	}
	
	public String get_MacAddress(byte[] byte_MacAddress) { //MAC Byte�ּҸ� String���� ��ȯ
		String MacAddress = "";
		for (int i = 0; i < 6; i++) { 
			//2�ڸ� 16������ �빮�ڷ�, �׸��� 1�ڸ� 16������ �տ� 0�� ����.
			MacAddress += String.format("%02X%s", byte_MacAddress[i], (i < MacAddress.length() - 1) ? "" : "");
			
			if (i != 5) {
				//2�ڸ� 16���� �ڸ� ���� �ڿ� "-"�ٿ��ֱ�
				MacAddress += "-";
			}
		} 
		System.out.println("mac_address:" + MacAddress);
		return MacAddress;
	}
 
	public boolean Receive(byte[] input) { //�޽��� Receive
		if (input != null) {
			byte[] data = input;   //byte ������ input data
			Text = new String(data); //�Ʒ������� �ö�� �޽����� String text�� ��ȯ����
			ChattingArea.append("[RECV] : " + Text + "\n"); //ä��â�� ���Ÿ޽����� ������
			return false;
		}
		return false ;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}
 
	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}
 
	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}
 
	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}
 
	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}
 
	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
 
	}
 
}