package org.pericles.appraisaltool.sciencedemo.util;

/**
 * A tool to change date format. 
 * 
 * @author Jun Zhang
 */
public class DateTool {
	
	/**
	 * check if the given year is a leap year.
	 * @param year the year to check
	 * @return true if it is a leap year
	 */
	public static boolean isLeapYear(int year) {
		if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400) == 0) {
			return true;
		}
		return false;
	}
	
	private static String addZeroToDay(int day) {
		if (day < 10) {
			return "0"+String.valueOf(day);
		}
		else {
			return String.valueOf(day);
		}
	}
	
	//change date format from 2004.66398895933 to 26/08/2004
	public static String changeDateFormat(String dateInString) {
		String year = dateInString.substring(0, dateInString.indexOf("."));
		int dayInYear = 0;
		if (isLeapYear(Integer.valueOf(year))) {
			dayInYear = Math.round(Float.valueOf(dateInString.substring(dateInString.indexOf("."))) * 366);
		}
		else {
			dayInYear = Math.round(Float.valueOf(dateInString.substring(dateInString.indexOf("."))) * 365);
		}
		String dayAndMonth = "";
		if (dayInYear <= 31) {
			dayAndMonth = addZeroToDay(dayInYear)+"/01";
		}
		else if (isLeapYear(Integer.valueOf(year))) {
			if (dayInYear <= 60) {
				dayAndMonth = addZeroToDay(dayInYear - 31)+"/02";
			}
			else if (dayInYear <= 91) {
				dayAndMonth = addZeroToDay(dayInYear - 60)+"/03";
			}
			else if (dayInYear <= 121) {
				dayAndMonth = addZeroToDay(dayInYear - 91)+"/04";
			}
			else if (dayInYear <= 152) {
				dayAndMonth = addZeroToDay(dayInYear - 121)+"/05";
			}
			else if (dayInYear <= 182) {
				dayAndMonth = addZeroToDay(dayInYear - 152)+"/06";
			}
			else if (dayInYear <= 213) {
				dayAndMonth = addZeroToDay(dayInYear - 182)+"/07";
			}
			else if (dayInYear <= 244) {
				dayAndMonth = addZeroToDay(dayInYear - 213)+"/08";
			}
			else if (dayInYear <= 274) {
				dayAndMonth = addZeroToDay(dayInYear - 244)+"/09";
			}
			else if (dayInYear <= 305) {
				dayAndMonth = addZeroToDay(dayInYear - 274)+"/10";
			}
			else if (dayInYear <= 335) {
				dayAndMonth = addZeroToDay(dayInYear - 305)+"/11";
			}
			else {
				dayAndMonth = addZeroToDay(dayInYear - 335)+"/12";
			}
		}
		else {
			if (dayInYear <= 59) {
				dayAndMonth = addZeroToDay(dayInYear - 31)+"/02";
			}
			else if (dayInYear <= 90) {
				dayAndMonth = addZeroToDay(dayInYear - 59)+"/03";
			}
			else if (dayInYear <= 120) {
				dayAndMonth = addZeroToDay(dayInYear - 90)+"/04";
			}
			else if (dayInYear <= 151) {
				dayAndMonth = addZeroToDay(dayInYear - 120)+"/05";
			}
			else if (dayInYear <= 181) {
				dayAndMonth = addZeroToDay(dayInYear - 151)+"/06";
			}
			else if (dayInYear <= 212) {
				dayAndMonth = addZeroToDay(dayInYear - 181)+"/07";
			}
			else if (dayInYear <= 243) {
				dayAndMonth = addZeroToDay(dayInYear - 212)+"/08";
			}
			else if (dayInYear <= 273) {
				dayAndMonth = addZeroToDay(dayInYear - 243)+"/09";
			}
			else if (dayInYear <= 304) {
				dayAndMonth = addZeroToDay(dayInYear - 273)+"/10";
			}
			else if (dayInYear <= 334) {
				dayAndMonth = addZeroToDay(dayInYear - 304)+"/11";
			}
			else {
				dayAndMonth = addZeroToDay(dayInYear - 334)+"/12";
			}
		}
		return dayAndMonth+"/"+year;
	}
	
	public static int getCharacterCount(String s, Character c) {
		int counter = 0;
		for( int i=0; i<s.length(); i++ ) {
		    if( s.charAt(i) == c) {
		        counter++;
		    } 
		}
		return counter;
	}
	
	//Translate date string from 2008-03-20^^<http://www.w3.org/2001/XMLSchema#date> or 2008-03-20 to 20/03/2008
	public static String getDisplayFormatDate(String xmlFormatDate) {
		String temp = xmlFormatDate;
		if (temp.contains("^")) {
			temp = temp.substring(0, temp.indexOf("^"));
		}
		if (getCharacterCount(temp, '-') == 2) {
			String year = temp.substring(0, temp.indexOf("-"));
			String month = temp.substring(temp.indexOf("-")+1,temp.lastIndexOf("-"));
			String day = temp.substring(temp.lastIndexOf("-")+1);
			return day+"/"+month+"/"+year;
		}
		else return "XML Format Date Incorrect";
	}
	
	//Translate date string from 20/03/2008 to 2008-03-20
	public static String getXMLFormatDate(String disPlayFormatDate) {
		if (getCharacterCount(disPlayFormatDate, '/') == 2) {
			String year = disPlayFormatDate.substring(disPlayFormatDate.lastIndexOf("/")+1);
			String month = disPlayFormatDate.substring(disPlayFormatDate.indexOf("/")+1, disPlayFormatDate.lastIndexOf("/"));
			String day = disPlayFormatDate.substring(0, disPlayFormatDate.indexOf("/"));
			return year+"-"+month+"-"+day;
		}
		return "Display Format Date Incorrect";
	}
	
	//Translate date string from XML format P2Y^^http://www.w3.org/2001/XMLSchema#duration to �1 years.
	public static String getDisplayFormatConfidence(String xmlFormatConfidence) {
		String temp = xmlFormatConfidence;
		if (temp.contains("^")) {
			temp = temp.substring(0, temp.indexOf("^"));
		}
		temp = temp.replace("P", "±");
		if (temp.contains("1")) {
			temp = temp.replace("Y", " Year");
		}
		else {
			temp = temp.replace("Y", " Years");
		}
			
		return temp;
	}
}
