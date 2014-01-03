package com.lmc.filtersourcefile;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FilterMapper extends Mapper<Object, Text, Text, Text> {
	public void map(Object key, Text value,Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer spite1 = new StringTokenizer(line, "\r\n");
		StringTokenizer spite2 = new StringTokenizer(line, "\7");
		int words = 5;
		int count = 0;
		String subString = "";
		String urlstring = "";
		String tempString = "";
		Text url = new Text();
		Text word = new Text();
		while (spite1.hasMoreTokens()) {
			subString = "";
			urlstring = "";
			for (count = 0; count < words && spite2.hasMoreTokens(); count++) {
				tempString = spite2.nextToken();
				if (tempString != null ) {
					if((count == 3 || count == 4) && tempString.equals("null")){
						count = 0;
						break;
					}
					if (count == 0)
						urlstring = tempString.toString();
					else {
						if (count == 1)
							subString = tempString.toString();
						else
							subString = subString + "\7" + tempString;
					}
				} else
					break;
			}
			if (count == words) {
				if(url.toString().equals("null"))continue;
				url.set(urlstring);
				word.set(subString);
				context.write(url, word);
			}
			spite1.nextToken();
		}
	}
	
}

