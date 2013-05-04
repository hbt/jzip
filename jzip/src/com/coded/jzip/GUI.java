package com.coded.jzip;

/*
 * Copyright (C) 2006 Hassen Ben Tanfous
 * All right reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	3. Neither the name of the Hassen Ben Tanfous nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * GUI.java
 * est l'interface graphique de JZIP
 * elle fait appel à toutes les fonctions existantes de l'API ZIP et GZIP
 * bug avec stored et deflated rectifie
 * ajout de la compression pour les dossiers
 * Date: 29/05/2005
 * @author Hassen Ben Tanfous
 * @version 1.2
 */

//imports
import java.io.*; //Input Ouput
import java.util.*;
import java.util.zip.*;

import javax.swing.*; //affichage

import javax.swing.event.*;
import java.awt.*; //container
import java.awt.event.*;
import javax.swing.text.*;

import com.coded.jzip.gzip.*;
import com.coded.jzip.zip.*;
import com.coded.jzip.msg.*;

public class GUI extends JFrame {
    private JTabbedPane paneTab; //panneau de tabulation

    private JPanel paneZip, //panneau pour Zip
            paneGzip; //panneau pour GZip

    private JButton boutonZip,
            boutonUnzip,
            boutonGzip,
            boutonGunzip,
            boutonLire;

    private JFileChooser jfcFichiers,
            jfcZip,
            jfcUnzip,
            jfcGzip,
            jfcGunzip,
            jfcLireArchive,
            jfcZipTo,
            jfcGzipTo;

    private File[] tabZip,
            tabGzip,
            tabGunzip;

    private File fUnzip,
            fGzip,
            fGunzip;

    private JLabel
            lblLevel, //level de compression (Zip)
            lblNomsZip,
            lblNomsGzip,
            lblOperations,
            lblOperations2; //pour Gzip

    private JSeparator sepLigne;
//
//    private JRadioButton
//            radioDeflated; //compression d'après level (lblLevel)

    private JScrollPane scrollZip,
            scrollGzip;

    private Container c;

    private JSpinner spinCompteur; //compteur pour level de compression

    private SpinnerNumberModel spinModeleNb; //modèle de compteur

    private Number nbVal; //nombre sélectionné d'après le compteur

    private JTextArea txtZip, //accumule la sélection des fichiers
            txtGzip;

    private GZip gzip;

    private Zip zip;

    private Messagerie msg;

    private String[] tabExtension = {
                                    "txt", "pdf", "mp3", "jpg"},
                                    tabDescription = {
            "Document texte",
            "Adobe Acrobat Document",
            "MP3 File", "Image JPEG"};

    private JCheckBox checkZipTo,
            checkZipHere,
            checkZipFolder,
            checkGzipTo,
            checkGzipHere,
            checkGzipFolder;

    private int iVal; //valeur de la compression Zip deflated

    private static final int BUFFER = 2048; //taille du buffer

    private static final byte[] data = new byte[BUFFER];

    private Filtres[] tabFiltresFichiers;

    private long progressValueZip,
            progressValueGzip;

    private int progressValZip,
            progressValGzip;

    public GUI() {
        instancierComposants();
        configurerComposants();
    }

