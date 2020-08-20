package burp;

import java.awt.Component;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import burp.GUI.ParamExecInfo;

public class BurpExtender implements IBurpExtender,IHttpListener,ITab {
    public final static String extensionName = "JS Flash";
	public final static String version ="1.0";
	public static IBurpExtenderCallbacks callbacks;
	public static IExtensionHelpers helpers;
	public static PrintWriter stdout;
	public static PrintWriter stderr;
	public static GUI gui;
	public static boolean isOpen=false;
 
	@SuppressWarnings("static-access")
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();
		this.stdout = new PrintWriter(callbacks.getStdout(),true);
		this.stderr = new PrintWriter(callbacks.getStderr(),true);
		
		callbacks.setExtensionName(extensionName+" "+version);
		callbacks.registerHttpListener(this);

		BurpExtender.this.gui = new GUI();
		SwingUtilities.invokeLater(new Runnable()
	      {
	        public void run()
	        {
	          BurpExtender.this.callbacks.addSuiteTab(BurpExtender.this);
	          stdout.println(Utils.getBanner());
	        }
	      });
		
	}
	
	 
	 
 
	@Override
	public String getTabCaption() {
		return extensionName;
	}

	@Override
	public Component getUiComponent() {
		return gui.getComponet();
	}
	
	public static void main(String args[]) {
		 //System.out.println("hello=======");
	}




	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		//如果插件未启用或未指定url，则直接返回
		if (!isOpen||gui.getUrlList().size()==0)return;
		if (messageIsRequest) {//如果是request请求
         IRequestInfo analyzeRequest = helpers
					.analyzeRequest(messageInfo); // 对消息体进行解析
		 String url=analyzeRequest.getUrl().toString().replace(":80/", "/");
		   if(url.indexOf("?")!=-1) {
			   url=url.substring(0,url.indexOf("?"));
		   }
		  
		 if(!gui.getUrlList().contains(url)) {
			 return;
		 }
		  
			String request = new String(messageInfo.getRequest());//得到request字符串
		
			List<ParamExecInfo>   paramExecInfoList=gui.getParamExecInfoList();
			
			 for(ParamExecInfo p:paramExecInfoList) {
				 if(p.getStartStr().getText().trim().equals("")||p.getJsMethodName().getText().trim().equals("")) {
					 continue;
				 }
				 try {
					request=Utils.changeRequest(request, p.getStartStr().getText(), p.getEndStr().getText(), p.getJsMethodName().getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this.getUiComponent(), e.getMessage()," 请检查以下异常信息", 1);
				    return;
				}  
			 }
			 
				  analyzeRequest = helpers
						.analyzeRequest(request.getBytes()); // 对消息体进行解析
				 
				byte[] body = request.substring(
						analyzeRequest.getBodyOffset()).getBytes();
				List<String> headers = analyzeRequest.getHeaders(); 
				   for (int i = headers.size() - 1; i >= 0; i--) {
			            String str = headers.get(i);
			            if (str.startsWith("Content-Length: ")) {
			            	headers.remove(str);
			            	break;
			            }
			        }
				
 	 messageInfo.setRequest(helpers.buildHttpMessage(headers, body));//处理过的新的request
		}
		}
		
	}
