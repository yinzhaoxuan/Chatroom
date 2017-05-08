package com.cms.tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatDate {
	
	/**
	 * 格式化日期
	 * @return
	 */
	public static String getCurDate(){
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(new Date());
	}
}
