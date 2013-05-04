package com.coded.jzip.zip;

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
 * Zip.java
 * permet de compresser et de décompresser des archives Zip dans des Thread
 * permet de lire et de rechercher un fichier dans une archive Zip
 * rectification du bug avec stored et deflated
 * ajout des fonctionnalites pour la compression et la decompresson des dossiers
 * Date: 16/01/2006
 * @author Hassen Ben Tanfous
 * @version 1.2
 */

//imports
import java.io.*;
import javax.swing.*;
import java.awt.*;
import com.coded.jzip.msg.*;
import java.util.zip.*;

import java.awt.event.*;
import javax.swing.text.*;
import java.util.Enumeration;

public class Zip {
    public static final int BUFFER = 4096;

    public static final int COMPRESSION_MAX = 9;
    public static final int COMPRESSION_MIN = 0;

    private File dest,
            source;

    private File[] tabFiles;

    private Messagerie msg;
    private int level; //level de compression 1-9
//    private boolean stored; //méthode de compression Stored or Deflated


    public Zip() {
        msg.titre = "JZip par HBT";
    }

    /**
     * permet de compresser les fichiers dans une archive
     * @param tabFichiers File[]
     * @param archive File
     */
    public Zip(File[] tabFichiers, File archive) {
        this();
        tabFiles = tabFichiers;
        dest = archive;
    }

    /**
     * permet de décompresser un fichier source vers sa destination
     * @param source File
     * @param destination File
     */
    public Zip(File source, File destination) {
        this();
        this.source = source;
        this.dest = destination;
    }

    /**
     * permet de lire l'archive source
     * @param source File
     */
    public Zip(File source) {
        this();
        this.source = source;
    }

    public void compresser() {
        new CompresserZip().start();
    }

