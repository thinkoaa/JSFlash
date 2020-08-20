package burp;


import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class GUI{
	private JPanel contentPane;
	private JCheckBox isOpenCBX;
	private JLabel lbJavaScriptPath;
	private JTextField javaScriptPath;
	private JLabel lburlName;
	private JTextField  url;
	private JButton button;
	private JButton refresh;
	private JButton addBtn;
	private List<ParamExecInfo> paramExecInfoList=new ArrayList<ParamExecInfo>();
	private List<String> urlList  =new ArrayList<String>();;

	public GUI() {
		contentPane = new JPanel();
		lbJavaScriptPath = new JLabel("JS文件的绝对路径：");
		lbJavaScriptPath.setFont(lbJavaScriptPath.getFont().deriveFont(20.0f));
		javaScriptPath = new JTextField(10);
		 
		javaScriptPath.setEditable(false);
		javaScriptPath.setBackground(Color.LIGHT_GRAY);
		button=new JButton("选择文件");
		button.setFont(button.getFont().deriveFont(20.0f));
		refresh=new JButton("重新获取");
		refresh.setFont(refresh.getFont().deriveFont(20.0f));
		
		
		addBtn=new JButton("增加处理参数");
		addBtn.setFont(addBtn.getFont().deriveFont(20.0f));
		
		lburlName = new JLabel("请指定url：");
		lburlName.setFont(lburlName.getFont().deriveFont(20.0f));
		url = new JTextField(10);
		contentPane.add(lbJavaScriptPath);
		contentPane.add(javaScriptPath);
		contentPane.add(lburlName);
		contentPane.add(url);
		contentPane.add(button);
		contentPane.add(refresh);
		contentPane.add(addBtn);
		isOpenCBX=new JCheckBox();
		isOpenCBX.setBounds(240, 50,30, 30);
		contentPane.add(isOpenCBX);
		JLabel jb=new JLabel("是否启用");
		jb.setFont(jb.getFont().deriveFont(20.0f));
		jb.setBounds(270, 50,150, 35);
		contentPane.add(jb);
		contentPane.setLayout(null);
		
		lbJavaScriptPath.setBounds(240, 100, 300, 50);
		javaScriptPath.setBounds(480, 100, 500, 35);
		button.setBounds(1000, 100, 150, 35);
		refresh.setBounds(1250, 100, 150, 35);
		
		lburlName.setBounds(240, 180, 300, 50);
		url.setBounds(480, 180, 850, 35);
		url.setFont(lburlName.getFont().deriveFont(20.0f));
		url.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String urlText=url.getText().trim();
				 String []urlTextArray=urlText.split("\\|");
				 urlList.clear();
				 for(String tmp:urlTextArray) {
					 String str=tmp.replace(":80/", "/");
					  if(str.indexOf("?")!=-1) {
						  str=str.substring(0,str.indexOf("?"));
					   }
					  urlList.add(str);
				 }
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				
				
			}
		});
		
		addBtn.setBounds(1250, 250, 180, 35);
		
		addBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			    //每点击一次，生成一组输入框，位置下移，避免覆盖
				int y=(paramExecInfoList.size()+1)*70;
				int yPos=250+y;
				JTextArea startStr=new JTextArea(15,30);
				startStr.setBounds(480, yPos, 300, 60);
				contentPane.add(startStr);
				 
				JTextArea endStr=new JTextArea(15,30);
				endStr.setBounds(850, yPos, 300, 60);
				contentPane.add(endStr);
				
				JTextField  jsFunName=new JTextField();
				jsFunName.setBounds(1160, yPos, 200, 30);
				contentPane.add(jsFunName);
				SwingUtilities.updateComponentTreeUI(contentPane);
				 
				ParamExecInfo  paramExecInfo=new ParamExecInfo();
				paramExecInfo.setStartStr(startStr);
				paramExecInfo.setEndStr(endStr);
				paramExecInfo.setJsMethodName(jsFunName);
				 
				paramExecInfoList.add(paramExecInfo);
				
				
			}
		});
		
		isOpenCBX.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				BurpExtender.isOpen=isOpenCBX.isSelected();
			}
		});
 
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // 按钮点击事件
				JFileChooser chooser = new JFileChooser(); // 设置选择器
				int returnVal = chooser.showOpenDialog(button); // 是否打开文件选择框
				if (returnVal == JFileChooser.APPROVE_OPTION) { // 如果符合文件类型
					String filepath = chooser.getSelectedFile().getAbsolutePath(); // 获取绝对路径
					javaScriptPath.setText(filepath);
					 try {
						 Utils.initJsEngine(filepath);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(getComponet(), ex.getMessage()," 请检查以下异常信息", 1);
						    return;
						} 
                   
				}
			}
		});
		
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // 按钮点击事件
				 if(javaScriptPath.getText()!=null) {
					 try {
						 Utils.initJsEngine(javaScriptPath.getText());
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(getComponet(), ex.getMessage()," 请检查以下异常信息", 1);
						    return;
						} 
				 } 
			}
		});
//		this.getContentPane().add(contentPane);
//		 this.setVisible(true);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		this.setBounds(130,250,1500,650);
		 BurpExtender.callbacks.customizeUiComponent(contentPane);
	}
    public static void main(String[] args) {
		new GUI();
	}
	
	public Component getComponet() {
		return contentPane;
	}

//	public JTextField getFunctionName() {
//		return functionName;
//	}
//	public JTextField getJavaScriptPath() {
//		return javaScriptPath;
//	}
	
	public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }
	public static void test(String startStr,String endStr) {
		try{
		String content="";
		FileReader fr = new FileReader("C:\\Users\\LXP\\Desktop\\content.txt");
		char[] buf = new char[6];
		int len;
		while ((len = fr.read(buf)) != -1) {
			String str = new String(buf, 0, len);
			content+=str;
		}
		
		fr.close();
		int entPoint=content.indexOf(endStr);
		if(endStr==null||endStr.trim().equals("")) {
			entPoint=content.length();
		} 

		 System.out.println(content.substring(content.indexOf(startStr) + startStr.length(),entPoint).trim());
		}catch (Exception e) {
		 System.out.println();
		}
		
		
	}
	public List<String> getUrlList() {
		return urlList;
	}
	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
	public JTextField getUrl() {
		return url;
	}
	public void setUrl(JTextField url) {
		this.url = url;
	}
	
	public List<ParamExecInfo> getParamExecInfoList() {
		return paramExecInfoList;
	}
	public void setParamExecInfoList(List<ParamExecInfo> paramExecInfoList) {
		this.paramExecInfoList = paramExecInfoList;
	}
	class ParamExecInfo{
		 private JTextArea startStr;
		 private JTextArea endStr;
		 private JTextField jsMethodName;
		 
		public JTextArea getStartStr() {
			return startStr;
		}
		public void setStartStr(JTextArea startStr) {
			this.startStr = startStr;
		}
		public JTextArea getEndStr() {
			return endStr;
		}
		public void setEndStr(JTextArea endStr) {
			this.endStr = endStr;
		}
		public JTextField getJsMethodName() {
			return jsMethodName;
		}
		public void setJsMethodName(JTextField jsMethodName) {
			this.jsMethodName = jsMethodName;
		}
		 
		 
		 
		
	}
	
	
}
