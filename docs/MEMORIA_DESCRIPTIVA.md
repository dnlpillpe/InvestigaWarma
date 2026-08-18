# Memoria Descriptiva — InvestigaWarma: Academia de Jóvenes Científicos

Versión 1.0.0 · Fecha de redacción: 18 de agosto de 2026

## 1. Identificación

| Campo | Valor |
|---|---|
| Nombre del proyecto | InvestigaWarma |
| Nombre comercial | InvestigaWarma: Academia de Jóvenes Científicos |
| Plataforma | Android nativo (Kotlin, Jetpack Compose) |
| Paquete de aplicación | `com.investigawarma.app` |
| Versión | 1.0.0 |
| Público objetivo | Niños y niñas de 8 a 12 años |
| Tipo de producto | Aplicación educativa offline de complejidad media |

## 2. Problema

Gran parte del software educativo dirigido a la enseñanza de ciencias en edad escolar reduce el método científico a cuestionarios de opción múltiple o a fichas digitales que imitan el papel. Esto no refleja cómo se investiga realmente: observar, formular preguntas propias, construir hipótesis, diseñar y ejecutar experimentos, analizar resultados y comunicar descubrimientos. El resultado suele ser una experiencia que se siente como una tarea escolar más, sin la curiosidad ni la sensación de progreso que mantendría a un niño volviendo a usar la aplicación.

## 3. Justificación

Los niños de 8 a 12 años ya son capaces de sostener sistemas de progreso, comparar resultados, gestionar colecciones y seguir historias con cierta complejidad. Una aplicación que aproveche esa capacidad —convirtiendo el método científico en un ciclo de juego real (misión, exploración, interacción, feedback, recompensa, desbloqueo)— puede enseñar pensamiento crítico y competencias científicas de forma más efectiva y memorable que un cuestionario tradicional, sin sacrificar rigor pedagógico.

## 4. Objetivo general

Desarrollar una aplicación Android educativa, offline y con identidad visual propia, que enseñe las etapas del método científico (observación, formulación de preguntas, hipótesis, experimentación, análisis y comunicación de resultados) a niños de 8 a 12 años mediante una experiencia de juego real, con progreso persistente y contenido suficiente para múltiples sesiones de uso.

## 5. Objetivos específicos

- Implementar un ciclo de juego completo (misión → explorar → observar → preguntar → hipotetizar → experimentar → analizar → descubrir → desbloquear) para cada una de las 40 misiones iniciales.
- Construir simuladores científicos reales (planta, movimiento, temperatura) cuyo resultado se calcule a partir de las variables elegidas por el jugador, no de texto fijo.
- Implementar cinco tipos de minijuegos de investigación (detective, ordenar, patrones, clasificar, construir) con evaluación real de la respuesta.
- Persistir de forma real, mediante Room, todo el progreso del jugador: misiones, hipótesis, experimentos, desafíos, diario científico, colección e insignias.
- Implementar un diario científico con notas de texto y grabación de voz real (máx. 60 segundos), respetando la privacidad del menor.
- Garantizar el funcionamiento 100% offline y sin recolección de datos personales.
- Cubrir la lógica de dominio con una batería amplia de pruebas automáticas (76 pruebas).
- Documentar el proyecto de forma completa y honesta, incluyendo las limitaciones reales del entorno de desarrollo.

## 6. Público

Niños y niñas de 8 a 12 años que usan un dispositivo Android propio o familiar, con o sin supervisión adulta directa, en sesiones cortas (5 a 20 minutos). El diseño evita tanto la estética "de bebé" como el tono condescendiente: el lenguaje, los retos y la progresión están calibrados para que un niño de este rango se sienta capaz e inteligente, no tratado como principiante absoluto.

## 7. Alcance

La versión 1.0.0 incluye: onboarding de 4 pantallas, mapa principal ("Academia Científica") con 6 zonas, 40 misiones con el ciclo completo de 6 pasos cada una, 30 experimentos con motores de simulación reales (planta, movimiento, temperatura), 50 desafíos (10 de cada uno de los 5 tipos), diario científico con notas de texto y de voz, Museo Científico Personal con 20 coleccionables y 15 insignias, sistema de niveles (4 niveles) basado en XP real, estadísticas calculadas desde datos persistidos, y ajustes de sonido/vibración y reinicio de progreso.

## 8. Exclusiones

Quedan fuera de esta versión: conexión a Internet o servicios en la nube, autenticación o cuentas de usuario, publicidad, compras dentro de la app, comunicación entre menores, reconocimiento de voz o de imagen mediante servicios externos, soporte para tablets/orientación horizontal optimizada (funciona en vertical, orientación principal), y localización a idiomas distintos del español.

## 9. Requisitos funcionales

