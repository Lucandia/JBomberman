package com.esame;

/**
 * La classe JBomberMan rappresenta il punto di ingresso dell'applicazione.
 * Contiene il metodo main che avvia l'applicazione e richiama il menu principale.
 */
public class JBomberMan {
    
    /**
     * Il metodo main avvia l'esecuzione del gioco.
     * Pre-carica tutti gli effetti sonori e avvia l'applicazione.
     * 
     * @param args gli argomenti della riga di comando
     */
    public static void main(String[] args) {
        AudioUtils.preloadAll(); // Preload all sound effects
        MainMenu.launchMainMenu(args);
    }
}
