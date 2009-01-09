/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.desktop.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.desktop.Desktop;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.visualization.peaklist.PeakListTableWindow;
import net.sf.mzmine.modules.visualization.tic.TICVisualizer;
import net.sf.mzmine.project.impl.MZmineProjectImpl;
import net.sf.mzmine.util.GUIUtils;
import net.sf.mzmine.util.components.DragOrderedJList;
import net.sf.mzmine.util.dialogs.NameChangeDialog;
import net.sf.mzmine.util.dialogs.NameChangeable;

/**
 * This class implements a selector of raw data files and alignment results
 */
public class ItemSelector extends JPanel implements ActionListener,
        MouseListener, ListSelectionListener {

    public static final String DATA_FILES_LABEL = "Raw data files";
    public static final String PEAK_LISTS_LABEL = "Peak lists";

    private DragOrderedJList rawDataFiles, peakLists;
    private DefaultListModel rawDataFilesModel, peakListsModel;
    private JPopupMenu dataFilePopupMenu, peakListPopupMenu;

    /**
     * Constructor
     */
    public ItemSelector(Desktop desktop) {

        // Create panel for raw data objects
        JPanel rawDataPanel = new JPanel();
        JLabel rawDataTitle = new JLabel(DATA_FILES_LABEL);
        MZmineProjectImpl project = (MZmineProjectImpl) MZmineCore.getCurrentProject();
        rawDataFilesModel = project.getRawDataListModel();
        rawDataFiles = new DragOrderedJList(rawDataFilesModel);
        rawDataFiles.setCellRenderer(new ItemSelectorListRenderer());
        rawDataFiles.addMouseListener(this);
        rawDataFiles.addListSelectionListener(this);
        JScrollPane rawDataScroll = new JScrollPane(rawDataFiles);

        rawDataPanel.setLayout(new BorderLayout());
        rawDataPanel.add(rawDataTitle, BorderLayout.NORTH);
        rawDataPanel.add(rawDataScroll, BorderLayout.CENTER);
        rawDataPanel.setMinimumSize(new Dimension(150, 10));

        // Create panel for alignment results
        JPanel resultsPanel = new JPanel();
        JLabel resultsTitle = new JLabel(PEAK_LISTS_LABEL);

        peakListsModel = project.getPeakListsListModel();
        peakLists = new DragOrderedJList(peakListsModel);
        peakLists.setCellRenderer(new ItemSelectorListRenderer());
        peakLists.addMouseListener(this);
        peakLists.addListSelectionListener(this);
        JScrollPane resultScroll = new JScrollPane(peakLists);

        resultsPanel.setLayout(new BorderLayout());
        resultsPanel.add(resultsTitle, BorderLayout.NORTH);
        resultsPanel.add(resultScroll, BorderLayout.CENTER);
        resultsPanel.setMinimumSize(new Dimension(200, 10));

        // Add panels to a split and put split on the main panel
        setPreferredSize(new Dimension(200, 10));
        setLayout(new BorderLayout());

        JSplitPane rawAndResultsSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, rawDataPanel, resultsPanel);
        add(rawAndResultsSplit, BorderLayout.CENTER);

        rawAndResultsSplit.setDividerLocation(300);

        dataFilePopupMenu = new JPopupMenu();
        GUIUtils.addMenuItem(dataFilePopupMenu, "Show TIC", this, "SHOW_TIC");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Rename", this, "RENAME_FILE");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Remove", this, "REMOVE_FILE");

        peakListPopupMenu = new JPopupMenu();
        GUIUtils.addMenuItem(peakListPopupMenu, "Show peak list", this,
                "SHOW_ALIGNED_PEAKLIST");
        GUIUtils.addMenuItem(peakListPopupMenu, "Rename", this,
                "RENAME_PEAKLIST");
        GUIUtils.addMenuItem(peakListPopupMenu, "Remove", this,
                "REMOVE_PEAKLIST");

    }

    void addSelectionListener(ListSelectionListener listener) {
        rawDataFiles.addListSelectionListener(listener);
        peakLists.addListSelectionListener(listener);
    }

    // Implementation of action listener interface

    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        if (command.equals("RENAME_FILE")) {
            RawDataFile[] selectedFiles = getSelectedRawData();
            for (RawDataFile file : selectedFiles) {
                if (file instanceof NameChangeable) {
                    NameChangeDialog dialog = new NameChangeDialog(
                            (NameChangeable) file);
                    dialog.setVisible(true);
                }
            }
        }

        if (command.equals("REMOVE_FILE")) {
            RawDataFile[] selectedFiles = getSelectedRawData();
            for (RawDataFile file : selectedFiles)
                MZmineCore.getCurrentProject().removeFile(file);
        }

        if (command.equals("SHOW_TIC")) {
            RawDataFile[] selectedFiles = getSelectedRawData();
            TICVisualizer.showNewTICVisualizerWindow(selectedFiles, null);
        }

        if (command.equals("RENAME_PEAKLIST")) {
            PeakList[] selectedPeakLists = getSelectedPeakLists();
            for (PeakList peakList : selectedPeakLists) {
                if (peakList instanceof NameChangeable) {
                    NameChangeDialog dialog = new NameChangeDialog(
                            (NameChangeable) peakList);
                    dialog.setVisible(true);
                }
            }
        }

        if (command.equals("REMOVE_PEAKLIST")) {
            PeakList[] selectedPeakLists = getSelectedPeakLists();
            for (PeakList peakList : selectedPeakLists)
                MZmineCore.getCurrentProject().removePeakList(peakList);
        }

        if (command.equals("SHOW_ALIGNED_PEAKLIST")) {
            PeakList[] selectedPeakLists = getSelectedPeakLists();
            Desktop desktop = MZmineCore.getDesktop();
            for (PeakList peakList : selectedPeakLists) {
                PeakListTableWindow window = new PeakListTableWindow(peakList);
                desktop.addInternalFrame(window);
            }
        }

    }

    /**
     * Returns selected raw data objects in an array
     */
    public RawDataFile[] getSelectedRawData() {

        Object o[] = rawDataFiles.getSelectedValues();

        RawDataFile res[] = new RawDataFile[o.length];

        for (int i = 0; i < o.length; i++) {
            res[i] = (RawDataFile) (o[i]);
        }

        return res;

    }

    /**
     * Sets the active raw data item in the list
     */
    public void setActiveRawData(RawDataFile rawData) {
        rawDataFiles.setSelectedValue(rawData, true);
    }

    public PeakList[] getSelectedPeakLists() {

        Object o[] = peakLists.getSelectedValues();

        PeakList res[] = new PeakList[o.length];

        for (int i = 0; i < o.length; i++) {
            res[i] = (PeakList) (o[i]);
        }

        return res;

    }

    public void mouseClicked(MouseEvent e) {

        if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {

            if (e.getSource() == rawDataFiles) {
                int clickedIndex = rawDataFiles.locationToIndex(e.getPoint());
                if (clickedIndex < 0)
                    return;
                RawDataFile clickedFile = (RawDataFile) rawDataFilesModel.get(clickedIndex);
                TICVisualizer.showNewTICVisualizerWindow(
                        new RawDataFile[] { clickedFile }, null);
            }

            if (e.getSource() == peakLists) {
                int clickedIndex = peakLists.locationToIndex(e.getPoint());
                if (clickedIndex < 0)
                    return;
                PeakList clickedPeakList = (PeakList) peakListsModel.get(clickedIndex);
                PeakListTableWindow window = new PeakListTableWindow(
                        clickedPeakList);
                Desktop desktop = MZmineCore.getDesktop();
                desktop.addInternalFrame(window);
            }

        }

    }

    public void mouseEntered(MouseEvent e) {
        // ignore

    }

    public void mouseExited(MouseEvent e) {
        // ignore
    }

    public void mousePressed(MouseEvent e) {

        if (e.isPopupTrigger()) {
            if (e.getSource() == rawDataFiles)
                dataFilePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            if (e.getSource() == peakLists)
                peakListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (e.getSource() == rawDataFiles)
                dataFilePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            if (e.getSource() == peakLists)
                peakListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void valueChanged(ListSelectionEvent event) {

        Object src = event.getSource();

        // Update the highlighting of peak list list in case raw data list
        // selection has changed and vice versa.
        if (src == rawDataFiles) {
            peakLists.repaint();
        }

        if (src == peakLists) {
            rawDataFiles.repaint();
        }

    }

    public void reloadDataModel() {
        MZmineProjectImpl project = (MZmineProjectImpl) MZmineCore.getCurrentProject();

        rawDataFilesModel = project.getRawDataListModel();
        rawDataFiles.setModel(rawDataFilesModel);
        rawDataFiles.repaint();

        peakListsModel = project.getPeakListsListModel();
        peakLists.setModel(peakListsModel);
        peakLists.repaint();

    }

}