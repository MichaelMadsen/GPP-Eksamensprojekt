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

/**
 * Her kan man gennemg� alle de informationer, man har indtastet under en booking
 * N�r klassen kaldes fra en bookings-s�gning, kan man ogs� �ndre i eksisterende bookinger fra denne klasse
 * 
 * @author Michael Frikke Madsen, Tajanna Bye Kj�rsgaard og Nicoline Warming Larsen.
 *
 */

public class Gennemse extends JFrame{
	private JPanel panel, panelKontaktoplysninger, panelRejse, panelUdrejse, panelHjemrejse;
	private JPanel panelPladser, panelPladserUdrejse, panelPladserHjemrejse;
	private JPanel panelPassengers, panelPris, panelKnapper, flowPanel1, flowPanel2;
	private JPanel flowPanel3;
	private JLabel firstname, surname, postalCode, cityName;
	private JLabel name, address, city;
	private JLabel country, phoneNumber, email, kontaktoplysninger, udrejse, hjemrejse;
	private JLabel airport, afgang, ankomst, rejsetid;
	private JLabel pladser, labelSeat, labelPassengers, passenger, birthday, header;
	private JLabel labelPris, total, prisTekst;
	private JButton editCustomerButton, editReservation, editPassengers;
	private JButton tilbage, bestil, slet, gem, annuller;
	private boolean importingCustomer;
	
	//Ting, der skal sendes til databasen
	private ArrayList<Person> passengers;
	private Customer customer;
	private ArrayList<Plads> reserved1, reserved2;
	private Departure d1, d2;
	private boolean turRetur;
	private Booking b;
	private Kundeoplysninger ko;
	
	
	//Constructor som kaldes ved almindelig tur/retur booking
	public Gennemse(ArrayList<Plads> reserved1, ArrayList<Plads> reserved2, ArrayList<Person> passengers, 
			Customer customer, Departure d1, Departure d2, boolean importingCustomer, Kundeoplysninger ko) {
		this.passengers = passengers;
		this.customer = customer;
		this.reserved1 = reserved1;
		this.reserved2 = reserved2;
		this.d1 = d1;
		this.d2 = d2;
		turRetur = true;
		this.importingCustomer = importingCustomer;
		this.ko = ko;

		makeGennemseWindow(true, false);
		setTitle("Gennemse");
		knapperGennemse();
	}

	//Constructor som kaldes ved Enkeltrejse booking
	public Gennemse(ArrayList<Plads> reserved, Departure d1, ArrayList<Person> passengers, 
			Customer customer, boolean importingCustomer, Kundeoplysninger ko) {
		this.passengers = passengers;
		this.customer = customer;
		this.reserved1 = reserved;
		this.d1 = d1;
		turRetur = false;
		this.importingCustomer = importingCustomer;
		this.ko = ko;

		makeGennemseWindow(false, false);
		setTitle("Gennemse");
		knapperGennemse();
	}

	//Constructor som kaldes, n�r man skal gennemse en eksisterende booking og have mulighed for at �ndre den
	public Gennemse(ArrayList<Plads> reserved, Departure d1, Booking b, Customer customer) {
		this.b = b;
		passengers = b.getPassengers();
		this.customer = customer;
		this.reserved1 = reserved;
		this.d1 = d1;
		turRetur = false;

		for(int i=0; i<reserved.size(); i++) {
			System.out.println(reserved.get(i).toString());
		}

		makeGennemseWindow(false, true);
		setTitle("Bookinginfo");
		knapperRedigering();
	}

