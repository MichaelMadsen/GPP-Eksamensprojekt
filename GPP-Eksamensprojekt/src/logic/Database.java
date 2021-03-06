package logic;

import java.sql.*;
import java.util.ArrayList;

/**
 * Denne class bruges n�r vi skal sende queries til databasen. 
 * Den indeholder alle de relevante metoder for at oprette og hente data fra databasen
 * @author Michael Frikke Madsen, Tajanna Bye Kj�rsgaard og Nicoline Warming Larsen.
 *
 */

public class Database {
	private final Connection connection;
	private final Statement dbStatement;
	
	String host, database, user, password;
	
	//�bner forbindelsen til en database
	public Database(String host, String database, String user, String password) throws SQLException {
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException exn) {
			System.out.println("Could not find the path: " +exn);
		}
		
		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
		dbStatement = connection.createStatement();
		
	}
	
	//Sender en string til databasen, og returnerer det resultset, der kommer tilbage
	public ResultSet execute(String cmd) throws SQLException {
		boolean ok;
			ok = dbStatement.execute(cmd);
		if (ok) {
				return dbStatement.getResultSet();
		} else {
			return null;
		}
	}
	
	//Lukker forbindelsen til databasen
	public void close() throws SQLException {
		connection.close();
	}
	
	//Henter reserverede s�der p� den angivne afgang
	public ResultSet queryGetReservedSeats(int departureId) throws SQLException {
		String query;
		query = "SELECT " +
				"Booking.seats " +
				"FROM " +
				"Booking " +
				"WHERE " +
				"Booking.departure_id = " + departureId;
		ResultSet rs = this.execute(query);
		return rs;	
	}
	
	//Finder id'et p� det fly, der er tilknyttet en bestemt departure
	public ResultSet queryGetAirplane(int departureId) throws SQLException {
		String query;
		query = "SELECT " +
				"airplane_id " +
				"FROM " +
				"Departures " +
				"WHERE " +
				"Departures.id = " + departureId;
		ResultSet rs = this.execute(query);
		return rs;
	}
	
	//Finder en passager ud fra ID'et
	public Person queryGetPassenger(int passengerId) throws SQLException {
		String query;
		Person p;
		query = "SELECT " +
				"* " +
				"FROM " +
				"Person " +
				"WHERE Person.id = " + passengerId;
		ResultSet rs = this.execute(query);
		rs.next();
		p = new Person(rs.getString("Firstname"), rs.getString("Surname"), rs.getString("Birthday"));
		p.setId(rs.getInt("id"));
		return p;
	}
	
	//Henter antal r�kker p� et fly
	public ResultSet queryGetRows(int departureId) throws SQLException {
		ResultSet airId = queryGetAirplane(departureId);
		airId.next();
		int airplaneId = airId.getInt("airplane_id"); 
		String query;
		query = "SELECT " +
				"Airplane.rows " +
				"FROM " +
				"Airplane " +
				"WHERE " +
				"Airplane.id = " + airplaneId;
		ResultSet rs = this.execute(query);
		return rs;
	}
	
	//Antal kolonner p� et fly
	public ResultSet queryGetCols(int departureId) throws SQLException {
		ResultSet airId = queryGetAirplane(departureId);
		airId.next();
		int airplaneId = airId.getInt("airplane_id");
		String query;
		query = "SELECT " +
				"Airplane.columns " +
				"FROM " +
				"Airplane " +
				"WHERE " +
				"Airplane.id = " + airplaneId;
		ResultSet rs = this.execute(query);
		return rs;
	}
	
	//Henter tomme kolonner fra et fly, dvs. mellemgangene
	public ResultSet queryGetEmptyCols(int departureId) throws SQLException {
		ResultSet airId = queryGetAirplane(departureId);
		airId.next();
		int airplaneId = airId.getInt("airplane_id");
		String query;
		query = "SELECT " +
				"Airplane.empty_columns " +
				"FROM " +
				"Airplane " +
				"WHERE " +
				"Airplane.id = " + airplaneId;
		ResultSet rs = this.execute(query);
		return rs;
	}
	
	//Henter lufthavnens ID ud fra dens navn
	public ResultSet queryGetAirportId(String airport) throws SQLException {
		String query;
		query = "SELECT " +
				"Airports.id " +
				"FROM " +
				"Airports " +
				"WHERE " +
				"Airports.name = \"" + airport+"\"";
		ResultSet rs = this.execute(query);
		return rs;
	}
	
	//Henter afgange p� den angivne dato, for de angivne lufthavne
	public ResultSet queryGetDeparturesOnDate(String date, int departureAirport, int arrivalAirport) throws SQLException {
		String query;
		query = "SELECT " +
				"* " +
				"FROM " +
				"Departures " +
				"WHERE " +
				"Departures.Departure_date = " + date + " " +
				"AND " +
				"Departures.departure_airport_id = " + departureAirport + " " +
				"AND " +
				"Departures.arrival_airport_id = " + arrivalAirport;
		ResultSet rs = this.execute(query);
		return rs;
	}
	
	//Henter afgange i den angivne periode, dvs mellem 2 datoer.
	public ArrayList<Departure> queryGetDeparturesInPeriod(String afterDate, String beforeDate, int departureAirport, int arrivalAirport) throws SQLException {
		ArrayList<Departure> departures = new ArrayList<>();
		String query;
		query = "SELECT " +
				"* " +
				"FROM " +
				"Departures " +
				"WHERE " +
				"Departures.departure_date >= " + afterDate + " " +
				"AND " +
				"Departures.departure_date <= " + beforeDate + " " +
				"AND " +
				"Departures.departure_airport_id = " + departureAirport + " " +
				"AND " +
				"Departures.arrival_airport_id = " + arrivalAirport + " " +
				"ORDER BY " +
				"Departures.Departure_date ASC";
		ResultSet rs = this.execute(query);
		
		//L�ber resultsettet igennem og tilf�jer alle fundne departures til en ArrayList
		Database db = new Database("mysql.itu.dk", "Swan_Airlines", "swan", "mintai");
		while(rs.next()) {
			int id = rs.getInt("id");
			departures.add(db.queryGetDeparture(id));
		}
		db.close();
		return departures;
	}
	
	//Finder en bestemt departure ud fra et id
	public Departure queryGetDeparture(int departureId) throws SQLException {
		String query;
		query = "SELECT " +
				"* " +
				"FROM " +
				"Departures " +
				"WHERE " +
				"Departures.id = "+ departureId;
		ResultSet rs = this.execute(query);
		rs.next();
		//gemmer informationer fra resultsettet
		int airplaneId = rs.getInt("airplane_id");
		int departureAirportId = rs.getInt("departure_airport_id");
		int arrivalAirportId = rs.getInt("arrival_airport_id");
		Timestamp departureTime = rs.getTimestamp("departure_time");
		Timestamp arrivalTime = rs.getTimestamp("arrival_time");
		Date departureDate = rs.getDate("Departure_date");
		int price = rs.getInt("price");
		
		//finder afgangslufthavnen
		String query2;
		query2 = "SELECT " +
				"* " +
				"FROM " +
				"Airports " +
				"WHERE " +
				"Airports.id = " + departureAirportId;
		ResultSet rs2 = this.execute(query2);
		rs2.next();
		String departureName = rs2.getString("name");
		String departureAbbrevation = rs2.getString("abbrevation");
		
		//finder ankomstlufthavnen
		String query3;
		query3 = "SELECT " +
				"* " +
				"FROM " +
				"Airports " +
				"WHERE " +
				"Airports.id = " + arrivalAirportId;
		ResultSet rs3 = this.execute(query3);
		rs3.next();
		String arrivalName = rs3.getString("name");
		String arrivalAbbrevation = rs3.getString("abbrevation");
		
		//laver departure ud fra informationerne i de 3 resultsets
		Departure d = new Departure(departureId, airplaneId, departureAirportId, arrivalAirportId, 
				departureTime, arrivalTime, departureDate, price, 
				departureName, departureAbbrevation, arrivalName, arrivalAbbrevation);
		return d;
	}

	//skaber en booking, og finder den herefter igen, denne gang med et database-skabt id tilknyttet. Bruges i kvitteringen.
	public Booking queryMakeBooking(int departureId, int customerId, String seats, String passengerIds) throws SQLException {
		String query = "INSERT INTO Booking (departure_id, customer_id, seats, passenger_ids) " +
				"VALUES ('" + departureId + "', '" + customerId + "', '" + seats + "', '" + passengerIds + "'); ";
		System.out.println(query);
		this.execute(query);
		String query2 = "SELECT " +
				"* " +
				"FROM " +
				"Booking " +
				"WHERE " +
				"Departure_id = '" +departureId+"' " +
				"AND " +
				"Customer_id = '" + customerId +"' " +
				"AND " +
				"seats = '"+seats+"' " +
				"AND " +
				"passenger_Ids = '"+passengerIds+"'";
		ResultSet rs = this.execute(query2);
		rs.next();
		int id = rs.getInt("id");
		int departureId2 = rs.getInt("departure_id");
		int customerId2 = rs.getInt("Customer_id");
		String seats2 = rs.getString("seats");
		String passengerIds2 = rs.getString("Passenger_ids");
		
		Booking b = new Booking(departureId2, seats2, passengerIds2, false);
		b.setId(id);
		return b;
	}
	
	//Skaber en customer i databasen, og returner dens givne ID
	public int queryMakeCustomer(Customer c) throws SQLException {
		String query = "INSERT INTO Customer (Firstname, Surname, Address, City, Postal_Code, Country, Email, Phone_Number) " +
				"VALUES ('" + c.GetFirstname() + "', '" + c.GetSurname() + "', '" + c.GetAdress() + "', '" + c.GetCity() + "', '" + c.GetPostalCode() + "', '" + c.GetCountry() + "', '" + c.GetEmail() + "', '" + c.GetPhone() + "'); ";
		System.out.println(query);
		this.execute(query);
		
		//find customers givne id og returner det
		String query2 = "SELECT " +
				"id " +
				"FROM " +
				"Customer " +
				"WHERE " +
				"Customer.Firstname = '" + c.GetFirstname() + "' " +
				"AND " +
				"Customer.Surname = '" + c.GetSurname() + "' " +
				"AND " +
				"Customer.Phone_Number = '" + c.GetPhone() + "'";
		ResultSet rs = this.execute(query2);
		rs.next();
		return rs.getInt("id");
	}
	
	//Skaber en passager i databasen, og returnerer dens givne id
	public int queryMakePassenger(Person p) throws SQLException {
		//inds�t passageren i databasen
		String query = "INSERT INTO Person (Firstname, Surname, Birthday) " +
				"VALUES ('" + p.getFirstname() + "', '" + p.getSurname() + "', '" + p.getBirthday() + "'); ";
		this.execute(query);
		
		//find hans givne id og returner det
		String query2 = "SELECT " +
				"id " +
				"FROM " +
				"Person " +
				"WHERE " +
				"Person.Firstname = '" + p.getFirstname() + "' " +
				"AND " +
				"Person.Surname = '" + p.getSurname() + "' " +
				"AND " +
				"Person.Birthday = '" + p.getBirthday() + "'";
		ResultSet rs = this.execute(query2);
		rs.next();
		return rs.getInt("id");
	}
	
	//Finder en customer ved at s�ge p� en af hans felter (telefonnummer, addresse eller email, har vi valgt at bruge).
	//Constructoren tager imod det, der s�ges efter (f.eks. "Customer.email"), og s�gestrengen ("email@gmail.com")
	public Customer queryFindCustomer(String searchingFor, String arg) throws SQLException {
		String query;
		query = "SELECT " +
				"* " +
				"FROM " +
				"Customer " +
				"WHERE " +
				searchingFor + " = '" + arg+ " '";
		ResultSet rs = this.execute(query);
		rs.next();
		int id = rs.getInt("id");
		String firstname = rs.getString("firstname");
		String surname = rs.getString("surname");
		String address = rs.getString("address");
		String city = rs.getString("city");
		String postalCode = rs.getString("postal_code");
		String country = rs.getString("country");
		String email = rs.getString("email");
		String phoneNumber = rs.getString("phone_number");
		
		Customer c = new Customer(firstname, surname, email, phoneNumber, address, city, postalCode, country);
		c.setId(id);
		return c;
	}

	//Finder alle bookinger foretaget af en bestemt Customer, l�gger dem i en ArrayList, og returner dem
	public ArrayList<Booking> queryFindBookingsMadeBy(int customerId) throws SQLException {
		ArrayList<Booking> bookings = new ArrayList<Booking>();
		String query;
		query = "SELECT " +
				"* " +
				"FROM " +
				"Booking " +
				"WHERE " +
				"Booking.customer_id = " + customerId;
		ResultSet rs = this.execute(query);
		 
		while(rs.next()) {
			int id = rs.getInt("id");
			int dId = rs.getInt("departure_id");
			String seats = rs.getString("seats");
			String passengers = rs.getString("passenger_ids");
			
			Booking b = new Booking(dId, seats, passengers, true);
			b.setId(id);
			bookings.add(b);
		}
		return bookings;
	}
	
	//Sletter en booking fra databasen
	public void queryDeleteBooking(int id) throws SQLException {
		String query;
		query = "DELETE " +
				"FROM " +
				"Booking " +
				"WHERE " +
				"Booking.id = " + id;
		this.execute(query);
	}
	
	//Opdaterer en Customers oplysninger i databasen
	public void queryUpdateCustomer(int id, String firstname, String surname, String address, String city, String postalCode, 
			String country, String email, String phoneNumber) throws SQLException {
		String query;
		query = "UPDATE " +
				"Customer " +
				"SET " +
				"Firstname = '" + firstname +"', " +
				"Surname = '" + surname + "', " +
				"Address = '" + address + "', " +
				"City = '" + city + "', " +
				"Postal_Code = '" + postalCode + "', " +
				"Country = '" + country + "', " +
				"Email = '" + email + "', " +
				"Phone_Number = '" + phoneNumber + "' " +
				"WHERE " +
				"Customer.id = " +id;
		this.execute(query);
	}
	
	//Opdaterer en Persons oplysninger id atabasen
	public void queryUpdatePassenger(int id, String firstname, String surname, String birthday) throws SQLException {
		String query;
		query = "UPDATE " +
				"Person " +
				"SET " +
				"Firstname = '" + firstname + "', " +
				"Surname = '" + surname + "', " +
				"Birthday = '" + birthday + "' " +
				"WHERE " +
				"Person.id = " +id;
		this.execute(query);
	}
	
	//Opdaterer valgte s�der i en booking
	public void queryUpdateBookingSeats(int id, String seats) throws SQLException {
		String query;
		query = "UPDATE " +
				"Booking " +
				"SET " +
				"seats = '" + seats +"' " +
				"WHERE " +
				"Booking.id = " +id;
		this.execute(query);
	}
}
