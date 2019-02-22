/**
 * Created by Ed0 in 16 feb. 2019
 */
package es.ed0.linecounter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ed0.linecounter.ResultTable.ResultPopulator;

public class Profile {

	private final static String profiles_data = "profiles.data";
	private final int longerFilesMaxCount = 3;
	
	private int number;
	private String name;
	private ArrayList<Project> projects;
	private ArrayList<String> extensions;
	
	public Profile(int number, String name, ArrayList<Project> projects, ArrayList<String> extensions) {
		this.number = number;
		this.name = name;
		this.projects = projects;
		this.extensions = extensions;
	}
	
	public void printInfo() {
		System.out.println("========================================");
		
		System.out.println("Profile " + name);
		System.out.print("\t- Extensions (" + extensions.size() + "):\t");
		for (int i = 0; i < extensions.size(); i++)
			System.out.print((i != 0 ? ", " : "") + extensions.get(i));
		System.out.println();
		
		System.out.println("\t- Projects (" + projects.size() + "):");
		for (Project pr : projects)
			System.out.println("\t\t- " + pr.getPath() + "\tFiles loaded: " + pr.isExplored());
		
		System.out.println("========================================");
	}

	
	public void printResults() {
		for (Project p : projects)
			p.explore(extensions);
		
		System.out.println("Profile " + name);
		System.out.print("\t- Extensions:\t");
		for (int i = 0; i < extensions.size(); i++)
			System.out.print((i != 0 ? ", " : "") + extensions.get(i));
		System.out.println();

		
		ResultTable<Project> projectTable = new ResultTable<Project>(projects, "Path", "Lines", "Size", "Longest file");
		
		projectTable.setResultPopulator(new ResultPopulator<Project>() {
			@Override
			public ArrayList<String> getViewForRow(int index, Project entry) {
				final ArrayList<String> data = new ArrayList<String>();
				
				data.add(entry.getPath());
				data.add(entry.getTotalLines() + "");
				data.add(entry.getSize());
				data.add(entry.getLongerFiles(1).get(0).getName());
				
				return data;
			}
		});
		
		projectTable.print();
		
		for (Project p : projects) {
			System.out.println(p.getPath() + " longer files");
			List<File> top = p.getLongerFiles(longerFilesMaxCount);
			for (File f0 : top)
				System.out.println("\t- " + f0.getName() + " - Lines: " + Project.getTotalLinesForFile(f0));
		}
		
	}
	
	
	public static ArrayList<Profile> getProfiles() {
		final ArrayList<Profile> ret = new ArrayList<Profile>();
		try {
			final File f = new File("profiles.data");
			if (!f.exists())
				f.createNewFile();
			final BufferedReader br = new BufferedReader(new FileReader(f));
			int counter = 0;
			String line = null;
			while((line = br.readLine()) != null) {
				try {
					String[] data = line.split(";");
					String name = data[0];
					ArrayList<Project> ps = new ArrayList<Project>();
					for (String p : getArrayFromCsv(data[1], "&"))
						ps.add(new Project(p));
					ArrayList<String> exts = getArrayFromCsv(data[2], "&");
					ret.add(new Profile(counter, name, ps, exts));
				} catch (IndexOutOfBoundsException | IOException e) {
					continue;
				}
				counter++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void delete() throws IOException {
		final File file = new File(profiles_data);
		if (!file.exists())
			file.createNewFile();
		final BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
		final BufferedReader br = new BufferedReader(new FileReader(file));
		final StringBuilder sb = new StringBuilder();
		
		int counter = 0;
		String line = null;
		while((line = br.readLine()) != null) {
			if (counter != this.number)
				sb.append(line).append("\n");
			counter++;
		}
		
		br.close();
		bw.write(sb.toString());
		bw.flush();
		bw.close();
	}

	public void serialize() throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.name).append(";");
		
		for (int p = 0; p < projects.size(); p++)
			sb.append((p == 0 ? "" : "&")).append(projects.get(p).getPath());
		
		sb.append(";");
		
		for (int e = 0; e < extensions.size(); e++)
			sb.append((e == 0 ? "" : "&")).append(extensions.get(e));

		
		final File file = new File(profiles_data);
		if (!file.exists())
			file.createNewFile();
		final BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write(sb.toString() + "\n");
		bw.close();
	}
	
	public static ArrayList<String> getArrayFromCsv(String line, String separator) {
		final ArrayList<String> ret = new ArrayList<String>();
		String[] data = line.split(separator);
		for (String s : data)
			if (s.length() > 1)
				ret.add(s.trim());
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getExtensions() {
		return extensions;
	}


	public void setExtensions(ArrayList<String> extensions) {
		this.extensions = extensions;
	}


	public ArrayList<Project> getProjects() {
		return projects;
	}


	public void setProjects(ArrayList<Project> projects) {
		this.projects = projects;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
