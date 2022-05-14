package Tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class Tetris {

    //CONSTANTES
    final Image icono = new ImageIcon(getClass().getResource("/imagenes/logo.png")).getImage(); //Icono de la aplicación
    final int anchoFrame = 725; //Ancho de la ventana
    final int altoFrame = 725; //Alto de la ventana
    final Color colorFondo = Color.black; //Color de fondo
    final Point posicionInicial = new Point(7, 0); //Punto de inicio donde se colocan las piezas nuevas
    final ImageIcon[] cuadrados
            = //Cuadrados de 25x25 de colores
            {
                new ImageIcon(getClass().getResource("/imagenes/CuadradoCyan.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoAzul.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoNaranja.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoAmarillo.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoVerde.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoRosa.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoRojo.png")),
                new ImageIcon(getClass().getResource("/imagenes/CuadradoNegro.png")),
                new ImageIcon(getClass().getResource("/imagenes/Cuadricula.png")) //Este es la cuadricula de fondo 400x600
            };
    final Point[][][] Tetraminos = { //[PIEZA] [ROTACION] [PUNTO]
        {// Pieza-I
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
        },
        {// Pieza-L
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
        },
        {// Pieza-J
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
        },
        {// Pieza-O
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
        },
        {// Pieza-S
            {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
        },
        {// Pieza-T
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
        },
        {// Pieza-Z
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
        }
    };

    //VARIABLES GLOBALES
    JFrame ventana;
    Canvas panelJuego;
    Graphics2D g2;
    int[][] arrayLogico;
    int[][] arraySigPieza;
    int piezaActual;
    int sigPieza;
    int rotacion;
    int velocidad;
    int puntuacion;
    int lineasTotales;
    int nivel;
    Point posicionActual;
    Timer timer;
    private BufferStrategy strategy;
    boolean acelera, abajoPulsado, pausa;
    File file;
    Font font, fontTiempo;
    ImageIcon separador;
    int horas, minutos, segundos;
    long tiempoInicio, tiempoActual;
    Thread juego, tiempo, musicaFondo;

    public Tetris() {
        ventana = new JFrame("Tetris by Mario");
        ventana.setSize(anchoFrame, altoFrame);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.setIconImage(icono);
        ventana.setLocationRelativeTo(null);
        //Creo el panel de juego
        panelJuego = new Canvas();
        panelJuego.setBackground(colorFondo);
        //Añado el panel al frame principal
        ventana.add(panelJuego);
        ventana.setVisible(true);
        //Asigno los graficos del canvas a la variable g2
        g2 = creaDobleBuffer(panelJuego);
        //Añado el KeyListener para reconocer los eventos del teclado
        ventana.addKeyListener(kl);
        inicializaVariables();
        inicializaArray();
        sigPieza();
        imagenesPuntuaciones();
        pintaPuntuacion();
        playMusicaFondo("/musica/tetris.wav");
        comienzaJuego();
        comienzaTemporizador();
    }

    private void inicializaVariables() {
        piezaActual = (int) (Math.random() * 7); //De 0 a 6
        rotacion = 0; //De 0 a 3
        velocidad = 1000; //De 1000 a 0
        posicionActual = posicionInicial;
        puntuacion = 0; //Puntuacion conseguida
        lineasTotales = 0;
        nivel = 0;
        acelera = true; //Para que al pulsar el boton abajo acelere la pieza
        abajoPulsado = false;
        tiempoInicio = System.nanoTime();
        horas = 0;
        minutos = 0;
        segundos = 0;
        font = establecerFuente(35f);
        fontTiempo = establecerFuente(45f);
    }

    private void comienzaJuego() {
        juego = new Thread() {
            @Override
            public void run() {
                while (true) {
                    //while (!pausa) {
                    play();
                    espera(velocidad);
                    //}
                }
            }
        };
        juego.start();
    }

    private void comienzaTemporizador() {
        tiempo = new Thread() {
            @Override
            public void run() {
                while (true) {
                    //while (!pausa) {
                    tiempo();
                    espera(1000);
                    //}
                }
            }
        };
        tiempo.start();
    }

    private void inicializaArray() {
        arrayLogico = new int[24][16]; //Por defecto se inicializa con todos los valores a 0
        arraySigPieza = new int[4][3];
    }

    private void play() {
        actualiza();
        cae();
        pintaJuego();
        pintaTiempo();
    }

    private void tiempo() {
        segundos++;
        if (segundos == 60) {
            minutos++;
            segundos = 0;
        }
        if (minutos == 60) {
            horas++;
            minutos = 0;
        }
    }

    private void pintaTiempo() {
        g2.setColor(Color.BLACK);
        g2.fillRect(510, 180, 160, 65);
        g2.setFont(fontTiempo);
        g2.setColor(Color.WHITE);
        g2.drawString(String.format("%02d", horas) + ":" + String.format("%02d", minutos) + ":" + String.format("%02d", segundos), 542, 230);
        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    private void nuevaPieza() {
        piezaActual = sigPieza;
        rotacion = 0; //De 0 a 3
        posicionActual = new Point(7, 0);
        compruebaGameOver();
        sigPieza();
    }

    private void sigPieza() {
        borraSigPieza();
        sigPieza = (int) (Math.random() * 7); //De 0 a 6
        actualiza();
        pintaSigPieza();
    }

    private void pintaSigPieza() {
        ImageIcon cuadrado;
        int posicionInicialX = 555; //Posicion en X donde empieza a dibujar la siguiente ficha
        int posicionInicialY = 310; //Posicion en Y donde empieza a dibujar la siguiente ficha
        if (sigPieza == 0) { //Para que la ficha siguiente I quede centrada en el cuadrado de ficha siguiente
            posicionInicialX -= 10;
            posicionInicialY -= 10;
        }
        if (sigPieza == 3) { //Para que la ficha siguiente O quede centrada en el cuadrado de ficha siguiente
            posicionInicialX += 12;
        }
        for (int y = 0; y < arraySigPieza[0].length; y++) {
            for (int x = 0; x < arraySigPieza.length; x++) {
                g2.setStroke(new BasicStroke(2)); //Ancho de linea para el drawRect
                g2.setColor(colorFondo);
                g2.drawRect(posicionInicialX + (x * 25), y * 25 + posicionInicialY, 25, 25);
                switch (arraySigPieza[x][y]) {
                    case 1:
                        cuadrado = cuadrados[0];
                        break;
                    case 2:
                        cuadrado = cuadrados[1];
                        break;
                    case 3:
                        cuadrado = cuadrados[2];
                        break;
                    case 4:
                        cuadrado = cuadrados[3];
                        break;
                    case 5:
                        cuadrado = cuadrados[4];
                        break;
                    case 6:
                        cuadrado = cuadrados[5];
                        break;
                    case 7:
                        cuadrado = cuadrados[6];
                        break;
                    default:
                        cuadrado = cuadrados[7];
                        break;
                }
                g2.drawImage(cuadrado.getImage(), posicionInicialX + (x * 25), y * 25 + posicionInicialY, 25, 25, null);
            }
        }
    }

    private void compruebaGameOver() {
        for (int i = 0; i < Tetraminos[0][0].length; i++) {
            if (arrayLogico[posicionActual.y + Tetraminos[piezaActual][rotacion][i].y][posicionActual.x + Tetraminos[piezaActual][rotacion][i].x] != 0) {
                pausa = true;
                JOptionPane.showMessageDialog(ventana, "GAME OVER", "Tetris", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
    }

    private void actualiza() { //Coloca la pieza en la posicion actual dentro del arrayLogico
        for (int i = 0; i < Tetraminos[0][0].length; i++) {
            arrayLogico[posicionActual.y + Tetraminos[piezaActual][rotacion][i].y][posicionActual.x + Tetraminos[piezaActual][rotacion][i].x] = piezaActual + 1;
            arraySigPieza[Tetraminos[sigPieza][0][i].x][Tetraminos[sigPieza][0][i].y] = sigPieza + 1;
        }
    }

    private void borraPieza() { //Pone la pieza a 0 dentro del array logico para borrarla
        for (int i = 0; i < Tetraminos[0][0].length; i++) {
            arrayLogico[posicionActual.y + Tetraminos[piezaActual][rotacion][i].y][posicionActual.x + Tetraminos[piezaActual][rotacion][i].x] = 0;
        }
    }

    private void borraSigPieza() {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 4; x++) {
                arraySigPieza[x][y] = 0;
            }
        }
        g2.setColor(colorFondo);
        g2.fillRect(510, 290, 165, 98);
    }

    private boolean choca(int x, int y, int rot) { //Comprueba si la ficha choca con algo
        for (Point p : Tetraminos[piezaActual][rot]) {
            if (x + p.x >= 0 && x + p.x <= 15 && y + p.y <= 23) { //Si está dentro del tablero
                if (arrayLogico[p.y + y][p.x + x] != 0) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void cae() { //Desplaza la pieza una posición mas abajo
        borraPieza();
        if (!choca(posicionActual.x, posicionActual.y + 1, rotacion)) {
            posicionActual.y += 1;
        } else {
            acelera = false; //No acelera la pieza nueva si mantienes la flecha abajo pulsada
            if (!abajoPulsado) {
                acelera = true;
            }
            actualiza();
            compruebaLinea();
            nuevaPieza();
            sigPieza();
        }
        actualiza();
        pintaJuego();
    }

    private void mueve(int x) { //Desplaza la pieza una posicion a la izquierda/derecha
        borraPieza();
        if (!choca(posicionActual.x + x, posicionActual.y, rotacion)) {
            posicionActual.x += x;
        }
        actualiza();
        pintaJuego();
    }

    private void rota() { //Rota la ficha
        borraPieza();
        int nuevaRotacion = (rotacion + 1);
        if (nuevaRotacion > 3) {
            nuevaRotacion = 0;
        }
        if (!choca(posicionActual.x, posicionActual.y, nuevaRotacion)) {
            rotacion = nuevaRotacion;
        }
        actualiza();
        pintaJuego();
    }

    private void pintaJuego() { //Pinta la cuadricula de juego según el array lógico
        ImageIcon cuadrado;
        for (int y = 0; y < arrayLogico.length; y++) {
            for (int x = 0; x < arrayLogico[0].length; x++) {
                switch (arrayLogico[y][x]) {
                    case 1:
                        cuadrado = cuadrados[0];
                        break;
                    case 2:
                        cuadrado = cuadrados[1];
                        break;
                    case 3:
                        cuadrado = cuadrados[2];
                        break;
                    case 4:
                        cuadrado = cuadrados[3];
                        break;
                    case 5:
                        cuadrado = cuadrados[4];
                        break;
                    case 6:
                        cuadrado = cuadrados[5];
                        break;
                    case 7:
                        cuadrado = cuadrados[6];
                        break;
                    default:
                        cuadrado = cuadrados[7];
                        break;
                }
                g2.drawImage(cuadrado.getImage(), 25 + (x * 25 + 5), y * 25 + 65, 25, 25, null);
            }
        }
        g2.drawImage(cuadrados[8].getImage(), 30, 65, 400, 600, null);
        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    private void pintaPuntuacion() { //Pinta las puntuaciones
        g2.setColor(colorFondo);
        g2.fillRect(510, 440, 160, 210);
        g2.setFont(font);
        g2.setColor(Color.white);
        g2.drawString("Puntuacion: " + puntuacion, 520, 500);
        g2.drawString("Líneas: " + lineasTotales, 520, 560);
        g2.drawString("Nivel: " + nivel, 520, 620);
        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    private Font establecerFuente(Float tamanyo) { //Establezco el tipo de Fuente
        Font fuente = null;
        file = new File(getClass().getResource("/fonts/GLSNECB.TTF").getPath(), "GLSNECB.TTF");
        try {
            String fName = "/fonts/GLSNECB.TTF";
            InputStream is = getClass().getResourceAsStream(fName);
            fuente = Font.createFont(Font.TRUETYPE_FONT, is);
            fuente = fuente.deriveFont(Font.PLAIN, tamanyo);
        } catch (FontFormatException | IOException e) {
            System.out.println("Exception de la Fuente: " + e);
        }
        return fuente;
    }

    private Graphics2D creaDobleBuffer(Canvas panel) {
        try {
            if (strategy == null || strategy.contentsLost()) {
                panel.createBufferStrategy(2); // Crea BufferStrategy para el renderizado
                strategy = panel.getBufferStrategy();
                Graphics g = strategy.getDrawGraphics();
                g2 = (Graphics2D) g;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        // ANTIALIASING
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    private void compruebaLinea() { //Comprueba si hay una linea entera y la elimina
        int contador;
        int lineas = 0;
        for (int y = 0; y < arrayLogico.length; y++) {
            contador = 0;
            for (int x = 0; x < arrayLogico[0].length; x++) {
                if (arrayLogico[y][x] != 0) {
                    contador++;
                }
                if (contador == arrayLogico[0].length) {
                    lineasTotales++;
                    lineas++;
                    while (y - 1 >= 0) {
                        System.arraycopy(arrayLogico[y - 1], 0, arrayLogico[y], 0, arrayLogico[0].length);
                        //Hace lo mismo que el for
                        /*for (int z = 0; z < arrayLogico[0].length; z++) {
                            arrayLogico[y][z] = arrayLogico[y - 1][z];
                        }*/
                        y--;
                    }
                    posicionActual = posicionInicial;
                    puntuacion += lineas * 100; //1 linea = 100; 2 lineas = 300; 3 lineas = 600; 4 lineas = 1000
                    if (lineasTotales % 10 == 0) {
                        nivel++;
                        aumentaVelocidad();
                    }
                    pintaPuntuacion();
                }
            }
        }
    }

    private void aumentaVelocidad() { //Aumenta la velocidad de caida cada nivel
        velocidad -= 200;
        if (velocidad == 0) {
            velocidad = 10;
        }
    }

    KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                mueve(-1);
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                mueve(1);
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                rota();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                abajoPulsado = true;
                if (acelera) {
                    cae();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if ((e.getKeyChar() == 'p' || e.getKeyChar() == 'P') && !pausa) {
                pausa = true;
                Object[] options = {"OK"};
                if (JOptionPane.showOptionDialog(ventana, "Pausa. ", "Tetris", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == JOptionPane.OK_OPTION) {
                    pausa = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pausa = true;
                if (JOptionPane.showConfirmDialog(null, "¿Desea cerrar la aplicación?\nEl progreso actual se perderá.", "Atención.", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    pausa = false;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                acelera = true;
                abajoPulsado = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                borraPieza();
                boolean estaAbajo = false;
                while (!estaAbajo) {
                    if (!choca(posicionActual.x, posicionActual.y + 1, rotacion)) {
                        posicionActual.y += 1;
                    } else {
                        estaAbajo = true;
                    }
                }
                actualiza();
                pintaJuego();
            }
            if (e.getKeyCode() == KeyEvent.VK_F5) {
                pausa = true;
                if (JOptionPane.showConfirmDialog(null, "¿Guardar partida?", "Guardar.", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    guardarPartida();
                }
                pausa = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_F6) {
                pausa = true;
                if (JOptionPane.showConfirmDialog(null, "¿Cargar partida?", "Cargar.", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    cargarPartida();
                    pintaPuntuacion();
                }
                pausa = false;
            }
        }
    };

    private void guardarPartida() {
        FileDialog fd = new FileDialog(ventana, "Elije un Fichero", FileDialog.SAVE);
        fd.setFile("tetris.sav");
        fd.setVisible(true);
        String filename = fd.getDirectory() + fd.getFile();

        FileOutputStream fos;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(filename);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(arrayLogico);
            oos.writeObject(arraySigPieza);
            oos.writeObject(piezaActual);
            oos.writeObject(sigPieza);
            oos.writeObject(rotacion);
            oos.writeObject(velocidad);
            oos.writeObject(puntuacion);
            oos.writeObject(lineasTotales);
            oos.writeObject(nivel);
            oos.writeObject(posicionActual);
            oos.writeObject(horas);
            oos.writeObject(minutos);
            oos.writeObject(segundos);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    private void cargarPartida() {
        FileDialog fd = new FileDialog(ventana, "Elije un Fichero", FileDialog.LOAD);
        fd.setFile("tetris.sav");
        fd.setVisible(true);
        String filename = fd.getDirectory() + fd.getFile();

        FileInputStream fis;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(filename);
            ois = new ObjectInputStream(fis);

            arrayLogico = (int[][]) ois.readObject();
            arraySigPieza = (int[][]) ois.readObject();
            piezaActual = (int) ois.readObject();
            sigPieza = (int) ois.readObject();
            rotacion = (int) ois.readObject();
            velocidad = (int) ois.readObject();
            puntuacion = (int) ois.readObject();
            lineasTotales = (int) ois.readObject();
            nivel = (int) ois.readObject();
            posicionActual = (Point) ois.readObject();
            horas = (int) ois.readObject();
            minutos = (int) ois.readObject();
            segundos = (int) ois.readObject();

        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    private void espera(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(ventana, ex);
        }
    }

    private void imagenesPuntuaciones() {
        separador = new ImageIcon(getClass().getResource("/imagenes/puntuacion.png"));
        g2.drawImage(separador.getImage(), 450, 0, 243, 695, null);
        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    private void playMusicaFondo(final String musica) {
        musicaFondo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(musica));
                    Clip clipFondo = AudioSystem.getClip();
                    clipFondo.open(AudioSystem.getAudioInputStream(in));
                    clipFondo.loop(Clip.LOOP_CONTINUOUSLY);
                    clipFondo.start();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    System.err.println(e);
                }
            }
        });
        musicaFondo.start();
    }

    public static void main(String[] args) {
        Tetris tetris = new Tetris();
    }
}
