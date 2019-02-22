/**
 * Created by Ed0 in 25 ene. 2019
 */
package es.ed0.linecounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import es.ed0.linecounter.ResultTable.ResultPopulator;

public class LineCounter {

	public static void main(String[] args) {
		new LineCounter();
	}

	public static final String
		help = "help",
		create = "create",
		show = "show",
		info = "info",
		print = "print",
		delete = "delete";
	
	
	
	private ArrayList<Profile> profiles;
	private final Scanner t;
	private boolean profilesDirty = false;
	
	private LineCounter() {
		
		System.out.println(".____    .__              _________                      __                ");
		System.out.println("|    |   |__| ____   ____ \\_   ___ \\  ____  __ __  _____/  |_  ___________ ");
		System.out.println("|    |   |  |/    \\_/ __ \\/    \\  \\/ /  _ \\|  |  \\/    \\   __\\/ __ \\_  __ \\");
		System.out.println("|    |___|  |   |  \\  ___/\\     \\___(  <_> )  |  /   |  \\  | \\  ___/|  | \\/");
		System.out.println("|_______ \\__|___|  /\\___  >\\______  /\\____/|____/|___|  /__|  \\___  >__|   ");
		System.out.println("        \\/       \\/     \\/        \\/                  \\/          \\/       ");

		System.out.println("\nHi! Type help to print a command list\n");

		profiles = Profile.getProfiles();
		printProfiles();
		
		t = new Scanner(System.in);
		
		boolean stop = false;
		while (!stop) {
			if (profilesDirty) {
				profiles = Profile.getProfiles();
				profilesDirty = false;
			}
			System.out.print("> ");
			final String line = t.nextLine();
			final String[] args = line.split(" ");			
			System.out.println(dispatchCommand(args));
		}
		
		t.close();
		
	}
	
	private String dispatchCommand(String[] args) {
		StringBuilder sb = new StringBuilder("");
		switch (args[0]) {
		case help:
			sb.append("\thelp - show command list\n"
					+ "\tcreate - create a new line counter profile\n"
					+ "\tprint n - count lines and print results for profile with name or number n\n"
					+ "\tshow - show all the profiles and their identification number\n"
					+ "\tinfo n - show info about the profile with name or number n\n"
					+ "\tdelete n - delete the profile with name or number n");
			break;
		case create:
			try {
				final Profile prof = createProfile();
				sb.append("New profile saved: " + prof.getNumber() + " -> "+ prof.getName());
				profilesDirty = true;
			} catch (IOException e) {
				e.printStackTrace();
				sb.append("Error saving profile. Reason: " + e.getLocalizedMessage());
			}
			break;
		case show:
			printProfiles();
			break;
		case print: case info: case delete:
			if (args.length < 2)
				sb.append("Syntax: \"").append(args[0]).append(" n\"  where n is the number or name of profile (write \"show\" to list the profiles)");
			else {
				Profile target = null;
				try {
					int pIndex = Integer.valueOf(args[1]);
					target = profiles.get(pIndex);
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					for (Profile pr : profiles)
						if (pr.getName().equals(args[1])) {
							target = pr;
							break;
						}
				}
				
				if (target == null) {
					sb.append("No Profile for " + args[1]);
				} else {
					switch (args[0]) {
					case print: target.printResults(); break;
					case info: target.printInfo(); break;
					case delete:
						try {
							target.delete();
							sb.append("Profile " + target.getName() + " deleted");
							profilesDirty = true;
						} catch (IOException e) {
							sb.append("Error deleting profile. Reason: " + e.getLocalizedMessage());
						}
						break;
					}
				}
				
			}
			break;
			default:
				sb.append("The command ").append(args[0]).append(" does not exist. Enter \"help\" for command list");
				break;
		}
		return sb.append("\n").toString();
	}
	
	private void printProfiles() {
		ResultTable<Profile> projectTable = new ResultTable<Profile>(profiles, "Nº", "Name", "Project count");
		
		projectTable.setResultPopulator(new ResultPopulator<Profile>() {
			@Override
			public ArrayList<String> getViewForRow(int index, Profile entry) {
				final ArrayList<String> data = new ArrayList<String>();
				data.add(index + "");
				data.add(entry.getName());
				data.add(entry.getProjects().size() + "");
				return data;
			}
		});
		
		projectTable.print();
	}
	
	private Profile createProfile() throws IOException {
		System.out.print(" - Profile name: ");
		String name = t.nextLine();
		System.out.print(" - Proyect paths (separated by commas): ");
		ArrayList<Project> ps = new ArrayList<Project>();
		for (String p : Profile.getArrayFromCsv(t.nextLine(), ","))
			try {
				ps.add(new Project(p));
			} catch (IOException e) {
				System.err.println("Project " + p + " could not be found.");
			}
		System.out.print(" - Code file extensions (separated by commas): ");
		ArrayList<String> exts = Profile.getArrayFromCsv(t.nextLine(), ",");
		
		Profile prof = new Profile(profiles.size(), name, ps, exts);
		prof.serialize();
		return prof;
	}
	
	
}