    public void decompresser() {
        new DecompresserZip().start();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

//    public void setStored(boolean stored) {
//        this.stored = stored;
//    }
//
//    public boolean isStored() {
//        return stored;
//    }

    public void lireArchive() {
        new LectureArchive().start();
    }

    /**
     * CompresserZip.java
     * permet de compresser dans un autre Thread des fichiers
     * Date: 29/12/2005
     * @author Hassen Ben Tanfous
     */
    private class CompresserZip extends Thread implements Runnable {
        private JFrame frame;
        private Container container;
        private JLabel lblInfos;
        private JProgressBar progressBar;
        private ZipOutputStream zout;
        private BufferedInputStream buffis;
        private ZipEntry entry;

        private int
                count,
                progressValue,
                progressValMax,
                ichoix;
        private String[] strOptions = {
                                      "Oui", "Non"};

        private byte[] data;

        public CompresserZip() {
            data = new byte[BUFFER];
            instancierComposants();
            configurerComposants();
            progressValue = 0;
            progressValMax = 0;
        }

        public void run() {
            compressionZip();
            frame.setVisible(false);
            stop();
        }

        private void compressionZip() {
            for (int i = 0; i < tabFiles.length; i++) {
                progressValMax += tabFiles[i].length();
            }

            progressBar.setMinimum(0);
            progressBar.setMaximum(progressValMax);
            progressBar.setValue(progressValue);

            try {
                if (dest.exists()) {
                    ichoix = JOptionPane.showOptionDialog(null,
                            "Le fichier " + dest.getName() +
                            " existe. Voulez-vous l'écraser ?",
                            "Confirmation", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, strOptions,
                            strOptions[1]);
                    if (ichoix == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                msg.msge("Impossible de supprimer le fichier " + dest.getName());
            }

            try {
                zout = new ZipOutputStream(new
                                           BufferedOutputStream(new
                        FileOutputStream(dest)));
//                if (stored) {
//                    zout.setMethod(zout.STORED);
//                } else {
//                    zout.setMethod(zout.DEFLATED);
//                    zout.setLevel(level);
//                }
                zout.setMethod(zout.DEFLATED);
                zout.setLevel(level);

                int count;
                for (int i = 0; i < tabFiles.length; i++) {
                    lblInfos.setText(tabFiles[i].getName());
                    verifierFichier (tabFiles[i]);
//                    if (tabFiles[i].isDirectory()) {
//
//                    }
//                    buffis = new BufferedInputStream(new
//                            FileInputStream(tabFiles[i]),
//                            BUFFER);
//
//                    entry = new ZipEntry(tabFiles[i].getName());
//
//                    zout.putNextEntry(entry);
//
//                    while ((count = buffis.read(data, 0, BUFFER)) != -1) {
//                        zout.write(data, 0, count);
//                        progressValue += count;
//                        progressBar.setValue(progressValue);
//                    }
//                    zout.closeEntry();
//                    buffis.close();
                }
                zout.close();

                msg.msgi("Compression terminée du fichier " +
                         dest.getName());
                lblInfos.setText("Compression terminée");
            } catch (IOException e) {
                e.printStackTrace();
                msg.msge("Fichier introuvable");
            }
        }

        /**
         * verifie si le fichier est un dossier ou un fichier
         * et fait appel a la compression
         * @param fichier File
         */
        private void verifierFichier(File fichier) {
            if (fichier.isDirectory()) {
                File[] files = fichier.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        verifierFichier(files[i]);
                    } else {
                        ajouterFichier(files[i]);
                    }
                }
            } else if (fichier.isFile()) {
                ajouterFichier(fichier);
            }
        }

        /**
         * ajoute le fichier à l'archive
         * @param fichier File
         */
        private void ajouterFichier(File fichier) {
            lblInfos.setText(fichier.getName());
            entry = new ZipEntry(fichier.getParent() + fichier.separator + fichier.getName());
            try {
                zout.putNextEntry(entry);
                buffis = new BufferedInputStream(new FileInputStream(fichier));
                while ((count = buffis.read(data, 0, BUFFER)) != -1) {
                    zout.write(data, 0, count);
                    progressValue += count;
                    progressBar.setValue(progressValue);
                }
                zout.closeEntry();
                buffis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        private void instancierComposants() {
            frame = new JFrame("JZip par Hassen Ben Tanfous compression ZIP");
            container = frame.getContentPane();

            lblInfos = new JLabel();
            progressBar = new JProgressBar();
        }

        private void configurerComposants() {
            container.setLayout(null);

            ajouterComposant(container, lblInfos, 125, 10, 300, 25);
            ajouterComposant(container, progressBar, 5, 50, 475, 25);

            frame.setSize(500, 125);
            frame.setLocation(new Point(300, 300));
            frame.setVisible(true);
        }

        private void ajouterComposant(Container c, Component comp, int x, int y,
                                      int x1, int y1) {
            comp.setBounds(x, y, x1, y1);
            c.add(comp);
        }

    }


    /**
     * DecompresserZip.java
     * permet de décompresser une archive Zip
     * Date: 29/12/2005
     * @author Hassen Ben Tanfous
     */
    private class DecompresserZip extends Thread implements Runnable {
        private JFrame frame;
        private Container container;
        private JLabel lblInfos;
        private JProgressBar progressBar;

        private ZipInputStream zis;
        private BufferedOutputStream buffos;
        private ZipEntry entry;

        private int count,
                progressValue,
                progressValMax;

        private byte[] data;


        public DecompresserZip() {
            instancierComposants();
            configurerComposants();
            progressValue = 0;
            progressValMax = 0;
            data = new byte[BUFFER];
        }

        public void run() {
            decompresserZip();
            frame.setVisible(false);
            stop();
        }

        private void decompresserZip() {
            progressValMax = (int) source.length();
            progressBar.setMinimum(0);
            progressBar.setMaximum(progressValMax);
            progressBar.setValue(progressValue);

            try {
                FileInputStream fis = new FileInputStream(source.
                        getAbsolutePath());
                BufferedInputStream buffis = new BufferedInputStream(fis);
                zis = new ZipInputStream(new BufferedInputStream(new
                        FileInputStream(source)));

                while ((entry = zis.getNextEntry()) != null) {
                    lblInfos.setText(entry.getName());
                    if (entry.isDirectory()) {

                        File dossierEntry = new File(dest.getAbsolutePath() +
                                dest.separator +
                                entry.getName() + dest.separator);
                        dossierEntry.mkdirs();

                    } else {
                        buffos = new BufferedOutputStream(new FileOutputStream(
                                dest.getAbsolutePath() + dest.separator +
                                entry.getName()), BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            buffos.write(data, 0, count);
                            progressValue += (count);
                            progressBar.setValue(progressValue);
                        }
                        buffos.close();
                        buffos.flush();
                    }
                }
                zis.closeEntry();
                zis.close();
                buffis.close();
                lblInfos.setText("Décompression Terminée");
            } catch (IOException e) {
            }
            msg.msgi("Décompression Terminée");
        }

        private void instancierComposants() {
            frame = new JFrame("JZip par Hassen Ben Tanfous compression ZIP");
            container = frame.getContentPane();

            lblInfos = new JLabel();
            progressBar = new JProgressBar();
        }

        private void configurerComposants() {
            container.setLayout(null);

            ajouterComposant(container, lblInfos, 125, 10, 300, 25);
            ajouterComposant(container, progressBar, 5, 50, 475, 25);

            frame.setSize(500, 125);
            frame.setLocation(new Point(300, 300));
            frame.setVisible(true);
        }

        private void ajouterComposant(Container c, Component comp, int x, int y,
                                      int x1, int y1) {
            comp.setBounds(x, y, x1, y1);
            c.add(comp);
        }
    }


    /**
     * LectureArchive.java
     * lit une archive et permet à l'utilisateur de faire une recherche
     * Date: 29/12/2005
     * @author Hassen Ben Tanfous
     */
    private class LectureArchive extends Thread {
        private JFrame frameLire;

        private Container cLire;

        private JTextArea areaLire;

        private JScrollPane scrollLire;

        private JTextField txtSearch;

        private JLabel lblSearch;

        private MonSurligneur monSurligneur;

        private String strContenu = "";

        private LectureArchive() {
            instancierComposants();
            configurerComposants();
        }

        public void run() {

            String strContenu = "";
            try {
                ZipFile zf = new ZipFile(source.getAbsolutePath());
                Enumeration entries = zf.entries();
                ZipEntry entry = null;
                while (entries.hasMoreElements()) {
                    entry = (ZipEntry) entries.nextElement();
                    strContenu += entry.getName();
                    strContenu += "\t" + entry.getSize() + "\n";
                }
            } catch (IOException e) {
                msg.msge("Fichier introuvable");
            }
            areaLire.setAutoscrolls(true);
            areaLire.setFont(new Font("courier", Font.PLAIN, 12));
            areaLire.setText(strContenu);
        }

        private void instancierComposants() {
            frameLire = new JFrame("Lecture d'archive Zip");
            cLire = frameLire.getContentPane();

            areaLire = new JTextArea();
            scrollLire = new JScrollPane(areaLire);

            txtSearch = new JTextField();
            lblSearch = new JLabel("Search:");

            monSurligneur = new MonSurligneur(Color.red);
        }

        private void configurerComposants() {
            //Modèle
            areaLire.setEditable(false);
            areaLire.setFont(new Font("courier", Font.PLAIN, 12));
            areaLire.setText(strContenu);
            txtSearch.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == txtSearch) {
                        rechercherFichier(areaLire, txtSearch.getText());
                    }
                }
            });
            cLire.setLayout(null);
            ajouterComposant(cLire, lblSearch, 10, 10, 50, 20);
            ajouterComposant(cLire, txtSearch, 60, 12, 200, 20);
            ajouterComposant(cLire, scrollLire, 10, 40, 250, 200);
            frameLire.setLocation(300, 200);
            frameLire.setSize(300, 300);
            frameLire.show();
        }

