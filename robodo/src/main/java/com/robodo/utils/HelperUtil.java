package com.robodo.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.robodo.model.EmailTemplate;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.model.Tokenization;

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

	
	public static String hashMap2String(HashMap<String,String> hm) {
		List<KeyValue> list = hm.keySet().stream().map(key-> {
			return new KeyValue(key, hm.get(key));
		}).toList();
		String jsonStr = JSONArray.toJSONString(list);
		return jsonStr;
	}
	
	public static HashMap<String,String>  String2HashMap(String data) {
		HashMap<String,String> hm= new HashMap<String,String>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			KeyValue[] values = mapper.readValue(data, KeyValue[].class);
			for (KeyValue kv : values) {
				hm.put(kv.getKey(), kv.getValue());
			}
			return hm;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void sendEmailByTemplate(EmailTemplate emailTemplate, ProcessInstanceStep step, RunnerUtil runnerUtil) {
		String instanceVariables = step.getProcessInstance().getInstanceVariables();
		HashMap<String, String> hmVars=String2HashMap(instanceVariables);
		long tokenDuration=Long.valueOf(runnerUtil.processService.getEnvProperty("token.duration"));
		Tokenization token = Tokenization.generateNewToken(runnerUtil.processService,"FOR_APPROVAL",step.getProcessInstance().getCode(),  tokenDuration);
		hmVars.put("token", token.getToken());
		emailTemplate.setSubject(replaceVariables(emailTemplate.getSubject(),hmVars));
		emailTemplate.setBody(replaceVariables(emailTemplate.getBody(), hmVars));
		sendEmail(step.getProcessInstance(), emailTemplate, runnerUtil);
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
		return List.of("instanceId","processInstance.code").stream().anyMatch(p->p.equals(key));
	}

	private static void sendEmail(ProcessInstance processInstance, EmailTemplate emailTemplate, RunnerUtil runnerUtil) {
		
		
		String smtpFrom = runnerUtil.getEnvironmentParameter("mail.from");
		String smtpHost=runnerUtil.getEnvironmentParameter("mail.smtp.host");
		String smtpPort=runnerUtil.getEnvironmentParameter("mail.smtp.port");
		String smtpSSLEnabled=runnerUtil.getEnvironmentParameter("mail.smtp.ssl.enable");
		String smtpAuth = runnerUtil.getEnvironmentParameter("mail.smtp.auth");
		String googleAuthKey=runnerUtil.getEnvironmentParameter("mail.google.authentication.key");

		runnerUtil.setVariable("mail.template.code", emailTemplate.getCode());
		runnerUtil.setVariable("mail.template.subject", emailTemplate.getSubject());
		runnerUtil.setVariable("mail.template.to", emailTemplate.getToAddress());
		runnerUtil.setVariable("mail.template.cc", emailTemplate.getCc());
		runnerUtil.setVariable("mail.template.bcc", emailTemplate.getBcc());
		runnerUtil.setVariable("mail.template.body", emailTemplate.getBody());
		
		runnerUtil.setVariable("mail.from", smtpFrom);
		runnerUtil.setVariable("mail.smtp.host", smtpHost);
		runnerUtil.setVariable("mail.smtp.port", smtpPort);
		runnerUtil.setVariable("mail.smtp.ssl.enable", smtpSSLEnabled);
		runnerUtil.setVariable("mail.smtp.auth", smtpAuth);
		runnerUtil.setVariable("mail.google.authentication.key", googleAuthKey);

		
		
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.ssl.enable", smtpSSLEnabled);
		properties.put("mail.smtp.auth", smtpAuth);
		

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpFrom, googleAuthKey);
            }
        });

		try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpFrom));
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
            
            MimeMultipart multipart = new MimeMultipart("related");
            
            
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(emailTemplate.getBody(), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            
            processInstance.getSteps().forEach(step->{
            	List<ProcessInstanceStepFile> files= runnerUtil.processService.getProcessInstanceStepFilesByStepId(step);
            	files.forEach(file-> {
            		if (file.isAttach()) {
            			// second part (the image)
                        BodyPart  messageImagePart = new MimeBodyPart();
                        runnerUtil.logger("adding file [%s] to the mail".formatted(file.getDescription()));
            			DataSource bds = new ByteArrayDataSource(byteArr2Blob(file.getBinarycontent()), ProcessInstanceStepFile.MIME_TYPE_SCREENSHOT);
         
            			try {
            				
        					messageImagePart.setDataHandler(new DataHandler(bds));
        					messageImagePart.setFileName("%s.png".formatted(file.getDescription()));
        					messageImagePart.setDescription(file.getDescription());
        	    			messageImagePart.setHeader("Content-ID", "<image>");
        	                multipart.addBodyPart(messageImagePart);
        				} catch (MessagingException e) {
        					e.printStackTrace();
        					String err="error attaching file [%s] : %s".formatted(file.getDescription(), e.getMessage());
        					throw new RuntimeException(err);
        				}
            		}
            	});
    			
            });
			
            message.setContent(multipart);
            
            runnerUtil.logger("Sending email [%s] to %s".formatted(emailTemplate.getSubject(), emailTemplate.getToAddress()));
            // Send message
            Transport.send(message);
            runnerUtil.logger("Sent message successfully....");
        } catch (Exception mex) {
			String err="error transmitting mail: %s".formatted(mex.getMessage());
			throw new RuntimeException(err);
        }
		
		
	}

	public static boolean isValidEmailAddress(String email) {
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

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public static byte[] byteArr2Blob(Blob blob) {
		try {
			int blobLength = (int) blob.length();  
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			return blobAsBytes;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static boolean isValidForFileName(String filename) {
		File file = new File(filename);
	    boolean created = false;
	    try {
	        created = file.createNewFile();
	        if (created) {
	        	file.delete();
	        }
	        return created;
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	return created;
	    } 
	}


	public static String limitString(String value, int maxLen) {
		if (value==null) {
			return null;
		}
		
		if (value.length()<=maxLen) {
			return value;
		}
		
		return "%s...".formatted(StringUtils.left(value, maxLen));
	}
	
	
	public static boolean patternMatches(String txt, String regexPattern) {
	    try {
			return Pattern.compile(regexPattern)
		      .matcher(txt)
		      .matches();	
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	
	public static boolean isValidCode(String code) {
		return HelperUtil.patternMatches(code, "^[0-9A-Za-z\\.\\\\_\\\\$]{5,100}$");
	}


}
