package z2;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GPanel extends JPanel{
    final static int SEEDS = 40;    // используется для перемешивания поля

    private int gameMode = 1; // 1 - изображение, 2 - символы
    private String imgPath = "tax.jpg"; // путь до исходного изображения
    private boolean firstClick; // для перемешивания поля по первому нажатию
    private BufferedImage img;  // сохраненное исходное изображение
    private Random random = new Random();

    private GCell[][] gCells = new GCell[4][4]; // клетки поля
    private Point freeSpaceCoordinate = new Point(); // координаты свободной клетки
    private GFrame gFrame;  // ссылка на frame в котором вызвано поле (для победы, блокировки переключения mode на frame)

    /* Клетка поля */
    private class GCell{
        static final int CELL_SIZE = 160;

        int number;
        BufferedImage image;
        public GCell(){

        }

        public GCell(int number, BufferedImage image) {
            this.number = number;
            this.image = image;
        }

        public GCell(GCell gCell) {
            this.number = gCell.number;
            this.image = gCell.image;
        }
    }

    public GPanel(GFrame gFrame) {
        try {
            this.gFrame = gFrame;

            img = ImageIO.read(new File(imgPath));

            initCells();

            setFocusable(true);

            addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    /* Перемещение клеток поля, блокирование переключателей на фрейме */
                    if (firstClick) {
                        mixCells();
                        gFrame.setRadioEnabled(false);
                        firstClick = false;
                    }
                    /* Нажатие на клетку */
                    else {
                        mousePressAction(e.getPoint());
                        checkWin();
                    }
                }
            });

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (firstClick) {
                        mixCells();
                        gFrame.setRadioEnabled(false);
                        firstClick = false;
                        return;
                    }

                    switch (e.getKeyCode()) {
                        // выбор направления
                        case KeyEvent.VK_UP:
                            keyPressedAction(0, 1);
                            break;
                        case KeyEvent.VK_DOWN:
                            keyPressedAction(0, -1);
                            break;
                        case KeyEvent.VK_LEFT:
                            keyPressedAction(1, 0);
                            break;
                        case KeyEvent.VK_RIGHT:
                            keyPressedAction(-1, 0);
                            break;
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void keyPressedAction(int dx, int dy) {
        int y = (int) freeSpaceCoordinate.getY() + dy;
        int x = (int) freeSpaceCoordinate.getX() + dx;

        if (x < 0 || y > 3 || x > 3 || y < 0)
            return;
        moveToFreeSpace(new Point(x, y));
        checkWin();
    }

    /* Действие на нажатие */
    private void mousePressAction(Point point) {
        int x = (int) point.getX(), y = (int) point.getY(),
                fieldSize = getGPanelSize();

        // выход за пределы
        if (x >= fieldSize || y >= fieldSize)
            return;

        Point targetPoint = new Point((x / GCell.CELL_SIZE) ,
                (y / GCell.CELL_SIZE));

        moveToFreeSpace(targetPoint);
    }
    /* Проверка на окончание игры, начало новой */
    private void checkWin() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 ; j++) {
                if (gCells[i][j].number != j*4 + i + 1)
                    return;
            }
        }

        JOptionPane.showMessageDialog(gFrame, "Вы выиграли, для начала новой игры нажмите \"Готово\"");
        startNewGame();
    }
    /* Отрезать от src изображения начиная с точки point */
    private BufferedImage cropImage(BufferedImage src, Point point) {
        return src.getSubimage((int) point.getX(), (int) point.getY(), GCell.CELL_SIZE, GCell.CELL_SIZE);
    }
    /* Начало новой игры */
    public void startNewGame() {
        initCells();
        repaint();
    }
    /* Инициализация клеток */
    private void initCells() {
        freeSpaceCoordinate.setLocation(3, 3);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 ; j++) {
                gCells[i][j] = new GCell(j*4 + i + 1, cropImage(img, new Point(i * GCell.CELL_SIZE, j * GCell.CELL_SIZE)));
            }
        }

        firstClick = true;
    }
    /* Перемешать клетки на поле*/
    private void mixCells() {
        int firstRandomNumber, secondRandomNumber;
        for (int i = 0; i < SEEDS; i++) {
            firstRandomNumber = random.nextInt(15);
            secondRandomNumber = random.nextInt(15);
            Point point1 = new Point(firstRandomNumber / 4, firstRandomNumber % 4);
            Point point2 = new Point(secondRandomNumber / 4, secondRandomNumber % 4);
            swapPoints(point1, point2);
        }

        repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        switch (gameMode) {
            case 1: {
                paintImagePanel(g);
                break;
            }
            case 2: {
                paintSignPanel(g2);
                break;
            }
        }

    }
    /* Отрисовка панели как изображения */
    private void paintImagePanel(Graphics g) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if (!(freeSpaceCoordinate.getX() == i && freeSpaceCoordinate.getY() == j))
                    g.drawImage(gCells[i][j].image, i * GCell.CELL_SIZE, j * GCell.CELL_SIZE, this);
            }
        }
    }
    /* Отрисовка панели как символы */
    private void paintSignPanel(Graphics2D g2) {
        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {

                if (!(freeSpaceCoordinate.getX() == i && freeSpaceCoordinate.getY() == j)) {
                    g2.setPaint(new Color(0xBC7A00));
                    g2.fill(new Rectangle2D.Double(i * GCell.CELL_SIZE - 2, j * GCell.CELL_SIZE - 2, GCell.CELL_SIZE - 4, GCell.CELL_SIZE - 4));
                    paintSymbol(i+1, j+1,gCells[i][j].number + "", g2);
                }

            }
        }
    }
    /* Печать символа по центру клетки*/
    private void paintSymbol(int x, int y, String text,Graphics2D g2){
        int w = GCell.CELL_SIZE * --x;
        int h = GCell.CELL_SIZE * --y;
        Font font = new Font("Verdana", Font.BOLD, 24);
        g2.setColor(Color.white);

        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int textX = ((GCell.CELL_SIZE - fm.stringWidth(text)) / 2);
        int textY = ((GCell.CELL_SIZE - (fm.getHeight() - fm.getDescent())) / 2);
        textY += fm.getAscent() - fm.getDescent();

        g2.drawString(text, w + textX, h + textY);
    }
    /* Размер поля */
    public int getGPanelSize() {
        return GCell.CELL_SIZE * 4;
    }
    /* Изменить mod */
    public void setGameModeAndRepaint(int gameMode) {
        this.gameMode = gameMode;
        repaint();
    }
    /* Может ли клетка быть перемещена на свободную позицию */
    private boolean canBeMoved(Point point) {
        int xDif = (int)point.getX() - (int)freeSpaceCoordinate.getX(), yDif = (int)point.getY() - (int)freeSpaceCoordinate.getY();
        return ((Math.abs(xDif) == 1) && ( yDif == 0)) || ((Math.abs(yDif) == 1) && ( xDif == 0));
    }
    /* Перемещение на свободную клетку */
    private void moveToFreeSpace(Point point) {
        if(canBeMoved(point)) {
            swapPoints(point, freeSpaceCoordinate);

            freeSpaceCoordinate = point;
        }
        repaint();
    }
    /* Перестановка клеток */
    private void swapPoints(Point point1, Point point2) {
        int x1 = (int) point1.getX(), y1 = (int) point1.getY(),
                x2 = (int) point2.getX(), y2 = (int) point2.getY();

        GCell temp = new GCell(gCells[x1][y1]);
        gCells[x1][y1] = gCells[x2][y2];
        gCells[x2][y2] = temp;
    }
}
