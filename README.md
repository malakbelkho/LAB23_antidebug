# JNIDemo – LAB 22

Application Android développée en **Java** avec intégration de code natif **C++** grâce à **JNI (Java Native Interface)**.

L’application permet de démontrer la communication entre une interface Android Java et une bibliothèque native C++ compilée avec le **NDK** et **CMake**.

L’interface a été personnalisée sous le nom **Native Bridge Studio**, avec un design moderne, coloré, interactif et structuré en cartes.

---

## Objectif

Le but de ce laboratoire est de :

- Créer un projet Android avec support C++ natif
- Comprendre le rôle de **JNI**, **NDK**, **CMake** et des bibliothèques `.so`
- Déclarer et appeler des méthodes natives depuis Java
- Envoyer des données Java vers C++
- Récupérer des résultats calculés côté natif
- Manipuler différents types de données :
  - `String`
  - `int`
  - `int[]`
  - `long`
- Afficher les résultats dans une interface Android moderne
- Lire les messages natifs dans **Logcat**
- Ajouter une extension de benchmark Java vs C++

---

## Description de l’application

L’application contient une seule activité principale présentant plusieurs démonstrations JNI.

Elle permet de tester :

- Une fonction native de bienvenue
- Le calcul natif d’un factoriel
- L’inversion native d’une chaîne de caractères
- La somme native d’un tableau d’entiers
- Un benchmark comparant un calcul Java et un calcul C++ via JNI

---

## Fonctionnement général

Le fonctionnement de l’application suit ce flux :

```text
Interface Android Java
        ↓
Méthodes natives déclarées dans MainActivity
        ↓
Chargement de la bibliothèque secure_bridge
        ↓
Exécution des fonctions C++ dans native-lib.cpp
        ↓
Retour des résultats vers Java
        ↓
Affichage dans l’interface
```

---

## Fonctionnalités

- Affichage d’un message généré côté C++
- Calcul du factoriel d’un nombre saisi par l’utilisateur
- Gestion des erreurs natives :
  - Nombre négatif
  - Dépassement de capacité `int`
- Inversion d’un texte envoyé depuis Java vers C++
- Somme d’un tableau d’entiers saisi sous forme de valeurs séparées par des virgules
- Extension benchmark :
  - L’utilisateur saisit un nombre d’itérations `N`
  - Java calcule la somme de `1` à `N`
  - C++ calcule la même somme via JNI
  - L’application affiche les deux résultats et les deux temps d’exécution
- Affichage des logs natifs dans Logcat avec un tag personnalisé
- Interface moderne avec :
  - Fond en dégradé
  - Cartes arrondies
  - Boutons colorés
  - Champs de saisie personnalisés
  - Résultats affichés dans des blocs visuels

---

## Technologies utilisées

- Android Studio
- Java
- C++
- JNI
- Android NDK
- CMake
- XML
- Kotlin DSL pour la configuration Gradle
- API minimum : 24 Android 7.0

---

## Aperçu de l’application

▶️ Une démonstration vidéo complète de l’application est disponible dans le dossier **Demo** du repository.

▶️ Une autre vidéo montre également les logs natifs dans **Logcat**.

⚠️ En cas de problème de lecture depuis le repository :

