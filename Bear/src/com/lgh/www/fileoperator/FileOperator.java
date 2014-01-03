package com.lgh.www.fileoperator;


import java.io.File;
import java.io.RandomAccessFile;

import org.junit.Test;
/**
 * <p>[SplitImageUtil]描述：文件分割工具类</p>
 * @作者 xbl
 * @日期 2011年2月25日 11:31:54
 */
public class FileOperator {


	/**
	 * <p>拆分文件</p>
	 * @param file 源文件
	 * @param count 拆分的文件个数
	 * @throws Exception
	 */
	public void split(String file ,int count) throws Exception
	{		
		System.out.println("Begin to split the file: "+file);
		RandomAccessFile raf = new RandomAccessFile(new File(file),"r");
		long length = raf.length();
		
		long theadMaxSize =  length / count; //每份的大小 1024 * 1000L;
		raf.close();

		long offset = 0L;
		for(int i=0;i< count-1;i++) //这里不去处理最后一份
		{
			long fbegin = offset;
			long fend = (i+1) * theadMaxSize;
			offset =write(file,i,fbegin,fend);
		}

		if(length- offset>0) //将剩余的都写入最后一份
			write(file,count-1,offset,length);
		System.out.println("The file has been split in "+count+" parts sucessfully, and named content-part*.txt");
	}
	/**
	 * <p>指定每份文件的范围写入不同文件</p>
	 * @param file 源文件
	 * @param index 文件顺序标识
	 * @param begin 开始指针位置
	 * @param end 结束指针位置
	 * @return
	 * @throws Exception
	 */
	private static long write(String file,int index,long begin,long end) throws Exception
	{
		RandomAccessFile in = new RandomAccessFile(new File(file),"r");
		int indexOfFile = file.indexOf(".");
		String newFile = file.substring(0,indexOfFile);
		RandomAccessFile out = new RandomAccessFile(new File(newFile+"-"+"part"+index+".txt"),"rw");
		byte[] b = new byte[1024];
		int n=0;
		in.seek(begin);//从指定位置读取

		while(in.getFilePointer() <= end && (n= in.read(b))!=-1)
		{
			out.write(b, 0, n);
		}
		long endPointer =in.getFilePointer();
		in.close();
		out.close();
		return endPointer;
	}
	/**
	 * <p>合并文件</p>
	 * @param file 指定合并后的文件
	 * @param tempFiles 分割前的文件名
	 * @param tempCount 文件个数
	 * @throws Exception
	 */
	public static void merge(String file,String tempFiles,int tempCount) throws Exception
	{
		System.out.println();
		System.out.println("Begin to merge file contentExtract-part*.txt");
		RandomAccessFile ok = new RandomAccessFile(new File(file),"rw");
		int indexOfFile = tempFiles.indexOf(".");
		String newFile = tempFiles.substring(0,indexOfFile);
		for(int i=0;i<tempCount;i++)
		{
			String mergeFile = newFile+"-part"+i+".txt";
			System.out.println("Merging the file: "+mergeFile);
			RandomAccessFile read = new RandomAccessFile(new File(mergeFile),"r");		
			byte[] b = new byte[1024];
			int n=0;
			while((n=read.read(b))!= -1)
			{
				ok.write(b, 0, n);
			}
			read.close();
		}
		ok.close();
		System.out.println("All contentExtract-part*.txt files have been merge in one file: extractAll.txt");
	}


}

