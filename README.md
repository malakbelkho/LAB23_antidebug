# NativeGuard – LAB 23

Application Android développée en **Java** avec intégration de code natif **C++** via **JNI (Java Native Interface)**.

Ce laboratoire prolonge le Lab 22 sur JNI en ajoutant une couche de protection native **anti-debug**.  
L’objectif est de détecter certains signaux suspects d’exécution, comme la présence d’un débogueur ou de bibliothèques souvent associées à l’instrumentation dynamique.

L’interface a été personnalisée sous le nom **Native Guard Console**, avec un design moderne, coloré, interactif et structuré en cartes.

---

## Objectif

Le but de ce laboratoire est de :

- Réutiliser un projet Android avec support JNI et C++
- Ajouter une couche défensive native dans `native-lib.cpp`
- Comprendre le rôle de **JNI**, **NDK**, **CMake** et des bibliothèques `.so`
- Détecter certains signaux de debug ou d’analyse dynamique
- Lire des informations système comme `/proc/self/status`
- Inspecter `/proc/self/maps` pour rechercher des signatures suspectes
- Remonter un état de sécurité natif vers Java
- Adapter l’interface Android selon le résultat de l’analyse
- Bloquer certaines fonctions sensibles en cas d’environnement suspect
- Journaliser les événements natifs dans **Logcat**

---

## Description de l’application

L’application contient une seule activité principale qui combine :

- Une console d’état de sécurité
- Une analyse native anti-debug
- Les fonctionnalités JNI du Lab 22
- Un blocage logique des fonctions sensibles si un signal suspect est détecté

L’application peut fonctionner dans deux états :

### Mode normal

Si aucun signal suspect n’est détecté :

- L’état affiché est **environnement fiable**
- Le code natif retourné est `0`
- Les fonctions natives restent accessibles
- L’utilisateur peut tester :
  - Le factoriel natif
  - L’inversion de texte
  - La somme d’un tableau
  - Le benchmark Java vs C++

### Mode restreint

Si un signal suspect est détecté :

- L’état affiché est **environnement suspect**
- Le code natif retourné peut être `1`, `2` ou `3`
- Les fonctions sensibles sont bloquées
- L’application ne crash pas, mais limite son comportement de manière contrôlée

---

## Fonctionnement général

Le fonctionnement de l’application suit ce flux :

```text
MainActivity.java
        ↓
Appel des méthodes natives JNI
        ↓
Chargement de la bibliothèque secure_bridge
        ↓
Exécution des contrôles anti-debug en C++
        ↓
Retour d’un code d’état à Java
        ↓
Adaptation de l’interface Android
        ↓
Autorisation ou blocage des fonctions sensibles
```

---

## Fonctionnalités

- Analyse native de l’environnement d’exécution
- Affichage d’un statut visuel :
  - Vert : environnement fiable
  - Rouge : environnement suspect
- Bouton pour relancer l’analyse native
- Retour d’un code d’état depuis C++
- Blocage logique des fonctions sensibles en cas de détection
- Affichage d’un message natif généré côté C++
- Calcul du factoriel côté natif
- Inversion d’une chaîne de caractères côté natif
- Somme d’un tableau d’entiers côté natif
- Benchmark Java vs C++ :
  - Calcul de la somme de `1` à `N`
  - Mesure du temps Java
  - Mesure du temps C++
- Logs natifs visibles dans Logcat
- Interface moderne avec :
  - Fond en dégradé
  - Cartes arrondies
  - Boutons colorés
  - États visuels de sécurité
  - Champs de saisie personnalisés

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
- Logcat
- API minimum : 24 Android 7.0

---

## Aperçu de l’application

▶️ Deux vidéos de démonstration sont disponibles dans le dossier **Demo** du repository :

- Une vidéo montrant l’application lancée normalement
- Une vidéo montrant l’application en mode debug

⚠️ En cas de problème de lecture depuis le repository :