1. El jugador puede crear un perfil local (alias + avatar) sin datos personales identificables.
2. El jugador puede explorar 6 zonas temáticas desde un mapa visual central.
3. El jugador puede iniciar, continuar y completar misiones que seguirán siempre el ciclo de 6 pasos del método científico.
4. El jugador puede construir preguntas científicas con tarjetas (conector + tema).
5. El jugador puede construir hipótesis en formato SI/ENTONCES/PORQUE, validadas estructuralmente.
6. El jugador puede ejecutar experimentos reales modificando variables (luz, agua, tiempo; superficie, peso, fuerza; temperatura inicial, aislamiento, tiempo) y observar un resultado calculado.
7. El jugador puede resolver desafíos de 5 tipos distintos con evaluación real de la respuesta.
8. El sistema debe desbloquear automáticamente la siguiente misión de una zona al completar la actual.
9. El sistema debe otorgar XP y estrellas al completar una misión, y calcular el nivel del jugador a partir del XP acumulado.
10. El sistema debe desbloquear insignias y objetos de colección según reglas basadas en progreso real (no manualmente).
11. El jugador puede escribir notas en su diario científico y grabar notas de voz de hasta 60 segundos.
12. El sistema debe mostrar estadísticas reales (progreso por zona, precisión en desafíos, hipótesis válidas, etc.).
13. El jugador puede silenciar sonido y vibración, y reiniciar su progreso completo.
14. Si se deniega el permiso de micrófono, el resto de la aplicación debe seguir siendo completamente funcional.

## 10. Requisitos no funcionales

- **Offline:** ninguna función principal depende de red; no se declara el permiso INTERNET.
- **Privacidad:** no se solicitan datos personales identificables; los archivos de audio se guardan en almacenamiento privado de la app.
- **Rendimiento:** listas y flujos de datos usan Room + Flow/StateFlow para actualizaciones reactivas sin bloquear el hilo principal.
- **Accesibilidad:** `contentDescription` en elementos interactivos e ilustrativos relevantes, tamaños táctiles ≥48dp, estados que no dependen únicamente del color (icono + texto).
- **Mantenibilidad:** separación clara `data/domain/ui`, datos semilla generados desde un script versionado en `tools/`.
- **Estabilidad de versiones:** todas las dependencias fijadas a versiones estables concretas (sin `+` ni `latest`).

## 11. Casos de uso principales

- **Primer inicio:** el jugador ve el onboarding, crea su alias y avatar, y llega al mapa de la Academia.
- **Completar una misión:** el jugador entra a una zona, elige una misión disponible, atraviesa los 6 pasos, y recibe XP, estrellas y un descubrimiento.
- **Ejecutar un experimento:** dentro de una misión (o libremente en el Laboratorio), el jugador ajusta variables con sliders y ejecuta el simulador correspondiente.
- **Resolver un desafío:** el jugador entra a un desafío de una zona y lo resuelve según su tipo (detective, ordenar, patrones, clasificar, construir).
- **Registrar en el diario:** el jugador escribe una nota o graba una nota de voz sobre lo que descubrió.
- **Revisar el museo:** el jugador consulta qué objetos e insignias ha desbloqueado y qué le falta.
- **Consultar estadísticas:** el jugador entra al Centro de Datos y ve su progreso real en gráficos y números.
- **Ajustar preferencias:** el jugador silencia sonido/vibración o reinicia su progreso.

## 12. Pantallas

Onboarding (4 páginas), Mapa de la Academia (home), Zona (por cada una de las 6 zonas, con su lista de misiones y desafíos), Misión (flujo de 6 pasos: observar, preguntar, hipotetizar, experimentar, analizar, descubrir), Diario Científico, Museo Científico, Centro de Datos (estadísticas), Ajustes. Total: 8 pantallas principales más las subpantallas del flujo de misión, en línea con el rango de 8-12 módulos recomendado por la especificación.

## 13. Flujo de navegación

```
Onboarding → Mapa de la Academia (Home)
Home → Zona (1 de 6) → Misión → (vuelve a) Zona → Home
Home → Diario Científico → Home
Home → Museo Científico → Home
Zona "Centro de Datos" → Estadísticas → Zona
Home → Ajustes → (reinicio) → Onboarding
```

## 14. Arquitectura

MVVM + Repository, con inyección de dependencias manual mediante `AppContainer` (sin frameworks adicionales, según lo recomendado por la especificación maestra). Capas:

- `data/local`: entidades Room, DAOs, `AppDatabase`, `DatabaseSeeder`, convertidores.
- `data/repository`: acceso a datos para la capa de dominio/UI (`PlayerRepository`, `MissionRepository`, `ExperimentRepository`, `ChallengeRepository`, `CollectionRepository`, `JournalRepository`, `StatsRepository`).
- `domain/model`: enumeraciones y modelos de dominio (Zone, MissionStatus, PlayerLevel, payloads serializables).
- `domain/logic`: lógica pura testeable en JVM (cálculo de nivel, validación de hipótesis, motores de simuladores, evaluador de desafíos, reglas de insignias).
- `ui`: Jetpack Compose (navegación, pantallas, componentes reutilizables, tema) + ViewModels con `StateFlow`.

