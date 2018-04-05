/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfourclient;

import static connectfourclient.Client.sInput;
import game.Game;
import game.Message;

import java.awt.Color;
import java.awt.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author merve
 */
class Listen extends Thread {

    public void run() {
        //soket bağlı olduğu sürece dön
        while (Client.socket.isConnected()) {

            try {
                //mesaj gelmesini bloking olarak dinyelen komut
                Message received = (Message) (sInput.readObject());
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre yapılacak işlemi ayır.
                switch (received.type) {
                    case Name:
                        break;
                    case RivalConnected:
                        String name = received.content.toString();
                        System.out.println("rival connected a girdi");
                        Game.ThisGame.txt_rival_name.setText(name);
                        System.out.println(Game.ThisGame.playerNum);
                        if (Game.ThisGame.playerNum == 1) {
                            Game.ThisGame.button1.setEnabled(true);
                            Game.ThisGame.button2.setEnabled(true);
                            Game.ThisGame.button3.setEnabled(true);
                            Game.ThisGame.button4.setEnabled(true);
                            Game.ThisGame.button5.setEnabled(true);
                            Game.ThisGame.button6.setEnabled(true);
                            Game.ThisGame.button7.setEnabled(true);
                            Game.ThisGame.label_myColor.setIcon(new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/red_coin.png"))).getImage().getScaledInstance(Game.ThisGame.label_myColor.getWidth(), Game.ThisGame.label_myColor.getHeight(), Image.SCALE_DEFAULT)));
                            
                            Game.ThisGame.panel_message.setVisible(true);
                            Game.ThisGame.label_message.setText("Oyun Başladı. Sizin Sıranız!");
                            
                        }
                        if (Game.ThisGame.playerNum == 2) {
                            Game.ThisGame.label_myColor.setIcon(new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/blue_coin.png"))).getImage().getScaledInstance(Game.ThisGame.label_myColor.getWidth(), Game.ThisGame.label_myColor.getHeight(), Image.SCALE_DEFAULT)));
                            Game.ThisGame.panel_message.setVisible(true);
                            Game.ThisGame.label_message.setText("Oyun Başladı. Rakibinizin hamle yapması bekleniyor!");
                            
                        }

                        Game.ThisGame.tmr_slider.start();
                        break;
                    case Disconnect:
                        break;
                    case Color:
                        //player'ın rengini gönderir
                        Color c = (Color) received.content;
                        Game.ThisGame.myColor = c;
                        break;
                    case Text:
                        // Game.ThisGame.txt_receive.setText(received.content.toString());
                        break;
                    case Board:
                        Game.ThisGame.boardStates = (int[][]) received.content;
                        break;
                    case Point:
                        //rakibin hamlesini çizdirmek için
                        Game.ThisGame.rivalPoint = (java.awt.Point) received.content;
                        break;
                    case PlayerNum:
                        System.out.println("player number! :" + (int) received.content);
                        Game.ThisGame.playerNum = (int) received.content;
                        break;
                    case Move:
                        //rakibe toplam hamle sayısını gönderir
                        Game.ThisGame.numberOfMove = (int) received.content;
                        break;
                    case Turn:
                        //rakibin oynamasını tetikler
                        Game.ThisGame.rivalTurn = (int) received.content;
                        break;
                    case Winner:
                        //kazanan bilgisi
                        Game.ThisGame.RivalVictory = (int)received.content;
                        break;
                    case Bitis:
                        break;

                }
            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            }
        }

    }
}

public class Client {

    //her clientın bir soketi olmalı
    public static Socket socket;

    //verileri almak için gerekli nesne
    public static ObjectInputStream sInput;
    //verileri göndermek için gerekli nesne
    public static ObjectOutputStream sOutput;
    //serverı dinleme thredi 
    public static Listen listenMe;

    public static void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            // input stream
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            // output stream
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();
            //ilk mesaj olarak isim gönderiyorum
            Message msg = new Message(Message.Message_Type.Name);
            msg.content = Game.ThisGame.txt_name.getText();
            Client.Send(msg);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
