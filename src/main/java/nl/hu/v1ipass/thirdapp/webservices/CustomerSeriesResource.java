package nl.hu.v1ipass.thirdapp.webservices;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import nl.hu.v1ipass.thirdapp.model.Customer;
import nl.hu.v1ipass.thirdapp.model.CustomerSeries;
import nl.hu.v1ipass.thirdapp.model.CustomerSeriesService;
import nl.hu.v1ipass.thirdapp.model.CustomerSeriesServiceProvider;
import nl.hu.v1ipass.thirdapp.model.CustomerService;
import nl.hu.v1ipass.thirdapp.model.CustomerServiceProvider;
import nl.hu.v1ipass.thirdapp.model.SerieServiceProvider;
import nl.hu.v1ipass.thirdapp.model.Series;
import nl.hu.v1ipass.thirdapp.model.SeriesService;

@Path("/customerseries")
public class CustomerSeriesResource {
CustomerService cs= CustomerServiceProvider.getCustomerService();
SeriesService ss=SerieServiceProvider.getSeriesService();
CustomerSeriesService css=CustomerSeriesServiceProvider.getCustomerService();

@Path("{id}+{password}")
@GET
@Produces("application/json")
public String SeriesByCustID(@PathParam("id") int code, @PathParam("password") String password) {
	Customer found = null;
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == code&&password.equals(c.getPassword())) {
	      found = c; break;
	    }
	  }
	JsonArrayBuilder jab = Json.createArrayBuilder();
	Date date = new Date();
	String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
	for (CustomerSeries c : css.getUnfinishedCustomerSeriesbyCustomerID(found, modifiedDate)) {
		JsonObjectBuilder job = createJson(c);
		

		jab.add(job);
	}
	for (CustomerSeries c : css.getFinishedCustomerSeriesbyCustomerID(found, modifiedDate)) {
		JsonObjectBuilder job = createJson(c);
		

		jab.add(job);
	}

	JsonArray array = jab.build();

	return (array.toString());
}
@Path("{id}+{password}/{day}")
@GET
@Produces("application/json")
public String SeriesbyCustIDDate(@PathParam("id") int code, @PathParam("password") String password, @PathParam("day") String day) {
	Customer found = null;
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == code&&password.equals(c.getPassword())) {
	      found = c; break;
	    }
	  }
	JsonArrayBuilder jab = Json.createArrayBuilder();
	Date date = new Date();
	String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
	for (Series c : ss.getSeriesbyCustomerIDDay(found, day, modifiedDate)) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("id", c.getCode());
		job.add("title", c.getTitle());
		job.add("genre", c.getGenre());
		job.add("episodes", c.getEpisodes());
		job.add("score",c.getScore());
		job.add("viewers", c.getViewers());
		job.add("studio", c.getProductionstudio());
		job.add("synopsis", c.getSynopsis());
		

		jab.add(job);
	}

	JsonArray array = jab.build();

	return (array.toString());
}
@Path("{id}+{password}/finished")
@GET
@Produces("application/json")
public String FinishedSeriesByCustID(@PathParam("id") int code, @PathParam("password") String password){
	Customer found = null;
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == code&&password.equals(c.getPassword())) {
	      found = c; break;
	    }
	  }
	JsonArrayBuilder jab = Json.createArrayBuilder();
	Date date = new Date();
	String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
	for (CustomerSeries custser: css.getFinishedCustomerSeriesbyCustomerID(found,modifiedDate)) {
		Series s=ss.getSeriesbyCode(custser.getSeriesID());
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("id", custser.getSeriesID());
		job.add("title", s.getTitle());
		job.add("genre", s.getGenre());
		job.add("episodes", s.getEpisodes());
		job.add("score",s.getScore());
		job.add("viewers", s.getViewers());
		job.add("synopsis", s.getSynopsis());
		job.add("myscore", custser.getScore());
		

		jab.add(job);
	}

	JsonArray array = jab.build();

	return (array.toString());
}
@Path("{id}+{password}/unfinished")
@GET
@Produces("application/json")
public String UnfinishedSeriesByCustID(@PathParam("id") int code, @PathParam("password") String password){
	Customer found = null;
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == code&&password.equals(c.getPassword())) {
	      found = c; break;
	    }
	  }
	JsonArrayBuilder jab = Json.createArrayBuilder();
	Date date = new Date();
	String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
	for (CustomerSeries custser: css.getUnfinishedCustomerSeriesbyCustomerID(found,modifiedDate)) {
		Series s=ss.getSeriesbyCode(custser.getSeriesID());
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("id", custser.getSeriesID());
		job.add("title", s.getTitle());
		job.add("genre", s.getGenre());
		job.add("episodes", s.getEpisodes());
		job.add("score",s.getScore());
		job.add("viewers", s.getViewers());
		job.add("synopsis", s.getSynopsis());
		job.add("email", custser.getEmail());
		

		jab.add(job);
	}

	JsonArray array = jab.build();

	return (array.toString());
}
@POST
@Produces("application/json")
public String createCustomer(InputStream is) {
  JsonObject object = Json.createReader(is).readObject();
  int CustID = object.getInt("CustomerID");
  int SeriesID = object.getInt("SeriesID");
  
  CustomerSeries cs = new CustomerSeries(CustID, SeriesID);
  css.addEntry(cs);
  return csToJson(cs).build().toString();
}
@DELETE
@Path("delete/{CustId}+{SeriesId}")
public Response deleteCustomer(@PathParam("CustId") int CustId, @PathParam("SeriesId") int SeriesId) {
  Customer found = null;
  Series found2=null;
  Date date = new Date();
	String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
  for (Customer c : cs.getAllCustomers()) {
    if (c.getId() == CustId) {
      found = c; break;
    }
  }
  for (Series s : ss.getAllAiringSeries(modifiedDate)) {
	    if (s.getCode() == SeriesId) {
	      found2 = s; break;
	    }
	  }
  if (found == null||found2==null) {
    return Response.status(Response.Status.NOT_FOUND).build();
  } else {
    css.deleteEntry(found, found2);
    return Response.ok().build();
  }
}
@PUT
@Path("/score")
public Response updateScore(@FormParam("customer") int custid, @FormParam("password") String password, @FormParam("series") int seriesid,
        @FormParam("score") int score) {
	Customer found = null;
	  Series found2=null;
	  System.out.println(score);
	  
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == custid&&c.getPassword().equals(password)) {
	      found = c; break;
	    }
	  }
	  for (Series s : ss.getAllSeries()) {
		    if (s.getCode() == seriesid) {
		      found2 = s; break;
		    }
		  }
	  System.out.println(found.getName()+found2.getTitle());
	  if (found == null||found2==null) {
		  Map<String, String> messages = new HashMap<String, String>();
	      messages.put("error", "Not found");
		    return Response.status(404).entity(messages).build();
		  } else {
			  css.updateScore(found, found2, score);
			  return Response.ok().build();
	      }
	  }
