/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetTAL;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Manence
 */
public class JPanelQCM extends javax.swing.JPanel {

    private QCM qcm;
    private ArrayList<ArrayList<Boolean>> answers;
    private int i;
    private int nbReponses;
    private boolean state;
    
    /**
     * Creates new form QCMJPanel
     */
    public JPanelQCM(QCM qcm) {
        //System.out.print(qcm.toString());
        this.qcm = qcm;
        this.i = 0;
        this.state = false;
        this.nbReponses = qcm.getQuestion(0).getReponses().size();
        this.answers = new ArrayList<ArrayList<Boolean>>();
        initComponents();
        this.showQuestion(0);
    }

    private void showQuestion(int i)
    {
        this.jLabelQuestion.setText(this.qcm.getQuestion(i).getQuestion());
        jPanelReponses.setReponses(this.qcm.getQuestion(i).getReponses());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelQuestion = new javax.swing.JLabel();
        jButtonOk = new javax.swing.JButton();
        jPanelReponses = new projetTAL.JPanelReponses(this.qcm.getQuestion(0).getReponses().size());
        jLabelScore = new javax.swing.JLabel();

        jLabelQuestion.setText("jLabel1");

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					jButtonOkActionPerformed(evt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelReponses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelQuestion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 455, Short.MAX_VALUE)
                        .addComponent(jLabelScore, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonOk)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelQuestion)
                    .addComponent(jLabelScore))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelReponses, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOk)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) throws IOException {//GEN-FIRST:event_jButtonOkActionPerformed
        if (this.state == false)
        {
            answers.add(this.jPanelReponses.getAnswer());
            this.jPanelReponses.showSolutions();
            this.state = true;
            this.i++;
            this.jButtonOk.setText("Next");
            if(this.i == this.qcm.getNbQuestion())
            {
                this.jLabelScore.setText("Score final: " + this.qcm.getScore(answers));
                this.jButtonOk.setVisible(false);
            }
        }
        else if(i < this.qcm.getNbQuestion())
        {
            this.jButtonOk.setText("Ok");
            showQuestion(i);
            this.state = false;
        }
    }//GEN-LAST:event_jButtonOkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelQuestion;
    private javax.swing.JLabel jLabelScore;
    private projetTAL.JPanelReponses jPanelReponses;
    // End of variables declaration//GEN-END:variables
}
