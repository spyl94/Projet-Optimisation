Projet d'Optimisation et Complexité
===================

Considérons un système informatique composé d'un calculateur disposant d'unités de disques et de bandes. Pour étudier a priori les performances (temps de réponse, débit, équilibrage de la configuration) 
d'un tel système, il est souvent intéressant de modéliser son comportement (ici, par un réseau de files 
d'attente). 

![Schéma](http://image.noelshack.com/fichiers/2013/24/1371415362-descriptif.jpg)

Les données du problème sont introduites dans un fichier de paramètres "para.p" dont le format est le suivant : 
- 1/351 probabilité_p1 
- 300/351 probabilité_p2 
- 5/351 temps_moyen_UC(seconde) 
- 0.05 temps_moyen_disque(seconde) 
- 0.2 temps_moyen_bande(seconde) 
- 0.25 lambda(nombre_de_travaux_par_seconde) 
- 50 ordre_de_multiprogrammation 
- 1000 nombre_de_travaux_a_traiter 
- 0.5 taux_d'utilisation_du_disque_D1
