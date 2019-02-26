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

import es.ed0.consoleui.ui.BorderStyle;
import es.ed0.consoleui.ui.Component;
import es.ed0.consoleui.ui.EntryTable;
import es.ed0.consoleui.ui.EntryTable.TablePopulator;
import es.ed0.consoleui.ui.Grid;
import es.ed0.consoleui.ui.Panel;
import es.ed0.consoleui.ui.Separator;
import es.ed0.consoleui.ui.Text;

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
	
	public Text getPrintInfo() {
		Text info = new Text("");
		info.append(new Separator(30));
		info.appendln("Profile " + name);
		info.append("\t- Extensions (" + extensions.size() + "):\t");
		for (int i = 0; i < extensions.size(); i++)
			info.append((i != 0 ? ", " : "") + extensions.get(i));
		info.appendln("");
		
		info.appendln("\t- Projects (" + projects.size() + "):");
		for (Project pr : projects)
			info.appendln("\t\t- " + pr.getPath() + "\tFiles loaded: " + pr.isExplored());

		info.append(new Separator(30));
		return info;
	}

	
	public Text getPrintResults() {
		for (Project p : projects)
			p.explore(extensions);
		
		Text results = new Text("");
		results.appendln("Profile " + name);
		results.append("\t- Extensions:\t");
		for (int i = 0; i < extensions.size(); i++)
			results.append((i != 0 ? ", " : "") + extensions.get(i));
		results.appendln("");
		
		EntryTable<Project> projectTable = new EntryTable<Project>(BorderStyle.unicode, projects, "Path", "Lines", "Size", "Longest file");
		
		projectTable.setTablePopulator(new TablePopulator<Project>() {
			@Override
			public ArrayList<Component> getViewForRow(int index, Project entry) {
				final ArrayList<Component> data = new ArrayList<Component>();
				
				data.add(new Text(entry.getPath()));
				data.add(new Text(entry.getTotalLines()));
				data.add(new Text(entry.getSize()));
				data.add(new Text(entry.getLongerFiles(1).get(0).getName()));
				
				return data;
			}
		});
		
		results.append(projectTable);
		
		Grid longerFilesGrid = new Grid(BorderStyle.unicode, 2, projects.size());

		for (Project p : projects) {
			longerFilesGrid.add(new Text(p.getPath()));
			final Text fileList = new Text("");
			List<File> top = p.getLongerFiles(longerFilesMaxCount);
			for (File f0 : top)
				fileList.appendln("- " + f0.getName() + " - Lines: " + Project.getTotalLinesForFile(f0));
			longerFilesGrid.add(fileList);
		}
		
		Panel longerFilesPanel = new Panel(BorderStyle.unicode, "Longer Files", longerFilesGrid);
		
		results.append(longerFilesPanel);
		
		return results;
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
		
		final BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
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
