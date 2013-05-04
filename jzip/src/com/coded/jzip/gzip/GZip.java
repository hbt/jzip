package com.coded.jzip.gzip;

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
 * GZip.java
 * permet de compresser et de décompresser des archives GZIP
 *  Date: 29/12/2005
 * @author Hassen Ben Tanfous
 * @version 1.1
 */

//imports
import java.io.*;
import javax.swing.*;
import java.util.zip.*;
import java.awt.*;
import com.coded.jzip.msg.*;

public class GZip {

    private static final int BUFFER = 4096;

    private File
            source,
            dest;

    private File[]
            tabSources,
            tabDest;

    private Messagerie msg;

    public GZip() {
        msg.titre = "JZip par HBT";
    }

    public GZip(File[] tabFichiers) {
        tabSources = tabFichiers;
    }

    public GZip(File fichier) {
        this.source = fichier;
    }

    public GZip(String fichier) {
        this(new File(fichier));
    }

    public GZip(File source, File destination) {
        this.source = source;
        this.dest = destination;
    }

    public GZip(String source, String destination) {
        this(new File(source), new File(destination));
    }

    public GZip(File[] sources, File[] destinations) {
        tabSources = sources;
        tabDest = destinations;
    }

    public void compresser() {
        new CompresserGzip().start();
    }

    public void decompresser() {
        new DecompresserGzip().start();
    }

    /**
     * DecompresserGzip.java
     * Decompresse une archive GZip
     *  Date: 29/12/2005
     * @author Hassen Ben Tanfous
     */
    private class DecompresserGzip extends Thread {
        private JFrame frame;
        private Container container;
        private JLabel lblInfos;
        private JProgressBar progressBar;

        private BufferedOutputStream buffos;
        private GZIPInputStream gzis;

        private int
                progressValue,
                progressValMax,
                count;
        private byte[] data;

        public DecompresserGzip() {
            progressValue = 0;
            progressValMax = 0;
            data = new byte[BUFFER];
            instancierComposants();
            configurerComposants();
        }

        public void run() {
            decompresserGzip();
            frame.setVisible(false);
            stop();
        }

        private void decompresserGzip() {
            //calcul de la taille de tous les fichiers
            for (int i = 0; i < tabSources.length; i++) {
                progressValMax += tabSources[i].length();
            }

            configProgressBar(progressBar, 0, progressValMax, progressValue);

            for (int i = 0; i < tabSources.length; i++) {
                try {
                    gzis = new GZIPInputStream(new BufferedInputStream(new
                            FileInputStream(tabSources[i])));
                    buffos = new BufferedOutputStream(new FileOutputStream(
                            tabDest[i]));

                    lblInfos.setText(tabSources[i].getName());

                    while ((count = gzis.read(data, 0, BUFFER)) != -1) {
                        buffos.write(data, 0, count);
                        progressValue += count;
                        progressBar.setValue(progressValue);
                    }

                    buffos.close();
                    gzis.close();
                } catch (IOException ex) {
                    msg.msge("erreur lors de la décompression");
                }
            }
        }

        private void instancierComposants() {
            frame = new JFrame("JZip par Hassen Ben Tanfous Décompression GZip");
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
    } //fin de la classe DecompresserGzip


    /**
     * CompresserGzip.java
     * Compresse une archive Gzip
     *  Date: 29/12/2005
     * @author Hassen Ben Tanfous
     */
    private class CompresserGzip extends Thread {
        private JFrame frame;
        private Container container;
        private JLabel lblInfos;
        private JProgressBar progressBar;
        private GZIPOutputStream gzout;
        private BufferedInputStream buffis;

        private int
                progressValue,
                progressValMax,
                count;
        private byte[] data;

        public CompresserGzip() {
            instancierComposants();
            configurerComposants();
            progressValue = 0;
            progressValMax = 0;
            data = new byte[BUFFER];

        }

        public void run() {
            compresserGzip();
            frame.setVisible(false);
            stop();
        }

        private void compresserGzip() {

            //calcul de la taille de tous les fichiers
            for (int i = 0; i < tabSources.length; i++) {
                progressValMax += tabSources[i].length();
            }

            configProgressBar(progressBar, 0, progressValMax,
                              progressValue);

            for (int i = 0; i < tabSources.length; i++) {
                try {
                    //configuration des flux
                    gzout = new GZIPOutputStream(new BufferedOutputStream(new
                            FileOutputStream(tabSources[i].getParent() +
                                             tabSources[i].separator +
                                             tabSources[i].getName() + ".gz")));

                    buffis = new BufferedInputStream(new FileInputStream(
                            tabSources[i].getAbsolutePath()));

                    //informations sur les fichiers
                    lblInfos.setText(tabSources[i].getName());

                    while ((count = buffis.read(data, 0, BUFFER)) != -1) {
                        gzout.write(data, 0, count);
                        progressValue += count;

                        progressBar.setValue(progressValue);
                    }
                    buffis.close();
                    gzout.close();
                } catch (IOException e) {
                    msg.msge("Erreur lors de la compression");
                }
            }
            msg.msgi("Compression Terminée");
        }

        private void instancierComposants() {
            frame = new JFrame("JZip par Hassen Ben Tanfous compression GZIP");
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
    } //fin de la classe CompresserGzip


    private void configProgressBar(JProgressBar bar, int min, int max,
                                   int value) {
        bar.setMinimum(min);
        bar.setMaximum(max);
        bar.setValue(value);
    }
}