    /**
     *  instancierComposants : instancie les attributs et
     *  initialise les constructeurs des composants
     *  retour: aucun (void)
     */
    private void instancierComposants() {

        //Le container
        c = getContentPane();

        //ajout des composants dans les containers en ordre
        paneTab = new JTabbedPane();

        //panneau Zip
        paneZip = new JPanel();
        paneZip.setLayout(null);

        //panneau GZip
        paneGzip = new JPanel();
        paneGzip.setLayout(null);

        //boutons Zip et Unzip
        boutonZip = new JButton("Compresser Zip");
        boutonUnzip = new JButton("Décompresser Zip");
        boutonLire = new JButton("Lire archive");

        //bouton Gzip et Gunzip
        boutonGzip = new JButton("Compresser Gzip");
        boutonGunzip = new JButton("Décompresser Gunzip");

        //JFileChooser Zip et Unzip
        jfcFichiers = new JFileChooser("");
        jfcZip = new JFileChooser("");
        jfcUnzip = new JFileChooser("");

        //JFileChooser Gzip et Gunzip
        jfcGzip = new JFileChooser("");
        jfcGunzip = new JFileChooser("");

        //Lire archive
        jfcLireArchive = new JFileChooser("");

        //DecompresserZip To checkbox
        jfcZipTo = new JFileChooser("");
        jfcGzipTo = new JFileChooser("");

        //filtres pour JFileChooser
        tabFiltresFichiers = new Filtres[4];

        //ligne séparatrice
        sepLigne = new JSeparator();

        //compteur
        spinCompteur = new JSpinner();
        spinModeleNb = new SpinnerNumberModel();
        nbVal = null;

        //label
//        lblMethode = new JLabel("Méthode de compression");
        lblLevel = new JLabel("Niveau de compression (0-9)");
        lblNomsZip = new JLabel("");
        lblNomsGzip = new JLabel("");
        lblOperations = new JLabel("Fichiers sélectionnés");
        lblOperations2 = new JLabel("Fichiers sélectionnés");

//        //radio button
//        radioStored = new JRadioButton("Stored");
//        radioDeflated = new JRadioButton("Deflated");

        //jtextarea
        txtZip = new JTextArea();
        txtGzip = new JTextArea();

        //jscrollpane
        scrollZip = new JScrollPane(txtZip);
        scrollGzip = new JScrollPane(txtGzip);

        //JProgressBar


        //JCheckBox
        checkZipTo = new JCheckBox("To ...");
        checkZipHere = new JCheckBox("Here");
        checkZipFolder = new JCheckBox("Folder ");
        checkGzipTo = new JCheckBox("To ...");
        checkGzipHere = new JCheckBox("Here");
        checkGzipFolder = new JCheckBox("Folder ");

        //GZip
        gzip = new GZip();

        //Zip
        zip = new Zip();

        //messagerie
        msg.titre = "JZip par HBT";
    }


