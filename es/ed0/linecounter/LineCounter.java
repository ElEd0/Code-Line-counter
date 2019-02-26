/**
 * Created by Ed0 in 25 ene. 2019
 */
package es.ed0.linecounter;

import java.io.IOException;
import java.util.ArrayList;

import es.ed0.consoleui.ConsoleUi;
import es.ed0.consoleui.input.CommandListener;
import es.ed0.consoleui.ui.BorderStyle;
import es.ed0.consoleui.ui.Component;
import es.ed0.consoleui.ui.EntryTable;
import es.ed0.consoleui.ui.EntryTable.TablePopulator;
import es.ed0.consoleui.ui.Text;

public class LineCounter implements CommandListener {

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
	private boolean profilesDirty = false;
	
	private ConsoleUi ui;
	
	private LineCounter() {
		ui = new ConsoleUi();
		ui.addCommands(help, create, show, info, print, delete);
		ui.addInputListener(this);
		
		ui.println(".____    .__              _________                      __                ");
		ui.println("|    |   |__| ____   ____ \\_   ___ \\  ____  __ __  _____/  |_  ___________ ");
		ui.println("|    |   |  |/    \\_/ __ \\/    \\  \\/ /  _ \\|  |  \\/    \\   __\\/ __ \\_  __ \\");
		ui.println("|    |___|  |   |  \\  ___/\\     \\___(  <_> )  |  /   |  \\  | \\  ___/|  | \\/");
		ui.println("|_______ \\__|___|  /\\___  >\\______  /\\____/|____/|___|  /__|  \\___  >__|   ");
		ui.println("        \\/       \\/     \\/        \\/                  \\/          \\/       ");

		ui.println("\nHi! Type help to print a command list\n");

		profiles = Profile.getProfiles();
		printProfiles();
		
		
		boolean stop = false;
		while (!stop) {
			if (profilesDirty) {
				profiles = Profile.getProfiles();
				profilesDirty = false;
			}
			ui.promptInput(">");
		}
		
		
	}

	/* (non-Javadoc)
	 * @see es.ed0.consoleui.input.CommandListener#onCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(String command, String[] args) {
		ui.println(dispatchCommand(command, args));
		return true;
	}
	
	
	private String dispatchCommand(String command, String[] args) {
		StringBuilder sb = new StringBuilder("");
		switch (command) {
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
			if (args.length < 1)
				sb.append("Syntax: \"").append(command).append(" n\"  where n is the number or name of profile (write \"show\" to list the profiles)");
			else {
				Profile target = null;
				try {
					int pIndex = Integer.valueOf(args[0]);
					target = profiles.get(pIndex);
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					for (Profile pr : profiles)
						if (pr.getName().equals(args[0])) {
							target = pr;
							break;
						}
				}
				
				if (target == null) {
					sb.append("No Profile for " + args[0]);
				} else {
					switch (command) {
					case print: ui.print(target.getPrintResults()); break;
					case info: ui.print(target.getPrintInfo()); break;
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
				sb.append("The command ").append(command).append(" does not exist. Enter \"help\" for command list");
				break;
		}
		return sb.append("\n").toString();
	}
	
	private void printProfiles() {
		EntryTable<Profile> profileTable = new EntryTable<Profile>(BorderStyle.hollow, profiles, "Name", "Project count");
		profileTable.setEnumerate(true);
		profileTable.setTablePopulator(new TablePopulator<Profile>() {
			@Override
			public ArrayList<Component> getViewForRow(int index, Profile entry) {
				final ArrayList<Component> row = new ArrayList<Component>();
				row.add(new Text(entry.getName()));
				row.add(new Text(entry.getProjects().size()));
				return row;
			}
		});
		ui.print(profileTable);
	}
	
	private Profile createProfile() throws IOException {
		String name = ui.promptText(" - Profile name: ");
		
		String projs = ui.promptText(" - Proyect paths (separated by commas): ");
		ArrayList<Project> ps = new ArrayList<Project>();
		for (String p : Profile.getArrayFromCsv(projs, ","))
			try {
				ps.add(new Project(p));
			} catch (IOException e) {
				ui.println("Project " + p + " could not be found.");
			}
		String extens = ui.promptText(" - Code file extensions (separated by commas): ");
		ArrayList<String> exts = Profile.getArrayFromCsv(extens, ",");
		
		Profile prof = new Profile(profiles.size(), name, ps, exts);
		prof.serialize();
		return prof;
	}

	
	
}
