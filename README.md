# Chimera Test Engine

Native Kotlin/Android foundation for the custom Chimera VRM/body/world engine.

## Included
- Android/Kotlin project
- Gradle build configuration
- GitHub Actions `.github/workflows/build.yml`
- Native OpenGL ES engine-owned rendering boundary (no Three.js/WebGL)
- Deterministic body state and action-marker test path
- Gothic bedroom test-world model: bed, chair, desk, nightstand, window
- GLB container / VRM metadata inspector foundation
- VRM file picker

## Build in Termux
```bash
pkg update -y
pkg install -y openjdk-17 gradle unzip git
cd ~/downloads
unzip -o Test-engine.zip
cd Test-engine
./gradlew assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

## Important
This is the engine foundation/milestone, not a finished VRM renderer. The architecture is deliberately separated so the next layers can add actual glTF skinning, VRM 0.x/1.0 humanoid mapping, expressions, spring bones, materials, collision, and motor control without replacing the body/world runtime.
