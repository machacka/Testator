# CloudEdge Motion Toggle App

Petite application Android (Kotlin) qui **active/désactive la détection de mouvement sur toutes les caméras CloudEdge** via un client *à intégrer* (inspiré de [CloudEdge4Tasker]).
Elle inclut **Google Sign-In pour l’application** (auth locale), un **workflow GitHub Actions** pour compiler automatiquement l’APK et une **UI minimale** (2 boutons).

> ⚠️ **Limitation importante :** à ce jour CloudEdge/Meari **ne publie pas d’API officielle** permettant un login via Google et le contrôle des caméras depuis une app tierce.  
> Le projet [CloudEdge4Tasker] utilise le **SDK Meari** et un **compte CloudEdge secondaire** afin de piloter les caméras (et contourner la restriction de session unique).  
> Cette app fournit donc un **client stub** à remplacer par une **implémentation réelle** basée sur le SDK Meari (ou votre propre reverse‑engineering).

- Références :
  - CloudEdge4Tasker : https://github.com/SimoneAvogadro/CloudEdge4Tasker  
  - CloudEdge (Play Store) : https://play.google.com/store/apps/details?id=com.cloudedge.smarteye

## Fonctionnalités

- Boutons **Activer** / **Désactiver** la détection (toutes caméras).
- **Google Sign‑In (One Tap)** pour s’authentifier *dans l’app* (⚠️ ne remplace pas le login CloudEdge ; servez‑vous‑en pour protéger l’accès aux actions).
- **Workflow GitHub Actions** prêt à l’emploi pour builder l’APK.

## Intégrer le contrôle CloudEdge réel

1. Récupérez le SDK/logiciel utilisé par [CloudEdge4Tasker] et implémentez `CloudEdgeClient` :
   - Échange d’identifiants **e‑mail/mot de passe** du **compte CloudEdge secondaire** (recommandé par CloudEdge4Tasker).
   - Méthodes à implémenter :
     - `enableMotionAll()`
     - `disableMotionAll()`
2. Partagez vos caméras depuis le compte principal vers ce **compte secondaire** (sinon les actions peuvent déconnecter l’app officielle).  
3. Remplacez `CloudEdgeClientStub` par votre implémentation (ex. `CloudEdgeClientMeariSdk`).

> ℹ️ Si CloudEdge publie un jour un *Exchange* OAuth (Google ID Token → jeton CloudEdge), branchez‑le dans `connectWithGoogleIdToken()`.

## Google Sign‑In (à configurer)

- Créez un **Client OAuth 2.0** de type **Web** dans Google Cloud Console.
- Remplacez `server_client_id_placeholder` (voir `app/src/main/res/values/ids.xml`).
- Ajoutez votre SHA‑1 debug/release si vous activez la vérification côté serveur.

## CI GitHub Actions

- Push du repo → build automatique de `app-debug.apk` et artefact téléchargeable.
- Fichier : `.github/workflows/android.yml`

## Lancer localement

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Arborescence

```
CloudEdgeMotionToggleApp/
 ├─ app/
 │  ├─ src/main/java/com/example/cloudedge/
 │  │   ├─ core/CloudEdgeClient.kt
 │  │   └─ ui/MainActivity.kt
 │  ├─ src/main/res/layout/activity_main.xml
 │  ├─ src/main/res/values/strings.xml
 │  ├─ src/main/res/values/colors.xml
 │  ├─ src/main/res/values/themes.xml
 │  ├─ src/main/res/values/ids.xml   # <-- Client ID Google à remplacer
 │  └─ AndroidManifest.xml
 ├─ .github/workflows/android.yml
 ├─ build.gradle.kts (racine)
 ├─ settings.gradle.kts
 └─ README.md
```

---

**Note conformité** : cette app n’envoie aucune donnée à CloudEdge tant que vous n’avez pas remplacé le client stub. Respectez les CGU CloudEdge/Meari et la législation en vigueur.

[CloudEdge4Tasker]: https://github.com/SimoneAvogadro/CloudEdge4Tasker
