# Build Report — InvestigaWarma v1.0.0

**Fecha de generación:** 18 de agosto de 2026 (UTC)

## Resumen honesto

**COMPILACIÓN NO VERIFICADA.**

El entorno usado para generar este proyecto es un contenedor Linux con JDK 21 y Gradle 8.14.3 instalados, pero:

- No tiene el **Android SDK** instalado (no existe `ANDROID_HOME`, no hay `platform-tools`, `build-tools` ni ninguna versión de plataforma).
- No tiene acceso de red a `dl.google.com`, `maven.google.com`, `repo.maven.apache.org` ni al Gradle Plugin Portal. Se comprobó explícitamente: las peticiones a esos hosts devuelven `403 host_not_allowed` o fallan la conexión.

Por lo tanto, **no fue posible** resolver el Android Gradle Plugin, Compose, Room, KSP ni ninguna otra dependencia, y en consecuencia no fue posible ejecutar `./gradlew clean/testDebugUnitTest/lintDebug/assembleDebug` con éxito. Esto se declara explícitamente en vez de simular un resultado, según la regla de honestidad de la especificación del proyecto.

## Stack utilizado (declarado, no verificado en ejecución)

Kotlin 1.9.24 · AGP 8.3.2 · KSP 1.9.24-1.0.20 · Gradle Wrapper 8.6 · Compose BOM 2024.06.00 · Compose Compiler 1.5.14 · Material 3 1.2.1 · Navigation Compose 2.7.7 · Room 2.6.1 · kotlinx-coroutines 1.8.1 · kotlinx-serialization-json 1.6.3 · JDK 17 · compileSdk/targetSdk 34 · minSdk 24.

## Comandos ejecutados y resultado real

### `gradle wrapper` (generación del wrapper, sin descargar el binario)

Estado: **ÉXITO**. Se generaron `gradlew`, `gradlew.bat` y `gradle/wrapper/gradle-wrapper.jar` usando la instalación local de Gradle 8.14.3, con `validateDistributionUrl = false` para evitar el chequeo de red durante la propia generación del wrapper.

### `./gradlew clean`

Estado: **NO EJECUTADO CON ÉXITO**. `./gradlew` intenta primero descargar la distribución Gradle 8.6 declarada en `gradle-wrapper.properties` (`https://services.gradle.org/distributions/gradle-8.6-bin.zip`), lo cual falla:

```
Exception in thread "main" java.io.IOException: Unable to tunnel through proxy. Proxy returns "HTTP/1.1 403 Forbidden"
```

Se intentó una alternativa usando la instalación local de Gradle 8.14.3 directamente (`gradle --offline tasks`), que sí arranca pero falla al resolver los plugins declarados en `build.gradle.kts`:

```
* What went wrong:
Plugin [id: 'com.android.application', version: '8.3.2', apply: false] was not found in any of the following sources:
- Gradle Core Plugins (plugin is not in 'org.gradle' namespace)
- Included Builds (No included builds contain this plugin)
- Plugin Repositories (could not resolve plugin artifact 'com.android.application:com.android.application.gradle.plugin:8.3.2')
```

Log completo guardado en `build_logs/gradle_tasks_attempt.log`.

### `./gradlew testDebugUnitTest`

Estado: **NO EJECUTADO** (bloqueado por lo anterior: sin resolución de plugins no hay tareas de test disponibles).

Sí se implementaron **78 métodos de prueba** (anotación `@Test`) en 8 archivos:

| Archivo | Pruebas | Tipo |
|---|---|---|
| `domain/LevelCalculatorTest.kt` | 13 | JVM pura |
| `domain/HypothesisValidatorTest.kt` | 9 | JVM pura |
| `domain/PlantSimulatorEngineTest.kt` | 8 | JVM pura |
| `domain/MovementSimulatorEngineTest.kt` | 7 | JVM pura |
| `domain/TemperatureSimulatorEngineTest.kt` | 7 | JVM pura |
| `domain/ChallengeEvaluatorTest.kt` | 13 | JVM pura |
| `domain/BadgeRulesTest.kt` | 12 | JVM pura |
| `data/DatabaseSeederTest.kt` | 7 | Room en memoria + Robolectric |
| **Total** | **76** (+2 pruebas auxiliares de progresión de nivel dentro de los mismos archivos) | |

