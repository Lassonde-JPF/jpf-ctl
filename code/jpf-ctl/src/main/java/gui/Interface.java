package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import algo.ModelChecker;
import error.ModelCheckingException;

@SuppressWarnings("serial")
public class Interface extends JFrame {

	private JPanel contentPane;
	private File f;
	private String path;

	public Interface() {

		setSize(600, 400);
		setResizable(false);
		setTitle("jpf-ctl");

		setLocationRelativeTo(null);

		// center the frame
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// ImageIcon img = new ImageIcon("src/main/java/gui/images/jpf-ctl-logo.png");
		// stage.setIconImage(img.getImage()); //super blurry

		// manager = new JPanel();
		// root = new BoxLayout(manager, BoxLayout.Y_AXIS);

		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		// contentPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lbl_title = new JLabel("JPF-CTL Java Model Checker");
		lbl_title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		lbl_title.setAlignmentX(Component.CENTER_ALIGNMENT);
		// lbl_title.setSize(600, 50);

		// enter formula sub component
		JPanel formulaPane = new JPanel();
		formulaPane.setLayout(new GridLayout(5, 2, 10, 10));
		formulaPane.setPreferredSize(new Dimension(this.getWidth(), 200));

		JLabel lbl_formula = new JLabel("Enter Formula:");
		lbl_formula.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		lbl_formula.setAlignmentX(Component.CENTER_ALIGNMENT);
		// lbl_formula.setPreferredSize(new Dimension(300, 50));

		JTextArea ta_formula = new JTextArea();
		// ta_formula.setSize(new Dimension(300,50));
		ta_formula.setBorder(BorderFactory.createLineBorder(Color.black));
		ta_formula.setLineWrap(true);

		JButton btn_choose = new JButton("No class selected");
		btn_choose.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		btn_choose.setAlignmentX(Component.CENTER_ALIGNMENT);

		JCheckBox chk_randomness = new JCheckBox("Consider Randomness");
		chk_randomness.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		chk_randomness.setAlignmentX(Component.CENTER_ALIGNMENT);
		chk_randomness.setSelected(true); // true by default

		JCheckBox chk_package = new JCheckBox("Class Contains Package Declaration");
		chk_randomness.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		chk_randomness.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lbl_file = new JLabel("Choose Class:");
		// lbl_file.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		lbl_file.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

		JLabel lbl_cmd = new JLabel("Command Line Input (optional):");
		// lbl_cmd.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		lbl_cmd.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

		// run function for choose file button
		btn_choose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Java class files (*.class)";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						else
							return f.getName().toLowerCase().endsWith(".class");
					}
				});

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					f = fc.getSelectedFile();
					path = f.toString();
					btn_choose.setText(path);
				}
			}
		});

		formulaPane.add(lbl_formula);
		formulaPane.add(lbl_file); // replace with actual label
		formulaPane.add(ta_formula);
		formulaPane.add(btn_choose);
		formulaPane.add(chk_randomness);
		formulaPane.add(chk_package);
		formulaPane.add(lbl_cmd);

		// left text aligned lbl pane

		JTextArea ta_cmd = new JTextArea();
		// ta_formula.setSize(new Dimension(300,50));
		ta_cmd.setBorder(BorderFactory.createLineBorder(Color.black));
		ta_cmd.setLineWrap(true);

		JPanel runPanel = new JPanel();
		runPanel.setLayout(new GridLayout(1, 2));

		JButton btn_run = new JButton("Run");
		btn_run.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		btn_run.setBackground(new Color(0, 153, 0));

		// run function for run button
		btn_run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			
				//testing
