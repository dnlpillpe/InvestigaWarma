# InvestigaWarma — Academia de Jóvenes Científicos

Aplicación Android educativa (offline, sin anuncios, sin cuentas) para niños de 8 a 12 años, donde el jugador se convierte en joven investigador: explora una Academia Científica dividida en zonas, cumple misiones que siguen el método científico completo (observar → preguntar → hipotetizar → experimentar → analizar → descubrir), realiza experimentos reales con motores de simulación propios, resuelve minijuegos de investigación, lleva un diario científico (texto y voz) y construye su Museo Científico Personal con coleccionables e insignias.

## Estado del proyecto

Código fuente completo e implementado: arquitectura MVVM + Repository, Room real (15 entidades), 40 misiones / 30 experimentos / 50 desafíos / 20 coleccionables / 15 insignias de contenido semilla, UI completa en Jetpack Compose, grabación de voz real (MediaRecorder), 76 pruebas unitarias.

**Compilación:** no verificada en el entorno donde se generó este proyecto, por falta de Android SDK y de acceso de red a los repositorios de Gradle/Google/Maven. Ver [`docs/BUILD_REPORT.md`](docs/BUILD_REPORT.md) para el detalle honesto y el log real del intento de build. El repositorio incluye un workflow de GitHub Actions (`.github/workflows/android-build.yml`) que compila, prueba y genera el APK automáticamente con acceso real a Internet en cuanto se haga `git push`.

## Cómo compilar

Requisitos: JDK 17, Android SDK (compileSdk 34, minSdk 24), conexión a Internet la primera vez (para resolver dependencias).

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Estructura del proyecto

```
app/                    Código fuente Android (Kotlin, Jetpack Compose, Room)
database/               schema.sql y sample_data.sql de referencia
docs/                   Documentación completa + PDFs
tools/                  Scripts usados para generar datos semilla e iconos
deliverables/           APK, ZIP del código fuente y PDFs finales
.github/workflows/      CI de GitHub Actions (build real con acceso a Internet)
```

## Documentación

- [Memoria descriptiva](docs/MEMORIA_DESCRIPTIVA.md)
- [Manual de usuario](docs/MANUAL_USUARIO.md)
- [Manual técnico](docs/MANUAL_TECNICO.md)
- [Base de datos](docs/BASE_DE_DATOS.md)
- [Reporte de build](docs/BUILD_REPORT.md)

## Privacidad

InvestigaWarma no usa Internet, no tiene backend, no recopila datos personales y no solicita nombre real, email, teléfono ni ubicación. El perfil del jugador usa solo un alias y un avatar local. El micrófono solo se activa bajo demanda del jugador, con permiso explícito, y toda la app permanece funcional si el permiso se deniega.
