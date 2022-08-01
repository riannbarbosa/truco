package Main;
import Base.Baralho;
import Base.Cartas;
import Base.Player;
import Graphics.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class Game extends JPanel implements Runnable {

    Thread gameThread;
    PlayerGraph playerGraph, playerEnemy;
    Player player, enemy;
    Baralho baralho;
    BufferedImage mesa;
    Mouse mouse = new Mouse();
    LinkedList<Cartas> first, mid, last;
    final int windowWidth = 1200, windowHeight = 700;
    final double FPS = 60;

    // Load mesa
    {
        try {
            this.mesa = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Sprites/mesa.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Game() {
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setPreferredSize(new Dimension(windowWidth,windowHeight));
        this.setFocusable(true);
        this.addMouseListener(this.mouse);
        this.baralho = new Baralho();
        this.player = new Player();
        this.enemy = new Player();
        this.baralho.distribuirCartasPlayer(this.player);
        this.baralho.distribuirCartasPlayer(this.enemy);
        this.first = new LinkedList<>();
        this.mid = new LinkedList<>();
        this.last = new LinkedList<>();

        this.playerGraph = new PlayerGraph(this.player, windowWidth, 450);
        this.playerEnemy = new PlayerGraph(this.enemy, windowWidth, 450);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();

    }

    public void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graphics2D = (Graphics2D) graph;
        graphics2D.drawImage(this.mesa,0,0,windowWidth,windowHeight,null);
        this.playerGraph.drawPlayerCard(graphics2D);
        this.playerEnemy.drawPlayerCard(graphics2D);
        graphics2D.dispose();


    }

    public void update() {
        playerGraph.update(this.mouse,this.first,this.mid,this.last);
        playerEnemy.update(true,2,this.first, this.mid, this.last);
        playerEnemy.update(true,3,this.first, this.mid, this.last);
        playerEnemy.update(true,1,this.first, this.mid, this.last);
        for(Cartas cartas: first)
            System.out.println(cartas.numero() + "|" + cartas.naipe());
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }
}
