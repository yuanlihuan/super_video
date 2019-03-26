package com.supers.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * 视频和音频合并
 * @author liulai
 */
public class MergeVideoMp3 {
	
	private String ffmpegEXE;
	
	public MergeVideoMp3(String ffmpegEXE){
		super();
		this.ffmpegEXE = ffmpegEXE;
	}
	
	public void convertor(String videoPath, String mp3Path,
			double time, String videoAndMp3Path) throws IOException {
		List<String> command = new ArrayList<String>();
		command.add(ffmpegEXE);
		command.add("-i");
		command.add(videoPath);
		command.add("-i");
		command.add(mp3Path);
		command.add("-t");
		command.add(String.valueOf(time));
		command.add("-y");
		command.add(videoAndMp3Path);
		
		/*for(String c : command){
			System.out.println(c+" ");
		}*/
		
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
		MergeVideoMp3 ffmpeg = new MergeVideoMp3("C:\\develop\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffmpeg.convertor("D:\\1.mp4", "D:\\muke\\mulic\\1.mp3", 23.1, "D:\\这是通过java生产的视频.mp4");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
