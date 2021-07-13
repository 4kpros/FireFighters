# FireFIghters

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
    
    . Bluetooth : permet de voir l'appareil sur lequel nous sommes connecte.
    
    . Setting : Permet de changer le theme.
    
    . Call : Permet de lancer un appel vers un numero de telephone.
    
    . SMS : Permet d'envoyer un message vers un numero de telephone.
    
    . SOS : Permet d'envoyer une alerte en ayant la possibilite d'ajouter une image, audio et sms.
    
    . Map view : Ici on affiche la map avec les points d'eau et d'alertes. Nous pouvons egalement tracer la route entre notre position et le point choisi.


Captures d'ecran

![Alt Text](https://docs.google.com/uc?export=download&id=1UhJEfYlR0jeM7EEhlOA5Xn4GiEscZNFU)
