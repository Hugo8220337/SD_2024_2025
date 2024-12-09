/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipp.estg.pages.chats.channelList;

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
public class ChannelListPage extends javax.swing.JFrame {

    private final Client client;

    /**
     * Map to store Channels and its respective index on the list.
     * Because the selected index of the list and the Channel Id might be different
     */
    private final Map<String, Channel> channelIdToChannelMap = new HashMap<>();

    /**
     * Creates new form CreateGroupPage
     */
    public ChannelListPage(Client client) {
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
        List<Channel> pendingRequests = jsonConverter.fromJsonToList(response, Channel.class);

        int indexOnList = 0;
        for (Channel channel : pendingRequests) {
            // add user to Map
            String indexOnListString = Integer.toString(indexOnList);
            channelIdToChannelMap.put(indexOnListString, channel);

            // add user to list
            groupList.add(
                    channel.getName()
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
        createChannelBtn1 = new javax.swing.JButton();
        joinChannelBtn = new javax.swing.JButton();
        errorLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backBtn.setText("Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        createChannelBtn1.setText("Create  Channel");
        createChannelBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createChannelBtn1ActionPerformed(evt);
            }
        });

        joinChannelBtn.setText("Join Channel");
        joinChannelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinChannelBtnActionPerformed(evt);
            }
        });

        errorLbl.setForeground(new java.awt.Color(255, 0, 0));
        errorLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(createChannelBtn1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(joinChannelBtn)
                                .addGap(24, 24, 24))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(createChannelBtn1)
                                        .addComponent(joinChannelBtn))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

    private void createChannelBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createChannelBtn1ActionPerformed
        String channelName = JOptionPane.showInputDialog("Enter the channel name:");
        if (channelName == null || channelName.isEmpty()) {
            errorLbl.setText("Channel name cannot be empty");
            return;
        }

        // Create channel (CREATE_CHANNEL "«channelName»")
        String request = CommandsFromClient.CREATE_CHANNEL + " " + "\"" + channelName + "\"";
        String response = client.sendMessageToServer(request);

        if (response.startsWith("ERROR:")) {
            errorLbl.setText("Error creating channel");
            return;
        }

        // Reload channels
        loadChannelsToList();
    }//GEN-LAST:event_createChannelBtn1ActionPerformed

    private void joinChannelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinChannelBtnActionPerformed

        int selectedIndex = groupList.getSelectedIndex();
        if (selectedIndex == -1) {
            errorLbl.setText("Select a channel to join");
            return;
        }

        // Get channel from Map
        String selectedIndexString = Integer.toString(selectedIndex);
        Channel selectedChannel = channelIdToChannelMap.get(selectedIndexString);

        // Join channel (JOIN_CHANNEL «channelId»)
        String request = CommandsFromClient.JOIN_CHANNEL + " " + selectedChannel.getId();
        String response = client.sendMessageToServer(request);

        if (response.startsWith("ERROR:")) {
            errorLbl.setText("Error joining channel");
            return;
        }

        // Open Channel Chat Page
        ChannelChatPage chatPage = new ChannelChatPage(client, selectedChannel);
        chatPage.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_joinChannelBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JButton createChannelBtn1;
    private javax.swing.JLabel errorLbl;
    private java.awt.List groupList;
    private javax.swing.JButton joinChannelBtn;
    // End of variables declaration//GEN-END:variables
}
