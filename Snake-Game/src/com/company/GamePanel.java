package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import static com.company.GamePanel.Direction.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600; //ancho de la pantalla
    static final int SCREEN_HEIGHT = 600; //alto de la pantalla
    static final int UNIT_SIZE = 25; // Se va a crear una cuadrícula en la pantalla, el tamaño de celda es 25
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75; // duración de cada frame en milisegundos
    final int x[] = new int[GAME_UNITS]; // x[] y y[] guardan las posiciónes (x,y) de cada parte del cuerpo de la serpiente
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; // longitud de la serpiente (empieza en 6 y va creciendo)
    int applesEaten; // cantidad de manzanas comidad (sirve para el puntaje)
    int appleX; // Posición x de la manzana
    int appleY; // Posición y de la manzana
    Direction direction = DOWN; // Dirección de movimiento de la serpiente (se cambia con el key listener
    boolean running = false; // running verifica que el juego no se ha acabado
    Timer timer; // timer recarga periodicamente la clase
    Random random; // sirve para crear posiciones aleatorias

    public GamePanel() {
        startGame();
    }


    public void startGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    // método que crea nuevas manzanas
    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    // hereda el método de volver a pintar del padre
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // método de volver a pintar
    public void draw(Graphics g) {
        if (running) {
            // se pinta la cuadrícula
            for (int i = 0; i < (SCREEN_HEIGHT / UNIT_SIZE); i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            for (int i = 0; i < (SCREEN_WIDTH / UNIT_SIZE); i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // se pinta la manzana
            g.setColor(Color.red);
            g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // se pinta la serpiente
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(Color.lightGray);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.red);
            g.setFont(new Font("Consolas", Font.BOLD,20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten
                    , (SCREEN_WIDTH- metrics.stringWidth("Score: " + applesEaten))/2
                    , 20);
        }
        else gameOver(g);
    }

    // mueve a la serpiente
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case UP:
                y[0] = y[0] - UNIT_SIZE;
                break;
            case DOWN:
                y[0] = y[0] + UNIT_SIZE;
                break;
            case LEFT:
                x[0] = x[0] - UNIT_SIZE;
                break;
            case RIGHT:
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    // Verifica si hay una colisión entre manzana y la cabeza de la snake
    public void checkApple() {
        //checks if head touches apple
        if (appleX == x[0] && appleY == y[0]) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    // Verifica si hay una colisión entre la serpiente y sí misma o entre la serpiente y los bordes de la pantalla
    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //checks if head collides with border

        //head touches left border
        if (x[0] < 0) {
            x[0] = SCREEN_WIDTH - UNIT_SIZE;
        }
        // head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            x[0] = 0;
        }
        // head touches upper border
        if (y[0] < 0) {
            y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        }
        // head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            y[0] = 0;
        }

        if (!running) {
            timer.stop();
        }
    }


    // muestra la pantalla de game over
    public void gameOver(Graphics g) {
        //GameOverText
        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD,20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten
                , (SCREEN_WIDTH- metrics.stringWidth("Score: " + applesEaten))/2
                , 20);

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD,75));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over"
                , (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2
                , SCREEN_HEIGHT /2);

        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD,25));
        metrics = getFontMetrics(g.getFont());
        g.drawString("\"Enter\" to try again"
                , (SCREEN_WIDTH - metrics.stringWidth("\"Enter\" to try again"))/2
                , (SCREEN_HEIGHT /2)+80);

        bodyParts = 6;
        applesEaten = 0;


    }

    // mueve a la serpiente, verifica si se se comió la manzana y verifia colisiones, si todo sale bien vuelve a pintar
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    // Subclase de tipo enum que contiene las direcciones posibles de la serpiente
    public enum Direction {
        RIGHT('R'), LEFT('L'), DOWN('D'), UP('U');
        private char direction;

        Direction(char direction) {
            this.direction = direction;
        }

        public char getDirection() {
            return direction;
        }
    }

    // Key listener
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if(running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != RIGHT) {
                            direction = LEFT;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != DOWN) {
                            direction = UP;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != UP) {
                            direction = DOWN;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != LEFT) {
                            direction = RIGHT;
                        }
                        break;
                }
            }
            else{
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                   startGame();
                }
            }
        }
    }
}
