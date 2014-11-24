package client_server;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;


@SuppressWarnings("serial")
public class Dialog extends JDialog {

   private JLabel label;
   private JButton OK;
   private ButtonGroup radios; 
   private JPanel grid;
   ArrayList<JRadioButton> buttonsList = new ArrayList<JRadioButton>();
   private Client frame;
   
   public Dialog(Client client,ArrayList<String> list) {
       super(client, true);
       frame = client;
       setLayout(new BorderLayout());
       grid = new JPanel();
       grid.setLayout(new GridLayout(0,1));
       radios = new ButtonGroup();
       OK = new JButton("OK");
       label = new JLabel("Choose Trump: ");
       initComponents(list);
       setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
       pack();
       this.setLocationRelativeTo(null);
       this.setVisible(true);
   }

   private void initComponents(ArrayList<String> list) {
	   JRadioButton temp = null;
	   handler h = new handler();
	   
	   for(String item:list){
		   temp = new JRadioButton(item);
		   grid.add(temp);
		   radios.add(temp);
		   buttonsList.add(temp);
		   
		   
	   }
	   this.add(label,BorderLayout.NORTH);
	   this.add(grid,BorderLayout.CENTER);
	   JPanel panel = new JPanel();
	   panel.setLayout(new FlowLayout());
	   OK.addActionListener(h);
	   panel.add(OK);
	   this.add(panel,BorderLayout.SOUTH);
	   
   }
   
   private class handler implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent event) {
		
		for(JRadioButton button:buttonsList){
			if(button.isSelected()){
				
				frame.SetTrump((String)button.getText());
				dispose();
			}
		}
	}
	   
   }
}


