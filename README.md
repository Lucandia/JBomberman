# JBomberman
Super Bomberman videogame written in Java

d) le decisioni di progettazione relative a ognuna delle specifiche (vedi
sotto)
e) I design pattern adottati, dove e perchè
f) l’uso degli stream
g) altre note progettuali e di sviluppo


1) gestione del profilo utente, nickname, avatar, partite giocate, vinte e perse, livello ...
2) gestione di una partita completa con almeno due livelli giocabili, due tipi di nemici con grafica e comportamento di gioco differenti, con gestione del punteggio, delle
vite, di 3 power up, shermata hai vinto, game over, continua.
3) uso appropriato di MVC [1,2], Observer Observable e di altri Design Pattern

Nel contesto di una applicazione JavaFX, il pattern MVC (Model-View-Controller) e il pattern Observer/Observable si integrano naturalmente. JavaFX ha già costruito nel suo framework i principi dell'Observer/Observable, specialmente attraverso le sue proprietà e le collezioni osservabili. Qui di seguito è spiegato come si possono utilizzare insieme questi pattern nel contesto di un videogioco come Super Bomberman.

4) adozione di Java Swing [2] o JavaFX [3] per la GUI:
JavaFX e' un framework piu' nuovo che e' stato costruito con lo scopo di sostituire Swing. Offre un'ampia gamma di funzionalità moderne e avanzate per la creazione di interfacce utente interattive simili al Web, incluso il supporto per lo stile CSS, la grafica 3D e i contenuti multimediali. Ci si aspetta che JavaFX cresca nel tempo. Incoraggia un approccio di sviluppo più moderno, fornendo un migliore supporto per il design pattern MVC (Model-View-Controller).
E hanno il sito molto figo

5) utilizzo appropriato di stream
6) riproduzione di audio sample (si veda appendice AudioManager.Java)
7) animazioni ed effetti speciali
