package com.lgh.www.main;

import java.io.File;

import com.lgh.www.fileoperator.FileOperator;
import com.lgh.www.sourcefile.SourceFile;

public class Bear {
	
	

	public static void main(String[] args) throws Exception {
		
		String contentFile = "testfile/contentfrompages/content.txt";
		String contentExtractFile = "testfile/contentfrompages/contentExtract.txt";
		String extractFile = "testfile/sourcefilefromcontent/extractAll.txt";
		int splitCount = 3;
				
		FileOperator fileOperator = new FileOperator();
		fileOperator.split(contentFile, splitCount);
		
		SourceFile sourceFile = new SourceFile();
		System.out.println();
		System.out.println("Begin to extract messages from file content-part*.txt!");
		sourceFile.done(contentFile, splitCount);

		fileOperator.merge(extractFile, contentExtractFile, splitCount);

	}

}