👉 [▶️ Voir la démo sur Google Drive](https://drive.google.com/drive/folders/1Wf2Ilwq0KAOufomd35x9siHPGJqmpGpu?usp=drive_link)

---

## Structure du projet

```text
JNIDemo/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ma/ensa/mobile/jnibridge/
│   │   │   │       └── MainActivity.java
│   │   │   │
│   │   │   ├── cpp/
│   │   │   │   ├── native-lib.cpp
│   │   │   │   └── CMakeLists.txt
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── bg_jni_screen.xml
│   │   │   │   │   ├── bg_jni_header.xml
│   │   │   │   │   ├── bg_jni_card.xml
│   │   │   │   │   ├── bg_jni_chip.xml
│   │   │   │   │   ├── bg_jni_input.xml
│   │   │   │   │   ├── bg_jni_result.xml
│   │   │   │   │   ├── bg_jni_button.xml
│   │   │   │   │   └── bg_jni_button_alt.xml
│   │   │   │   │
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml
│   │   │   │       └── themes.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── Demo/
│   ├── demo_app.mp4
│   └── demo_logcat.mp4
│
└── README.md
```

---

## Détails des fichiers principaux

### MainActivity.java

Ce fichier représente l’activité principale de l’application.

Il contient :

- Les déclarations des méthodes natives
- Le chargement de la bibliothèque C++
- La liaison entre l’interface XML et le code Java
- La gestion des boutons
- La validation des entrées utilisateur
- L’appel des fonctions natives
- L’affichage des résultats

Méthodes natives déclarées :

```java
private native String nativeGreeting();
private native int nativeFactorialSafe(int number);
private native String nativeMirrorText(String text);
private native int nativeSumValues(int[] values);
private native long nativeBenchmarkLoop(int rounds);
```

Chargement de la bibliothèque native :

```java
static {
    System.loadLibrary("secure_bridge");
}
```

Cette instruction permet à Android de charger la bibliothèque native générée à partir du code C++.

---

### native-lib.cpp

Ce fichier contient l’implémentation C++ des méthodes natives appelées depuis Java.

Il contient les fonctions JNI suivantes :

```cpp
nativeGreeting()
nativeFactorialSafe()
nativeMirrorText()
nativeSumValues()
nativeBenchmarkLoop()
```

Chaque fonction utilise une signature JNI basée sur le package Java :

```text
ma.ensa.mobile.jnibridge
```

Exemple de signature native :

```cpp
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeGreeting
```

Le fichier utilise aussi Logcat côté natif grâce à :

```cpp
#include <android/log.h>
```

Tag utilisé dans Logcat :

```text
LAB22_NATIVE_BRIDGE
```

---

### CMakeLists.txt

Ce fichier configure la compilation native C++.

Il permet de :

- Déclarer la bibliothèque native
- Indiquer le fichier source C++
- Lier la bibliothèque native avec la bibliothèque Android `log`

La bibliothèque générée est :

```text
libsecure_bridge.so
```

Elle est chargée côté Java avec :

```java
System.loadLibrary("secure_bridge");
```

Important :

Le nom passé à `System.loadLibrary()` ne contient pas le préfixe `lib` ni l’extension `.so`.

---

### activity_main.xml

Ce fichier définit l’interface graphique de l’application.

Il contient :

- Un `ScrollView`
- Une grande section d’en-tête
- Une carte pour le message natif
- Une carte pour le factoriel
- Une carte pour l’inversion de texte
- Une carte pour la somme du tableau
- Une carte pour le benchmark Java vs C++

Les composants principaux sont :

```text
txtNativeHello
inputFactorial
btnFactorial
txtFactorialResult

inputText
btnReverse
txtReverseResult

inputNumbers
btnArray
txtArrayResult

inputBenchmarkRounds
btnBenchmark
txtBenchmarkResult
```

---

### colors.xml

Ce fichier contient la palette de couleurs personnalisée de l’application.

Il définit notamment :

- Les couleurs du fond
- Les couleurs du header
- Les couleurs des boutons
- Les couleurs des cartes
- Les couleurs des textes
- Les couleurs des bordures

Cette personnalisation permet d’obtenir une interface plus moderne et plus agréable visuellement.

---

### Design res/drawable

Les fichiers `drawable` personnalisent l’apparence de l’application.

#### bg_jni_screen.xml

Fond principal de l’application avec un dégradé foncé et coloré.

#### bg_jni_header.xml

Carte d’en-tête avec un dégradé bleu, violet et cyan.

#### bg_jni_card.xml

Cartes arrondies utilisées pour organiser les fonctionnalités.

#### bg_jni_chip.xml

Petit badge affichant le titre du laboratoire.

#### bg_jni_input.xml

Fond personnalisé des champs de saisie.

#### bg_jni_result.xml

Fond utilisé pour afficher les résultats.

#### bg_jni_button.xml

Bouton principal avec dégradé orange et rose.

#### bg_jni_button_alt.xml

Bouton secondaire avec dégradé turquoise et bleu.

---

## Fonctions natives implémentées

### 1. Message natif

```java
nativeGreeting()
```

Cette fonction retourne un message généré côté C++.

Résultat attendu :

```text
Connexion active : Java communique avec C++ via JNI.
```

---

### 2. Factoriel sécurisé

```java
nativeFactorialSafe(int number)
```

Cette fonction calcule le factoriel d’un nombre côté C++.

Exemple :

```text
10! = 3628800
```

Gestion des erreurs :

```text
-1 : nombre négatif refusé
-2 : dépassement de la limite int
```

---

### 3. Inversion de texte

```java
nativeMirrorText(String text)
```

Cette fonction reçoit une chaîne Java, la transforme en chaîne C++, l’inverse, puis renvoie le résultat à Java.

Exemple :

```text
Entrée : JNI is powerful
Sortie : lufrewop si INJ
```

---

### 4. Somme d’un tableau

```java
nativeSumValues(int[] values)
```

Cette fonction reçoit un tableau Java `int[]`, le traite côté C++, puis retourne la somme.

Exemple :

```text
Entrée : 10, 20, 30, 40, 50
Sortie : 150
```

---

### 5. Benchmark Java vs C++

```java
nativeBenchmarkLoop(int rounds)
```

Cette extension permet de comparer Java et C++ sur le même calcul.

L’utilisateur saisit un nombre `N`.

Java calcule :

```text
1 + 2 + 3 + ... + N
```

C++ calcule exactement le même traitement.

Les deux temps sont mesurés avec :

```java
System.nanoTime()
```

Cette extension permet d’observer que JNI permet d’exécuter du code natif, mais que C++ n’est pas automatiquement plus rapide pour tous les traitements, car le passage Java vers natif possède aussi un coût.

---

## Tests réalisés

### Test 1 : Factoriel normal

Entrée :

```text
10
```

Résultat attendu :

```text
Résultat natif : 10! = 3628800
```

---

### Test 2 : Factoriel négatif

Entrée :

```text
-5
```

Résultat attendu :

```text
Erreur native -1 : le factoriel d’un nombre négatif est refusé.
```

---

### Test 3 : Dépassement de capacité

Entrée :

```text
20
```

Résultat attendu :

```text
Erreur native -2 : dépassement de la limite int détecté.
```

---

### Test 4 : Inversion de texte

Entrée :

```text
JNI is powerful
```

Résultat attendu :

```text
Texte inversé par C++ : lufrewop si INJ
```

---

### Test 5 : Somme du tableau

Entrée :

```text
10, 20, 30, 40, 50
```

Résultat attendu :

```text
Somme native du tableau : 150
```

---

### Test 6 : Tableau vide

Entrée :

```text

```

Résultat attendu :

```text
Somme native du tableau : 0
```

---

### Test 7 : Benchmark Java vs C++

Entrée :

```text
5000000
```

Calcul effectué :

```text
Somme de 1 à 5 000 000
```

Résultat attendu :

```text
Résultat Java = Résultat C++
Temps Java affiché
Temps C++ affiché
```

Le résultat numérique attendu est :

```text
12500002500000
```

---

## Vérification Logcat

Les fonctions natives écrivent des messages dans Logcat.

Tag utilisé :

```text
LAB22_NATIVE_BRIDGE
```

Exemples de logs attendus :

```text
nativeGreeting() appelee depuis Java
Factoriel calcule en natif
Texte inverse en natif
Somme calculee en natif
Benchmark natif termine
```

Ces logs permettent de vérifier que les fonctions C++ sont réellement exécutées.

---

## Erreurs fréquentes rencontrées

### UnsatisfiedLinkError

Cette erreur peut apparaître si :

- Le nom de la bibliothèque dans `System.loadLibrary()` est incorrect
- Le nom dans `CMakeLists.txt` ne correspond pas
- La signature JNI ne correspond pas au package Java
- Le fichier `.so` n’a pas été généré correctement

Dans ce projet :

```java
System.loadLibrary("secure_bridge");
```

doit correspondre à :

```cmake
add_library(
        secure_bridge
        SHARED
        native-lib.cpp
)
```

---

### Erreur de signature JNI

La signature C++ dépend du package Java.

Package utilisé :

```java
package ma.ensa.mobile.jnibridge;
```

Donc les fonctions natives commencent par :

```cpp
Java_ma_ensa_mobile_jnibridge_MainActivity_
```

Si le package change, les signatures natives doivent aussi être modifiées.

---

### Erreur de ressources Android

Des erreurs de type `resource color not found` peuvent apparaître si une couleur utilisée dans un thème ou un layout n’existe pas dans `colors.xml`.

Pour éviter cela, toutes les couleurs nécessaires au thème Android et au design personnalisé ont été définies dans `colors.xml`.

---

## Ce que ce laboratoire m’a permis de comprendre

Ce laboratoire m’a permis de comprendre :

- Comment Android peut appeler du code C++ grâce à JNI
- Comment déclarer des méthodes natives dans une activité Java
- Comment compiler une bibliothèque native avec CMake
- Comment charger une bibliothèque `.so` avec `System.loadLibrary`
- Comment échanger des données entre Java et C++
- Comment manipuler des chaînes et des tableaux via JNI
- Comment gérer les erreurs côté natif
- Comment utiliser Logcat pour suivre l’exécution du code C++
- Pourquoi JNI doit être utilisé avec méthode et pas systématiquement

---

## Conclusion

Ce laboratoire permet de comprendre le fonctionnement de JNI dans une application Android réelle.

L’application **JNIDemo** montre comment Java peut communiquer avec du code C++ natif pour exécuter différents traitements : message natif, factoriel, inversion de texte, somme de tableau et benchmark.

L’extension benchmark montre que JNI n’est pas seulement une technique d’appel de fonctions natives, mais aussi un moyen d’étudier les performances entre Java et C++. Cependant, elle montre aussi que C++ n’est pas automatiquement plus rapide dans tous les cas, car les transitions entre Java et le code natif ont un coût.

Ce lab constitue une base solide pour des projets Android plus avancés utilisant du code natif, comme :

- Traitement d’image
- Chiffrement natif
- Intégration de bibliothèques C/C++
- Détection anti-debug
- Sécurité applicative Android
- Optimisation de calculs lourds
