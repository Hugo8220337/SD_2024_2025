/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipp.estg.pages.massEvacuationApproval;

import ipp.estg.Client;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.models.MassEvacuation;
import ipp.estg.pages.main.MainPage;
import ipp.estg.utils.JsonConverter;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Page to approve or deny Mass Evacuation Requests
 */
public class MassEvacuationApprovalPage extends JFrame {

    private final Client client;

    /**
     * Map to store the Mass Evacuation Request and its respective index on the list.
     * Because the selected index of the list and the Mass Evacuation Request Id might be different
     */
    private final Map<String, MassEvacuation> requestIdToRequestMap = new HashMap<>();

    /**
     * Creates new form UserApprovalPage
     */
    public MassEvacuationApprovalPage(Client client) {
        this.client = client;

        initComponents();
        loadPendingClientsToList();
    }

    private void loadPendingClientsToList() {
        RequestsForApprovalList.removeAll();
        requestIdToRequestMap.clear();

        // Get pending users (PENDING_APPROVALS)
        String request = CommandsFromClient.GET_MASS_EVACUATION_PENDING_APPROVALS;
        String response = client.sendMessageToServer(request);

        // Parse response
        JsonConverter jsonConverter = new JsonConverter();
        List<MassEvacuation> pendingRequests = jsonConverter.fromJsonToList(response, MassEvacuation.class);

        int indexOnList = 0;
        for(MassEvacuation massEvacuationReq : pendingRequests) {
            // add user to Map
            String indexOnListString = Integer.toString(indexOnList);
            requestIdToRequestMap.put(indexOnListString, massEvacuationReq);

            // add user to list
            RequestsForApprovalList.add(
                    "ID: " + massEvacuationReq.getId() + " Message: " + massEvacuationReq.getMessage()
            );

            indexOnList++;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        approveRequestBtn = new javax.swing.JButton();
        denyRequestBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        RequestsForApprovalList = new java.awt.List();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        approveRequestBtn.setText("Approve Request");
        approveRequestBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approveRequestBtnActionPerformed(evt);
            }
        });

        denyRequestBtn.setText("Deny Request");
        denyRequestBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                denyRequestBtnActionPerformed(evt);
            }
        });

        backBtn.setText("Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        errorLabel.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Mass Evacuation Approval");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(backBtn)
                        .addGap(143, 143, 143)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(denyRequestBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
                                .addComponent(approveRequestBtn)
                                .addGap(18, 18, 18))
                            .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 58, Short.MAX_VALUE)
                .addComponent(RequestsForApprovalList, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backBtn)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RequestsForApprovalList, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(approveRequestBtn)
                    .addComponent(denyRequestBtn))
                .addGap(28, 28, 28)
                .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        MainPage mainPage = new MainPage(client);
        mainPage.setVisible(true); // open mainPage
        this.dispose(); // close current page
    }//GEN-LAST:event_backBtnActionPerformed

    private void denyRequestBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_denyRequestBtnActionPerformed
        int selectedIndex = RequestsForApprovalList.getSelectedIndex();
        String selectedIndexString = Integer.toString(selectedIndex);

        if (selectedIndex == -1) {
            errorLabel.setText("Please select a request to approve or deny.");
            return;
        }

        // Extrair massEvacuationId do item selecionado (DENY_MASS_EVACUATION «massEvacuationIdToDeny»)
        int massEvacuationId = requestIdToRequestMap.get(selectedIndexString).getId();

        // send request to server and get response
        String request = CommandsFromClient.DENY_MASS_EVACUATION + " " + massEvacuationId;
        String response = client.sendMessageToServer(request);

        if(response == null || response.startsWith("ERROR")) {
            errorLabel.setText(response);
            return;
        }

        // aviso de sucesso
        JOptionPane.showMessageDialog(null,
                "Mass Evacuation Request denied successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);


        // refresh list when request is accepted
        loadPendingClientsToList();
    }//GEN-LAST:event_denyRequestBtnActionPerformed

    private void approveRequestBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approveRequestBtnActionPerformed
        int selectedIndex = RequestsForApprovalList.getSelectedIndex();
        String selectedIndexString = Integer.toString(selectedIndex);

        if (selectedIndex == -1) {
            errorLabel.setText("Please select a request to approve or deny.");
            return;
        }

        // Extrair massEvacuationId do item selecionado
        int massEvacuationId = requestIdToRequestMap.get(selectedIndexString).getId();

        // Send Request to Server (APROVE «massEvacuationIdToApprove»)
        String request = CommandsFromClient.APPROVE_MASS_EVACUATION + " " + massEvacuationId;
        String response = client.sendMessageToServer(request);

        if(response == null || response.startsWith("ERROR")) {
            errorLabel.setText(response);
            return;
        }

        // aviso de sucesso
        JOptionPane.showMessageDialog(null,
                "Mass Evacuation Request approved successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);


        // refresh list when request is accepted
        loadPendingClientsToList();

    }//GEN-LAST:event_approveRequestBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.List RequestsForApprovalList;
    private javax.swing.JButton approveRequestBtn;
    private javax.swing.JButton backBtn;
    private javax.swing.JButton denyRequestBtn;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