Estas pruebas son código Kotlin real y sintácticamente revisado a mano (no se pudo compilar con `kotlinc` porque tampoco hay un compilador Kotlin standalone disponible en este entorno), pero **su ejecución no está verificada**. Tests aprobados: **desconocido (no ejecutados)**. Tests fallidos: **desconocido (no ejecutados)**.

### `./gradlew lintDebug`

Estado: **NO EJECUTADO** (mismo bloqueo de resolución de dependencias).

### `./gradlew assembleDebug`

Estado: **NO EJECUTADO**. No existe `app/build/outputs/apk/debug/app-debug.apk`.

### APK

**No generado.** No hay archivo `deliverables/InvestigaWarma-v1.0.0.apk` ni hash SHA-256 asociado, porque no se pudo compilar. Cualquier valor que se presentara aquí sería inventado, y la especificación del proyecto prohíbe explícitamente simular resultados.

### PDF

| Documento | Estado | Páginas | Tamaño |
|---|---|---|---|
| `docs/pdf/MEMORIA_DESCRIPTIVA.pdf` | Generado y validado | 6 | 66 793 bytes |
| `docs/pdf/MANUAL_USUARIO.pdf` | Generado y validado | 4 | 47 920 bytes |
| `docs/pdf/MANUAL_TECNICO.pdf` | Generado y validado | 4 | 55 518 bytes |

Validación realizada con `pdfinfo` y `pdftotext`: cabecera `%PDF-1.4` correcta en los tres, tamaño de página A4, múltiples páginas, tablas y encabezados numerados presentes en el texto extraído, y caracteres españoles (á, é, í, ñ, ó) confirmados en el contenido extraído de cada PDF. Generados con `pandoc` + `wkhtmltopdf`, ambos ejecutados localmente sin red.

## Código fuente generado (verificación estática, no compilación)

- **105** archivos Kotlin, **6 902** líneas de código en `app/src/`.
- Verificación automática de balance de llaves/paréntesis sobre los 97 archivos de `main/`: **0 problemas**.
- Verificación automática de que el `package` de cada archivo coincide con su ruta de carpeta: **0 discrepancias**.
- Búsqueda de `TODO()`, `NotImplementedError()` y "Próximamente": **0 coincidencias** (ninguna función declarada como implementada quedó como placeholder).
- Búsqueda de declaraciones de clase/objeto duplicadas entre archivos: **0 duplicados**.

Estas verificaciones no sustituyen una compilación real, pero descartan la clase de errores más comunes (desbalance de sintaxis, paquete mal ubicado, placeholders olvidados, colisión de nombres) sin necesidad de un compilador Kotlin.

## Cómo obtener una compilación real

1. **Recomendado:** hacer `git push` de este repositorio a GitHub. El workflow `.github/workflows/android-build.yml` ejecuta exactamente `clean → testDebugUnitTest → lintDebug → assembleDebug` en `ubuntu-latest`, con acceso real a Internet y Android SDK preinstalado, y publica el APK y los reportes como artefactos del workflow.
2. **Alternativa local:** abrir el proyecto en Android Studio (con SDK Manager configurado) en una máquina con acceso a Internet, o ejecutar los mismos comandos Gradle desde una terminal con esos mismos requisitos.

## Limitaciones reales declaradas

- Sin Android SDK local.
- Sin acceso de red a repositorios de dependencias (Google Maven, Maven Central, Gradle Plugin Portal, npm, PyPI también bloqueados por la misma política de red del entorno).
- Sin compilador Kotlin standalone (`kotlinc`) disponible para al menos verificar el módulo de dominio puro de forma aislada.
- En consecuencia: 0 tareas Gradle de compilación, prueba, lint o ensamblado pudieron ejecutarse con éxito en este entorno.

No se inventaron resultados de compilación, pruebas, lint ni APK en ningún documento de este proyecto.
