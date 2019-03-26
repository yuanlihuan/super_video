package com.supers.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * 截取视频的一个图片
 * @author liulai
 */
public class MergeVideoJpg {
	
	private String ffmpegEXE;
	
	public MergeVideoJpg(String ffmpegEXE){
		super();
		this.ffmpegEXE = ffmpegEXE;
	}
	
	public void convertor(String videoPath, String jpgPath) throws IOException {
		List<String> command = new ArrayList<String>();
		command.add(ffmpegEXE);
		command.add("-ss");
		command.add("00:00:01");
		command.add("-y");
		command.add("-i");
		command.add(videoPath);
		command.add("-vframes");
		command.add("1");
		command.add(jpgPath);
		
		for(String c : command){
			System.out.println(c+" ");
		}
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		
		
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		
		@SuppressWarnings("unused")
		String line = "";
		while ( (line = br.readLine()) != null ) {
		}
		
		if (br != null) {
			br.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}
		
	}
	//只能生成不带背景音乐的视频 与音乐组合
	public static void main(String[] args) {
		MergeVideoJpg ffmpeg = new MergeVideoJpg("C:\\develop\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe");
		System.out.println("1111");
		try {
			ffmpeg.convertor("D:\\1.mp4", "D:\\1.jpg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
