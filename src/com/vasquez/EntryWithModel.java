/*
 * Copyright 2013-2017 Jonathan Vasquez <jon@xyinn.org>
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.vasquez;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

public class EntryWithModel {
    private String entriesFile;
    private ArrayList<Entry> list;
    private String[] columnNames = {"Version", "Exp", "Last", "Path to \"Game.exe\"", "Flags"};
    private EntryModel peModel;
    
    public EntryWithModel() {
        entriesFile = "Entries.json";
        list = loadData();
        peModel = new EntryModel();
    }

    public Entry getLastRanEntry() {
        for(Entry e: list) {
            if (e.WasLastRan) {
                return e;
            } 
        }
        return null;
    }
    
    public int addEntry(String version, String path, String flags, boolean expansion) {
        return peModel.addEntry(version, path, flags, expansion);
    }

    public int modifyEntry(String version, String path, String flags, boolean expansion, boolean wasLastRan, int e) {
    	// Check to make sure that there is only one last ran selected.
    	// An easy way to do this is just to set everything to false
    	// and only enable the one we want.
        if (wasLastRan) {
        	for(Entry entry: list) {
        		entry.WasLastRan = false;
        	}
        }  
        peModel.modifyEntry(version, path, flags, expansion, wasLastRan, e);
        return e;
    }

    public int delEntry(int e) {
        return peModel.delEntry(e);
    }

    public Entry getEntry(int i) {
        return list.get(i);
    }
    
    public int copyEntry(int e) {
        return peModel.copyEntry(e);
    }

    public Object getValueAt(int row, int col) {
        return peModel.getValueAt(row, col);
    }

    public String getSelectedVersion(int row) {
        return peModel.getSelectedVersion(row);
    }

    public String getSelectedPath(int row) {
        return peModel.getSelectedPath(row);
    }

    public String getSelectedFlags(int row) {
        return peModel.getSelectedFlags(row);
    }

    public boolean isSelectedExpansion(int row) {
        return peModel.isSelectedExpansion(row);
    }
    
    public boolean wasLastRan(int row) {
    	return peModel.wasLastRan(row);
    }

    public int getSize() {
        return list.size();
    }

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(entriesFile))) {
            bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(list));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Entry> loadData() {
        ArrayList<Entry> stagedEntries = new ArrayList<>();
        
        try {
            File entryFile = new File(entriesFile);

            if(entryFile.exists()) 
            {
                String fileContents = FileUtils.readFileToString(entryFile, "UTF-8");
                ArrayList<Entry> potentialEntryList = new Gson().fromJson(fileContents, new TypeToken<ArrayList<Entry>>(){}.getType());
                return potentialEntryList != null ? potentialEntryList : stagedEntries;
            }
            
            JOptionPane.showMessageDialog(null,
            		"Welcome to Bliss Version Switcher. \n\nPlease add an entry for your Game.exe and make sure that it is the\n" +
            		"same version as what's in that folder, and then click Launch.\n" +
            		"Picking a different version than what's in the folder will cause problems.\n\n" +
            		"For example, if my Diablo II folder is at D:\\Games\\Diablo II\\Game.exe\n" +
            		"and that folder is at 1.13d, and I'm playing expansion, with window\n" +
            		"mode and no sound, my initial entry for the switcher would look as follows:\n\n" +
            		"Version: 1.13d\n" +
            		"Path (Game.exe): D:\\Games\\Diablo II\\Game.exe\n" +
            		"Flags: -w -ns\n" +
            		"Expansion: Yes");
            
            entryFile.createNewFile();    
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return stagedEntries;
    }

    public EntryModel getModel() {
        return peModel;
    }

    public ArrayList<Entry> getList() {
        return list;
    }

    private class EntryModel extends AbstractTableModel {
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return list.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {

            Entry e = list.get(row);

            switch(col) {
            case 0:
                return e.Version;
            case 1:
                return e.IsExpansion;
            case 2:
                return e.WasLastRan;
            case 3:
                return e.Path;
            case 4:
                return e.Flags;
            default:
                return "error";
            }
        }

        // Get the class type of the column so that we can get the check boxes to render correctly
        public Class getColumnClass(int c) {
              return getValueAt(0, c).getClass();
        }

        public String getSelectedVersion(int row) {
            return list.get(row).Version;
        }

        public String getSelectedPath(int row) {
            return list.get(row).Path;
        }

        public String getSelectedFlags(int row) {
            return list.get(row).Flags;
        }

        public boolean isSelectedExpansion(int row) {
            return list.get(row).IsExpansion;
        }
        
        public boolean wasLastRan(int row) {
        	return list.get(row).WasLastRan;
        }

        public int addEntry(String version, String path, String flags, boolean expansion) {
             list.add(new Entry(version, path, flags, expansion, false));

             saveData();
             fireTableDataChanged();

             return list.size()-1;
        }

        public int modifyEntry(String version, String path, String flags, boolean expansion, boolean wasLastRan, int e) {
            Entry t = list.get(e);

            t.Version = version;
            t.IsExpansion = expansion;
            t.Path = path;
            t.Flags = flags;
            t.WasLastRan = wasLastRan;

            saveData();
            fireTableDataChanged();

            return e;
        }

        public int delEntry(int entry) {
            if(entry != -1) {
                list.remove(entry);
                saveData();
                fireTableDataChanged();

                return entry - 1;
            }

            // Returns null if some error happened
            return -1;
        }
        
        // Copies the entry that is passed to this method and then inserts it into the list
        public int copyEntry(int entry) {
            int next = entry + 1;

            if(entry != -1) {
                Entry oldEntry = list.get(entry);
                Entry newEntry = new Entry(oldEntry.Version, oldEntry.Path, oldEntry.Flags, oldEntry.IsExpansion, false);

                list.add(next, newEntry);

                saveData();
                fireTableDataChanged();

                return next;
            } else { return -1; }
        }
    }
}