@PUT
@Path("/status")
public Response updateStatus(@FormParam("customer") int custid, @FormParam("password") String password, @FormParam("series") int seriesid,
        @FormParam("status") String status) {
	Customer found = null;
	  Series found2=null;
	  
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == custid&&c.getPassword().equals(password)) {
	      found = c; break;
	    }
	  }
	  for (Series s : ss.getAllSeries()) {
		    if (s.getCode() == seriesid) {
		      found2 = s; break;
		    }
		  }
	  System.out.println(found.getName()+found2.getTitle());
	  if (found == null||found2==null) {
		  Map<String, String> messages = new HashMap<String, String>();
	      messages.put("error", "Not found");
		    return Response.status(404).entity(messages).build();
		  } else {
			  css.updateStatus(found, found2, status);
			  return Response.ok().build();
	      }
	  }
@PUT
@Path("email/{CustomerId}+{SeriesId}+{Email}")
public String updateEmail(@PathParam("CustomerId") int custid, @PathParam("SeriesId") int seriesid,
        @PathParam("Email") String email) {
	Customer found = null;
	  Series found2=null;
	  Date date = new Date();
		String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
		System.out.println(modifiedDate);
		System.out.println("Succes!");
		System.out.println(custid+" "+seriesid+" "+email);
	  for (Customer c : cs.getAllCustomers()) {
	    if (c.getId() == custid) {
	      found = c; break;
	    }
	  }
	  for (Series s : ss.getAllAiringSeries(modifiedDate)) {
		    if (s.getCode() == seriesid) {
		      found2 = s; break;
		    }
		  }
	  System.out.println(found.toString()+found2.toString());
	  if (found == null||found2==null) {
		    return ("Error: Series or Customer not found!");
		  } else {
			  css.updateEmail(found, found2, email);
			  return ("Update succesful");
	      }
	  }
