package client_server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class ConnectToServerFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	JLabel serverLabel;
	JLabel okLabel;
	JTextField serverField;
	JButton okButton;
	
	public ConnectToServerFrame() {
		initComponent();
	}
	
	
	public void initComponent(){	
	setTitle("Connect to server");
	
	serverLabel = new JLabel("Enter server address ");
	okLabel = new JLabel("Press to connect server ");
	serverField = new JTextField(30);
	okButton = new JButton("Connect");
	
	
	okButton.addActionListener(new ActionListener() {
		
		@SuppressWarnings("unused")
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() == okButton) {
				GameFrame game = new GameFrame();
			}
			
		}
	});
	
	setMinimumSize(new Dimension(100, 150));
	setSize(600, 100);

	layoutComponents();
	
	setLocationRelativeTo(null);
	setDefaultCloseOperation(javax.swing. WindowConstants.DISPOSE_ON_CLOSE);
	setVisible(true);
	
	}
	
    public void layoutComponents() {
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(serverLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 0;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(serverField, gc);
		
        gc.gridy++;
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		
		gc.gridx = 0;
		gc.insets = new Insets(0, 0, 0, 5);
		gc.anchor = GridBagConstraints.LINE_END;
		add(okLabel, gc);
		
		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(okButton, gc);
    }

}
