package com.robodo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
			
			while(true) {
				int pos=sb.indexOf(find);
				if (pos==-1) break;
				sb.delete(pos, pos+find.length());
				if (isSecret(key)) {
					sb.insert(pos, encrypt(hmVars.get(key)));
				} else {
					sb.insert(pos, hmVars.get(key));
				}
				
			}
		}
		
		
		return sb.toString();
	}
	
	
	private static boolean isSecret(String key) {
		return List.of("instanceId","processinstance.code").stream().anyMatch(p->p.equals(key));
	}

	private static boolean sendEmail(EmailTemplate emailTemplate, RunnerUtil runnerUtil) {
		String from = "yildirayelmaci@gmail.com";
		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "zffxuvhipdjvlfkn");
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

	private static final String ALGORITHM = "AES";
	private static final String passKey="!228yESIamaGoodRobot!9003$12331";

	private static SecretKeySpec secretKey;
    private static byte[] key;
    
    

    public static void prepareSecreteKey() {
        MessageDigest sha = null;
        try {
            key = passKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    static {
    	prepareSecreteKey();
    }

    public static String encrypt(String in) {
    	byte[] input = in.getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] arr= cipher.doFinal(input);	
            return byteArr2HexString(arr);
        } catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
    
    public static String decrypt(String in) {
    	byte[] bytes=hexStringToByteArr(in);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] bytesDecrypted= cipher.doFinal(bytes);
            return new String(bytesDecrypted, Charset.forName("utf8"));
        } catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }   

    private static String byteArr2HexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(String.format("%02X", b));
	    }
	    return sb.toString();
	}
    
    private static byte[] hexStringToByteArr(String hexString) {
    	byte[] bytes=new byte[hexString.length()/2];
    	int loc=0;
    	for (int i=0;i<hexString.length()/2;i++) {
            int firstDigit = toDigit(hexString.charAt(i*2+0));
            int secondDigit = toDigit(hexString.charAt(i*2+1));
            byte b= (byte) ((firstDigit << 4) + secondDigit);
            bytes[loc++]=b;
    	}
    	return bytes;
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
              "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

	
    
    public static void main(String[] args) {
    	String str="YıldırayElmacı/123";
    	String encrypted = new String(encrypt(str));
    	System.err.println("encrypted : "+encrypted);
    	String decrypted = new String(decrypt(encrypted));
    	System.err.println("encrypted : "+decrypted);
    }
 
}