@GET
@Path("sendmail/{CustomerId}")
public String sendEmail(@PathParam("CustomerId") int custid) {
	  Customer found=null;
	  Date date = new Date();
		String modifiedDate= new SimpleDateFormat("dd/MM/YY").format(date);
		System.out.println(modifiedDate);
		 for (Customer c : cs.getAllCustomers()) {
			    if (c.getId() == custid) {
			      found = c; break;
			    }}
	  for (CustomerSeries custs: css.getEmailbyCustomerID(found, modifiedDate)) {
		  Series s=ss.getSeriesbyCode(custs.getSeriesID());
		  String host = "smtp.gmail.com";
	        String from = "luccah06071@gmail.com";
	        String pass = "Burdeos1";
	   try{
		   //genereer email
	        Properties props = System.getProperties();
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtp.user", from);
	        props.put("mail.smtp.password", pass);
	        props.put("mail.smtp.port", "587");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.debug", "true");

	        Session session = Session.getInstance(props, new GMailAuthenticator(from, pass));
	        MimeMessage message = new MimeMessage(session);
	        Address fromAddress = new InternetAddress(from);
	        Address toAddress = new InternetAddress(found.getEmail());

	        message.setFrom(fromAddress);
	        message.setRecipient(Message.RecipientType.TO, toAddress);

	        message.setSubject("A Series you follow is about to end");
	        message.setText("The following series you are following will end soon: "+s.getTitle());
	        Transport transport = session.getTransport("smtp");
	        transport.connect(host, from, pass);
	        message.saveChanges();
	        Transport.send(message);
	        transport.close();


		} catch (MessagingException ex) {
			throw new RuntimeException(ex);  
		}	
	  }
	  return ("Email sent!");
}
@GET
@Path("sendverif/{email}+{code}")
public String sendVerification(@PathParam("email") String email, @PathParam("code") String code) {
		  String host = "smtp.gmail.com";
	        String from = "luccah06071@gmail.com";
	        String pass = "Burdeos1";
	   try{
		   //genereer email
	        Properties props = System.getProperties();
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtp.user", from);
	        props.put("mail.smtp.password", pass);
	        props.put("mail.smtp.port", "587");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.debug", "true");

	        Session session = Session.getInstance(props, new GMailAuthenticator(from, pass));
	        MimeMessage message = new MimeMessage(session);
	        Address fromAddress = new InternetAddress(from);
	        Address toAddress = new InternetAddress(email);

	        message.setFrom(fromAddress);
	        message.setRecipient(Message.RecipientType.TO, toAddress);

	        message.setSubject("Login attempt with Netflix");
	        message.setText("An attempt to login to your account has been made. To continue the login, use this code: "+code);
	        Transport transport = session.getTransport("smtp");
	        transport.connect(host, from, pass);
	        message.saveChanges();
	        Transport.send(message);
	        transport.close();


		} catch (MessagingException ex) {
			throw new RuntimeException(ex);  
		}
	  return ("Email sent!");
}
private JsonObjectBuilder csToJson(CustomerSeries c) {
	JsonObjectBuilder job = Json.createObjectBuilder();
	job.add("custid", c.getCustID());
	job.add("seriesid", c.getSeriesID());
	job.add("myscore", c.getScore());
	job.add("finish", c.getFinished());
	job.add("email",c.getEmail());
	return job;
}
public JsonObjectBuilder createJson(CustomerSeries c) {
	JsonObjectBuilder job = Json.createObjectBuilder();
	try {
Series series=ss.getSeriesbyCode(c.getSeriesID());
Date date = new Date();
Date modifiedDate= new SimpleDateFormat("yyyy-MM-dd").parse(series.getEnddate().substring(0,10));
job.add("id", series.getCode());
job.add("title", series.getTitle());
job.add("genre", series.getGenre());
job.add("episodes", series.getEpisodes());
job.add("duration", series.getDuration());
job.add("airday", series.getAirday());
if (modifiedDate.before(date)) {
	job.add("status", "Finished airing");
}else {
	job.add("status", "Currently airing");
}
job.add("start", series.getStartdate().substring(0,10));
job.add("end", series.getEnddate().substring(0, 10));
job.add("viewers", series.getViewers());
job.add("studio", series.getProductionstudio());
job.add("synopsis", series.getSynopsis());
job.add("score",c.getScore());
job.add("status", c.getStatus());
job.add("email", c.getEmail());
}
catch(java.text.ParseException e) {
	e.printStackTrace();
}
return job;
	
}
}
