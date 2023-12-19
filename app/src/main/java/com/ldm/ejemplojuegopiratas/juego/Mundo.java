package com.ldm.ejemplojuegopiratas.juego;

import java.util.Random;

public class Mundo {
    static final int MUNDO_ANCHO = 10;
    static final int MUNDO_ALTO = 13;
    static final int INCREMENTO_PUNTUACION = 10;
    static final float TICK_INICIAL = 0.5f;
    static final float TICK_DECREMENTO = 0.05f;
    static final int MIN_TICKS_CACTUS_APARECER = 5;
    static final int MAX_TICKS_CACTUS_APARECER = 20;
    static final int MIN_TICKS_CACTUS_DESAPARECER = 20;
    static final int MAX_TICKS_CACTUS_DESAPARECER = 45;

    static final int MIN_TICKS_CESTA_APARECER = 30;
    static final int MAX_TICKS_CESTA_APARECER = 40;
    static final int MIN_TICKS_CESTA_DESAPARECER = 10;
    static final int MAX_TICKS_CESTA_DESAPARECER = 20;

    public Cesta cesta;

    public Cactus cactus;
    public boolean CactusCogido = false;
    public boolean CestaCogida=false;

    public RamoFlores ramo;

    public Flores botin;
    public boolean finalJuego = false;
    public int puntuacion = 0;

    boolean campos[][] = new boolean[MUNDO_ANCHO][MUNDO_ALTO];
    Random random = new Random();
    float tiempoTick = 0;
    static float tick = TICK_INICIAL;

    int ticksCactus = random.nextInt(MAX_TICKS_CACTUS_APARECER - MIN_TICKS_CACTUS_APARECER + 1) + MIN_TICKS_CACTUS_APARECER;
    int ticksCesta = random.nextInt(MAX_TICKS_CACTUS_APARECER - MIN_TICKS_CACTUS_APARECER + 1) + MIN_TICKS_CACTUS_APARECER;
    public Mundo() {
        ramo = new RamoFlores();
        colocarObstaculos(true,true,true);
        cactus = null;
        cesta=null;
    }

    private void colocarObstaculos(boolean colocarBotin,boolean colocarCactus,boolean colocarCesta) {

        if(colocarBotin || colocarCactus || colocarCesta){
            for (int x = 0; x < MUNDO_ANCHO; x++) {
                for (int y = 0; y < MUNDO_ALTO; y++) {
                    campos[x][y] = false;
                }
            }
        }


        int len = ramo.partes.size();
        for (int i = 0; i < len; i++) {
            Tripulacion parte = ramo.partes.get(i);
            campos[parte.x][parte.y] = true;
        }
        if (!colocarBotin) campos[botin.x][botin.y] = true;
        if (!colocarCactus && cactus != null) campos[cactus.x][cactus.y] = true;
        if (!colocarCesta && cesta != null) campos[cesta.x][cesta.y] = true;

        int botinX,botinY;
        if(colocarBotin){
            botinX = random.nextInt(MUNDO_ANCHO);
            botinY = random.nextInt(MUNDO_ALTO);

            while (true) {
                if (campos[botinX][botinY] == false)
                    break;
                botinX += 1;
                if (botinX >= MUNDO_ANCHO) {
                    botinX = 0;
                    botinY += 1;
                    if (botinY >= MUNDO_ALTO) {
                        botinY = 0;
                    }
                }
            }
            botin = new Flores(botinX, botinY, random.nextInt(3));
        }

        if (colocarCactus) {
            botinX = random.nextInt(MUNDO_ANCHO);
            botinY = random.nextInt(MUNDO_ALTO);
            while (true) {
                if (!campos[botinX][botinY]) {
                    campos[botinX][botinY] = true;
                    break;
                }
                botinX += 1;
                if (botinX >= MUNDO_ANCHO) {
                    botinX = 0;
                    botinY += 1;
                    if (botinY >= MUNDO_ALTO) {
                        botinY = 0;
                    }
                }
            }
            cactus = new Cactus(botinX, botinY);
        }

        if (colocarCesta) {
            botinX = random.nextInt(MUNDO_ANCHO);
            botinY = random.nextInt(MUNDO_ALTO);
            while (true) {
                if (!campos[botinX][botinY]) {
                    campos[botinX][botinY] = true;
                    break;
                }
                botinX += 1;
                if (botinX >= MUNDO_ANCHO) {
                    botinX = 0;
                    botinY += 1;
                    if (botinY >= MUNDO_ALTO) {
                        botinY = 0;
                    }
                }
            }
            cesta = new Cesta(botinX, botinY);
        }


    }

    public void update(float deltaTime) {
        boolean colocarBotin = false;
        boolean colocarCactus = false;
        boolean colocarCesta = false;

        if (finalJuego)

            return;

        tiempoTick += deltaTime;

        while (tiempoTick > tick) {
            tiempoTick -= tick;
            if (ramo.partes.size() < (int) ((MUNDO_ANCHO * MUNDO_ALTO) * 0.1)){
                ticksCesta--;
                ticksCactus--;
            }
            else{
                cactus = null;
                cesta=null;

                ticksCesta=1;
                ticksCactus = 1;
            }
            ramo.avance();
            if (ramo.comprobarChoque()) {
                finalJuego = true;
                return;
            }

            Tripulacion head = ramo.partes.get(0);
            if (head.x == botin.x && head.y == botin.y) {
                puntuacion += INCREMENTO_PUNTUACION;
                ramo.abordaje();
                if (ramo.partes.size() == MUNDO_ANCHO * MUNDO_ALTO) {
                    finalJuego = true;
                    return;
                } else {
                    colocarBotin=true;
                }

                if (puntuacion % 100 == 0 && tick - TICK_DECREMENTO > 0) {
                    tick = TICK_INICIAL - (TICK_DECREMENTO * (puntuacion % 100));
                }
            }
            if(cactus != null){
                if (head.x == cactus.x && head.y == cactus.y) {

                    puntuacion -= INCREMENTO_PUNTUACION;
                    if(puntuacion < 0) puntuacion = 0;
                    CactusCogido = true;

                    ticksCactus = 0;
                }
            }
            if(cesta!=null){
                if(head.x==cesta.x && head.y==cesta.y){
                    ramo.dejarFlor();
                    ticksCesta=0;
                    CestaCogida=true;
                }
            }


            if (ticksCactus == 0) {
                if (cactus != null) {

                    cactus = null;
                    ticksCactus = random.nextInt(MAX_TICKS_CACTUS_APARECER - MIN_TICKS_CACTUS_APARECER + 1) + MIN_TICKS_CACTUS_APARECER;
                } else {

                    colocarCactus = true;
                    ticksCactus = random.nextInt(MAX_TICKS_CACTUS_DESAPARECER - MIN_TICKS_CACTUS_DESAPARECER + 1) + MIN_TICKS_CACTUS_DESAPARECER;
                }
            }



            if (ticksCesta == 0) {
                if (cesta != null) {

                    cesta = null;
                    ticksCesta = random.nextInt(MAX_TICKS_CESTA_APARECER - MIN_TICKS_CESTA_APARECER + 1) + MIN_TICKS_CESTA_APARECER;
                } else {

                    colocarCesta = true;
                    ticksCesta = random.nextInt(MAX_TICKS_CESTA_DESAPARECER - MIN_TICKS_CESTA_DESAPARECER + 1) + MIN_TICKS_CESTA_DESAPARECER;
                }
            }



            colocarObstaculos(colocarBotin,colocarCactus,colocarCesta);
        }
    }
}