👉 [▶️ Voir les démonstrations sur Google Drive](https://drive.google.com/drive/folders/1BmzD28JzH7GD-vd2ovQ_52NOFOnzmFWu?usp=sharing)

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
│   │   │   │   │   ├── bg_jni_button_alt.xml
│   │   │   │   │   ├── bg_security_safe.xml
│   │   │   │   │   ├── bg_security_alert.xml
│   │   │   │   │   ├── bg_security_neutral.xml
│   │   │   │   │   └── bg_guard_scan_button.xml
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
│   ├── demo_run_normal.mp4
│   └── demo_debug_mode.mp4
│
└── README.md
```

---

## Détails des fichiers principaux

### MainActivity.java

Ce fichier représente l’activité principale de l’application.

Il contient :

- La déclaration des méthodes natives
- Le chargement de la bibliothèque C++
- La liaison entre le layout XML et le code Java
- Le lancement de l’analyse de sécurité native
- L’affichage du résultat de sécurité
- L’activation ou la désactivation des fonctions sensibles
- La gestion des boutons
- L’appel des fonctions JNI du Lab 22

Méthodes natives principales :

```java
private native boolean isDebugDetected();
private native int nativeSecurityCode();
private native String nativeSecuritySummary();

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

Cette instruction charge la bibliothèque native générée à partir du code C++.

---

### native-lib.cpp

Ce fichier contient toute la logique native C++.

Il contient deux types de fonctions :

- Les fonctions de sécurité anti-debug
- Les fonctions JNI héritées du Lab 22

La logique anti-debug vérifie principalement :

- La valeur de `TracerPid` dans `/proc/self/status`
- Les signatures suspectes dans `/proc/self/maps`

Le fichier utilise Logcat côté natif grâce à :

```cpp
#include <android/log.h>
```

Tag utilisé dans Logcat :

```text
LAB23_NATIVE_GUARD
```

---

### CMakeLists.txt

Ce fichier configure la compilation native avec CMake.

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
- Un header principal
- Une carte d’état de sécurité
- Un bouton pour relancer l’analyse native
- Une zone de message natif
- Une carte pour le factoriel
- Une carte pour l’inversion de texte
- Une carte pour la somme du tableau
- Une carte pour le benchmark Java vs C++

Composants principaux :

```text
cardSecurityStatus
txtSecurityTitle
txtSecurityDetails
btnSecurityScan

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

- Les couleurs du fond général
- Les couleurs du header
- Les couleurs des cartes
- Les couleurs des boutons
- Les couleurs des états de sécurité
- Les couleurs des champs de saisie
- Les couleurs des résultats

---

### Design res/drawable

Les fichiers `drawable` personnalisent l’apparence de l’application.

#### bg_jni_screen.xml

Fond principal de l’application avec un dégradé foncé.

#### bg_jni_header.xml

Header principal avec un dégradé bleu, violet et cyan.

#### bg_jni_card.xml

Carte standard utilisée pour les fonctionnalités JNI.

#### bg_jni_chip.xml

Badge indiquant le numéro du laboratoire.

#### bg_jni_input.xml

Fond personnalisé des champs de saisie.

#### bg_jni_result.xml

Fond utilisé pour afficher les résultats.

#### bg_jni_button.xml

Bouton principal avec dégradé orange et rose.

#### bg_jni_button_alt.xml

Bouton secondaire avec dégradé bleu et turquoise.

#### bg_security_safe.xml

Carte verte affichée lorsque l’environnement est fiable.

#### bg_security_alert.xml

Carte rouge affichée lorsqu’un signal suspect est détecté.

#### bg_security_neutral.xml

Carte neutre affichée avant ou pendant l’analyse.

#### bg_guard_scan_button.xml

Bouton de relance de l’analyse native.

---

## Logique anti-debug native

### 1. Détection via TracerPid

L’application lit le fichier :

```text
/proc/self/status
```

Puis recherche la ligne :

```text
TracerPid
```

Principe :

```text
TracerPid = 0
→ aucun débogueur attaché

TracerPid > 0
→ un processus de trace ou de debug est détecté
```

Cette méthode permet d’obtenir un signal simple sur l’état de debug du processus.

---

### 2. Inspection de /proc/self/maps

L’application lit le fichier :

```text
/proc/self/maps
```

Ce fichier contient les régions mémoire et certaines bibliothèques chargées dans le processus courant.

La couche native recherche des signatures comme :

```text
frida
xposed
substrate
gdbserver
libgdb
lldb
magisk
zygisk
```

Si une de ces chaînes est trouvée, l’application considère qu’un outil d’analyse ou d’instrumentation peut être présent.

---

## Codes d’état de sécurité

La couche native retourne un code permettant à Java d’adapter le comportement de l’application :

| Code | Signification |
|---|---|
| `0` | Environnement normal, aucune anomalie détectée |
| `1` | Signal de debug ou de trace détecté |
| `2` | Bibliothèque ou outil suspect détecté dans `/proc/self/maps` |
| `3` | Plusieurs signaux suspects détectés simultanément |

Dans la démonstration, les cas les plus facilement observables sont :

- `0` lors d’un lancement normal
- `1` lorsqu’un signal de debug ou de trace est détecté

Les codes `2` et `3` sont prévus dans la logique native, mais ils nécessitent un environnement contenant réellement des bibliothèques ou outils d’instrumentation détectables.  
Le projet ne cherche pas à installer ces outils pour forcer la détection. Ils sont donc documentés comme cas prévus par le mécanisme défensif.

---

## Comportement selon l’état détecté

### Code 0 : environnement fiable

Lorsque le code retourné est `0` :

- La carte de sécurité devient verte
- Le message indique que l’environnement est fiable
- Les fonctions natives sensibles sont autorisées
- L’utilisateur peut utiliser toutes les fonctionnalités

Exemple d’affichage :

```text
État sécurité : environnement fiable
Code natif : 0
Les fonctions natives sensibles sont autorisées.
```

---

### Code 1, 2 ou 3 : environnement suspect

Lorsque le code retourné est différent de `0` :

- La carte de sécurité devient rouge
- Le message indique un environnement suspect
- Les boutons des fonctions sensibles sont désactivés
- Le mode restreint est activé

Exemple d’affichage :

```text
État sécurité : environnement suspect
Code natif : 1
Mode restreint activé.
```

---

## Fonctions natives conservées du Lab 22

### 1. Message natif

```java
nativeGreeting()
```

Cette fonction retourne un message généré côté C++.

Résultat attendu :

```text
Zone native active : C++ repond via JNI.
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
Entrée : JNI Anti Debug
Sortie : gubeD itnA INJ
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

Cette fonction permet de comparer Java et C++ sur le même calcul.

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

Cette comparaison montre que JNI permet d’exécuter du code natif, mais que C++ n’est pas automatiquement plus rapide dans tous les cas, car le passage Java vers C++ possède aussi un coût.

---

## Tests réalisés

### Test 1 : lancement normal

Action :

```text
Run app
```

Résultat attendu :

```text
État sécurité : environnement fiable
Code natif : 0
Fonctions natives autorisées
```

Objectif :

```text
Vérifier que l’application peut fonctionner normalement lorsque l’environnement ne présente pas de signal suspect.
```

---

### Test 2 : relance de l’analyse native

Action :

```text
Cliquer sur Relancer l’analyse native
```

Résultat attendu :

```text
Le statut de sécurité est mis à jour selon le résultat renvoyé par le C++.
```

Objectif :

```text
Montrer que l’analyse est exécutée côté natif à la demande.
```

---

### Test 3 : factoriel normal

Entrée :

```text
10
```

Résultat attendu :

```text
Résultat natif : 10! = 3628800
```

---

### Test 4 : factoriel négatif

Entrée :

```text
-5
```

Résultat attendu :

```text
Erreur native -1 : nombre négatif refusé.
```

---

### Test 5 : dépassement de capacité

Entrée :

```text
20
```

Résultat attendu :

```text
Erreur native -2 : dépassement de la limite int.
```

---

### Test 6 : inversion de texte

Entrée :

```text
JNI Anti Debug
```

Résultat attendu :

```text
Texte inversé par C++ : gubeD itnA INJ
```

---

### Test 7 : somme du tableau

Entrée :

```text
10, 20, 30, 40, 50
```

Résultat attendu :

```text
Somme native du tableau : 150
```

---

### Test 8 : benchmark Java vs C++

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
Résultat Java : 12500002500000
Résultat C++ : 12500002500000
Temps Java affiché
Temps C++ affiché
```

---

### Test 9 : lancement en mode debug

Action :

```text
Debug app
```

Résultat possible :

```text
État sécurité : environnement suspect
Code natif : 1
Mode restreint activé
```

Objectif :

```text
Montrer que l’application peut détecter un signal de debug ou de trace, puis bloquer les fonctions sensibles.
```

Remarque :

```text
Le comportement peut varier selon l’émulateur, Android Studio et le mode de lancement. Les protections anti-debug simples ne garantissent pas une détection parfaite dans tous les environnements.
```

---

## Vérification Logcat

Les fonctions natives écrivent des messages dans Logcat.

Tag utilisé :

```text
LAB23_NATIVE_GUARD
```

Exemples de logs attendus :

```text
TracerPid = 0 : aucun debugger attache
Inspection maps : aucune signature suspecte detectee
Etat securite : environnement OK
Factoriel natif calcule
Somme native calculee
Benchmark natif termine
```

En cas d’environnement suspect :

```text
Debug detecte via TracerPid
Etat securite : trace/debug detecte
Mode restreint active
```

Ces logs permettent de vérifier que les contrôles sont réellement exécutés dans le code C++ natif.

---

## Erreurs fréquentes rencontrées

### Faux positif avec ptrace

Au début, le contrôle basé sur :

```cpp
ptrace(PTRACE_TRACEME, 0, nullptr, nullptr);
```

pouvait provoquer un faux positif, car il modifiait lui-même l’état du processus.

Pour stabiliser le comportement du TP, la logique a été adaptée en utilisant principalement :

```text
TracerPid dans /proc/self/status
```

Principe :

```text
TracerPid = 0
→ aucun débogueur attaché

TracerPid > 0
→ signal de debug ou de trace détecté
```

---

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

## Limites de l’approche

Cette protection reste pédagogique et ne rend pas l’application inviolable.

Limites importantes :

- La détection peut varier selon l’environnement
- Des faux positifs peuvent apparaître
- Certains outils peuvent contourner ou masquer leur présence
- Un seul signal ne suffit pas pour garantir une détection complète
- Plus la protection est complexe, plus la maintenance devient difficile

L’objectif du laboratoire n’est donc pas de créer une protection parfaite, mais de comprendre comment structurer une couche native défensive dans une application Android.

---

## Ce que ce laboratoire m’a permis de comprendre

Ce laboratoire m’a permis de comprendre :

- Comment Android peut appeler du code C++ grâce à JNI
- Comment structurer une couche native défensive
- Comment lire des informations système comme `/proc/self/status`
- Comment inspecter `/proc/self/maps`
- Comment retourner un code d’état natif vers Java
- Comment adapter l’interface selon un résultat natif
- Comment bloquer certaines fonctions sensibles sans faire crasher l’application
- Comment utiliser Logcat pour suivre les contrôles C++
- Pourquoi JNI doit être utilisé avec méthode
- Pourquoi une protection anti-debug simple reste limitée

---

## Conclusion

Ce laboratoire constitue une extension directe du Lab 22 sur JNI.

L’application **NativeGuard** montre comment une application Android peut utiliser une bibliothèque native C++ pour réaliser des contrôles défensifs simples.  
Le code C++ analyse certains signaux comme `TracerPid` et `/proc/self/maps`, puis retourne un état de sécurité à Java.

Selon cet état, l’application affiche un environnement fiable ou suspect et autorise ou bloque certaines fonctions sensibles.

Ce projet montre que JNI peut être utilisé pour déplacer une partie de la logique sensible dans une couche native, plus difficile à analyser que du code Java classique.  
Cependant, cette approche reste une couche de durcissement pédagogique, et non une protection parfaite.

Ce lab constitue une base solide pour des projets Android plus avancés liés à :

- Sécurité applicative mobile
- Anti-debugging
- Détection d’instrumentation
- Durcissement Android
- Intégration de logique sensible en C++
- Analyse et défense mobile
