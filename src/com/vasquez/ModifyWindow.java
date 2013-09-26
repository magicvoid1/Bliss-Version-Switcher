/* 
 * Copyright (C) 2013 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * 
 * */

package com.vasquez;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ModifyWindow extends JFrame {
	public ModifyWindow(EntryWithModel tableManager, JTable entryTable, int selectedEntry) {
		super("Modify Entry");
		
		// Set window properties
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// Bring in the table manager and entry table resources
		this.tableManager = tableManager;
		this.entryTable = entryTable;
		this.selectedEntry = selectedEntry;
		
		// Create components and listeners
		JButton modify = new JButton("Modify");
		JButton cancel = new JButton("Cancel");
		
		modify.addActionListener(new modifyListener());
		cancel.addActionListener(new cancelListener());
		
		JLabel versionL = new JLabel("Version:");
		JLabel pathL = new JLabel("Path:");
		JLabel flagsL = new JLabel("Flags:");
		
		version = new JTextField(tableManager.getSelectedVersion(selectedEntry));
		path = new JTextField(tableManager.getSelectedPath(selectedEntry));
		flags = new JTextField(tableManager.getSelectedFlags(selectedEntry));
		expansion = new JCheckBox("Expansion", tableManager.isSelectedExpansion(selectedEntry));
		
		// Create the layout and add the components to their respective places
		JPanel centerPanel = new JPanel(new GridLayout(3,2));
		JPanel southPanel = new JPanel(new GridLayout(1,2));
		
		southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

		getContentPane().add(BorderLayout.CENTER, centerPanel);
		getContentPane().add(BorderLayout.SOUTH, southPanel);
		
		southPanel.add(expansion);
		southPanel.add(modify);
		southPanel.add(cancel);
			
		centerPanel.add(versionL);
		centerPanel.add(version);
		centerPanel.add(pathL);
		centerPanel.add(path);
		centerPanel.add(flagsL);
		centerPanel.add(flags);		
	}
	
	private class modifyListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			
			// If none of the fields are empty, than add the field. None of the fields can be empty or the program will crash
			// because it won't be able to parse the values correctly.
			if(!version.getText().isEmpty() && !path.getText().isEmpty() && !flags.getText().isEmpty()) {
				tableManager.modifyEntry(version.getText(), path.getText(), flags.getText(), expansion.isSelected(), selectedEntry);
			}
			
			entryTable.repaint();
			dispose();
		}
	}
	
	private class cancelListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			dispose();
		}
	}
	
	private EntryWithModel tableManager;
	private JTable entryTable;
	private JTextField version;
	private JTextField path;
	private JTextField flags;
	private JCheckBox expansion;
	private int selectedEntry;
}