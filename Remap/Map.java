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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

import net.iharder.dnd.FileDrop;

public class Map extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// max values for x and y
	final static int MAX_X = 530;
	final static int MAX_Y = 310;
	
	static int mode = 2;
	static int count = 0;

	public static void main(String[] args) {

		JButton b1 = new JButton("Generate map");
		JPanel jp = new JPanel();
		JFrame jf = new JFrame();
		JTextField map = new JTextField(30);
		JSlider space = new JSlider(JSlider.HORIZONTAL, 100, 500, 100);
		JLabel text = new JLabel();
		JRadioButton isCustom = new JRadioButton("Set Custom Spacing                                                ");
		JRadioButton isRandom = new JRadioButton("Completly Random                                                  ");
		JRadioButton isInherit = new JRadioButton("Inherit Spacing                                                         ");
		JRadioButton isSquare = new JRadioButton("Only Square Jumps                                                ");
		isRandom.setSelected(true);

		text.setText("Choose Spacing:");
		text.setVisible(false);
		space.setMajorTickSpacing(100);
		space.setMinorTickSpacing(10);
		space.setPaintTicks(true);
		space.setPaintLabels(true);
		space.setVisible(false);

		jf.setLayout(new FlowLayout());

		jf.add(text);
		jf.setTitle("AutoMapper");
		jf.setVisible(true);
		jf.setSize(400, 350);
		jp.add(map);
		jf.add(space);
		jf.add(isCustom);
		jf.add(isRandom);
		jf.add(isInherit);
		jf.add(isSquare);
		jf.add(jp);
		jf.add(b1, BorderLayout.CENTER);
		jf.setResizable(false);
		jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
        ButtonGroup group = new ButtonGroup();
        group.add(isCustom);
        group.add(isRandom);
        group.add(isInherit);
        group.add(isSquare);
       
        //action listeners for radiobuttons
        isCustom.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent evt) {
               
                mode = 1;
                text.setVisible(true);
                space.setVisible(true);
            }
        });
        isRandom.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent evt) {
               
                mode = 2;
                text.setVisible(false);
                space.setVisible(false);
            }
        });
        isInherit.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent evt) {
               
                mode = 3;
                text.setVisible(false);
                space.setVisible(false);
            }
        });
        isSquare.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent evt) {
               
                mode = 4;
                text.setVisible(false);
                space.setVisible(false);
            }
        });

		// FileDrop class for getting file path.
		new FileDrop(jp, new FileDrop.Listener() {

			public void filesDropped(java.io.File[] files) {

				map.setText(files[0].getAbsolutePath());
			}
		});

		// Button is pushed
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				int spacing = space.getValue();

				String path = map.getText();

				Random rand = new Random();

				path = path.replace("\"", "");
				path = path.replace("\\", "\\\\");

				Scanner scan;

				try {

					File file = new File(path);

					String[] pathPart = path.split("\\[");
					String newPath = pathPart[0] + "[auto map].osu";

					// making a new file
					PrintWriter writer = new PrintWriter(newPath, "UTF-8");

					scan = new Scanner(file, "utf-8");

					double time = 0;
					int tmpX = 0, tmpY = 0;
					String[] prevParts = new String[6];
					String temp;
					boolean found = false;

					// Finding the Hitobjects in the file
					while (!found) {

						temp = scan.nextLine();

						// changing name of version
						if (temp.length() >= 8 && temp.substring(0, 7).equals("Version")) {
							String[] parts = temp.split(":");
							temp = parts[0] + ":auto map";
						}

						writer.println(temp);

						if (temp.equals("[HitObjects]")) {
							found = true;
						}
					}
					
					boolean wasStream = true;
					// Changing the x and y values.
					while (scan.hasNextLine()) {
						temp = scan.nextLine();
						
						String result = "";
						String[] parts = temp.split(",");

						// if not stream and not slider
						if (Double.parseDouble(parts[2]) - time > 100 && parts.length <= 6) {
							if (mode == 2) {
								String[] tmpStr = totallyRandom().split(",");
								parts[0] = tmpStr[0];
								parts[1] = tmpStr[1];
							}
							else if(mode == 3){
								int tmpSpace = sameSpacing(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), tmpX, tmpY);
								String[] tmpStr = chooseSpacing(tmpSpace, tmpX, tmpY).split(",");
								parts[0] = tmpStr[0];
								parts[1] = tmpStr[1];
							}
							else if(mode == 4){
								if(count > 0){
									String[] tmpStr = squareJumps(tmpX, tmpY).split(",");
									parts[0] = tmpStr[0];
									parts[1] = tmpStr[1];
								}
							}
							
							// if last object was stream or slider
							else if (prevParts.length > 6 && spacing > 300 || wasStream && spacing > 300) {

								int r = rand.nextInt(2);
								if (r == 0)
									r = -1;
								
								parts[0] = String.valueOf(MAX_X/2 + spacing*r/2);
								parts[1] = String.valueOf(rand.nextInt(MAX_Y));

							} else if(mode == 1){
		
								String[] tmpStr = chooseSpacing(spacing, tmpX, tmpY).split(",");
								parts[0] = tmpStr[0];
								parts[1] = tmpStr[1];
							}
							wasStream = false;
						}
						// if stream and slider or if just stream
						else if (Double.parseDouble(parts[2]) - time <= 100 && parts.length > 6 || parts.length <= 6) {
							parts[0] = String.valueOf(tmpX);
							parts[1] = String.valueOf(tmpY);

							// if it was a stream
							if (parts.length > 6)
								wasStream = false;
							else
								wasStream = true;
						}

						for (int i = 0; i < parts.length - 1; i++) {
							result += parts[i] + ",";
						}
						result += parts[parts.length - 1];

						// print new x and y values
						writer.println(result);

						time = Double.parseDouble(parts[2]);
						tmpX = Integer.parseInt(parts[0]);
						tmpY = Integer.parseInt(parts[1]);
						prevParts = parts;
						
						count++;
						
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

	// returns string of a random value.
	public static String totallyRandom() {

		Random rand = new Random();

		return String.valueOf(rand.nextInt(MAX_X)) + "," + String.valueOf(rand.nextInt(MAX_Y));
	}

	// returns string of x and y values with custom set spacing.
	public static String chooseSpacing(int spacing, int oldX, int oldY) {

		Random rand = new Random();

		// set new x value.
		int nwX = rand.nextInt(2 * spacing) + oldX - spacing;

		// make sure its in range.
		while (nwX < 0 || nwX > MAX_X) {
			if (nwX < 0 || nwX > MAX_X)
				nwX = rand.nextInt(2 * spacing) + oldX - spacing;
		}

		// Start calculating where the new y value should be, relative to the
		// spacing and x value.
		int xLength = nwX - oldX;

		double yLength = xLength * xLength - spacing * spacing;

		int randomY = rand.nextInt(2);
		if (randomY == 0)
			randomY = -1;

		yLength = Math.sqrt(-1 * yLength) * randomY;

		int nwY = (int) yLength + oldY; // done calculating!

		// if the new circle is not in the playfield, return new values.
		if (nwX > 0 && nwX < MAX_X && nwY > 0 && nwY < MAX_Y) {

			return String.valueOf(nwX) + "," + String.valueOf(nwY);

		}
		// else, run method again.
		else {

			return chooseSpacing(spacing, oldX, oldY);
		}
	}
	public static int sameSpacing(int x, int y, int oldX, int oldY) {
		
		int res;
		
		res = (int) Math.sqrt((oldY-y)*(oldY-y) + (oldX-x)*(oldX-x));
		
		return res;
	}

	public static String squareJumps(int oldX, int oldY){
		
		String res;
		int x;
		int y;
		
		if(count % 4 == 1){
			x = oldX - 20;
			y = oldY - 220;
		}
		else if(count % 4 == 2){
			x = oldX - 200;
			y = oldY;
		}
		else if(count % 4 == 3){
			x = oldX;
			y = oldY + 200;
		}
		else{
			x = oldX + 200;
			y = oldY;
		}
		
		if(x>MAX_X || x<0 || y>MAX_Y || y<0){
			
			count = 3;
			x = 200;
			y = 300;
		}
	
		res = x + "," + y;
			
		return res;		
	}
}
