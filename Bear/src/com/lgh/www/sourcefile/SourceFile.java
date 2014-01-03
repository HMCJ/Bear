package com.lgh.www.sourcefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;



import javax.management.RuntimeErrorException;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

public class SourceFile {
	
	public static String read(File file){
		System.out.println("reading: "+file);
			StringBuilder sb = new StringBuilder(1024*1024);//1M大小的缓冲区
			try{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String s;
				try{
					while((s = br.readLine()) != null){
						sb.append(s);
						sb.append("\n");
					}
				}finally{
					br.close();
				}
			}catch(IOException e){
				throw new RuntimeException(e);	
			}
			return sb.toString();		
	}
	
	public static void write(String text,File file){
		System.out.println("Writing: "+file);
		try{
			PrintWriter pw = new PrintWriter(file);
			try{
				pw.write(text);
			}finally{
				pw.close();
			}
		}catch(IOException e){
				throw new RuntimeException(e);
			}
	}
	
	public static void done(String file,int number){
			
		int indexOfFile = file.indexOf(".");
		String newFileName = file.substring(0,indexOfFile);
		String str;
		for(int i=0; i < number;i++){
			File inputFile = new File(newFileName+"-part"+i+".txt");
			File outputFile = new File(newFileName+"Extract"+"-part"+i+".txt");
			str = read(inputFile);
			String[] units = str.split("Recno");
			
			StringBuilder sb = new StringBuilder();
			for(int j = 1; j <= units.length-1;j++){			
				sb.append(match(units[j]));
			}
			write(sb.toString(),outputFile);
		}	
		System.out.println("The messages from file content-part*.txt have been extract sucessfully, and stored in file contentExtract-part*.txt");
	}
	
	public static String match(String str){
		StringBuilder sb = new StringBuilder();		
		sb.append(matchURL(str)+'\007'+matchHot(str)+'\007'+matchAuthor(str)+'\007'+matchTitle(str)+'\007'+matchContent(str)+"\r\n");
		return sb.toString();
	}
	
	public static String matchURL(String str){
		//提取url
		String urlRegex = "URL.*showtopic.*";
		PatternCompiler compiler = new Perl5Compiler();
		Pattern pattern = null;
		StringBuilder sb = new StringBuilder();
		try{
			pattern = compiler.compile(urlRegex, Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcher = new Perl5Matcher();
			if(matcher.contains(str, pattern)){
				MatchResult result = matcher.getMatch();
				String allUrl = result.toString();
				String url = allUrl.substring(6);				
				return url;				
			}
		}catch(MalformedPatternException e){
			e.printStackTrace();
		}	
		return null;
	}
	
	public static String matchHot(String str){
		//提取浏览人数
		String urlRegex = "<div class=\"hm\" style=\"padding-top:14px;\">(.*)</div>";
		PatternCompiler compiler = new Perl5Compiler();
		Pattern pattern = null;
		try{
			pattern = compiler.compile(urlRegex, Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcher = new Perl5Matcher();
			if(matcher.contains(str, pattern)){
				MatchResult result = matcher.getMatch();
				String message = result.toString();
				int begin = message.indexOf("</span>");
				int end = message.indexOf("<span class=\"pipe\">");
				String number = message.substring(begin+8, end);	
				return number;
			}
		}catch(MalformedPatternException e){
			e.printStackTrace();
		}	
		return null;
	}
	
	public static String matchAuthor(String str){
		//提取作者
		String urlRegex = "<div\\s+class=\"poster\">(.|\n)*?</div>";
		PatternCompiler compiler = new Perl5Compiler();
		Pattern pattern = null;
		try{
			pattern = compiler.compile(urlRegex, Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcher = new Perl5Matcher();
			if(matcher.contains(str, pattern)){
				MatchResult result = matcher.getMatch();
				String message = result.toString();
				String author;
				int begin = message.indexOf("title=\"未在线\"");
				int end = message.indexOf("</span>");
				if(begin == -1){
					begin = message.indexOf("title=\"在线\"");
					author = message.substring(begin+11, end);
				}else{
					author = message.substring(begin+12, end);
				}							
				return author;
			}
		}catch(MalformedPatternException e){
			e.printStackTrace();
		}	
		return null;
	}
	
	public static String matchTitle(String str){
		//提取题目
		String urlRegex = "<span id=\"topictitle\"to title=\"(.*)\" style=\"cursor:pointer;\">(.*)</span>";
		PatternCompiler compiler = new Perl5Compiler();
		Pattern pattern = null;
		try{
			pattern = compiler.compile(urlRegex, Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcher = new Perl5Matcher();
			if(matcher.contains(str, pattern)){
				MatchResult result = matcher.getMatch();
				return result.group(2);
			}
		}catch(MalformedPatternException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String matchContent(String str){
		String urlRegex = "<div id=\"firstpost\">(.*)</div>";
		PatternCompiler compiler = new Perl5Compiler();
		Pattern pattern = null;
		try{
			pattern = compiler.compile(urlRegex, Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcher = new Perl5Matcher();
			if(matcher.contains(str, pattern)){
				MatchResult result = matcher.getMatch();
				String message = result.toString();
				int end = message.indexOf("</div>");
				String content =  message.substring(20, end);	
			
				return content.replaceAll("<.*?>", "")
						.replaceAll("&nbsp", "")
						.replaceAll("\\(ShowFormatBytesStr[^)]*[)][^)]*\\)", "")
						.replaceAll("\\s{2,}"," ")
						.replaceAll("[;]+", " ")
						.replaceAll("(&#\\d+)", "")
						.replaceAll("(([0-1][0-9])|2[0-3]):[0-5][0-9]:[0-5][0-9]","")
						.replaceAll("\\d{4}-[0-1]\\d-[0-3]\\d", "")
						;
			}
		}catch(MalformedPatternException e){
			e.printStackTrace();
		}	
		return null;
	}
		
}