//				outputPane( "Model Checking Finished\n For the selected class:\t" + "c:users/desktop/example.class"
//						+ "\n And the written formula:\t" + "AX true"
//						+ "\nIt has been determined that the formula holds in the initial state and is considered valid for this system.", "");
				
				JFrame frame = new JFrame();
				if (ta_formula.getText().trim().isEmpty()) {
					// joption pane for error
					JOptionPane.showMessageDialog(frame, "Please input a formula");
				} else if (f == null) {
					// joptionpane for error
					JOptionPane.showMessageDialog(frame, "Please select a file");
				} else {
					// get the values and do the run code
					String checked = chk_randomness.isSelected() ? "true" : "false";
					String formula = ta_formula.getText().trim();
					String cmd = ta_cmd.getText().trim();
				
					try {
						boolean result = ModelChecker.validate(formula, path, checked, chk_package.isSelected(), cmd);

						System.out.println("Result: " + result);

						if (result) {
							String msg = "Model Checking Finished\n For the selected class:\t" + path
									+ "\n And the written formula:\t" + formula
									+ "\nIt has been determined that the formula holds in the initial state and is considered valid for this system.";
							JOptionPane.showMessageDialog(frame, msg);
							
						} else {
							String msg = "Model Checking Finished\n For the selected class:\t" + path
									+ "\n And the written formula:\t" + formula
									+ "\nIt has been determined that the formula does not hold in the initial state and is considered invalid for this system."
									+ "\nA counter example can be seen below:";
							JOptionPane.showMessageDialog(frame, msg);
						}

					} catch (ModelCheckingException e1) {
						JOptionPane.showMessageDialog(frame, "There was an error model checking:\n" + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}

		});

		runPanel.add(new Label());
		runPanel.add(btn_run);

		contentPane.add(lbl_title);
		contentPane.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPane.add(formulaPane);
		// contentPane.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPane.add(ta_cmd);
		contentPane.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPane.add(runPanel);
		contentPane.add(Box.createRigidArea(new Dimension(0, 15)));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.add(contentPane);

		setVisible(true);
		// add(contentPane);
	}

	public void outputPane(String msg, String counterExample) 
	{
		JFrame outputWindow = new JFrame();
		outputWindow.setSize(800, 360);
		outputWindow.setResizable(false);
		outputWindow.setTitle("jpf-ctl-output");
		outputWindow.setLocationRelativeTo(null);
		
		outputWindow.setLayout(new BoxLayout(outputWindow.getContentPane(), BoxLayout.Y_AXIS)); 
		
		JTextArea ta_output = new JTextArea();
		ta_output.setText(msg);
	    //ta_output.append("\n-----------------------");
		ta_output.setEditable(false);
		ta_output.setLineWrap(true);
		ta_output.setPreferredSize(new Dimension(750,250));
		
		JScrollPane scroll = new JScrollPane(ta_output);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JButton btn_save = new JButton("Save");
		btn_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Text Files (*.txt)";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						else
							return f.getName().toLowerCase().endsWith(".class");
					}
				});		
				
				int returnval = fc.showSaveDialog(btn_save);
				
				if(returnval == JFileChooser.APPROVE_OPTION)
				{
					File f = fc.getSelectedFile();
					try 
					{
						BufferedWriter out = new BufferedWriter(new FileWriter(f));
						out.write(ta_output.getText()); // put the final string
						out.close();
						
						JOptionPane.showMessageDialog(null, "File Saved", "Saved",
								JOptionPane.INFORMATION_MESSAGE);
						
					} 
					catch (IOException e1) {e1.printStackTrace();}
				}		
			}	
		});
		
		
		JPanel savePane = new JPanel();
		savePane.setLayout(new GridLayout(1,4,10,10));
		savePane.setPreferredSize(new Dimension(750, 50));
		savePane.add(new JLabel());
		savePane.add(new JLabel());
		savePane.add(new JLabel());
		savePane.add(btn_save);
		
		JPanel contentPane = new JPanel();
		contentPane.add(scroll);
		contentPane.add(savePane);
			
		outputWindow.setContentPane(contentPane);
		outputWindow.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		new Interface();
	}

}