    private void configurerComposants() {
        //disposition des composants
        paneZip.setLayout(null);
        paneGzip.setLayout(null);

        //modèle texte Zip et Gzip
        txtZip.setEditable(false);
        txtGzip.setEditable(false);

        //modèle bouton Zip et Gzip
        boutonZip.setToolTipText("Compresse les documents sélectionnés");
        boutonUnzip.setToolTipText("Décompresse l'archive Zip sélectionné");

        boutonGzip.setToolTipText("Compresse le fichier sélectionné");
        boutonGunzip.setToolTipText("Décompresse l'archive Gzip sélectionné");

        //modèle compteur
        spinCompteur.setModel(spinModeleNb);
        spinCompteur.setValue(new Integer(Zip.COMPRESSION_MAX)); //initialisation
        iVal = Zip.COMPRESSION_MAX;

//        //modèle radio
//        radioStored.setToolTipText("Aucune compression");
//        radioDeflated.setToolTipText("Compresse d'après le level choisi");
//        radioDeflated.setSelected(true);

        //modèle JFileChoosers sélection de fichiers
        construireFiltre(tabExtension, tabDescription, tabFiltresFichiers,
                         jfcFichiers);
        jfcFichiers.setAcceptAllFileFilterUsed(true);
        jfcFichiers.setMultiSelectionEnabled(true);
        jfcFichiers.setToolTipText("Sélectionner un ou plusieurs fichiers");
        jfcFichiers.setApproveButtonText("Ajouter");
        jfcFichiers.setDialogTitle("Ajouter les fichiers à la sélection");
        jfcFichiers.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //modèle JFileChooser save compression Zip
        jfcZip.addChoosableFileFilter(new Filtres("zip",
                                                  "Fichier compressé Zip"));
        jfcZip.setAcceptAllFileFilterUsed(false);
        jfcZip.setMultiSelectionEnabled(false);
        jfcZip.setToolTipText("Entrez un nom d'archive");
        jfcZip.setApproveButtonText("Enregistrer");
        jfcZip.setDialogTitle("Enregistrer l'archive Zip");

        //modèle JFileChooser décompression Zip
        jfcUnzip.addChoosableFileFilter(new Filtres("zip",
                "Fichier compressé Zip"));
        jfcUnzip.setAcceptAllFileFilterUsed(false);
        jfcUnzip.setMultiSelectionEnabled(false);
        jfcUnzip.setToolTipText("Ouvre l'archive à décompresser");
        jfcUnzip.setApproveButtonText("Décompresser");
        jfcUnzip.setDialogTitle("Décompresser une archive Zip");

        //modèle JFileChooser compression Gzip
        construireFiltre(tabExtension, tabDescription, tabFiltresFichiers,
                         jfcGzip);
        jfcGzip.setAcceptAllFileFilterUsed(true);
        jfcGzip.setMultiSelectionEnabled(true);
        jfcGzip.setToolTipText("Compresse les fichiers");
        jfcGzip.setApproveButtonText("Compresser");
        jfcGzip.setDialogTitle("Compresser une archive Gzip");

        //modèle JFileChooser décompression Gzip
        jfcGunzip.setAcceptAllFileFilterUsed(false);
        jfcGunzip.addChoosableFileFilter(new Filtres(".gz", "Gzip File"));
        jfcGunzip.setMultiSelectionEnabled(true);
        jfcGunzip.setToolTipText("Décompresse les fichiers sélectionnés");
        jfcGunzip.setApproveButtonText("Décompresser");
        jfcGunzip.setDialogTitle("Décompresser une archive Gzip");

        //modèle JFileChooser lecture d'archive
        jfcLireArchive.setAcceptAllFileFilterUsed(false);
        jfcLireArchive.addChoosableFileFilter(new Filtres(".zip",
                "Zip File"));
        jfcLireArchive.setMultiSelectionEnabled(false);
        jfcLireArchive.setDialogTitle("Lecture d'archive");
        jfcLireArchive.setApproveButtonText("Lire");

        //modèle JFileChooser decompresserZip To checkbox
        jfcZipTo.setAcceptAllFileFilterUsed(false);
        jfcZipTo.setMultiSelectionEnabled(false);
        jfcZipTo.setDialogTitle("To Folder:");
        jfcZipTo.setApproveButtonText("Unzip");

        jfcGzipTo.setAcceptAllFileFilterUsed(false);
        jfcGzipTo.setMultiSelectionEnabled(false);
        jfcGzipTo.setDialogTitle("");
        jfcGzipTo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //ajout écouteur boutons Zip
        boutonZip.addActionListener(alZip);
        boutonUnzip.addActionListener(alZip);
        boutonLire.addActionListener(alLire);
        boutonLire.setToolTipText(
                "Lis et permet de faire des recherches dans une archive");

        //ajout écouteur boutons Gzip
        boutonGzip.addActionListener(alGzip);
        boutonGunzip.addActionListener(alGzip);

        //ajout écouteur au compteur
        spinCompteur.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                nbVal = spinModeleNb.getNumber();
                iVal = nbVal.intValue();
                if (iVal > Zip.COMPRESSION_MAX) {
                    spinCompteur.setValue(new Integer(Zip.COMPRESSION_MAX));
                } else if (iVal < Zip.COMPRESSION_MIN) {
                    spinCompteur.setValue(new Integer(Zip.COMPRESSION_MIN));
                }
            }
        });

//        //ajout écouteur aux boutons radio
//        radioStored.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    radioDeflated.setSelected(false);
//                }
//            }
//        });
//
//        radioDeflated.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                    radioStored.setSelected(false);
//                }
//            }
//        });

        //Modèle JCheckBox
        checkZipFolder.setSelected(true);
        checkGzipHere.setSelected(true);

        //ajout écouteurs au JCheckBox
        checkZipTo.addActionListener(alCheck);
        checkZipFolder.addActionListener(alCheck);
        checkZipHere.addActionListener(alCheck);

        checkGzipTo.addActionListener(alCheck);
        checkGzipFolder.addActionListener(alCheck);
        checkGzipHere.addActionListener(alCheck);

        //ajouter les composants au panel Zip
        //partie compresser Zip
        ajouterComposant(paneZip, boutonZip, 0, 35, 150, 35);
        ajouterComposant(paneZip, lblLevel, 200, 5, 200, 35);
