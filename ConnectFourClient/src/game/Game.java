/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import connectfourclient.Client;
import java.awt.Color;
import java.awt.Font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author merve
 */
public class Game extends javax.swing.JFrame {
    //framedeki komponentlere erişim için satatik oyun değişkeni

    public static Game ThisGame;
    //ekrandaki resim değişimi için timer yerine thread
    public Thread tmr_slider;
    public Point locations[][];
    public int boardStates[][];
    static int myColumn = 0;
    int myRow = 0;
    int myVictory = 0;
    public int RivalVictory = 0;
    public Point myPoint, rivalPoint;
    public Color myColor, rivalColor;
    public int playerNum = 0;
    public int numberOfMove = 0;
    public int rivalTurn = 0;
    public int myTurn = 0;
    int drawControl2 = 0;
    int drawControl1 = 0;

    /**
     * Creates new form Game
     */
    public Game() {
        initComponents();
        this.setTitle("Connect Four");
        ThisGame = this;
        locations = new Point[7][6];    //circle location
        boardStates = new int[7][6];   //hamle matrisi
        panel_message.setVisible(false);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                boardStates[i][j] = 0;
                Point p = new Point((int) (242 + i * 91.5), 50 + j * 92);

                locations[i][j] = p;
                // System.out.println(locations[i][j]);
            }
        }

        tmr_slider = new Thread(() -> {
            //
            while (Client.socket.isConnected()) {
                try {
                    Thread.sleep(1500);
                    panel_message.setVisible(false);

                    if (playerNum == 1) { //1.oyuncu
                        if (numberOfMove == 0) {
                            label_turnMsg.setText("Sıra \n Sizde!");
                            unlockTheButtons();
                        } else if (numberOfMove == 42 && myVictory != 1 && RivalVictory != 1) {
                            label_turnMsg.setText("KAYBETTİNİZ!");
                            int answer = JOptionPane.showConfirmDialog(null, "Daha fazla hamle yok! Yeniden oynamak istiyor musunuz?", null, 0, JOptionPane.QUESTION_MESSAGE);
                            Reset();
                            if (answer == JOptionPane.YES_OPTION) {
                                startNewGame();
                            }

                            tmr_slider.stop();

                        } else {
                            if (rivalTurn == 1) { //rakibin sırasıysa
                                lockTheButtons();
                                if (myVictory == 1) { //kendi kazandıysa
                                    label_turnMsg.setText("TEBRİKLER!");
                                    int answer = JOptionPane.showConfirmDialog(null, "Tebrikler! Yeniden oynamak istiyor musunuz?", null, 0, JOptionPane.QUESTION_MESSAGE);
                                    Reset();
                                    if (answer == JOptionPane.YES_OPTION) {
                                        startNewGame();
                                    }
                                    tmr_slider.stop();
                                }
                                drawControl1 = 0;
                                label_turnMsg.setText("Sıra \n Rakipte!");
                            } else if (rivalTurn == 0 && drawControl1 == 0) { //kendi sırasıysa
                                drawControl1 = 1;
                                //rakibin hamlesini çizdirme
                                movingCircle m = new movingCircle(rivalPoint, Color.blue);
                                m.setOpaque(false);
                                m.setVisible(true);
                                m.setSize(this.getWidth(), this.getHeight());
                                ThisGame.add(m);

                                label_turnMsg.setText("Sıra \n Sizde!");
                                unlockTheButtons();

                            }
                            //is rival connect four?
                            if (RivalVictory == 1) { //rakip kazandıysa
                                Thread.sleep(2000);
                                System.out.println("Rival wins!!");
                                label_turnMsg.setText("KAYBETTİNİZ!");
                                Reset();
                                int answer = JOptionPane.showConfirmDialog(null, "Kaybettiniz! Yeniden oynamak istiyor musunuz?", null, 0, JOptionPane.QUESTION_MESSAGE);
                                if (answer == JOptionPane.YES_OPTION) {
                                    startNewGame();
                                }

                                lockTheButtons();
                                tmr_slider.stop();
                            }
                        }
                    } else if (playerNum == 2) { //2.oyuncu

                        if (numberOfMove == 0) {
                            lockTheButtons();
                            label_turnMsg.setText("Sıra \n Rakipte!");
                        } else if (numberOfMove == 42 && myVictory != 1 && RivalVictory != 1) { //toplam hamle sayısı
                            label_turnMsg.setText("KAYBETTİNİZ!");
                            Reset();
                            int answer = JOptionPane.showConfirmDialog(null, "Daha fazla hamle yok! Yeniden oynamak istiyor musunuz?", null, 0, JOptionPane.QUESTION_MESSAGE);
                            if (answer == JOptionPane.YES_OPTION) {
                                startNewGame();
                            }

                            tmr_slider.stop();
                        } else {
                            if (rivalTurn == 0 && drawControl2 == 0) { //kendi sırasıysa
                                drawControl2 = 1;
                                //rakibin hamlesini çizdirme
                                movingCircle m = new movingCircle(rivalPoint, Color.red);
                                m.setOpaque(false);
                                m.setVisible(true);
                                m.setSize(this.getWidth(), this.getHeight());
                                ThisGame.add(m);

                                label_turnMsg.setText("Sıra \n Sizde!");
                                unlockTheButtons();

                            } else if (rivalTurn == 1) { //rakibin sırasıysa
                                drawControl2 = 0;
                                lockTheButtons();
                                if (myVictory == 1) { //kendi kazandıysa
                                    System.out.println("You win!!");
                                    Reset();
                                    label_turnMsg.setText("TEBRİKLER!");
                                    int answer = JOptionPane.showConfirmDialog(null, "Tebrikler! Yeniden oynamak istiyor musunuz?", null, 0, JOptionPane.QUESTION_MESSAGE);
                                    if (answer == JOptionPane.YES_OPTION) {
                                        startNewGame();
                                    }
                                    tmr_slider.stop();
                                }
                                label_turnMsg.setText("Sıra \n Rakipte!");
                            }
                            //is rival connect four?
                            if (RivalVictory == 1 && drawControl2 == 1) { //rakip kazandıysa
                                Thread.sleep(2000);
                                label_turnMsg.setText("Kaybettiniz!");
                                Reset();
                                int answer = JOptionPane.showConfirmDialog(null, "Kaybettiniz! Yeniden oynamak istiyor musunuz?", null, 0, JOptionPane.QUESTION_MESSAGE);
                                if (answer == JOptionPane.YES_OPTION) {
                                    startNewGame();
                                }
                                lockTheButtons();
                                tmr_slider.stop();
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button1 = new javax.swing.JButton();
        button2 = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        button5 = new javax.swing.JButton();
        button6 = new javax.swing.JButton();
        button7 = new javax.swing.JButton();
        connect_button = new javax.swing.JButton();
        txt_name = new javax.swing.JTextField();
        txt_rival_name = new javax.swing.JTextField();
        panel_message = new javax.swing.JPanel();
        label_message = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        label_myColor = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        label_turnMsg = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        button1.setBackground(new java.awt.Color(23, 138, 147));
        button1.setText("1");
        button1.setEnabled(false);
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        button2.setBackground(new java.awt.Color(23, 138, 147));
        button2.setText("2");
        button2.setEnabled(false);
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        button3.setBackground(new java.awt.Color(23, 138, 147));
        button3.setText("3");
        button3.setEnabled(false);
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });

        button4.setBackground(new java.awt.Color(23, 138, 147));
        button4.setText("4");
        button4.setEnabled(false);
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });

        button5.setBackground(new java.awt.Color(23, 138, 147));
        button5.setText("5");
        button5.setEnabled(false);
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });

        button6.setBackground(new java.awt.Color(23, 138, 147));
        button6.setText("6");
        button6.setEnabled(false);
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });

        button7.setBackground(new java.awt.Color(23, 138, 147));
        button7.setText("7");
        button7.setEnabled(false);
        button7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button7ActionPerformed(evt);
            }
        });

        connect_button.setBackground(new java.awt.Color(199, 199, 199));
        connect_button.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        connect_button.setForeground(new java.awt.Color(241, 114, 17));
        connect_button.setText("Connect");
        connect_button.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(233, 82, 22), 3, true));
        connect_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connect_buttonActionPerformed(evt);
            }
        });

        txt_name.setBackground(new java.awt.Color(255, 250, 250));
        txt_name.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        txt_name.setForeground(new java.awt.Color(241, 114, 17));
        txt_name.setText("Name");
        txt_name.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(230, 76, 16), 3, true));

        txt_rival_name.setEditable(false);
        txt_rival_name.setBackground(new java.awt.Color(255, 250, 250));
        txt_rival_name.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        txt_rival_name.setForeground(new java.awt.Color(69, 119, 129));
        txt_rival_name.setText("Rival Name");
        txt_rival_name.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(103, 211, 232), 3, true));
        txt_rival_name.setEnabled(false);

        panel_message.setBackground(new java.awt.Color(168, 227, 236));
        panel_message.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(49, 162, 184), 2, true));

        label_message.setFont(new java.awt.Font("Noto Sans", 1, 15)); // NOI18N
        label_message.setForeground(new java.awt.Color(30, 12, 211));

        javax.swing.GroupLayout panel_messageLayout = new javax.swing.GroupLayout(panel_message);
        panel_message.setLayout(panel_messageLayout);
        panel_messageLayout.setHorizontalGroup(
            panel_messageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_message, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );
        panel_messageLayout.setVerticalGroup(
            panel_messageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_messageLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(label_message, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(199, 199, 199));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(230, 87, 19), 3, true));

        jLabel2.setBackground(new java.awt.Color(253, 239, 239));
        jLabel2.setFont(new java.awt.Font("Noto Sans", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(230, 87, 19));
        jLabel2.setText("Sizin renginiz:");

        label_myColor.setBackground(new java.awt.Color(235, 96, 22));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label_myColor, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_myColor, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(143, 130));

        label_turnMsg.setFont(new java.awt.Font("Noto Sans", 1, 20)); // NOI18N
        label_turnMsg.setForeground(new java.awt.Color(50, 204, 219));
        label_turnMsg.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(69, 199, 214), 3, true));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_turnMsg, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(label_turnMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel1.setBackground(new java.awt.Color(199, 199, 199));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/board.png"))); // NOI18N
        jLabel1.setLabelFor(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(422, 422, 422)
                .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(txt_name, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(180, 180, 180)
                .addComponent(panel_message, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(692, 692, 692)
                .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(242, 242, 242)
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(602, 602, 602)
                .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(910, 910, 910)
                .addComponent(txt_rival_name, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(connect_button, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(332, 332, 332)
                .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(782, 782, 782)
                .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jLabel1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(txt_name, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panel_message, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(430, 430, 430)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(txt_rival_name, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(connect_button, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void Reset() {
        if (Client.socket != null) {
            if (Client.socket.isConnected()) {
                Client.Stop();
            }
        }

    }

    public void startNewGame() {
        this.setVisible(false);
        Game g = new Game();
        g.setVisible(true);
    }
     
    public class movingCircle extends JPanel implements ActionListener {

        Timer t = new Timer(0, this);

        int x, y;
        double velX = 3, velY = 3;
        Color color;
        int finishPoint = 520;

        public movingCircle(Point p, Color c) {
            x = (int) p.getX() - 14;
            y = 20;
            finishPoint = (int) p.getY() - 12;
            color = c;
        }

        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.clearRect(x, y, 60, 60);
            Ellipse2D circle = new Ellipse2D.Double(x, y, 70, 70);
            g.setColor(color);
            g2.fill(circle);
            t.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (y <= finishPoint) {
                repaint();
                y += velY;
            }

        }

    }

    public boolean isPlayable(int column) {
        return boardStates[column][0] == 0;
    }

    void lockTheButtons() {
        Game.ThisGame.button1.setEnabled(false);
        Game.ThisGame.button2.setEnabled(false);
        Game.ThisGame.button3.setEnabled(false);
        Game.ThisGame.button4.setEnabled(false);
        Game.ThisGame.button5.setEnabled(false);
        Game.ThisGame.button6.setEnabled(false);
        Game.ThisGame.button7.setEnabled(false);
    }

    void unlockTheButtons() {
        Game.ThisGame.button1.setEnabled(true);
        Game.ThisGame.button2.setEnabled(true);
        Game.ThisGame.button3.setEnabled(true);
        Game.ThisGame.button4.setEnabled(true);
        Game.ThisGame.button5.setEnabled(true);
        Game.ThisGame.button6.setEnabled(true);
        Game.ThisGame.button7.setEnabled(true);
    }

    public void drawTheMove(int myColumn) {

        for (int i = 5; i >= 0; i--) {
            if (boardStates[myColumn][i] == 0) {
                myRow = i;
                break;
            }
        }
        boardStates[myColumn][myRow] = playerNum;

        movingCircle m = new movingCircle(locations[myColumn][myRow], myColor);
        m.setOpaque(false);
        m.setVisible(true);
        m.setSize(this.getWidth(), this.getHeight());

        ThisGame.add(m);
        myPoint = locations[myColumn][myRow];
        numberOfMove++;
        myTurn = 0;
        rivalTurn = 1;
    }

    public void sendMessagesToRival() {
        Message msg = new Message(Message.Message_Type.Board);  //hamle matrisi
        msg.content = boardStates;
        Client.Send(msg);

        Message msg2 = new Message(Message.Message_Type.Point); //rakibin hamlesinin lokasyonu
        msg2.content = myPoint;
        Client.Send(msg2);

        Message msg3 = new Message(Message.Message_Type.Move); //toplam hamle sayısı
        msg3.content = numberOfMove;
        Client.Send(msg3);

        Message msg4 = new Message(Message.Message_Type.Turn); //rakibe hamle yapması için tetik
        msg4.content = myTurn;
        Client.Send(msg4);
    }

    public boolean isConnenctFour() {
        //board kontrol 
        
        int value = boardStates[myColumn][myRow];
        int counter = 0;
        int i = myRow;
        int j = myColumn;

        //dikey kontrol
        while (i < 6 && boardStates[myColumn][i] == value) {
            counter++;
            i++;
        }
        if (counter == 4) {
            return true;
        }
        //yatay kontrol
        counter = 0;
        while (j < 7 && boardStates[j][myRow] == value) {
            counter++;
            j++;
        }
        j = myColumn - 1;
        while (j >= 0 && boardStates[j][myRow] == value) {
            counter++;
            j--;
        }
        if (counter == 4) {
            return true;
        }
        //capraz1 kontrol
        j = myColumn;
        i = myRow;
        counter = 0;
        while (j < 7 && i < 6 && boardStates[j][i] == value) {
            counter++;
            i++;
            j++;
        }
        j = myColumn - 1;
        i = myRow - 1;
        while (j >= 0 && i >= 0 && boardStates[j][i] == value) {
            counter++;
            i--;
            j--;
        }
        if (counter == 4) {
            return true;
        }

        //capraz2 kontrol
        counter = 0;
        j = myColumn;
        i = myRow;
        while (j < 7 && i >= 0 && boardStates[j][i] == value) {
            counter++;
            j++;
            i--;
        }
        j = myColumn - 1;
        i = myRow + 1;
        while (i < 6 && j >= 0 && boardStates[j][i] == value) {
            counter++;
            j--;
            i++;
        }
        if (counter == 4) {
            return true;
        }
        return false;

    }
    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed

        myColumn = 0;

        if (isPlayable(myColumn)) {  //kolon boş ise
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
            label_turnMsg.setText("Bu kolon dolu!");
        }


    }//GEN-LAST:event_button1ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        myColumn = 3;
        if (isPlayable(myColumn)) {
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
            label_turnMsg.setText("Bu kolon dolu!");
        }
    }//GEN-LAST:event_button4ActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        myColumn = 2;

        if (isPlayable(myColumn)) {
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
           label_turnMsg.setText("Bu kolon dolu!");
        }
    }//GEN-LAST:event_button3ActionPerformed

    private void connect_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connect_buttonActionPerformed
        Client.Start("127.0.0.1", 2000);

        connect_button.setEnabled(false);
        txt_name.setEnabled(false);


    }//GEN-LAST:event_connect_buttonActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        myColumn = 1;

        if (isPlayable(myColumn)) {
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
           label_turnMsg.setText("Bu kolon dolu!");
        }

    }//GEN-LAST:event_button2ActionPerformed

    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        myColumn = 4;

        if (isPlayable(myColumn)) {
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
            label_turnMsg.setText("Bu kolon dolu!");
        }
    }//GEN-LAST:event_button5ActionPerformed

    private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
        myColumn = 5;

        if (isPlayable(myColumn)) {
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
            label_turnMsg.setText("Bu kolon dolu!");
        }
    }//GEN-LAST:event_button6ActionPerformed

    private void button7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button7ActionPerformed
        myColumn = 6;

        if (isPlayable(myColumn)) {
            drawTheMove(myColumn);
            sendMessagesToRival();
            if (isConnenctFour()) {
                myVictory = 1;
                Message msg = new Message(Message.Message_Type.Winner);
                msg.content = myVictory;
                Client.Send(msg);
            }
        } else {
            label_turnMsg.setText("Bu kolon dolu!");
        }
    }//GEN-LAST:event_button7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Game().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton button1;
    public javax.swing.JButton button2;
    public javax.swing.JButton button3;
    public javax.swing.JButton button4;
    public javax.swing.JButton button5;
    public javax.swing.JButton button6;
    public javax.swing.JButton button7;
    public javax.swing.JButton connect_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JLabel label_message;
    public javax.swing.JLabel label_myColor;
    private javax.swing.JLabel label_turnMsg;
    public javax.swing.JPanel panel_message;
    public javax.swing.JTextField txt_name;
    public javax.swing.JTextField txt_rival_name;
    // End of variables declaration//GEN-END:variables
}
