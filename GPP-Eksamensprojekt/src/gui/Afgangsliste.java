package gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import logic.*;

public class Afgangsliste extends JFrame {
	
	private JPanel jp1Udrejse, jp2Udrejse, jp3Udrejse, jp4Udrejse;
	private JPanel jp1Hjemrejse, jp2Hjemrejse, jp3Hjemrejse, jp4Hjemrejse;
	private JLabel label1, label2, label3, label4;
	private JLabel weekNumber;
	private JLabel labelHjemrejse, labelUdrejse;
	private JButton lastWeek, nextWeek, next;
	private TableColumn column;
	private JTable table;
	private JTabbedPane jtp, jtp2;
	
	private AfgangS�gning as;
	ArrayList<Afgang> departures;
	
    public Afgangsliste(Date date, String departureAirport, String arrivalAirport) throws SQLException {
    	
    	//Opretter AfgangS�gning
    	as = new AfgangS�gning(date, departureAirport, arrivalAirport);
    	departures = as.getDepartures();
    	
        setTitle("Afgange");
        
        //Laver vores fane-vinduer
        jtp = new JTabbedPane();
        jtp2 = new JTabbedPane();
        
        //S�tter BorderLayout i contentPane, og laver panels indeni
        getContentPane().setLayout(new BorderLayout());
        //NORTH
        JPanel panelNorth = new JPanel();
        getContentPane().add(panelNorth, BorderLayout.NORTH);
        panelNorth.setLayout(new BorderLayout());
        //CENTER
        JPanel panelCenter = new JPanel();
        getContentPane().add(panelCenter, BorderLayout.CENTER);
        panelCenter.setLayout(new BorderLayout());
        //SOUTH
        JPanel panelSouth = new JPanel();
        getContentPane().add(panelSouth, BorderLayout.SOUTH);
        panelSouth.setLayout(new FlowLayout());
        
        //S�tter fane-vinduerne ind i layouts'ene
        panelNorth.add(jtp, BorderLayout.CENTER);
        panelCenter.add(jtp2, BorderLayout.CENTER);
        
        //Laver layout til panelNorth NORTH
        JPanel panelNorthJtp = new JPanel();
        panelNorth.add(panelNorthJtp, BorderLayout.NORTH);
        panelNorthJtp.setLayout(new FlowLayout());
        
        //Laver layout til panelCenter NORTH
        JPanel panelCenterJtp2 = new JPanel();
        panelCenter.add(panelCenterJtp2, BorderLayout.NORTH);
        panelCenterJtp2.setLayout(new FlowLayout());
        
        //S�tter knapper og uge-label ind i FlowLayouts
        buttonsAndLabel(panelNorthJtp);
        
        buttonsAndLabel(panelCenterJtp2);
        
        										//opreter et table - jeg har �ndret meget i hvordan det laves.
        departureTable(departures);
		
		//S�tter bredden af columns
		//setWidth(0, 120);
		//setWidth(1, 120);
		//setWidth(2, 120);
		//setWidth(3, 120);
		//setWidth(4, 120);
		
		//Opretter panels
        jp1Udrejse = new JPanel();
        jp1Hjemrejse = new JPanel();
        
        labelUdrejse = new JLabel();
        labelUdrejse.setText("Onsdag d. 28. november 2012" + "Udrejse - Lufthavn");
        jp1Udrejse.add(labelUdrejse);
        
		table(jp1Udrejse);
		//Tilf�jer panel jp1Udrejse til jtp
        jtp.addTab("28/11", jp1Udrejse);
		
        labelHjemrejse = new JLabel();
        labelHjemrejse.setText("Onsdag d. 28. november 2012" + "Hjemrejse - Lufthavn");
		jp1Hjemrejse.add(labelHjemrejse);
		
		table(jp1Hjemrejse);
		//Tilf�jer panel jp1Hjemrejse til jtp
        jtp2.addTab("28/11", jp1Hjemrejse);
        
        //Laver en next-knap
        next = new JButton("N�ste");
        panelSouth.add(next);
        next.addActionListener(new Listener());
        
        /**
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        
		/**
        label2 = new JLabel();
        label2.setText("Torsdag d. 29. november 2012");
        label3 = new JLabel();
        label3.setText("Fredag d. 30. november 2012");
        label4 = new JLabel();
        label4.setText("L�rdag d. 1. december 2012");
        
        jp2.add(label2);
        jp3.add(label3);
        jp4.add(label4);
        
        // Her tilf�jes titlerne til fanerne
        jtp.addTab("28/11", jp1);
        jtp.addTab("29/11", jp2);
        jtp.addTab("30/11", jp3);
        jtp.addTab("01/12", jp4); */

        setPreferredSize(new Dimension(640, 460));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    
    							
    private void setWidth(JTable table, int i, int j) {
    	column = table.getColumnModel().getColumn(i);
    	
    	column.setMinWidth(j);
		column.setMaxWidth(j);
		column.setPreferredWidth(j);
    }
    
    															//her er den gamle table kode. Nedenunder er �n der laver et table baseret p� et array af afgange
//    private void table(){
//    	//Laver skemaerne
//        String columns[] = {"Pris","Afrejse - ankomst","Rejsetid", "Lufthavne", "Ledige pladser"};
//		Object data[][] = {
//				{"500-800 kr.", "16.00-18.00", "02.00", "CPH-LON", "215"},
//				{"600-800 kr.", "21.00-23.00", "02.00", "CPH-LON", "100"}
//				//{"Tom",new Integer(20),"Male"},
//		};
//		table = new JTable(data,columns);
//    }
    
    private void departureTable(final ArrayList<Afgang> departures) {
    	DefaultTableModel model = new DefaultTableModel(); 
    	table = new JTable(model); 

    	//Laver columns
    	model.addColumn("Pris"); 
    	model.addColumn("Afrejse - Ankomst"); 
    	model.addColumn("Rejsetid");
    	model.addColumn("Lufthavne");
    	model.addColumn("Ledige pladser");

    	//Tilf�jer rejser
    	for(int i=0; i<departures.size(); i++) {
    		Afgang a = departures.get(i);
    		//TODO mangler at tilf�je priser i databasen
    		String price = "1234-1235 kr.";
    		String time = a.getDepartureTime()+" - "+a.getArrivalTime();
    		//TODO tilf�j udregning af rejsetid
    		String travelTime = "20 years";
    		String fromTo = a.getDepartureAirport()+" - "+a.getArrivalAirport();
    		String seats = a.getSeats();
    		
    		model.addRow(new Object[]{price, time, "02.00", fromTo, seats});
    	}
    	
    	//s�tter bredden af kolonner
    	setWidth(table, 0, 120);
    	setWidth(table, 1, 120);
    	setWidth(table, 2, 120);
    	setWidth(table, 3, 120);
    	setWidth(table, 4, 120);
    	
    	//tilf�jer actionlistener som �bner r�kkens afgang som pladss�gning
    	table.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			int row = table.getSelectedRow();
    			System.out.println("You've clicked on row "+row);
    			
    			int id = departures.get(row).getId()+1;
    			System.out.println("Du har trykket p� en afgang med id: "+id);
    			try {
					Pladsbooking pb = new Pladsbooking(id-1);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					System.out.println("Something sql went wrong.");
					e1.printStackTrace();
				}
    		}
    	});
    }
    
    	
    
    private void buttonsAndLabel(JPanel panel){
    	lastWeek = new JButton("Forrige uge");
        //lastWeek.addActionListener(new Listener());
    	//men skal have en actionlistener for hver, lige nu bliver den f�rste overskrevet
        
        nextWeek = new JButton("N�ste uge");
        //nextWeek.addActionListener(new Listener());
    	//men skal have en actionlistener for hver, lige nu bliver den f�rste overskrevet
        
        weekNumber = new JLabel("uge");
        
        panel.add(lastWeek);
        panel.add(weekNumber);
        panel.add(nextWeek);
    }
    
    private void table(JPanel panel){
    										//lige nu laves begge table med samme departure array - det skal selvf�lgelig �ndres
    	departureTable(departures);
        //Indhold af panel
        panel.add(table.getTableHeader());
        panel.add(table);
    }
    
    private class Listener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            if(event.getSource() == nextWeek) {
            	System.out.println("N�ste uge");
            } else if(event.getSource() == lastWeek) {
            	System.out.println("Forrige uge");
            } else if(event.getSource() == next) {
            	System.out.println("N�ste");
            }
        }
    }
}