        private void ajouterComposant(Container c, Component comp, int x, int y,
                                      int x1, int y1) {
            comp.setBounds(x, y, x1, y1);
            c.add(comp);
        }

        /**
         * permet de surligner dans le texte le fichier trouvé
         * @param txtComp JTextComponent
         * @param pattern String
         */
        private void rechercherFichier(JTextComponent txtComp, String pattern) {
            enleverHighlight(txtComp);
            try {
                Highlighter surligneur = txtComp.getHighlighter();
                Document doc = txtComp.getDocument();
                String txt = doc.getText(0, doc.getLength());
                int pos = 0;
                while ((pos = txt.indexOf(pattern, pos)) >= 0) {
                    surligneur.addHighlight(pos, pos + pattern.length(),
                                            monSurligneur);
                    pos += pattern.length();
                }
            } catch (BadLocationException e) {}
        }

        /**
         * enleve les highlighter dans txtComp pour les remplacer par mes
         * surligneurs
         * @param txtComp JTextComponent
         */
        private void enleverHighlight(JTextComponent txtComp) {
            Highlighter surligneur = txtComp.getHighlighter();
            Highlighter.Highlight[] tabSurligneurs = surligneur.getHighlights();
            for (int i = 0; i < tabSurligneurs.length; i++) {
                if (tabSurligneurs[i].getPainter() instanceof MonSurligneur) {
                    surligneur.removeHighlight(tabSurligneurs[i]);
                }
            }
        }

        /**
         * MonSurligneur.java
         * représente le surligneur de texte
         * Date: 29/12/2005
         * @author Hassen Ben Tanfous
         */
        private class MonSurligneur extends DefaultHighlighter.
                DefaultHighlightPainter {
            public MonSurligneur(Color couleur) {
                super(couleur);
            }
        }
    } //fin de la classe LectureArchive

}
