# Calculator (Android)

Simple Kotlin calculator app: + − × ÷ %, decimal, backspace, live preview, BODMAS support.

## APK kaise milegi (GitHub Actions se)
1. Is project ko GitHub repo me push karo (branch `main`).
2. Repo ke **Actions** tab me **Build APK** workflow chalega.
3. Workflow khatam hone par run page ke neeche **Artifacts → calculator-debug-apk** download karo.
4. Zip extract karke `app-debug.apk` phone me install karo.

Release ke liye tag push karo: `git tag v1.0 && git push origin v1.0` -> APK Releases me aa jayegi.

## Local build
Android Studio me open karo, ya: `gradle assembleDebug`