	//Laver vinduet
	public void makeGennemseWindow(boolean turRetur, boolean editing) {

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

		//Tilf�jer knap til redigering
		if(editing) {
			editCustomerButton = new JButton("Redig�r kundeoplysninger");
			editCustomerButton.addActionListener(new Listener());
			panelKontaktoplysninger.add(editCustomerButton);
		}

		//Panel til rejse-info
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
		airport = new JLabel(d1.getDepartureAirportName() + " "+ d1.getDepartureAirportAbbrevation() 
				+ " - " + d1.getArrivalAirportName() + " " + d1.getArrivalAirportAbbrevation());
		panelUdrejse.add(airport);
		afgang = new JLabel("Afgang: " + d1.getDepartureDate() + " "+ d1.getDepartureTime());
		panelUdrejse.add(afgang);
		ankomst = new JLabel("Ankomst: " + d1.getDepartureDate() + " "  + d1.getArrivalTime());
		panelUdrejse.add(ankomst);
		rejsetid = new JLabel("Rejsetid: " + d1.getTravelTime());
		panelUdrejse.add(rejsetid);

		//Hjemrejse-info
		panelHjemrejse = new JPanel();
		panelRejse.add(panelHjemrejse);

		if(turRetur) {
			panelHjemrejse.setLayout(new GridLayout(5,1));
			panelHjemrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			panelHjemrejse.setBackground(Color.lightGray);

			hjemrejse = new JLabel("Hjemrejse");
			hjemrejse.setFont(new Font("String", Font.BOLD, 16));
			panelHjemrejse.add(hjemrejse);
			airport = new JLabel(d2.getDepartureAirportName() + " "+ d2.getDepartureAirportAbbrevation() + " - " + d2.getArrivalAirportName() + " " + d2.getArrivalAirportAbbrevation());
			panelHjemrejse.add(airport);
			afgang = new JLabel("Afgang: " + d2.getDepartureDate() + " " + d2.getDepartureTime());
			panelHjemrejse.add(afgang);
			ankomst = new JLabel("Ankomst: " + d2.getDepartureDate() + " " + d2.getArrivalTime());
			panelHjemrejse.add(ankomst);
			rejsetid = new JLabel("Rejsetid: " + d2.getTravelTime());
			panelHjemrejse.add(rejsetid);
		}

		//Pladser info
		panelPladser = new JPanel();
		panel.add(panelPladser);
		panelPladser.setLayout(new GridLayout(1,2,10,10));
		panelPladser.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//Info om pladser til udrejsen
		panelPladserUdrejse = new JPanel();
		panelPladser.add(panelPladserUdrejse);
		panelPladserUdrejse.setLayout(new GridLayout(10,1));
		panelPladserUdrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelPladserUdrejse.setBackground(Color.lightGray);

		pladser = new JLabel("Pladser udrejse");
		pladser.setFont(new Font("String", Font.BOLD, 16));
		panelPladserUdrejse.add(pladser);
		
		//Hvis vi er ved at se booking, tilf�j redig�r knap
		if(editing) {
			editReservation = new JButton("Redig�r valgte pladser");
			panelPladserUdrejse.add(editReservation);
			editReservation.addActionListener(new Listener());
		}

		//Bestilte pladser
		antalPladser(reserved1, panelPladserUdrejse);

		//Info om pladser til hjemrejsen
		panelPladserHjemrejse = new JPanel();
		panelPladser.add(panelPladserHjemrejse);

		if(turRetur) {
			panelPladserHjemrejse.setLayout(new GridLayout(10,1));
			panelPladserHjemrejse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			panelPladserHjemrejse.setBackground(Color.lightGray);

			pladser = new JLabel("Pladser hjemrejse");
			pladser.setFont(new Font("String", Font.BOLD, 16));
			panelPladserHjemrejse.add(pladser);

			antalPladser(reserved2, panelPladserHjemrejse);
		}

		//Passagerer
		flowPanel2 = new JPanel();
		panel.add(flowPanel2);
		flowPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

		panelPassengers = new JPanel();
		flowPanel2.add(panelPassengers);
		panelPassengers.setLayout(new BoxLayout(panelPassengers, BoxLayout.PAGE_AXIS));

		labelPassengers = new JLabel("Passagerer");
		labelPassengers.setFont(new Font("String", Font.BOLD, 16));
		panelPassengers.add(labelPassengers);

		//Skaber knap til at �ndre i passagerer, 
		//hvis vi er i gang med at rette i eksisterende booking
		if(editing) {
			editPassengers = new JButton("Redig�r passagerer");
			editPassengers.addActionListener(new Listener());
			panelPassengers.add(editPassengers);
		}

		//Skaber passagerer
		passengers(passengers.size());

		//Pris
		flowPanel3 = new JPanel();
		panel.add(flowPanel3);
		flowPanel3.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

		panelPris = new JPanel();
		flowPanel3.add(panelPris);
		panelPris.setLayout(new BoxLayout(panelPris, BoxLayout.PAGE_AXIS));

		labelPris = new JLabel("Pris");
		labelPris.setFont(new Font("String", Font.BOLD, 16));
		panelPris.add(labelPris);

		if(turRetur) {
			prisTekst = new JLabel(passengers.size() + " x s�der � " + d1.getPrice() + " + " + passengers.size() + " x s�der � " + d2.getPrice());
			total = new JLabel("Total = " + (passengers.size()*d1.getPrice()+passengers.size()*d2.getPrice()));
		} else {
			prisTekst = new JLabel(passengers.size() + " x s�der � " + d1.getPrice());
			total = new JLabel("Total = " + (passengers.size()*d1.getPrice()) + " kr.");
		}

		panelPris.add(prisTekst);
		total.setFont(new Font("String", Font.BOLD, 14));
		panelPris.add(total);

		setPreferredSize(new Dimension(640, 500));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		setResizable(false);

	}

