# CloudEdge4Tasker-lite

Projet minimal inspiré de CloudEdge4Tasker — livré avec un workflow GitHub Actions pour builder un APK debug automatiquement.

## Étapes pour obtenir un APK via GitHub Actions

1. Crée un nouveau repository GitHub (privé ou public).
2. Clone ce repo localement, ou upload le ZIP fourni.
3. Pousse le repo sur GitHub (push vers `main`).
4. Dans GitHub, va dans `Actions` → tu verras le workflow `android-build.yml` lancer un build. Une fois terminé, l'APK `app/build/outputs/apk/debug/app-debug.apk` sera disponible en tant qu'artifact.

## Personnalisation

- Remplace `CloudEdgeApi.kt` par une implémentation réelle (Retrofit ou SDK Meari) pour gérer login, lister caméras, commandes.
- Configure DataStore / Secure Storage pour stocker le token de manière sécurisée.
- Ajuste le `AndroidManifest.xml` et les permissions si tu veux ajouter accès fichier, notifications, etc.

## Notes de sécurité

- Utilise un compte CloudEdge secondaire pour éviter d'exposer ton compte principal.
- Ne laisse pas le repo public si tu commit des credentials.

