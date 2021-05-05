# Monopoly

Projet de programmation (PI4 2021) de L2 Informatique (Université de Paris).  
Sujet: Jeu de plateau d'achat et vente de propriétés.  
Projet réalisé sous l'encadrement de Edwin Hamel-De le Court.  

## Présentation
Le Monopoly est un jeu où l'on achète, loue et vend des propriétés de façon à accroître ses richesses.  
Les joueurs doivent parcourir les cases du plateau en essayant d'agrandir leur empire, d'éviter les loyers exorbitants des propriétés des adversaires et éviter les cartes malchanceuses.  
Les rues, prix et cartes sont similaires à celles du Monopoly original.
	
## Fonctionnalités
*Fonctionnalités basiques:*
- Plateau de jeu composé de différentes cases (case départ, cases propriétés possédant un prix d'achat, un loyer et une couleur).
- Mode de jeu tour par tour.
- Achat et vente des propriétés.
- Paiement des loyers.
- Augmentation des loyers en fonction du nombre de propriétés de la même famille possédée par un même joueur.
- Déplacement des pions sur le plateau avec un lancer de dés.
- Système de faillite.
- Système de victoire et défaite.
- Robots.
- Accueil avec sélection du nombre de joueurs, leur pseudonyme et s'ils sont des robots.
- Interface graphique.
- Jouable sur terminal, mais les fonctionnalités suivantes ne sont pas implémentées: Robots, vente de la carte 'Libération de prison', cartes 'Chance' et 'Communauté', défis, maisons et hôtels, règles, pseudonymes, carte immobilière, historique et tchat.

*Fonctionnalités supplémentaires:*
- Prison.
- Taxes et impôts.
- Cartes 'Chance' et 'Communauté'.
- Case 'Propriétés Spéciales' : Gares, Electricité, Eau.
- Réseau et tchat.
- Système de défis.
- Améliorations des maisons et hôtels.
- Historique.

## Règles
Voici les règles complètes du jeu. Elles peuvent aussi être consultées depuis le bouton 'Règles' directement en jeu.  

*PRINCIPE DU JEU:*  
Le Monopoly est un jeu où l'on achète, loue et vend des propriétes de façon à accroître ses richesses.  
En partant de la case départ, déplacez votre pion sur le plateau de jeu suivant votre résultat au lancer de dés.  
Quand vous arrivez sur une case qui n'appartient encore à personne, vous pouvez l'acheter à la banque.  
Les joueurs qui sont propriétaires percoivent des loyers de la part des adversaires s'arrêtant sur leur terrain.  
La construction de maisons et hotels ou l'acquisition de plusieurs biens de la même couleur augmente considérablement le loyer que vous pouvez percevoir pour vos propriétés.  
Vous devez toujours vous plier aux instructions données par les cartes de caisse de communauté et chance. Parfois, vous serez envoyé en prison.  
	
*COMMENT GAGNER:*  
Etre le dernier joueur qui n'ait pas fait faillite.  

*COMMENT PERDRE:*  
Etre en faillite: ne plus avoir d'argent ou de propriétes à vendre.  
	
*DEROULEMENT D'UN TOUR:*  
Cliquez sur le bouton "Lancer" et vos pions se déplaceront suivant le nombre obtenu au lancement des dés. Vous avez la possibilité de tomber sur une case:  
- Propriétes: Si cette propriété n'est pas encore achetée, vous pouvez choisir de l'acheter. Plus vous avez de propriétés appartenant au même groupe de couleur, plus le loyer est grand (pour les compagnies, le loyer est la somme que vous avez obtenu au lancer de dés, fois le palier de loyer marqué sur la carte).   
        En revanche, si cette propriété appartient déjà à un autre joueur, vous devez lui verser un loyer ou vous avez la possibilité de lui lancer un défi si vous refusez ce loyer.  
		S'il s'avère que votre argent n'excède pas le montant du loyer, vous devez vendre autant de vos biens que nécessaire jusqu'à atteindre cette somme.  
		De plus, si vous avez assez d'argent, vous pouvez demander à racheter la propriété.  
		Si le propriétaire accepte le rachat et qu'il y a des bâtiments sur le terrain, il recoit l'argent du prix d'achat de la propriété plus la moitié du prix d'achat de ses bâtiments.  
- Chance/Caisse de communaute: A l'arrivee sur l'une de ces cases, vous piocherez une carte contenant une indication que vous devrez suivre.  
		Si vous tirez une carte "Vous êtes libéré de prison", vous pouvez la garder jusqu'à ce que vous décidiez de l'utiliser.  
		Une carte peut vous demander de vous rendre à une autre case. Si vous passez par la case depart, recevez 2000e.  
		Vous ne passez pas par la case départ quand vous êtes envoyé en prison.  
- Taxe/Impôt: Si vous vous arrêtez sur l'une de ces cases, payez le montant indiqué a la banque.  
- Prison: Si vous arrivez sur la case "Prison", vous serez immédiatement envoyé en prison et vous y resterez pendant 3 tours.  
- Départ: Vous percevrez 2000e a chaque fois que vous passerez par la case "Départ".  
Vous cliquerez ensuite sur le bouton "Fin" qui laissera le tour au joueur suivant.  

*FIN DE PARTIE:*  
La partie se termine lorsqu'il ne reste plus qu'un joueur en jeu.  
	
*OPTIONS:*  
- Robot: Vous pouvez jouer contre des robots ou faire une partie uniquement composée de robots.  
- Défi: En tombant sur la propriete d'un autre joueur, vous payez le loyer mais vous avez la possibilité de lancer un défi au propriétaire.   
	Si ce dernier l'accepte, vous lancez tous les deux les dés et le joueur ayant obtenu le plus grand nombre gagne.  
	Si vous êtes le gagnant, le propriétaire vous rembourse le loyer que vous avez payé.  
	Si vous perdez, vous devez repayer le loyer et s'il y a égalité, rien ne se passe (vous aurez donc tout de même paye le loyer une fois).  
- Maisons/hotels:  
    Vous devez posséder tous les terrains d'un même groupe de couleur pour pouvoir acheter des maisons.  
	Vous devez construire uniformément: vous ne pouvez pas construire de deuxième maison sur un terrain tant que vous n'avez pas construit une maison sur chaque terrain du même groupe de couleur.  
    Vous pouvez construire jusqu'à 4 maisons sur un même terrain.  
	Vous ne pouvez pas construire de bâtiments sur les gares ni sur les compagnies.  
    Une fois que vous avez 4 maisons sur une propriété, vous pouvez les échanger contre un hôtel. Vous devez payer en plus le prix de l'hôtel.  
    Vous ne pouvez construire qu'un seul hôtel par terrain et vous ne pouvez pas construire de maison sur un terrain disposant déjà d'un hôtel.  
	Il n'y a pas d'uniformité à respecter dans la revente des bâtiments. Si la revente casse l'uniformité, vous pouvez tout de même acheter des batiments sur la proprieté pour rattraper le retard que la revente a causé.  
    Le prix de revente d'un bâtiment est la moitié de son prix d'achat. Si vous vendez un hôtel, il n'y a plus de bâtiments sur le terrain.  
    Chaque propriété possédant des maisons ou un hôtel augmente son loyer.  
- Historique: Pour suivre le déroulement des actions en cours et en détail, cliquez sur le bouton "Historique" et une fenetre s'ouvrira, décrivant les actions du jeu en temps réel.  
- Tchat: Le tchat est une zone de communication où vous pouvez discuter avec vos adversaires.  


## Script de compilation