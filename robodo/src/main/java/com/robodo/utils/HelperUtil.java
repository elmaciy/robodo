package com.robodo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.tomcat.util.json.JSONParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.robodo.model.EmailTemplate;
import com.robodo.model.ProcessInstanceStep;

public class HelperUtil {
	
	public static String normalizeAmount(String priceStr) {
		String removedCurrency=priceStr.replaceAll("₺", "").strip();
		if (removedCurrency.length()<3) {
			removedCurrency="0000"+removedCurrency;
		}
		int len=removedCurrency.length();
		String decimalChar=removedCurrency.substring(len-3,len-2);
		String tamSayi=removedCurrency;
		String ondalik="00";
		
		if ((",.".contains(decimalChar))) {
			tamSayi=removedCurrency.substring(0,len-3);
			ondalik=removedCurrency.substring(len-2);
		}
		
		tamSayi=tamSayi.replace(".", "").replace(",","");
		
		double tamsayiDbl=Double.valueOf(tamSayi);
		double ondalikDbl=Double.valueOf(ondalik)/100;
		
		DecimalFormat df = new DecimalFormat();
		df.setGroupingSize(3);
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		df.setDecimalFormatSymbols(symbols);
		
		return df.format(tamsayiDbl+ondalikDbl);
	}

	public static byte[] getFileAsByteArray(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			return  Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String hashMap2String(HashMap<String,String> hm) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(hm);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HashMap<String,String>  String2HashMap(String data) {
		
		HashMap<String,String> hm= new HashMap<String,String>();
		try {
			JSONParser parser = new JSONParser(data);
			LinkedHashMap<String, Object> linkedList = parser.parseObject();
			linkedList.keySet().stream().forEach(key->{
				hm.put(key, (String) linkedList.get(key));
			});
			return hm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String,String>();
	}

	public static boolean sendEmailByTemplate(EmailTemplate emailTemplate, ProcessInstanceStep step, RunnerUtil runnerUtil) {
		String instanceVariables = step.getProcessInstance().getInstanceVariables();
		HashMap<String, String> hmVars=String2HashMap(instanceVariables);
		emailTemplate.setSubject(replaceVariables(emailTemplate.getSubject(),hmVars));
		emailTemplate.setBody(replaceVariables(emailTemplate.getBody(), hmVars));
		return sendEmail(emailTemplate, runnerUtil);
		
	}

	
	private static String replaceVariables(String inputStr, HashMap<String, String> hmVars) {
		StringBuilder sb=new StringBuilder(inputStr);
		
		List<String> keys = hmVars.keySet().stream().collect(Collectors.toList());
		
		for (String key : keys) {
			String find="${%s}".formatted(key);
			System.err.println("xxxxxxxxxxx searching for : %s".formatted(find));
			
			while(true) {
				int pos=sb.indexOf(find);
				if (pos==-1) break;
				sb.delete(pos, pos+find.length());
				sb.insert(pos, hmVars.get(key));
			}
		}
		
		
		return sb.toString();
	}
	
	
	private static boolean sendEmail(EmailTemplate emailTemplate, RunnerUtil runnerUtil) {
		String from = "yildirayelmaci@gmail.com";
		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("yildirayelmaci@gmail.com", "zffxuvhipdjvlfkn");
            }
        });
		
		//test amacli. silinecek sonra
		session.setDebug(true);
		
		try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            List<String> toList = Splitter.on(",").trimResults().splitToList(emailTemplate.getToAddress().replaceAll(";", ","));
            for (String email : toList) {
            	if (!isValidEmailAddress(email)) continue;
            	message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }
            
            if (emailTemplate.getCc()!=null) {
            	List<String> ccList = Splitter.on(",").trimResults().splitToList(emailTemplate.getCc().replaceAll(";", ","));
                for (String email : ccList) {
                	if (!isValidEmailAddress(email)) continue;
                	message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
                }
            }
            
            if (emailTemplate.getBcc()!=null) {
            	List<String> bccList = Splitter.on(",").trimResults().splitToList(emailTemplate.getBcc().replaceAll(";", ","));
                for (String email : bccList) {
                	if (!isValidEmailAddress(email)) continue;
                	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
                }
            }
            
            message.setSubject(emailTemplate.getSubject());
            message.setContent( emailTemplate.getBody(), "text/html; charset=utf-8" );
            
            runnerUtil.logger("Sending email [%s] to %s".formatted(emailTemplate.getSubject(), emailTemplate.getToAddress()));
            // Send message
            Transport.send(message);
            runnerUtil.logger("Sent message successfully....");
            return true;
        } catch (Exception mex) {
            mex.printStackTrace();
            return false;
        }
		
		
	}

	private static boolean isValidEmailAddress(String email) {
		 boolean valid = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException e) {
		      valid = false;
		   }
		   return valid;
	}


}
