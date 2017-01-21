package Remap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.iharder.dnd.FileDrop;

public class Map extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		
		JButton b1 = new JButton("Generate map");
		JPanel jp = new JPanel();
		JFrame jf = new JFrame();
		JTextField text = new JTextField(30);
		
		jf.setLayout(new FlowLayout());
		
		jf.setTitle("AutoMapper");
		jf.setVisible(true);
		jf.setSize(400, 200);
		jp.add(text);
		jf.add(jp);
		jf.add(b1, BorderLayout.CENTER);
		jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		new  FileDrop( jp, new FileDrop.Listener()
		  {   public void  filesDropped( java.io.File[] files )
		      {   
		          text.setText(files[0].getAbsolutePath());
		          
		      }   
		  }); 
		
	
		b1.addActionListener(new ActionListener() {
			  @Override
			   public void actionPerformed(ActionEvent evt) {
			        
				  	String path = text.getText();
						
				  	path = path.replace("\"", "");
					path = path.replace("\\", "\\\\");
						
				   	File file = new File(path);
						
					Scanner scan;
					try {
						
						String[] pathPart = path.split("\\[");
						String newPath = pathPart[0] + "[auto map].osu";
						
						PrintWriter writer = new PrintWriter(newPath, "UTF-8");
						
						scan = new Scanner(file, "utf-8");
						Random rand = new Random();
					    	
				    	double time=0;
						int tmpX=0, tmpY=0;
						String temp;
						boolean found = false;
					    	
				    	while(found == false){
							
				    		temp = scan.nextLine();
				    		
				    		if(temp.length() >= 8 && temp.substring(0, 7).equals("Version")){
				    			String[] parts = temp.split(":");
				    			temp = parts[0] + ":auto map";
				    		}
				    		
							writer.println(temp);	
							
							if(temp.equals("[HitObjects]")){
								found = true;
							}
						}
						while(scan.hasNextLine() == true){
							temp = scan.nextLine();
							String result = "";
							String[] parts = temp.split(",");
								
							if(Double.parseDouble(parts[2]) - time > 100 && parts.length <= 6){
								parts[0] = String.valueOf(rand.nextInt(450 + 50));
								parts[1] = String.valueOf(rand.nextInt(250 + 65));
							}
							else if(parts.length <= 6){
								parts[0] = String.valueOf(tmpX);
								parts[1] = String.valueOf(tmpY);
							}
							for(int i=0; i<parts.length-1; i++){
								result += parts[i] + ",";
							}
							result += parts[parts.length-1];
									
							writer.println(result);
							time = Double.parseDouble(parts[2]);
							tmpX = Integer.parseInt(parts[0]);
							tmpY = Integer.parseInt(parts[1]);
									
						}
						writer.close();
						scan.close();
					} catch (FileNotFoundException e) {
						
						System.out.println("File not found.");
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
					
						e.printStackTrace();
					} 
					
			    }
			});
			
		}	
}	
