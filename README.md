# FireFIghters

✔✔✔✔✔✔ CECI EST DANS LE CADRE D'UN PROJET SCOLAIRE.

Cette application permet de gerer en temps reel les cas d'incendis.
Comme fonctionnalites nous avons :
- Gestion des comptes utilisateurs
- Gestion des ressources
- Visualisation des donnees sur la map en temps reel
- Lancement de l'alerte grace au la photo, l'audio, la messagerie et l'appel
- reception de l'alerte grace a la notificaton push (pas execute en background mais execute sur l'activite principale qui d'alleurs n'est que la seule activite car nous avons opte pour une meilleure performance en gerant les fragments)
- Changement de theme (pour le fun)

Technologies
- Android Java
- Firebase
- Architecture components (MVVM architecture)
- Mapbox

Comment est ce que ca marche :
- Nous avons 3 pages au depart (Home, Emergencies, Profil)
- Dans l'onglet HOME, on a les boutons :
    
    * Bluetooth : permet de voir l'appareil sur lequel nous sommes connecte.
    
    * Setting : cette page permet de changer le theme.
    
    * Call : il permet de lancer un appel vers un numero de telephone.
    
    * SMS : permet d'envoyer un message vers un numero de telephone.
    
    * SOS : permet d'envoyer une alerte en ayant la possibilite d'ajouter une image, audio et sms.
    
    * Map view : ici on affiche la map avec les points d'eau et d'alertes. Nous pouvons egalement tracer la route entre notre position et le point choisi.

- Dans l'onglet EMERGENCIES :
    * La liste des alertes : cette liste contient toutes les alertes.

- L'onglet PROFIL :
    * Page de connexion : elle permet de se connecter et acceder aux informations de son profil mais egalement aux fonctionnalites supplementaires.
    * Page d'enregistrement (utilisateur lambda) : Permet a un utilisateur quelconque de s'enregistrer.
    * Page des fonctionnalites supplementaires : nous avons ici 4 pages en fonction du profil :

        # Pour utilisateur lambda : possibilite d'ajout d'un point d'eau.
        
        # Pour sapeur pompier : possibilite d'ajout d'un point d'eau, de manager les ressources et ainsi que les alertes.
        
        # Pour chef d'unite : possibilite d'ajout d'un point d'eau, de manager les ressources et ainsi que les alertes et de travailler sur une alerte.
        
        # Pour d'administrateur : possibilite d'ajout d'un point d'eau, des sapeurs pompiers, des unites de travail et de manager toutes les ressources et personnes.


NB: Les identifiants de chaque type de compte
- Utilisateur : 
    * email : prosper@gmail.com
    * mot de passe : prosper
- Sapeur pompier : 
    * email : ff@gmail.com
    * mot de passe : prosper
- Chef d'unite de UN1 : 
    * email : c@gmail.com
    * mot de passe : prosper
- Administrateur : 
    * email : a@gmail.com
    * mot de passe : prosper

Captures d'ecran

![Alt Text](https://docs.google.com/uc?export=download&id=1UhJEfYlR0jeM7EEhlOA5Xn4GiEscZNFU)
