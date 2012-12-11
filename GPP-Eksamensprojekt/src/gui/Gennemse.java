package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import logic.Booking;
import logic.Customer;
import logic.Database;
import logic.Departure;
import logic.Person;
import logic.Plads;

public class Gennemse extends JFrame{
	//Alle oplysninger
	
	//Antal pladser, pris, personoplysninger...
	
	//Tilbage- og til betaling-knap
	
	private JPanel panel, panelKontaktoplysninger, panelRejse, panelUdrejse, panelHjemrejse;
	private JPanel panelPladser, panelPladserUdrejse, panelPladserHjemrejse;
	private JPanel panelPassengers, panelPris, panelKnapper, flowPanel1, flowPanel2;
	private JPanel flowPanel3;
	private JLabel name, firstname, surname, address, city, postalCode, cityName;
	private JLabel  country, phoneNumber, email, kontaktoplysninger, udrejse, hjemrejse;
	private JLabel airport, ap1, ap2, afgang, ankomst, rejsetid, lufthavn1, lufthavn2;
	private JLabel pladser, labelSeat, labelPassengers, passenger, birthday, header;
	private JLabel labelPris, total, prisTekst;
	//private int antalPassagerer;
	
	private JButton tilbage, bestil;
	
	//Ting, der skal sendes til databasen
	private ArrayList<Person> passengers;
	private Customer customer;
	//private ArrayList<Booking> bookings = new ArrayList<>();
	private ArrayList<Plads> reserved1, reserved2;
	private Departure d1, d2;
	
	
	
	public Gennemse(ArrayList<Plads> reserved1, ArrayList<Plads> reserved2, ArrayList<Person> passengers, Customer customer,
			Departure d1, Departure d2) {

		this.passengers = passengers;
		this.customer = customer;
		this.reserved1 = reserved1;
		this.reserved2 = reserved2;
		this.d1 = d1;
		this.d2 = d2;
		
		makeGennemseWindow(true, true);
		
		makeGennemseWindow(true, false);

	}
	
	public Gennemse(Departure d1, ArrayList<Person> passengers, Customer customer) {
		this.passengers = passengers;
		this.customer = customer;
		this.d1 = d1;
	}


