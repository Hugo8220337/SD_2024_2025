/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipp.estg.pages.main;

import ipp.estg.Client;
import ipp.estg.pages.login.LoginPage;
import ipp.estg.pages.userApproval.UserApprovalPage;

/**
 *
 * @author User
 */
public class MainPage extends javax.swing.JFrame {


    private final Client client;

    /**
     * Creates new form MainPage
     */
    public MainPage(Client client) {
        initComponents();

        this.client = client;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aproveNewUsersBtn = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        aproveNewUsersBtn.setText("Aprove New Users");
        aproveNewUsersBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aproveNewUsersBtnActionPerformed(evt);
            }
        });

        errorLabel.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        errorLabel.setForeground(new java.awt.Color(239, 0, 0));
        errorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(105, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(aproveNewUsersBtn)
                        .addGap(134, 134, 134))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))))
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(logoutBtn)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(logoutBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addComponent(aproveNewUsersBtn)
                .addGap(32, 32, 32)
                .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aproveNewUsersBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aproveNewUsersBtnActionPerformed
        UserApprovalPage userApprovalPage = new UserApprovalPage(client);
        userApprovalPage.setVisible(true); // open UserApprovalPage
        this.setVisible(false); // close current page
    }//GEN-LAST:event_aproveNewUsersBtnActionPerformed

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
        // clear auth variables
        client.setAuthToken("");
        client.setLoggedUserId("");
        
        // close this window and opens the login window
        LoginPage loginPage = new LoginPage(client);
        loginPage.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aproveNewUsersBtn;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JButton logoutBtn;
    // End of variables declaration//GEN-END:variables
}
