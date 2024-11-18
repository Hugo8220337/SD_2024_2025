/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipp.estg.pages.userApproval;

import ipp.estg.Client;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.models.User;
import ipp.estg.pages.main.MainPage;
import ipp.estg.utils.JsonConverter;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Page to approve or deny users
 */
public class UserApprovalPage extends javax.swing.JFrame {

    private final Client client;

    /**
     * Map to store the user and its respective index on the list.
     * Because the selected index of the list and the user id might be different
     */
    private final Map<String, User> userIdToUserMap = new HashMap<>();

    /**
     * Creates new form UserApprovalPage
     */
    public UserApprovalPage(Client client) {
        this.client = client;

        initComponents();
        loadPendingClientsToList();
    }

    private void loadPendingClientsToList() {
        usersForApprovalList.removeAll();
        userIdToUserMap.clear();

        // Get pending users (PENDING_APPROVALS «currentUserId»)
        String request = CommandsFromClient.GET_PENDING_APPROVALS + " " + client.getLoggedUserId();
        String response = client.sendMessageToServer(request);

        // Parse response
        JsonConverter jsonConverter = new JsonConverter();
        List<User> pendingUsers = jsonConverter.fromJsonToList(response, User.class);

        int indexOnList = 0;
        for(User user : pendingUsers) {
            // add user to Map
            String indexOnListString = Integer.toString(indexOnList);
            userIdToUserMap.put(indexOnListString, user);

            // add user to list
            usersForApprovalList.add(
                    "ID: " + user.getId() + " Username: " + user.getUsername()
                            + " Email: " + user.getEmail() + " Type: " + user.getUserType()
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

        approveUserBtn = new javax.swing.JButton();
        denyUserBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        usersForApprovalList = new java.awt.List();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        approveUserBtn.setText("Approve User");
        approveUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approveUserBtnActionPerformed(evt);
            }
        });

        denyUserBtn.setText("Deny User");
        denyUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                denyUserBtnActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(backBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(usersForApprovalList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(denyUserBtn)
                                .addGap(281, 281, 281)
                                .addComponent(approveUserBtn))
                            .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(backBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usersForApprovalList, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(approveUserBtn)
                    .addComponent(denyUserBtn))
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

    private void denyUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_denyUserBtnActionPerformed
        int selectedIndex = usersForApprovalList.getSelectedIndex();
        String selectedIndexString = Integer.toString(selectedIndex);

        if (selectedIndex == -1) {
            errorLabel.setText("Please select a user to approve.");
            return;
        }

        // Extrair userId do item selecionado
        int userId = userIdToUserMap.get(selectedIndexString).getId();

        // send request to server and get response (DENY_USER «userThatDeniesId» «userForDenyId»)
        String request = CommandsFromClient.DENY_USER + " " + client.getLoggedUserId() + " " + userId;
        String response = client.sendMessageToServer(request);

        if(response == null || response.startsWith("ERROR")) {
            errorLabel.setText(response);
            return;
        }

        // aviso de sucesso
        JOptionPane.showMessageDialog(null,
                "User denied successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);


        // refresh list when user is accepted
        loadPendingClientsToList();
    }//GEN-LAST:event_denyUserBtnActionPerformed

    private void approveUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approveUserBtnActionPerformed
        int selectedIndex = usersForApprovalList.getSelectedIndex();
        String selectedIndexString = Integer.toString(selectedIndex);

        if (selectedIndex == -1) {
            errorLabel.setText("Please select a user to approve or deny.");
            return;
        }

        // Extrair userId do item selecionado
        int userId = userIdToUserMap.get(selectedIndexString).getId();

        // Send Request to Server (APROVE «userThatApprovesId» «userForValidationId»)
        String request = CommandsFromClient.APPROVE_USER + " " + client.getLoggedUserId() + " " + userId;
        String response = client.sendMessageToServer(request);

        if(response == null || response.startsWith("ERROR")) {
            errorLabel.setText(response);
            return;
        }

        // aviso de sucesso
        JOptionPane.showMessageDialog(null,
                "User approved successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);


        // refresh list when user is accepted
        loadPendingClientsToList();

    }//GEN-LAST:event_approveUserBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton approveUserBtn;
    private javax.swing.JButton backBtn;
    private javax.swing.JButton denyUserBtn;
    private javax.swing.JLabel errorLabel;
    private java.awt.List usersForApprovalList;
    // End of variables declaration//GEN-END:variables
}
