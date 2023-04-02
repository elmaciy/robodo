package com.robodo.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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

}
