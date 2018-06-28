package com.sellaspot.datamodel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class RESTData {

	public static boolean updateuser(String userId, String sessionId, String firstName, String lastName,
			String email, String password, String phone, String city,
			String zip, String state, String country, int notification) {

		
		
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/updateuser";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		Serializer serializer = new Persister();

		try {
			// Create a RegisterUser
			UpdateUserRequest updateUserRequest = new UpdateUserRequest();
			updateUserRequest.setFirstname(firstName);
			updateUserRequest.setLastname(lastName);
			updateUserRequest.setPassword(password);
			updateUserRequest.setEmail(email);
			updateUserRequest.setPhone(phone);
			updateUserRequest.setCity(city);
			updateUserRequest.setZip(Integer.parseInt(zip));
			updateUserRequest.setState(state);
			updateUserRequest.setCountry(country);
			updateUserRequest.setNotification(notification);
			
			serializer.write(updateUserRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("updateUser REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPut httpPut = new HttpPut(urlToSendRequest);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPut.addHeader("user-id", userId);
			httpPut.addHeader("session-id", sessionId);
			httpPut.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPut);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("updateUser RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			UpdateUserResponse updateUserResponse = deserializer.read(
					UpdateUserResponse.class, new StringReader(input));

			if (updateUserResponse.getStatus() != null
					&& updateUserResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean validateSession(String userId, String sessionId) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/validate/" + userId+"?sessionid=" + sessionId;
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getSpots RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			SessionValidationResponse sessionValidationResponse = deserializer.read(SessionValidationResponse.class, new StringReader(
					input));

			if (sessionValidationResponse.getStatus() != null
					&& sessionValidationResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}
	
	public static UserDetailResponse getUserDetails(String userId, String sessionId, String otherUserId) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/userdetail/" + otherUserId;
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getUserDetails RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			UserDetailResponse UserDetailResponse = deserializer.read(UserDetailResponse.class, new StringReader(
					input));
			
			return UserDetailResponse;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}
	
	public static List<Item> filterByCategory(String userId, String sessionId, String category) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/filter/" + category + "?expired=0&start=0&end=20";
		urlToSendRequest = urlToSendRequest.replaceAll(" ", "%20");
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("filterByCategory RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Items itemList = deserializer.read(Items.class, new StringReader(input));
			System.out.println(itemList);
			
			return itemList.getItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	
	public static boolean sellaspot(String userId, String sessionId,
			String venueName, 
			String itemDescription, String category,
			String price, String quantity,
			String latitude, String longitude, String city, String state, String address) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/sellaspot";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Create a spot
			SellASpotRequest sellASpotRequest = new SellASpotRequest();
			sellASpotRequest.setVenuename(venueName);
			sellASpotRequest.setItemdescription(itemDescription);
			sellASpotRequest.setCategory(category);
			sellASpotRequest.setPrice(price);
			sellASpotRequest.setQuantity(quantity);
			sellASpotRequest.setLatitude(latitude);
			sellASpotRequest.setLongitude(longitude);
			sellASpotRequest.setItemusertype("seller");
			sellASpotRequest.setAddress(address);
			sellASpotRequest.setCity(city);
			sellASpotRequest.setState(state);
			
			serializer.write(sellASpotRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("sellaspot REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("Content-Type", "application/xml");
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("sellASpot RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			SellASpotResponse sellASpotResponse = deserializer.read(
					SellASpotResponse.class, new StringReader(input));

			if (sellASpotResponse.getStatus() != null
					&& sellASpotResponse.getStatus().equalsIgnoreCase("success")
					&& sellASpotResponse.getReason().equalsIgnoreCase(
							"item added successfully")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	
	public static boolean registerDevice(String userId, String sessionId, String registrationId) {
		
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/registerdevice";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			
			DeviceRegisterRequest deviceRegisterRequest = new DeviceRegisterRequest();
			deviceRegisterRequest.setDeviceType("google");
			deviceRegisterRequest.setToken(registrationId);
			
			serializer.write(deviceRegisterRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("registerDevice REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("Content-Type", "application/xml");
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("registerDevice RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			DeviceRegisterResponse deviceRegisterResponse = deserializer.read(DeviceRegisterResponse.class,
					new StringReader(input));

			if (deviceRegisterResponse.getStatus() != null
					&& deviceRegisterResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}
	
	public static List<Item> getSpots(String userId, String sessionId, String userType) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/getspots?expired=0&start=0&end=20&usertype=" + userType;
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getSpots RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Items itemList = deserializer.read(Items.class, new StringReader(
					input));
			System.out.println(itemList);
			return itemList.getItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public static Item getSpotById(String itemId, String userId,
			String sessionId) {
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/getspots/"
				+ itemId;
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getSpotById RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Item item = deserializer.read(Item.class, new StringReader(input));
			System.out.println(item);
			return item;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String register(String firstName, String lastName,
			String email, String password, String phone, String city,
			String zip, String state, String country) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/register";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Create a RegisterUser
			RegisterUser registerUser = objFactory.createRegisterUser();
			registerUser.setFirstname(firstName);
			registerUser.setLastname(lastName);
			registerUser.setPassword(password);
			registerUser.setEmail(email);
			registerUser.setPhone(phone);
			registerUser.setCity(city);
			registerUser.setZip(Integer.parseInt(zip));
			registerUser.setState(state);
			registerUser.setCountry(country);

			serializer.write(registerUser, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("register REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("register RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			RegisterResponse regiterResponse = deserializer.read(
					RegisterResponse.class, new StringReader(input));

			if (regiterResponse.getStatus() != null
					&& regiterResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return regiterResponse.getUserid() + ","
						+ regiterResponse.getSessionid();
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String registerUser(String email, String password, String phone) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/newregister";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		Serializer serializer = new Persister();

		try {
			// Create a NewRegisterUserRequest
			NewRegisterUserRequest newRegisterUserRequest = new NewRegisterUserRequest();
			newRegisterUserRequest.setEmail(email);
			newRegisterUserRequest.setPassword(password);
			newRegisterUserRequest.setPhone(phone);

			serializer.write(newRegisterUserRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("NewRegisterUserRequest : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("NewRegisterUserResponse : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			NewRegisterUserResponse regiterResponse = deserializer.read(
					NewRegisterUserResponse.class, new StringReader(input));

			if (regiterResponse.getStatus() != null
					&& regiterResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return regiterResponse.getUserid() + ","
						+ regiterResponse.getSessionid();
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean updateProfile(String userId, String sessionId, String firstName, String lastName,
			String password, String email, String phone, String city, String zip,
			String state, String country) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/register";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Create a RegisterUser
			RegisterUser registerUser = objFactory.createRegisterUser();
			registerUser.setFirstname(firstName);
			registerUser.setLastname(lastName);
			registerUser.setPassword(password);
			registerUser.setEmail(email);
			registerUser.setPhone(phone);
			registerUser.setCity(city);
			registerUser.setZip(Integer.parseInt(zip));
			registerUser.setState(state);
			registerUser.setCountry(country);

			serializer.write(registerUser, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("updateProfile REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPut httpPut = new HttpPut(urlToSendRequest);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			//httpPost.addHeader("Content-Type", "application/xml");
			httpPut.addHeader("user-id", userId);
			httpPut.addHeader("session-id", sessionId);
			
			httpPut.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPut);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("updateProfile RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			RegisterResponse regiterResponse = deserializer.read(
					RegisterResponse.class, new StringReader(input));

			if (regiterResponse.getStatus() != null
					&& regiterResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean spotIt(String userId, String sessionId,
			String venueName, String myLocation,
			String itemDescription, String category,
			String price, String quantity,
			String itemUserType, String notified) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/spotit";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Spot it

			BuyASpotRequest buyASpotRequest = objFactory
					.createBuyASpotRequest();
			buyASpotRequest.setVenuename(venueName);
			buyASpotRequest.setAddress(myLocation);
			//buyASpotRequest.setCity(city);
			//buyASpotRequest.setState(state);
			buyASpotRequest.setItemdescription(itemDescription);
			//buyASpotRequest.setCountry(country);
			buyASpotRequest.setCategory(category);
			//buyASpotRequest.setDatetimeforitem(dateTime);
			buyASpotRequest.setPrice(Integer.parseInt(price));
			buyASpotRequest.setQuantity(Integer.parseInt(quantity));
			//buyASpotRequest.setUserdescription(userDescription);
			buyASpotRequest.setItemusertype(itemUserType);
			buyASpotRequest.setNotified(notified);

			serializer.write(buyASpotRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("spotIt REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("Content-Type", "application/xml");
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("spotIt RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			BuyASpotResponse buyASpotResponse = deserializer.read(
					BuyASpotResponse.class, new StringReader(input));

			if (buyASpotResponse.getStatus() != null
					&& buyASpotResponse.getStatus().equalsIgnoreCase("success")
					&& buyASpotResponse.getReason().equalsIgnoreCase(
							"item added successfully")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean bidOnSpot(String userId, String sessionId,
			String itemId, String bidPrice, String userDescription,
			String bidUserType) {

		/*
		 * POST - set header: user-id and session-id
		 * http://api.sellaspot.com/SpotMobAPI/resources/bid <?xml version="1.0"
		 * encoding="UTF-8" standalone="yes"?> <BidRequest> <itemid>2</itemid>
		 * <bidprice>10</bidprice> <userdescription>My name is
		 * blah</userdescription> <bidusertype>seller</bidusertype>
		 * </BidRequest>
		 */

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/bid";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Spot it

			BidRequest bidRequest = objFactory.createBidRequest();
			bidRequest.setItemid(Integer.parseInt(itemId));
			bidRequest.setBidprice(Integer.parseInt(bidPrice));
			bidRequest.setUserdescription(userDescription);
			bidRequest.setBidusertype(bidUserType);
			bidRequest.setUserid(Integer.parseInt(userId));
			serializer.write(bidRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("bidOnSpot REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("Content-Type", "application/xml");
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("bidOnSpot RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			BidResponse bidResponse = deserializer.read(BidResponse.class,
					new StringReader(input));

			if (bidResponse.getStatus() != null
					&& bidResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static String login(String email, String password) {
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/login";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Create a LoginRequest
			LoginRequest loginRequest = objFactory.createLoginRequest();
			loginRequest.setEmail(email);
			loginRequest.setPassword(password);

			serializer.write(loginRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("login REQUEST : " + xmlContentToSend);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("login RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			LoginResponse loginResponse = deserializer.read(
					LoginResponse.class, new StringReader(input));

			if (loginResponse.getStatus() != null
					&& loginResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return loginResponse.getUserid() + ","
						+ loginResponse.getSessionid() + "," + loginResponse.getFirstName();
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Bid> getMyBids(String userId, String sessionId) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/getmybids?expired=1";
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getMyBids RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Bids bidList = deserializer.read(Bids.class,
					new StringReader(input));
			System.out.println(bidList);
			return bidList.getBid();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Bid getBidById(String bidId, String userId, String sessionId) {
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/biddetail/"
				+ bidId + "?expired=1";
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getBidsById RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Bid bid = deserializer.read(Bid.class, new StringReader(input));
			System.out.println(bid);
			return bid;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Item> getItems(String userId, String sessionId) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/getitems?start=0&end=40&expired=1";
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getItems RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Items itemList = deserializer.read(Items.class, new StringReader(
					input));
			System.out.println(itemList);
			return itemList.getItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public static List<Bid> getAllBidsById(String itemId, String userId,
			String sessionId) {
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/getbids/"
				+ itemId + "?expired=1";
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getAllBidsById RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Bids bids = deserializer.read(Bids.class, new StringReader(input));
			System.out.println(bids);
			return bids.getBid();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static boolean acceptBid(String userId, String sessionId, int itemId, int bidId) {
		
		/*
		 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<AcceptBidRequest>
<itemid>1</itemid>
<bidid>1</bidid>
</AcceptBidRequest>
		 */
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/acceptbid";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();
		Serializer serializer = new Persister();

		try {
			// Create a AcceptBidRequest
			AcceptBidRequest acceptBidRequest = objFactory.createAcceptBidRequest();
			acceptBidRequest.setItemid(itemId);
			acceptBidRequest.setBidid(bidId);
			serializer.write(acceptBidRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("acceptBid REQUEST : " + xmlContentToSend);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);
			
			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("acceptBid RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			AcceptBidResponse acceptBidResponse = deserializer.read(
					AcceptBidResponse.class, new StringReader(input));

			if (acceptBidResponse.getStatus() != null
					&& acceptBidResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public static List<String> getAllCitiesForState(String state) {
		List<String> cityList = new ArrayList<String>();
		
		String urlToSendRequest = "http://api.sba.gov/geodata/city_links_for_state_of/" +state.toLowerCase()+".xml";
		String targetDomain = "api.sba.gov";

		try {
			// Create a AcceptBidRequest
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 80, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			
//			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
//			entity.setContentType("application/xml");
//			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getAllCitiesForState RESPONSE : " + sb.toString());
			
			parseXML(sb.toString(), cityList);
			return cityList;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
 	}
	
	public static void parseXML(String response, List<String> cityList) {

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			//response = response.replaceAll("<!DOCTYPE sites SYSTEM \"http://api.sba.gov/dtd/geodata.dtd\">", "");
			Document doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));
			
			doc.getDocumentElement().normalize();

			NodeList sites = doc.getElementsByTagName("sites");
			int sitesLength = sites.getLength();

			for (int s = 0; s < sitesLength; s++) {

				Node sitesNode = sites.item(s);
				
				if (sitesNode.getNodeType() == Node.ELEMENT_NODE) {

					Element sitesElement = (Element) sitesNode;

					NodeList siteList = sitesElement.getElementsByTagName("site");
					
					for (int i = 0; i < siteList.getLength(); i++) {
						
						Node siteNode = siteList.item(i);
						
						if (siteNode.getNodeType() == Node.ELEMENT_NODE) {

							Element siteElement = (Element) siteNode;
							NodeList nameList = siteElement.getElementsByTagName("name");
							Node nameNode = nameList.item(0);
							Element nameElem = (Element)nameNode;
							cityList.add(nameElem.getTextContent());
							
							
						}
					}
				}
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	
	public static boolean rateUser(String userId, String sessionId, int ratedItemId, int ratedUserId, int rating, String userType, String comments) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/rateuser";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		Serializer serializer = new Persister();

		try {
			// Spot it
			/*
			 * POST BELOW to :  rateuser
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<RateUserRequest>
	<rateduserid>2</rateduserid>
	<rateditemid>3</rateditemid>
	<raterusertype>buyer</raterusertype>
	<comments>blah..blah</comments>
	<rating>2</rating>
	</RateUserRequest>
			 */
			RateUserRequest rateUserRequest = new RateUserRequest();
			rateUserRequest.setRateduserid(ratedUserId);
			rateUserRequest.setRateditemid(ratedItemId);
			rateUserRequest.setRaterusertype(userType);
			rateUserRequest.setComments(comments);
			rateUserRequest.setRating(rating);
			
			serializer.write(rateUserRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("rateUser REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("Content-Type", "application/xml");
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("rateUser RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			RateUserResponse rateUserResponse = deserializer.read(
					RateUserResponse.class, new StringReader(input));

			if (rateUserResponse.getStatus() != null
					&& rateUserResponse.getStatus().equalsIgnoreCase("success")) {
				// If success, enter the user id and the sessionId in local db
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public static UserRating getRating(String userId, String sessionId, int bidderUserId) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/getuserratings/" + bidderUserId+"?start=0&end=10";
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("getMyBids RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			UserRating userRating = deserializer.read(UserRating.class,
					new StringReader(input));
			System.out.println(userRating);
			return userRating;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static boolean cancelBid(String userId, String sessionId,
			int itemId, int bidId) {

		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/cancelbid";
		String targetDomain = "api.sellaspot.com";
		StringWriter writer = new StringWriter();
		Serializer serializer = new Persister();

		try {

			CancelBidRequest cancelBidRequest = new CancelBidRequest();
			cancelBidRequest.setBidid(bidId);
			cancelBidRequest.setItemid(itemId);
			
			serializer.write(cancelBidRequest, writer);

			String xmlContentToSend = writer.toString();
			System.out.println("cancelBid REQUEST : " + xmlContentToSend);

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpPost httpPost = new HttpPost(urlToSendRequest);
			httpPost.addHeader("Content-Type", "application/xml");
			httpPost.addHeader("user-id", userId);
			httpPost.addHeader("session-id", sessionId);

			StringEntity entity = new StringEntity(xmlContentToSend, "UTF-8");
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(targetHost, httpPost);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("cancelBid RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();
			CancelBidResponse cancelBidResponse = deserializer.read(CancelBidResponse.class,
					new StringReader(input));

			if (cancelBidResponse.getStatus() != null
					&& cancelBidResponse.getStatus().equalsIgnoreCase("success")) {

				return true;
		
			} else {
			
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public static List<Item> searchSpots(String userId, String sessionId, String searchString) {
		
		searchString = searchString.replaceAll(" ", "%20");
		String urlToSendRequest = "http://api.sellaspot.com/SpotMobAPI/resources/search/" + searchString;
		String targetDomain = "api.sellaspot.com";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpHost targetHost = new HttpHost(targetDomain, 8080, "http");
			HttpGet httpGet = new HttpGet(urlToSendRequest);
			httpGet.addHeader("Content-Type", "application/xml");
			httpGet.addHeader("user-id", userId);
			httpGet.addHeader("session-id", sessionId);

			HttpResponse response = httpClient.execute(targetHost, httpGet);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s = "";
			StringBuilder sb = new StringBuilder();

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			System.out.println("searchSpots RESPONSE : " + sb.toString());

			String input = sb.toString();
			Serializer deserializer = new Persister();

			Items itemList = deserializer.read(Items.class, new StringReader(
					input));
			System.out.println(itemList);
			return itemList.getItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}