	//Knapper til Gennemse
	private void knapperGennemse() {
		//Knapper
		panelKnapper = new JPanel();
		panel.add(panelKnapper);
		panelKnapper.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

		tilbage = new JButton("Tilbage");
		panelKnapper.add(tilbage);
		bestil = new JButton("Bestil");
		panelKnapper.add(bestil);

		tilbage.addActionListener(new Listener());
		bestil.addActionListener(new Listener());
	}

	//Knapper til redigering
	private void knapperRedigering() {
		//Knapper
		panelKnapper = new JPanel();
		panel.add(panelKnapper);
		panelKnapper.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

		slet = new JButton("Slet booking");
		panelKnapper.add(slet);
		annuller = new JButton("Luk");
		panelKnapper.add(annuller);

		slet.addActionListener(new Listener());
		annuller.addActionListener(new Listener());
	}

	//Tilf�j pladsers navne
	private void antalPladser(ArrayList<Plads> reservedSeats, JPanel panel) {
		for(int i = 0; i < reservedSeats.size(); i++) {
			labelSeat = new JLabel(reservedSeats.get(i).GetName());
			panel.add(labelSeat);
		}
	}

	//Viser passager-info
	private void passengers(int antalPassagerer) {
		for(int i = 0; i < antalPassagerer; i++) {
			header = new JLabel("Passager " + (i+1));
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

	//Opdaterer information ved at lave et nyt vindue og fjerne det gamle
	public void reload(Customer c) {
		Gennemse g = new Gennemse(reserved1, d1, b, c);
		dispose();
	}

	private Gennemse getThis() {
		return this;
	}

	public void removeMe() {
		ko.removeMe();
		dispose();
	}

	private JFrame getFrame() {
		return this;
	}

	//Lytter til knapperne
	private class Listener implements ActionListener {
		public void actionPerformed(ActionEvent event){
			if(event.getSource() == tilbage) {
				dispose();
			} else if(event.getSource() == bestil) {
				if(turRetur) {
					Betaling betaling = new Betaling(reserved1, reserved2, passengers, customer, d1, d2, getThis());
    			} else if (!turRetur) {
    				Betaling betaling = new Betaling(reserved1, d1, passengers, customer, getThis());
    			}
    		} else if(event.getSource() == gem) {
    			//Placebo. I virkeligheden bliver data gemt n�r man �ndrer dem i de tilh�rende vinduer. Denne knap g�r bare at folk f�ler sig sikrere.
    			JOptionPane.showMessageDialog(getThis(), "�ndringerne er gemt.");
    			ko.removeMe();
    			dispose();
    		} else if(event.getSource() == annuller) {
    			//Anull�r knappen g�r ikke andet end at lukke vinduet
    			dispose();
    		} else if(event.getSource() == slet) {
    			//Sletter booking fra databasen.
    			try {
    				Database db = new Database("mysql.itu.dk", "Swan_Airlines", "swan", "mintai");
    				db.queryDeleteBooking(b.getId());
    				JOptionPane.showMessageDialog(getFrame(), "Booking slettet.");
    			} catch (SQLException e) {
    				JOptionPane.showMessageDialog(getFrame(), "Booking ikke slettet - kan ikke f� forbindelse til databasen.");
    				e.printStackTrace();
    			}
    		} else if(event.getSource() == editCustomerButton) {
    			//Hvis der klikkes p� edit passengers, �bn en Kundeoplysninger, med den nuv�rende kunde, s� den kan �ndres
    			Kundeoplysninger ko = new Kundeoplysninger(customer, getThis());
    		} else if(event.getSource() == editReservation) {
    			//Hvis man �nsker at �ndre i de reserverede s�der, �bn en Pladsbooking med informationerne fra bookingen
    			try {
    				Pladsbooking pb = new Pladsbooking(b.getdepartureId(), b, customer);
    			} catch (SQLException e) {
    				JOptionPane.showMessageDialog(getFrame(), "Fejl i kommunikation med serveren - pr�v igen");
    				e.printStackTrace();
    			}
    			//Hvis der klikkes p� edit passengers, �bn en Kundeoplysninger, med de nuv�rende passagerer, s� de kan �ndres
    		} else if(event.getSource() == editPassengers) {
    			Kundeoplysninger k = new Kundeoplysninger(passengers, getThis());
    		}
    	}
    }
}