	public void makeGennemseWindow(boolean turRetur, boolean editing) {

		setTitle("Gennemse");

		panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		//Scrollbar
		JScrollPane scroll = new JScrollPane(panel);
		getContentPane().add(scroll);

		//Panel til kontaktoplysninger
		flowPanel1 = new JPanel();
		panel.add(flowPanel1);
		flowPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

		panelKontaktoplysninger = new JPanel();
		flowPanel1.add(panelKontaktoplysninger);
		//panelKontaktoplysninger.setLayout(new GridLayout(7, 1));
		panelKontaktoplysninger.setLayout
		(new BoxLayout(panelKontaktoplysninger, BoxLayout.PAGE_AXIS));

		panelKontaktoplysninger.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//Tilf�jer kontaktoplysninger fra Customer
		kontaktoplysninger = new JLabel("Kontaktoplysninger");
		kontaktoplysninger.setFont(new Font("String", Font.BOLD, 16));
		panelKontaktoplysninger.add(kontaktoplysninger);
		name = new JLabel(customer.GetFirstname() + " " + customer.GetSurname());
		panelKontaktoplysninger.add(name);
		address = new JLabel(customer.GetAdress());
		panelKontaktoplysninger.add(address);
		city = new JLabel(customer.GetPostalCode() + " " + customer.GetCity());
		panelKontaktoplysninger.add(city);
		country = new JLabel(customer.GetCountry());
		panelKontaktoplysninger.add(country);
		phoneNumber = new JLabel(customer.GetPhone());
		panelKontaktoplysninger.add(phoneNumber);
		email = new JLabel(customer.GetEmail());
		panelKontaktoplysninger.add(email);
		
		//tilf�jer knap til redigering
		if(editing) {
			JButton editCustomerButton = new JButton("Redig�r kundeoplysninger");
			panelKontaktoplysninger.add(editCustomerButton);
		}
		

		panelRejse = new JPanel();
		panel.add(panelRejse);
		panelRejse.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panelRejse.setLayout(new GridLayout(1,2,10,10));
		panelRejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//Udrejse-info
		panelUdrejse = new JPanel();
		panelRejse.add(panelUdrejse);
		panelUdrejse.setLayout(new GridLayout(5,1));
		panelUdrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelUdrejse.setBackground(Color.lightGray);

		udrejse = new JLabel("Udrejse");
		udrejse.setFont(new Font("String", Font.BOLD, 16));
		panelUdrejse.add(udrejse);
		airport = new JLabel(d1.getDepartureAirportName() + " "+ d1.getDepartureAirportAbbrevation() + " - " + d1.getArrivalAirportName() + " " + d1.getArrivalAirportAbbrevation());
		panelUdrejse.add(airport);
		afgang = new JLabel("Afgang: " + d1.getDepartureTime());
		panelUdrejse.add(afgang);
		ankomst = new JLabel("Ankomst: " + d1.getArrivalTime());
		panelUdrejse.add(ankomst);
		rejsetid = new JLabel("Rejsetid: " + d1.getTravelTime());
		panelUdrejse.add(rejsetid);

		//Hjemrejse-info
		panelHjemrejse = new JPanel();
		panelRejse.add(panelHjemrejse);
		panelHjemrejse.setLayout(new GridLayout(5,1));
		panelHjemrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelHjemrejse.setBackground(Color.lightGray);

		hjemrejse = new JLabel("Hjemrejse");
		hjemrejse.setFont(new Font("String", Font.BOLD, 16));
		panelHjemrejse.add(hjemrejse);
		airport = new JLabel(d2.getDepartureAirportName() + " "+ d2.getDepartureAirportAbbrevation() + " - " + d2.getArrivalAirportName() + " " + d2.getArrivalAirportAbbrevation());
		panelHjemrejse.add(airport);
		afgang = new JLabel("Afgang: " + d2.getDepartureTime());
		panelHjemrejse.add(afgang);
		ankomst = new JLabel("Ankomst: " + d2.getArrivalTime());
		panelHjemrejse.add(ankomst);
		rejsetid = new JLabel("Rejsetid: " + d2.getTravelTime());
		panelHjemrejse.add(rejsetid);

		//Pladser info
		panelPladser = new JPanel();
		panel.add(panelPladser);
		//panelPladser.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panelPladser.setLayout(new GridLayout(1,2,10,10));
		panelPladser.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//Info om pladser til udrejsen
		panelPladserUdrejse = new JPanel();
		panelPladser.add(panelPladserUdrejse);
		panelPladserUdrejse.setLayout(new GridLayout(10,1));
		//panelPladserUdrejse.setLayout(new GridLayout(antalPassagerer,1));
		panelPladserUdrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelPladserUdrejse.setBackground(Color.lightGray);

		pladser = new JLabel("Pladser udrejse");
		pladser.setFont(new Font("String", Font.BOLD, 16));
		panelPladserUdrejse.add(pladser);

		//Bestilte pladser
		antalPladser(reserved1, panelPladserUdrejse);

		//Info om pladser til hjemrejsen
		panelPladserHjemrejse = new JPanel();
		panelPladser.add(panelPladserHjemrejse);
		panelPladserHjemrejse.setLayout(new GridLayout(10,1));
		//panelPladserUdrejse.setLayout(new GridLayout(antalPassagerer,1));
		panelPladserHjemrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelPladserHjemrejse.setBackground(Color.lightGray);

		pladser = new JLabel("Pladser hjemrejse");
		pladser.setFont(new Font("String", Font.BOLD, 16));
		panelPladserHjemrejse.add(pladser);

		antalPladser(reserved2, panelPladserHjemrejse);

		//Passagerer
		flowPanel2 = new JPanel();
		panel.add(flowPanel2);
		flowPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		//flowPanel2.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		panelPassengers = new JPanel();
		flowPanel2.add(panelPassengers);
		panelPassengers.setLayout(new BoxLayout(panelPassengers, BoxLayout.PAGE_AXIS));

		labelPassengers = new JLabel("Passagerer");
		labelPassengers.setFont(new Font("String", Font.BOLD, 16));
		panelPassengers.add(labelPassengers);

		//skaber passagerer
		passengers(passengers.size());

		//pris
		flowPanel3 = new JPanel();
		panel.add(flowPanel3);
		flowPanel3.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

		panelPris = new JPanel();
		flowPanel3.add(panelPris);
		panelPris.setLayout(new BoxLayout(panelPris, BoxLayout.PAGE_AXIS));

		labelPris = new JLabel("Pris");
		labelPris.setFont(new Font("String", Font.BOLD, 16));
		panelPris.add(labelPris);
		prisTekst = new JLabel("antalPassagerer" + " x s�der � " + "pris");
		panelPris.add(prisTekst);
		total = new JLabel("Total = " + "antalPassagerer*pris" + " kr.");
		total.setFont(new Font("String", Font.BOLD, 14));
		panelPris.add(total);

		//Knapper
		panelKnapper = new JPanel();
		panel.add(panelKnapper);
		panelKnapper.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

		tilbage = new JButton("Tilbage");
		panelKnapper.add(tilbage);
		bestil = new JButton("Bestil");
		panelKnapper.add(bestil);

		addActionListeners();

		setPreferredSize(new Dimension(640, 500));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		setResizable(false);

	}
	