## 15. Modelo de datos

Room con 15 entidades reales (ver `docs/BASE_DE_DATOS.md` para el detalle completo): `PlayerProfileEntity`, `ScientificMissionEntity`, `MissionStepEntity`, `HypothesisEntity`, `ExperimentEntity`, `ExperimentParameterEntity`, `ExperimentResultEntity`, `ScientificDiscoveryEntity`, `CollectionItemEntity`, `BadgeEntity`, `ScientificJournalEntity`, `VoiceEntryEntity`, `MissionProgressEntity`, `ChallengeEntity`, `ChallengeAttemptEntity`.

## 16. Reglas de negocio

- Una misión solo está disponible cuando la anterior de su zona está completada (progresión secuencial por zona).
- Una hipótesis se considera válida solo si sus tres campos (variable, resultado, explicación) tienen contenido real, distinto entre sí y de longitud razonable.
- El resultado de un experimento se calcula siempre mediante el motor correspondiente (`PlantSimulatorEngine`, `MovementSimulatorEngine`, `TemperatureSimulatorEngine`), nunca mediante texto fijo.
- Las insignias y los objetos de colección se desbloquean exclusivamente mediante `BadgeRules`, evaluando contadores reales derivados de la base de datos.
- El nivel del jugador se deriva siempre de su XP acumulado (`LevelCalculator`), nunca se asigna manualmente.
- Si el permiso de micrófono es denegado, la grabación de voz queda inhabilitada pero el resto de la aplicación permanece funcional.

## 17. Diseño UX

Identidad visual "academia científica futurista": paleta de azul profundo, violeta de laboratorio, cian de descubrimiento y ámbar de recompensa; iconografía propia por zona dibujada con Compose Canvas; personaje guía IRIS con expresiones propias; mapa de la Academia como centro de experiencia (no un menú de botones); estados visuales siempre acompañados de icono + texto, nunca solo color; microanimaciones de progreso, estrellas y desbloqueos.

## 18. Privacidad

No se solicitan datos personales identificables. El perfil usa alias y avatar local. El micrófono se solicita únicamente al intentar grabar una nota de voz, con explicación clara si se deniega. No hay publicidad, analítica, seguimiento ni servicios de terceros. No se declara el permiso `INTERNET`.

## 19. Pruebas

76 pruebas automáticas: 69 pruebas puras de dominio (JVM, sin dependencias de Android) que cubren cálculo de nivel, validación de hipótesis, los tres motores de simulación, el evaluador de desafíos y las reglas de insignias; y 7 pruebas de persistencia Room en memoria (Robolectric) que verifican el seed inicial y su idempotencia. Ver `docs/BUILD_REPORT.md` para el estado real de ejecución en este entorno.

## 20. Limitaciones

- El entorno usado para generar este proyecto no tuvo acceso a Android SDK ni a los repositorios de Gradle/Google Maven, por lo que la compilación no pudo verificarse localmente (ver `docs/BUILD_REPORT.md`).
- La mecánica de "arrastrar y soltar" del emparejamiento de etiquetas se implementó como una interacción táctil de dos toques (tocar origen, tocar destino) en lugar de arrastre físico con gestos, por robustez en pantallas pequeñas; el resultado sigue siendo una interacción real y evaluable, no una elección múltiple disfrazada.
- Los efectos de sonido usan `ToneGenerator` del sistema en lugar de archivos de audio embebidos, para mantener el proyecto ligero.
- No se generaron ilustraciones PNG/WebP externas; toda la identidad visual se construyó con vector drawables y Compose Canvas, siguiendo el orden de prioridad de la especificación maestra.

## 21. Posibles mejoras

Añadir más simuladores (química, sonido), soporte para tablets y modo horizontal, un modo "repaso" más elaborado basado en errores históricos, ilustraciones vectoriales más detalladas por misión, y exportación opcional del diario científico a un archivo local que la familia pueda revisar.

## 22. Conclusiones

InvestigaWarma implementa una experiencia educativa real y persistente centrada en el método científico, evitando deliberadamente el patrón de "formulario disfrazado de app infantil": cada mecánica (simuladores, desafíos, hipótesis, colección) está respaldada por lógica de dominio real, probada y persistida. La principal limitación del proyecto no es de diseño ni de alcance, sino del entorno de generación: la compilación final requiere un entorno con Android SDK y acceso a Internet, disponible automáticamente a través del workflow de GitHub Actions incluido en el repositorio.