//        ajouterComposant(paneZip, lblMethode, 400, 5, 150, 35);
        ajouterComposant(paneZip, spinCompteur, 240, 35, 50, 35);
//        ajouterComposant(paneZip, radioStored, 400, 35, 100, 35);
//        ajouterComposant(paneZip, radioDeflated, 400, 65, 100, 35);

        //ligne séparatrice
        ajouterComposant(paneZip, sepLigne, 0, 100, 600, 1);

        //partie décompresser
        ajouterComposant(paneZip, boutonUnzip, 0, 135, 150, 35);
        ajouterComposant(paneZip, lblNomsZip, 390, 120, 150, 25);
        ajouterComposant(paneZip, scrollZip, 200, 150, 350, 150);
        ajouterComposant(paneZip, lblOperations, 199, 115, 200, 38);
        ajouterComposant(paneZip, boutonLire, 0, 210, 120, 35);
        ajouterComposant(paneZip, checkZipTo, 10, 265, 50, 25);
        ajouterComposant(paneZip, checkZipHere, 60, 265, 50, 25);
        ajouterComposant(paneZip, checkZipFolder, 110, 265, 85, 25);

        //ajouter les composants au panel Gzip
        //partie compresser Gzip
        ajouterComposant(paneGzip, boutonGzip, 0, 35, 200, 35);
        ajouterComposant(paneGzip, boutonGunzip, 0, 90, 200, 35);
        ajouterComposant(paneGzip, scrollGzip, 250, 90, 300, 200);
        ajouterComposant(paneGzip, lblNomsGzip, 365, 68, 150, 25);
        ajouterComposant(paneGzip, lblOperations2, 250, 75, 200, 10);

        ajouterComposant(paneGzip, checkGzipTo, 10, 150, 150, 20);
        ajouterComposant(paneGzip, checkGzipHere, 10, 180, 100, 20);
        ajouterComposant(paneGzip, checkGzipFolder, 10, 210, 100, 20);

        //ajouter au jtabbedpane
        paneTab.addTab("Archive Zip", paneZip);

        paneTab.addTab("Archive Gzip", paneGzip);

        c.add(paneTab);

        setTitle("JZip par Hassen Ben Tanfous");
        setLocation(new Point(250, 150));
        setSize(new Dimension(600, 400));
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            try {
                UIManager.setLookAndFeel(
                        "javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (ClassNotFoundException e1) {
            } catch (InstantiationException e1) {
            } catch (IllegalAccessException e1) {
            } catch (UnsupportedLookAndFeelException e1) {
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }
    }

    //ActionListener alCheck
    //permet de decocher les autres options pour empecher l'utilisateur
    //de faire une erreur au niveau du chemin de decompression
    private ActionListener alCheck = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == checkZipTo) {
                checkZipFolder.setSelected(false);
                checkZipHere.setSelected(false);
            } else if (e.getSource() == checkZipFolder) {
                checkZipTo.setSelected(false);
                checkZipHere.setSelected(false);
            } else if (e.getSource() == checkZipHere) {
                checkZipTo.setSelected(false);
                checkZipFolder.setSelected(false);
            }

            else if (e.getSource() == checkGzipTo) {
                checkGzipFolder.setSelected(false);
                checkGzipHere.setSelected(false);
            } else if (e.getSource() == checkGzipFolder) {
                checkGzipTo.setSelected(false);
                checkGzipHere.setSelected(false);
            } else if (e.getSource() == checkGzipHere) {
                checkGzipFolder.setSelected(false);
                checkGzipTo.setSelected(false);
            }
        }
    };

    //ActionListener alLire
    //permet de lire une archive et de faire des recherche à l'aide de la
    private ActionListener alLire = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == boutonLire) {
                int choix = jfcLireArchive.showOpenDialog(null);
                if (choix == JFileChooser.APPROVE_OPTION) {
                    File f = jfcLireArchive.getSelectedFile();
                    zip = new Zip(f);
                    zip.lireArchive();
                }
            }
        }
    };


    //ActionListener alCompresserZip
    //permet de compresser les fichiers sélectionnées en créant un Thread
    private ActionListener alZip = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int ichoix;
            String strArchive;
            File zipped = null,
                          dossier = null;

            if (e.getSource() == boutonZip) {
                ichoix = jfcFichiers.showOpenDialog(null);
                if (ichoix == JFileChooser.APPROVE_OPTION) {
                    tabZip = jfcFichiers.getSelectedFiles();
                    remplirTexteFichiers(tabZip, "Zip");

                    do {
                        ichoix = jfcZip.showSaveDialog(null);
                        if (ichoix == JFileChooser.APPROVE_OPTION) {
                            strArchive = jfcZip.getSelectedFile().
                                         getAbsolutePath();
                            zip = new Zip(tabZip, new File(strArchive));
//                            if (radioStored.isSelected()) {
//                                zip.setStored(true);
//                            } else {
//                                zip.setStored(false);
//                                zip.setLevel(iVal);
//                            }
                            zip.setLevel(iVal);
                            zip.compresser();
                        }
                    } while (ichoix == 1);

                }
            } else if (e.getSource() == boutonUnzip) {
                ichoix = jfcUnzip.showOpenDialog(null);
                if (ichoix == JFileChooser.APPROVE_OPTION) {
                    zipped = jfcUnzip.getSelectedFile();

                    if (checkZipTo.isSelected()) {
                        jfcZipTo.setFileSelectionMode(JFileChooser.
                                DIRECTORIES_ONLY);
                        ichoix = jfcZipTo.showOpenDialog(null);
                        if (ichoix == JFileChooser.APPROVE_OPTION) {
                            dossier = jfcZipTo.getSelectedFile();
                        }
                    } else if (checkZipFolder.isSelected()) {
                        dossier = new File(zipped.getParent() +
                                           zipped.separator +
                                           zipped.getName().substring(0,
                                zipped.getName().lastIndexOf('.')));
                        dossier.mkdirs();
                    } else if (checkZipHere.isSelected()) {
                        dossier = new File(zipped.getParent());
                    }

                    zip = new Zip(zipped, dossier);
                    zip.decompresser();
                    //Démarrer un Thread pour la décompression
                }
            }
        }
    };

    //ActionListener alCompresserGzip permet de compresser des fichiers
    private ActionListener alGzip = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int ichoix;

            if (e.getSource() == boutonGzip) {
                ichoix = jfcGzip.showOpenDialog(null);
                if (ichoix == 0) {
                    tabGzip = jfcGzip.getSelectedFiles();
                    remplirTexteFichiers(tabGzip, "gzip");

                    gzip = new GZip(tabGzip);
                    gzip.compresser();
                }
            } else if (e.getSource() == boutonGunzip) {
                int index;
                String str = null;
                File dossier;
                File tabDest[] = null; //fichier destination

                if (e.getSource() == boutonGunzip) {
                    ichoix = jfcGunzip.showOpenDialog(null);
                    if (ichoix == JFileChooser.APPROVE_OPTION) {
                        tabGunzip = jfcGunzip.getSelectedFiles();
                        tabDest = new File[tabGunzip.length];
                        remplirTexteFichiers(tabGunzip, "gzip");
                        for (int i = 0; i < tabGunzip.length; i++) {

                            if (checkGzipTo.isSelected()) {
                                jfcGzipTo.setFileSelectionMode(JFileChooser.
                                        DIRECTORIES_ONLY);
                                jfcGzipTo.setDialogTitle(tabGunzip[i].
                                        getName() +
                                        " TO FOLDER:");
                                ichoix = jfcGzipTo.showOpenDialog(null);
                                if (ichoix == JFileChooser.APPROVE_OPTION) {
                                    index = tabGunzip[i].getName().indexOf(
                                            ".gz");
                                    str = jfcGzipTo.getSelectedFile().
                                          getAbsolutePath() +
                                          tabGunzip[i].separator +
                                          tabGunzip[i].getName().substring(
                                                  0,
                                                  index);

                                    tabDest[i] = new File(str);
                                }
                            } else if (checkGzipFolder.isSelected()) {
                                index = tabGunzip[i].getName().indexOf(
                                        ".gz");
                                dossier = new File(tabGunzip[i].
                                        getParentFile().
                                        getAbsolutePath() +
                                        tabGunzip[i].separator +
                                        tabGunzip[i].getName().substring(
                                                0, index));
                                dossier.mkdirs();
                                str = tabGunzip[i].getName().substring(0,
                                        index);

                                tabDest[i] = new File(dossier.
                                        getAbsolutePath() + dossier.separator +
                                        str);
                            } else if (checkGzipHere.isSelected()) {
                                index = tabGunzip[i].getAbsolutePath().
                                        indexOf(
                                                ".gz");
                                str = tabGunzip[i].getAbsolutePath().
                                      substring(
                                              0,
                                              index);

                                tabDest[i] = new File(str);
                            }
                        }
                        gzip = new GZip(tabGunzip, tabDest);
                        gzip.decompresser();
                    }
                }
            }
        }
    };

    /**
     * remplirTexteFichiers : remplit le champ de texte avec les noms des fichiers
     * qui ont été ajouter avec succès
     * @param tab[]: tableau de fichiers sélectionnés
     * @param type : type de compression Zip ou Gzip
     * retour: aucun (void)
     */
    private void remplirTexteFichiers(File[] tab, String type) {
        String strContenuZip = "";

        for (int i = 0; i < tab.length; i++) {
            strContenuZip += tab[i].getName() +
                    " est sélectionné avec succès\n";
        }

        if (type.equalsIgnoreCase("zip")) {
            txtZip.setText(strContenuZip);
        } else if (type.equalsIgnoreCase("gzip")) {
            txtGzip.setText(strContenuZip);
        }
    }

    /**
     * ajouterComposant : ajoute le composant dans le container
     * d'après les positions x1, y1, x2, y2
     * @param c: Container recevant le composant
     * @param comp : composant à ajouter au Container
     * @param x1 : position x
     * @param y1 : position y
     * @param x2 : largeur x1 - x2
     * @param y2 : hauteur y1 - y2
     * retour: aucun (void)
     */
    private void ajouterComposant(Container c, Component comp, int x1,
                                  int y1,
                                  int x2, int y2) {
        comp.setBounds(x1, y1, x2, y2);
        c.add(comp);
    }

    /**
     * construireFiltre : construit un filtre pour les types de fichiers
     * @param ext[]: tableau contenant les extensions des fichiers
     * @param desp[]: tableau contenant les descriptions des fichiers
     * @param filtre[]: tableau qui est construie
     * retour: aucun (void)
     */
    private void construireFiltre(String[] ext, String[] desp,
                                  Filtres[] filtre,
                                  JFileChooser jfc) {

        for (int i = 0; i < filtre.length; i++) {
            filtre[i] = new Filtres(ext[i], desp[i]);
            jfc.addChoosableFileFilter(filtre[i]);
        }
    }

    /**
     * permet de construire un filtre pour le JFileChooser
     */
    private class Filtres extends javax.swing.filechooser.FileFilter {
        private String strext, //extension du fichier
                strdesp; //description du fichier

        Filtres(String ext, String desp) {
            strext = ext;
            strdesp = desp;
        }

        //accepte et ajoute le fichier dans le filtre
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else if (f.isFile() &&
                       f.getName().toLowerCase().endsWith(strext)) {
                return true;
            }
            return false;
        }

        //obtenir description du fichier ajouté au filtre
        public String getDescription() {
            return strdesp;
        }
    }
}