	//tilf�j pladsers navne
	private void antalPladser(ArrayList<Plads> reservedSeats, JPanel panel) {
		for(int i = 0; i < reservedSeats.size(); i++) {
			labelSeat = new JLabel(reservedSeats.get(i).GetName());
			//labelSeat = new JLabel("R�kke " + "3, " + "s�de " + "a");
			panel.add(labelSeat);
		}
	}
	
	private void passengers(int antalPassagerer) {
		for(int i = 0; i < antalPassagerer; i++) {
			header = new JLabel("Passager " + i+1);
	        header.setFont(new Font("String", Font.BOLD, 14));
			panelPassengers.add(header);
			passenger = new JLabel(passengers.get(i).getFirstname() + " " + passengers.get(i).getSurname());
			panelPassengers.add(passenger);
			birthday = new JLabel(passengers.get(i).getBirthday());
			panelPassengers.add(birthday);
			JLabel emptyLabel = new JLabel(" ");
			panelPassengers.add(emptyLabel);
		}
	}
    
    //Tilf�jer actionListeners til de to knapper
    private void addActionListeners(){
    	tilbage.addActionListener(new Listener());
    	bestil.addActionListener(new Listener());
    }

    //Lytter til knapperne
    private class Listener implements ActionListener {
    	public void actionPerformed(ActionEvent event){
    		if(event.getSource() == tilbage) {
    			System.out.println("Going back");
    		} else if(event.getSource() == bestil) {
    			System.out.println("Bestiller");

    			//opretter passagerer
    			String passengerString = "";
    			int customerId = 0;
    			String seatNums1 = "";
    			String seatNums2 = "";

    			try {
    				Database db = new Database("mysql.itu.dk", "Swan_Airlines", "swan", "mintai");
    				
    				//skab passag�r og gem passengerId
    				for(int i=0; i<passengers.size(); i++) {
    					int passengerId = db.queryMakePassenger(passengers.get(i));
    					passengerString = passengerString + " " + passengerId;
    				}
    				
    				//skab customer og gem customerId
    				customerId = db.queryMakeCustomer(customer);

    				//lav string med id p� reserverede pladser ud
    				for(int j=0; j<reserved1.size(); j++) {
    					int num = reserved1.get(j).getSeatNo();
    					seatNums1 = num+" "+seatNums1;
    				}
    				//skab booking for udrejse
    				db.queryMakeBooking(d1.getDepartureId(), customerId, seatNums1, passengerString);
    				
    				//lav string med id p� reserverede pladser hjem
    				for(int j=0; j<reserved2.size(); j++) {
    					int num = reserved2.get(j).getSeatNo();
    					seatNums2 = num+" "+seatNums2;
    				}
    				//skab booking for hjemrejse
    				db.queryMakeBooking(d2.getDepartureId(), customerId, seatNums2, passengerString);


    				System.out.println("passengerString: "+passengerString);
    				System.out.println("customerId: "+customerId);

    			} catch (SQLException e) {
    				System.out.println("Something SQL went wrong when making passengers");
    				e.printStackTrace();
    			}
    		}
    	}
    }
}
