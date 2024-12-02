/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipp.estg.pages.notifications;

import ipp.estg.models.Notification;
import ipp.estg.pages.chats.channelList.*;
import ipp.estg.Client;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.models.Channel;
import ipp.estg.pages.chats.channelChat.ChannelChatPage;
import ipp.estg.pages.main.MainPage;
import ipp.estg.utils.JsonConverter;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author User
 */
public class NotificationListPage extends javax.swing.JFrame {

    private final Client client;

    /**
     * Map to store Notifications and its respective index on the list.
     * Because the selected index of the list and the Notification Id might be different
     */
    private final Map<String, Notification> channelIdToChannelMap = new HashMap<>();

    /**
     * Creates new form CreateGroupPage
     */
    public NotificationListPage(Client client) {
        this.client = client;

        initComponents();
        loadChannelsToList();
    }

    private void loadChannelsToList() {
        groupList.removeAll();
        channelIdToChannelMap.clear();

        // Get channels (GET_CHANNELS «userId»)
        String request = CommandsFromClient.GET_CHANNELS + " " + client.getLoggedUserId();
        String response = client.sendMessageToServer(request);

        // Parse response
        JsonConverter jsonConverter = new JsonConverter();
        List<Notification> pendingRequests = jsonConverter.fromJsonToList(response, Notification.class);

        int indexOnList = 0;
        for (Notification notification : pendingRequests) {
            // add user to Map
            String indexOnListString = Integer.toString(indexOnList);
            channelIdToChannelMap.put(indexOnListString, notification);

            // add user to list
            groupList.add(
                    notification.getNotificationDate()+ " - " + notification.getMessage()
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

        groupList = new java.awt.List();
        backBtn = new javax.swing.JButton();
        errorLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backBtn.setText("Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        errorLbl.setForeground(new java.awt.Color(255, 0, 0));
        errorLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(backBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(groupList, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(192, 192, 192)
                        .addComponent(errorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(backBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupList, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(errorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        MainPage mainPage = new MainPage(client);
        mainPage.setVisible(true); // open mainPage
        this.dispose(); // close current page
    }//GEN-LAST:event_backBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JLabel errorLbl;
    private java.awt.List groupList;
    // End of variables declaration//GEN-END:variables
}
