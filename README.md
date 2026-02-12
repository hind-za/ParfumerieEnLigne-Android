# ParfumerieEnLigne-Android

## Présentation

ParfumerieEnLigne est une application mobile Android dédiée à la vente de produits de beauté : parfums, maquillage et soins de la peau.

Développée en Java sous Android Studio dans un cadre académique, elle simule une plateforme e-commerce complète avec authentification, gestion du panier, paiement simulé et système de fidélité.

---

## Architecture Technique

- Langage : Java  
- IDE : Android Studio  
- SDK Minimum : API 26 (Android 8.0)  
- Stockage : SharedPreferences (stockage local)  
- Design : Material Design  

Les données utilisateur sont gérées via une classe dédiée `PrefsManager`, assurant une organisation claire et une gestion logique multi-utilisateur.

---

## Authentification et Sécurité

- Inscription et connexion utilisateur  
- Persistance de session  
- Restriction d’accès (Panier, Profil, Favoris)  
- Gestion multi-utilisateur basée sur des clés dynamiques (email)  

---

## Fonctionnalités Principales

### Page d’Accueil
- Produits les plus vendus  
- Barre de recherche  
- Navigation intuitive  

### Système de Recherche
- Recherche locale par mots-clés  
- Résultats dynamiques  

### Détails Produit
- Image, description, prix  
- Ajout au panier  
- Ajout aux favoris  
- Recommandations de produits  

### Wishlist
- Système de “Like”  
- Liste personnalisée par utilisateur  

### Panier
- Ajout / suppression d’articles  
- Modification des quantités  
- Calcul automatique du total  
- Persistance locale  

### Livraison
- Adresse  
- Ville  
- Numéro de téléphone  

### Paiement
- Paiement par carte (simulé)  
- Paiement à la livraison  
- Confirmation de commande  

### Points de Fidélité
- Attribution automatique après commande  
- Consultation dans le profil  

### Avis et Notation
- Système de notation sur 5 étoiles  
- Commentaires  
- Historique des avis par utilisateur  

### Notifications
- Notifications locales  
- Badge de compteur  

### Chatbot
- Chatbot basé sur des règles (rule-based)  
- Fonctionnement hors ligne  
- Assistance produits et promotions  

---

## Parcours Utilisateur

1. Ouverture de l’application  
2. Navigation ou recherche  
3. Consultation des détails produit  
4. Ajout au panier ou aux favoris  
5. Authentification  
6. Saisie des informations de livraison  
7. Choix du mode de paiement  
8. Validation de la commande  
9. Confirmation et attribution des points  

## Projet Académique

Développé dans le cadre du module de Programmation Mobile  
Université Cadi Ayyad – Faculté des Sciences et Techniques  
2025
