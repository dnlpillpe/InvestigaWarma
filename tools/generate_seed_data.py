#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generador de datos semilla de InvestigaWarma.

Este script NO se ejecuta en runtime de la app (la app usa los .kt generados,
que se versionan como código fuente normal). Se usa una sola vez para producir
un volumen de contenido real y variado (40 misiones, 30 experimentos,
50 desafíos, 20 coleccionables, 15 insignias) sin tener que escribir a mano
miles de líneas repetitivas, cumpliendo con la recomendación de la
especificación maestra de usar un "seeder" mantenible.

Uso: python3 tools/generate_seed_data.py
Escribe los archivos .kt directamente en
app/src/main/java/com/investigawarma/app/data/local/seed/
"""
import json
import os

OUT_DIR = os.path.join(
    os.path.dirname(__file__), "..", "app", "src", "main", "java",
    "com", "investigawarma", "app", "data", "local", "seed",
)
os.makedirs(OUT_DIR, exist_ok=True)

PKG = "com.investigawarma.app.data.local.seed"

ZONES = [
    "SALA_OBSERVACION",
    "LABORATORIO_EXPERIMENTAL",
    "BIODESCUBRIMIENTO",
    "PLANETA_TIERRA",
    "CENTRO_DE_DATOS",
]

DIFFICULTY_PATTERN = [1, 1, 2, 2, 2, 3, 3, 3]


def kstr(s: str) -> str:
    """Escapa un string para Kotlin."""
    return json.dumps(s, ensure_ascii=False)


def kjson(obj) -> str:
    """Serializa un dict/list a JSON válido y lo envuelve como raw string Kotlin
    con triple comillas para evitar escapes anidados."""
    text = json.dumps(obj, ensure_ascii=False)
    text = text.replace('"""', '\\"\\"\\"')
    return f'"""{text}"""'


# ---------------------------------------------------------------------------
# 1. MISIONES (40) + PASOS (6 por misión) + DESCUBRIMIENTOS (1 por misión)
# ---------------------------------------------------------------------------

MISSIONS_BY_ZONE = {
    "SALA_OBSERVACION": [
        ("La lupa descubre detalles", "Un objeto cotidiano esconde detalles que nadie ha notado todavía.",
         "Observar con atención y encontrar diferencias sutiles entre dos objetos parecidos.",
         "OBSERVE_COMPARE", ["observación", "detalle"]),
        ("Hechos y opiniones", "IRIS encontró un cuaderno con frases mezcladas: algunas son hechos, otras opiniones.",
         "Separar frases objetivas de frases subjetivas sobre un mismo objeto.",
         "CLASSIFY", ["hechos", "opiniones"]),
        ("El cuaderno del detective", "Un investigador dejó sus notas de campo desordenadas.",
         "Ordenar los pasos correctos para registrar una observación científica.",
         "ORDER_STEPS", ["registro", "método"]),
        ("Comparando bajo el microscopio", "Dos muestras parecen iguales a simple vista, pero no lo son.",
         "Comparar dos muestras y anotar sus diferencias reales.",
         "OBSERVE_COMPARE", ["microscopio", "comparación"]),
        ("Etiqueta lo que ves", "Un objeto misterioso llegó al laboratorio sin identificar.",
         "Arrastrar las etiquetas correctas hacia cada parte del objeto observado.",
         "DRAG_DROP_LABEL", ["partes", "etiquetas"]),
        ("El error del observador", "Un aprendiz de investigador cometió un error en su registro.",
         "Encontrar la afirmación incorrecta dentro de un registro de observación.",
         "DETECTIVE_FIND_ERROR", ["error", "registro"]),
        ("Patrones en la sala", "Los objetos de la sala parecen estar ordenados siguiendo una regla oculta.",
         "Descubrir el patrón visual que conecta a un grupo de objetos.",
         "FIND_PATTERN", ["patrón", "regla"]),
        ("Predicción del explorador", "Antes de tocar nada, un buen investigador imagina qué puede pasar.",
         "Predecir el resultado de una acción a partir de lo observado.",
         "PREDICT_OUTCOME", ["predicción", "observación"]),
    ],
    "LABORATORIO_EXPERIMENTAL": [
        ("La planta misteriosa", "Una planta del laboratorio está creciendo de forma distinta a las demás.",
         "Modificar luz y agua para descubrir qué factor influye más en su crecimiento.",
         "SIMULATE_PLANT", ["plantas", "crecimiento"]),
        ("Carrera de canicas", "Dos canicas idénticas recorren superficies distintas y llegan en momentos distintos.",
         "Modificar superficie, peso y fuerza para entender qué controla el movimiento.",
         "SIMULATE_MOVEMENT", ["movimiento", "fricción"]),
        ("El calor viajero", "Dos materiales expuestos al mismo calor se comportan de forma diferente.",
         "Comparar cómo distintos materiales conducen el calor.",
         "SIMULATE_TEMPERATURE", ["calor", "materiales"]),
        ("Instrumentos correctos", "Un experimento no puede completarse sin las herramientas adecuadas.",
         "Elegir los instrumentos correctos para un experimento concreto.",
         "DETECTIVE_FIND_ERROR", ["instrumentos", "método"]),
        ("La balanza equilibrada", "Dos objetos de distinto tamaño podrían pesar lo mismo... o no.",
         "Predecir qué combinación de pesos equilibra la balanza.",
         "PREDICT_OUTCOME", ["peso", "equilibrio"]),
        ("Variables bajo control", "Todo experimento tiene variables que cambian y variables que se miden.",
         "Clasificar correctamente variables independientes y dependientes.",
         "CLASSIFY", ["variables", "experimento"]),
        ("El experimento con errores", "Un procedimiento de laboratorio tiene un paso mal hecho.",
         "Encontrar el error en un procedimiento experimental.",
         "DETECTIVE_FIND_ERROR", ["procedimiento", "error"]),
        ("Repite y confirma", "Un buen experimento se puede repetir siguiendo siempre el mismo orden.",
         "Ordenar correctamente las etapas del método científico.",
         "ORDER_STEPS", ["método científico", "repetición"]),
    ],
    "BIODESCUBRIMIENTO": [
        ("El animal desconocido", "Un rastro extraño apareció cerca del bosque de la Academia.",
         "Analizar pistas de hábitat y alimentación para identificar al animal.",
         "CLASSIFY", ["animales", "pistas"]),
        ("Plantas del bosque", "Dos hojas parecen iguales, pero pertenecen a especies distintas.",
         "Comparar hojas y encontrar las características que las diferencian.",
         "OBSERVE_COMPARE", ["plantas", "hojas"]),
        ("Cadena alimenticia", "Los seres vivos de un ecosistema están conectados entre sí.",
         "Ordenar correctamente una cadena alimenticia.",
         "ORDER_STEPS", ["ecosistema", "alimentación"]),
        ("Etiqueta el ecosistema", "Un diagrama de ecosistema perdió todas sus etiquetas.",
         "Arrastrar cada etiqueta al elemento correcto del ecosistema.",
         "DRAG_DROP_LABEL", ["ecosistema", "elementos"]),
        ("El insecto camuflado", "Un insecto se esconde usando patrones de su entorno.",
         "Encontrar el patrón de camuflaje que usa el insecto.",
         "FIND_PATTERN", ["camuflaje", "patrón"]),
        ("Hábitat correcto", "Cada animal necesita condiciones específicas para vivir.",
         "Predecir en qué hábitat sobreviviría mejor un animal.",
         "PREDICT_OUTCOME", ["hábitat", "supervivencia"]),
        ("El error del biólogo", "Un informe de campo describe mal las características de una especie.",
         "Encontrar la afirmación biológica incorrecta.",
         "DETECTIVE_FIND_ERROR", ["biología", "error"]),
        ("Ciclo de vida", "Un organismo cambia mucho entre su nacimiento y su forma adulta.",
         "Ordenar las etapas del ciclo de vida de un organismo.",
         "ORDER_STEPS", ["ciclo de vida", "metamorfosis"]),
    ],
    "PLANETA_TIERRA": [
        ("El agua desaparecida", "El agua de un charco desapareció en un día soleado.",
         "Ordenar las etapas del ciclo del agua para explicar qué ocurrió.",
         "ORDER_STEPS", ["evaporación", "agua"]),
        ("El ciclo del clima", "Distintos fenómenos climáticos se repiten a lo largo del año.",
         "Clasificar fenómenos climáticos según la estación en la que ocurren.",
         "CLASSIFY", ["clima", "estaciones"]),
        ("Energía en movimiento", "El viento puede mover objetos de formas distintas según su fuerza.",
         "Modificar la fuerza del viento y observar su efecto sobre distintos objetos.",
         "SIMULATE_MOVEMENT", ["viento", "energía"]),
        ("Temperatura del planeta", "Distintas superficies del planeta se calientan de forma diferente.",
         "Comparar cómo se calientan distintas superficies terrestres.",
         "SIMULATE_TEMPERATURE", ["temperatura", "superficies"]),
        ("Cuida el ambiente", "Algunas acciones cotidianas ayudan al planeta y otras lo dañan.",
         "Clasificar acciones cotidianas según su efecto en el ambiente.",
         "DRAG_DROP_LABEL", ["ambiente", "sostenibilidad"]),
        ("Patrones del clima", "Los datos de temperatura de una semana esconden una tendencia.",
         "Encontrar el patrón en una serie de datos climáticos.",
         "FIND_PATTERN", ["clima", "datos"]),
        ("Predicción meteorológica", "Con ciertas condiciones se puede anticipar el clima del día siguiente.",
         "Predecir el clima probable a partir de datos observados.",
         "PREDICT_OUTCOME", ["meteorología", "predicción"]),
        ("El reportero con errores", "Una noticia ambiental contiene una afirmación incorrecta.",
         "Encontrar el error en una noticia sobre el ambiente.",
         "DETECTIVE_FIND_ERROR", ["ambiente", "noticias"]),
    ],
    "CENTRO_DE_DATOS": [
        ("Lee el gráfico", "Un gráfico de barras esconde una historia si sabes leerlo.",
         "Interpretar un gráfico y responder qué representa.",
         "OBSERVE_COMPARE", ["gráficos", "interpretación"]),
        ("Compara resultados", "Dos grupos de investigadores obtuvieron resultados distintos.",
         "Comparar dos conjuntos de datos y decidir cuál es más confiable.",
         "OBSERVE_COMPARE", ["datos", "comparación"]),
        ("Encuentra el patrón numérico", "Una serie de números sigue una regla oculta.",
         "Descubrir el patrón numérico y predecir el siguiente valor.",
         "FIND_PATTERN", ["números", "patrón"]),
        ("Clasifica los datos", "Un conjunto de datos mezclados necesita ser organizado.",
         "Clasificar datos según el criterio correcto.",
         "CLASSIFY", ["datos", "clasificación"]),
        ("Ordena la serie", "Los datos de un experimento están fuera de orden cronológico.",
         "Ordenar una serie de datos según el momento en que ocurrieron.",
         "ORDER_STEPS", ["cronología", "datos"]),
        ("El dato erróneo", "Un valor no encaja con el resto de los datos registrados.",
         "Encontrar el dato atípico dentro de un conjunto de resultados.",
         "DETECTIVE_FIND_ERROR", ["datos", "error"]),
        ("Predicción con datos", "Los datos anteriores pueden ayudar a anticipar lo que vendrá.",
         "Predecir un resultado futuro basándose en datos previos.",
         "PREDICT_OUTCOME", ["predicción", "datos"]),
        ("Construye tu gráfico", "Un buen investigador sabe qué preguntar a sus propios datos.",
         "Construir una pregunta científica a partir de un conjunto de datos.",
         "BUILD_QUESTION", ["gráficos", "preguntas"]),
    ],
}

XP_BY_DIFFICULTY = {1: 60, 2: 90, 3: 130}
STARS_BY_DIFFICULTY = {1: 1, 2: 2, 3: 3}

missions = []
mission_steps = []
discoveries = []
mid_counter = 1

for zone in ZONES:
    entries = MISSIONS_BY_ZONE[zone]
    prev_id = None
    for idx, (title, story, objective, mechanic, tags) in enumerate(entries):
        mid = f"m{mid_counter:02d}"
        mid_counter += 1
        difficulty = DIFFICULTY_PATTERN[idx]
        missions.append({
            "id": mid,
            "zone": zone,
            "orderIndex": idx + 1,
            "title": title,
            "story": story,
            "objective": objective,
            "mechanicType": mechanic,
            "difficulty": difficulty,
            "xpReward": XP_BY_DIFFICULTY[difficulty],
            "starReward": STARS_BY_DIFFICULTY[difficulty],
            "tags": tags,
            "requiredMissionId": prev_id,
        })

        discoveries.append({
            "id": f"d{mid[1:]}",
            "missionId": mid,
            "title": f"Descubrimiento: {title}",
            "description": f"Completaste \"{title}\" y confirmaste: {objective[0].lower()}{objective[1:]}",
        })

        prev_id = mid

assert len(missions) == 40, len(missions)
assert len(discoveries) == 40
missions_by_id = {m["id"]: m for m in missions}

# ---------------------------------------------------------------------------
# 2. EXPERIMENTOS (30) + PARÁMETROS
# ---------------------------------------------------------------------------

PLANT_THEMES = [
    "Girasol de laboratorio", "Frijol germinado", "Helecho de sombra", "Cactus joven",
    "Menta de maceta", "Tomate cherry", "Musgo húmedo", "Albahaca de ventana",
    "Trigo en bandeja", "Planta carnívora",
]
MOVEMENT_THEMES = [
    "Canica sobre hielo", "Auto de juguete", "Bloque de madera", "Pelota de goma",
    "Carrito con ruedas", "Disco sobre alfombra", "Cubo sobre rampa", "Trineo de mesa",
    "Bola de metal", "Cápsula deslizante",
]
TEMPERATURE_THEMES = [
    "Metal vs. madera", "Agua vs. aceite", "Roca vs. arena", "Vidrio vs. plástico",
    "Tela vs. papel aluminio", "Cerámica vs. plástico", "Piedra vs. corcho",
    "Asfalto vs. césped", "Metal oscuro vs. metal claro", "Agua dulce vs. agua salada",
]

experiments = []
experiment_parameters = []

# Vinculamos los primeros de cada tipo a las misiones SIMULATE_* correspondientes
plant_mission_links = {0: "m09", 3: "m35"}  # (no aplica realmente m35 a plant; se corrige abajo si procede)
sim_plant_missions = [m["id"] for m in missions if m["mechanicType"] == "SIMULATE_PLANT"]
sim_movement_missions = [m["id"] for m in missions if m["mechanicType"] == "SIMULATE_MOVEMENT"]
sim_temp_missions = [m["id"] for m in missions if m["mechanicType"] == "SIMULATE_TEMPERATURE"]

for i, theme in enumerate(PLANT_THEMES):
    eid = f"exp_plant_{i+1:02d}"
    difficulty = (i % 3) + 1
    linked = sim_plant_missions[i] if i < len(sim_plant_missions) else None
    experiments.append({
        "id": eid, "zone": "LABORATORIO_EXPERIMENTAL", "title": theme,
        "description": f"Modifica luz y agua y observa cómo crece: {theme.lower()}.",
        "simulatorType": "PLANT", "difficulty": difficulty, "missionId": linked,
    })
    experiment_parameters += [
        {"experimentId": eid, "name": "luz", "unit": "lumens", "minValue": 0, "maxValue": 100, "stepValue": 10, "defaultValue": 50},
        {"experimentId": eid, "name": "agua", "unit": "ml", "minValue": 0, "maxValue": 100, "stepValue": 10, "defaultValue": 50},
        {"experimentId": eid, "name": "dias", "unit": "días", "minValue": 1, "maxValue": 30, "stepValue": 1, "defaultValue": 7},
    ]

for i, theme in enumerate(MOVEMENT_THEMES):
    eid = f"exp_move_{i+1:02d}"
    difficulty = (i % 3) + 1
    linked = sim_movement_missions[i] if i < len(sim_movement_missions) else None
    experiments.append({
        "id": eid, "zone": "LABORATORIO_EXPERIMENTAL", "title": theme,
        "description": f"Cambia superficie, peso y fuerza y observa el desplazamiento: {theme.lower()}.",
        "simulatorType": "MOVEMENT", "difficulty": difficulty, "missionId": linked,
    })
    experiment_parameters += [
        {"experimentId": eid, "name": "superficie", "unit": "índice de fricción", "minValue": 0, "maxValue": 2, "stepValue": 1, "defaultValue": 1},
        {"experimentId": eid, "name": "peso", "unit": "g", "minValue": 10, "maxValue": 500, "stepValue": 10, "defaultValue": 100},
        {"experimentId": eid, "name": "fuerza", "unit": "N", "minValue": 1, "maxValue": 50, "stepValue": 1, "defaultValue": 10},
    ]

for i, theme in enumerate(TEMPERATURE_THEMES):
    eid = f"exp_temp_{i+1:02d}"
    difficulty = (i % 3) + 1
    linked = sim_temp_missions[i] if i < len(sim_temp_missions) else None
    experiments.append({
        "id": eid, "zone": "LABORATORIO_EXPERIMENTAL", "title": theme,
        "description": f"Compara cómo cambian de temperatura dos materiales: {theme.lower()}.",
        "simulatorType": "TEMPERATURE", "difficulty": difficulty, "missionId": linked,
    })
    experiment_parameters += [
        {"experimentId": eid, "name": "temperaturaInicial", "unit": "°C", "minValue": 0, "maxValue": 100, "stepValue": 5, "defaultValue": 20},
        {"experimentId": eid, "name": "aislamiento", "unit": "índice 0-1", "minValue": 0, "maxValue": 1, "stepValue": 0.1, "defaultValue": 0.5},
        {"experimentId": eid, "name": "minutos", "unit": "min", "minValue": 1, "maxValue": 60, "stepValue": 1, "defaultValue": 10},
    ]

assert len(experiments) == 30, len(experiments)

# ---------------------------------------------------------------------------
# 3. DESAFÍOS (50) — 5 tipos x 10, distribuidos entre las 5 zonas activas
# ---------------------------------------------------------------------------

challenges = []

detective_scenarios = [
    ("El informe del clima", ["Hoy llovió en el desierto.", "El desierto recibe poca lluvia al año.",
                               "La temperatura bajó por la noche.", "El cielo estaba nublado."], 0),
    ("El registro de la planta", ["La planta creció 2 cm en una semana.", "La planta recibió luz solar directa.",
                                   "La planta nunca necesita agua.", "La maceta tiene buen drenaje."], 2),
    ("La ficha del animal", ["El pez respira por branquias.", "El pez vive fuera del agua sin problema.",
                              "El pez tiene aletas para nadar.", "El pez es un animal acuático."], 1),
    ("El experimento de temperatura", ["El metal se calentó más rápido que la madera.",
                                        "Ambos materiales estaban a la misma temperatura inicial.",
                                        "La madera es mejor conductor que el metal.",
                                        "Se midió la temperatura cada 5 minutos."], 2),
    ("El dato del gráfico", ["La barra más alta representa el mayor valor.", "Todas las barras miden lo mismo.",
                              "El eje vertical muestra la cantidad.", "El gráfico tiene un título."], 1),
    ("La cadena alimenticia", ["El sol es el productor de energía.", "El león se come directamente al sol.",
                                "Las plantas producen su propio alimento.", "Los herbívoros comen plantas."], 1),
    ("El reporte de movimiento", ["El objeto en hielo se deslizó más lejos.", "La fricción del hielo es mayor que la de la alfombra.",
                                   "Se usó la misma fuerza en ambos casos.", "El peso del objeto no cambió."], 1),
    ("La hipótesis del laboratorio", ["La hipótesis dice SI, ENTONCES y PORQUE.", "Una hipótesis siempre es verdadera sin comprobarla.",
                                       "La hipótesis se prueba con un experimento.", "La hipótesis se puede corregir."], 1),
    ("El ciclo del agua", ["El agua se evapora con el calor del sol.", "El agua evaporada desaparece para siempre.",
                            "El vapor de agua forma nubes.", "La lluvia cae desde las nubes."], 1),
    ("La muestra del microscopio", ["Las dos muestras se ven idénticas a simple vista.", "El microscopio no ayuda a observar detalles.",
                                     "Las muestras tienen texturas distintas.", "Se compararon bajo la misma luz."], 1),
]

order_variants = [
    "Ordena la investigación del laboratorio",
    "Ordena la investigación de campo",
    "Ordena la investigación del clima",
    "Ordena la investigación del ecosistema",
    "Ordena la investigación de datos",
    "Ordena la investigación del agua",
    "Ordena la investigación del movimiento",
    "Ordena la investigación biológica",
    "Ordena la investigación astronómica",
    "Ordena la investigación química",
]
ORDER_STEPS_CANON = ["observación", "pregunta", "hipótesis", "experimento", "conclusión"]

pattern_sequences = [
    ([2, 4, 6, 8], 10), ([1, 2, 4, 8], 16), ([5, 10, 15, 20], 25), ([3, 6, 9, 12], 15),
    ([1, 4, 9, 16], 25), ([10, 8, 6, 4], 2), ([100, 90, 80, 70], 60), ([1, 1, 2, 3, 5], 8),
    ([2, 6, 18], 54), ([0, 5, 10, 15], 20),
]

classify_sets = [
    ("Clasifica seres vivos", [("perro", "vivo"), ("piedra", "no vivo"), ("árbol", "vivo"), ("silla", "no vivo")], ["vivo", "no vivo"]),
    ("Clasifica estados del agua", [("hielo", "sólido"), ("vapor", "gaseoso"), ("lluvia", "líquido"), ("nieve", "sólido")], ["sólido", "líquido", "gaseoso"]),
    ("Clasifica fuentes de energía", [("sol", "renovable"), ("carbón", "no renovable"), ("viento", "renovable"), ("petróleo", "no renovable")], ["renovable", "no renovable"]),
    ("Clasifica animales", [("águila", "ave"), ("salmón", "pez"), ("lobo", "mamífero"), ("rana", "anfibio")], ["ave", "pez", "mamífero", "anfibio"]),
    ("Clasifica materiales", [("madera", "aislante"), ("cobre", "conductor"), ("plástico", "aislante"), ("hierro", "conductor")], ["aislante", "conductor"]),
    ("Clasifica hechos y opiniones", [("el agua hierve a 100°C", "hecho"), ("el verano es la mejor estación", "opinión"),
                                       ("la Tierra gira alrededor del Sol", "hecho"), ("los perros son mejores que los gatos", "opinión")], ["hecho", "opinión"]),
    ("Clasifica variables", [("cantidad de luz", "independiente"), ("altura de la planta", "dependiente"),
                              ("cantidad de agua", "independiente"), ("número de hojas", "dependiente")], ["independiente", "dependiente"]),
    ("Clasifica el clima", [("lluvia", "húmedo"), ("desierto", "seco"), ("nieve", "frío"), ("selva", "húmedo")], ["húmedo", "seco", "frío"]),
    ("Clasifica instrumentos", [("microscopio", "observación"), ("balanza", "medición"), ("lupa", "observación"), ("termómetro", "medición")], ["observación", "medición"]),
    ("Clasifica descubrimientos", [("nueva especie", "biología"), ("nuevo planeta", "astronomía"), ("nuevo mineral", "geología"), ("nueva vacuna", "medicina")], ["biología", "astronomía", "geología", "medicina"]),
]

build_sets = [
    ("Arma el experimento de la planta", ["maceta", "regla", "regadera"], ["termómetro de cocina", "casco de bicicleta"]),
    ("Arma el experimento de temperatura", ["termómetro", "cronómetro", "dos materiales"], ["microscopio", "brújula"]),
    ("Arma el experimento de movimiento", ["cronómetro", "rampa", "objeto rodante"], ["lupa", "balanza de cocina"]),
    ("Arma la observación de campo", ["lupa", "cuaderno", "lápiz"], ["tubo de ensayo", "mechero"]),
    ("Arma el análisis de datos", ["tabla de datos", "calculadora", "gráfico"], ["red de mariposas", "guantes de jardín"]),
    ("Arma la exploración biológica", ["binoculares", "guía de campo", "cuaderno"], ["probeta", "imán"]),
    ("Arma el laboratorio de química segura", ["guantes", "gafas de protección", "vaso medidor"], ["telescopio", "brújula"]),
    ("Arma el estudio del clima", ["termómetro", "pluviómetro", "anemómetro"], ["microscopio", "lupa"]),
    ("Arma el estudio astronómico", ["telescopio", "cuaderno de observación", "linterna roja"], ["balanza", "termómetro de cocina"]),
    ("Arma la investigación del agua", ["vaso medidor", "termómetro", "cronómetro"], ["red de mariposas", "brújula"]),
]

zone_cycle = ZONES  # 5 zonas activas
for i in range(10):
    zone = zone_cycle[i % len(zone_cycle)]
    difficulty = (i % 3) + 1

    # DETECTIVE
    title, statements, error_idx = detective_scenarios[i]
    challenges.append({
        "id": f"chal_det_{i+1:02d}", "zone": zone, "type": "DETECTIVE", "title": title,
        "difficulty": difficulty,
        "data": {"statements": statements, "errorIndex": error_idx},
    })

    # ORDER
    challenges.append({
        "id": f"chal_ord_{i+1:02d}", "zone": zone, "type": "ORDER", "title": order_variants[i],
        "difficulty": difficulty,
        "data": {"steps": ORDER_STEPS_CANON},
    })

    # PATTERN
    seq, answer = pattern_sequences[i]
    challenges.append({
        "id": f"chal_pat_{i+1:02d}", "zone": zone, "type": "PATTERN", "title": f"Encuentra el patrón #{i+1}",
        "difficulty": difficulty,
        "data": {"sequence": seq, "answer": answer},
    })

    # CLASSIFY
    ctitle, items, categories = classify_sets[i]
    challenges.append({
        "id": f"chal_cla_{i+1:02d}", "zone": zone, "type": "CLASSIFY", "title": ctitle,
        "difficulty": difficulty,
        "data": {"items": [{"label": lbl, "category": cat} for lbl, cat in items], "categories": categories},
    })

    # BUILD
    btitle, correct, distractors = build_sets[i]
    challenges.append({
        "id": f"chal_bui_{i+1:02d}", "zone": zone, "type": "BUILD", "title": btitle,
        "difficulty": difficulty,
        "data": {"correctInstruments": correct, "distractorInstruments": distractors},
    })

assert len(challenges) == 50, len(challenges)

# ---------------------------------------------------------------------------
# 3b. PASOS DE MISIÓN (6 por misión = 240) — se generan aquí porque el paso
#     EXPERIMENT de cada misión se apoya en los experimentos y desafíos ya
#     construidos arriba, para que la interacción sea real y no un texto fijo.
# ---------------------------------------------------------------------------

VOCAB_BANK = {
    "SALA_OBSERVACION": [
        ("Hecho", "Una afirmación que se puede comprobar."),
        ("Opinión", "Una idea personal que puede cambiar de persona a persona."),
        ("Observación", "Registrar lo que perciben los sentidos, sin inventar nada."),
        ("Detalle", "Una característica pequeña pero importante de lo observado."),
        ("Comparar", "Buscar semejanzas y diferencias entre dos elementos."),
        ("Registro", "Anotar de forma ordenada lo que se observa."),
        ("Patrón", "Una regla que se repite y se puede predecir."),
        ("Evidencia", "Aquello que respalda una observación."),
    ],
    "LABORATORIO_EXPERIMENTAL": [
        ("Variable", "Un factor que puede cambiar en un experimento."),
        ("Control", "Mantener todo igual excepto la variable que se estudia."),
        ("Instrumento", "Herramienta usada para medir u observar."),
        ("Procedimiento", "Los pasos ordenados que se siguen en un experimento."),
        ("Resultado", "Lo que se obtiene después de experimentar."),
        ("Repetición", "Volver a hacer el experimento para confirmar resultados."),
        ("Medición", "Asignar un número a una propiedad observada."),
        ("Hipótesis", "Una posible explicación que se pone a prueba."),
    ],
    "BIODESCUBRIMIENTO": [
        ("Hábitat", "El lugar donde vive un ser vivo."),
        ("Especie", "Un grupo de seres vivos con características comunes."),
        ("Ecosistema", "Los seres vivos y su ambiente interactuando juntos."),
        ("Adaptación", "Una característica que ayuda a un ser vivo a sobrevivir."),
        ("Cadena alimenticia", "El camino que sigue la energía entre seres vivos."),
        ("Ciclo de vida", "Las etapas por las que pasa un ser vivo."),
        ("Camuflaje", "Confundirse con el entorno para pasar desapercibido."),
        ("Clasificación", "Agrupar seres vivos según sus características."),
    ],
    "PLANETA_TIERRA": [
        ("Evaporación", "Cuando el agua líquida se convierte en vapor."),
        ("Clima", "El patrón de condiciones atmosféricas de una región."),
        ("Energía", "La capacidad de producir un cambio o movimiento."),
        ("Sostenibilidad", "Usar los recursos sin agotarlos para el futuro."),
        ("Superficie", "La capa externa de un material o del planeta."),
        ("Fenómeno natural", "Un evento que ocurre en la naturaleza."),
        ("Predicción", "Anticipar lo que va a pasar con base en datos."),
        ("Ambiente", "El entorno natural que nos rodea."),
    ],
    "CENTRO_DE_DATOS": [
        ("Dato", "Un valor recogido durante una observación o experimento."),
        ("Gráfico", "Una representación visual de datos."),
        ("Patrón numérico", "Una regla que sigue una serie de números."),
        ("Promedio", "El valor típico de un conjunto de datos."),
        ("Comparación", "Poner dos o más datos uno junto al otro para analizarlos."),
        ("Tendencia", "La dirección general que siguen los datos con el tiempo."),
        ("Dato atípico", "Un valor que no encaja con el resto de los datos."),
        ("Conclusión", "Lo que se puede afirmar después de analizar los datos."),
    ],
}

COMPARE_BANK = {
    "SALA_OBSERVACION": ["tamaño", "color", "textura", "forma"],
    "LABORATORIO_EXPERIMENTAL": ["temperatura", "velocidad", "peso", "resultado"],
    "BIODESCUBRIMIENTO": ["tamaño de las hojas", "color", "forma", "textura"],
    "PLANETA_TIERRA": ["temperatura", "humedad", "nubosidad", "viento"],
    "CENTRO_DE_DATOS": ["valor máximo", "valor mínimo", "promedio", "tendencia"],
}

PREDICT_BANK = {
    "SALA_OBSERVACION": ["cambiará poco", "cambiará mucho", "se mantendrá exactamente igual"],
    "LABORATORIO_EXPERIMENTAL": ["el resultado aumentará", "el resultado disminuirá", "el resultado no cambiará"],
    "BIODESCUBRIMIENTO": ["el organismo se adaptará", "el organismo no sobrevivirá", "el organismo no cambiará"],
    "PLANETA_TIERRA": ["el clima será más cálido", "el clima será más frío", "el clima seguirá igual"],
    "CENTRO_DE_DATOS": ["el valor seguirá subiendo", "el valor bajará", "el valor se mantendrá estable"],
}

CHALLENGE_TYPE_BY_MECHANIC = {
    "CLASSIFY": "CLASSIFY",
    "ORDER_STEPS": "ORDER",
    "FIND_PATTERN": "PATTERN",
    "DETECTIVE_FIND_ERROR": "DETECTIVE",
    "BUILD_QUESTION": "BUILD",
}

experiment_by_mission = {e["missionId"]: e["id"] for e in experiments if e["missionId"]}
# Un desafío de cada tipo por zona, reutilizado como el "experimento" de las misiones
# cuya mecánica coincide (así la interacción es real: datos ya validados por ChallengeEvaluator).
challenge_by_zone_and_type = {}
for c in challenges:
    challenge_by_zone_and_type.setdefault((c["zone"], c["type"]), c["id"])

for m in missions:
    mid = m["id"]
    zone = m["zone"]
    mechanic = m["mechanicType"]
    tags = m["tags"]
    story = m["story"]
    objective = m["objective"]
    idx = m["orderIndex"]

    # --- contenido específico del paso EXPERIMENT según la mecánica ---
    if mechanic in ("SIMULATE_PLANT", "SIMULATE_MOVEMENT", "SIMULATE_TEMPERATURE"):
        experiment_content = {"kind": "experiment", "experimentId": experiment_by_mission.get(mid)}
    elif mechanic in CHALLENGE_TYPE_BY_MECHANIC:
        ctype = CHALLENGE_TYPE_BY_MECHANIC[mechanic]
        experiment_content = {"kind": "challenge", "challengeId": challenge_by_zone_and_type.get((zone, ctype))}
    elif mechanic == "OBSERVE_COMPARE":
        criteria = COMPARE_BANK[zone]
        # Rotación determinista por misión: 2 de 4 criterios se marcan como diferencia real.
        offset = idx % len(criteria)
        statements = [f"El objeto A y el objeto B son distintos en: {c}." for c in criteria]
        correct_indices = sorted({offset, (offset + 2) % len(criteria)})
        experiment_content = {
            "kind": "compare",
            "prompt": "Marca las diferencias reales entre los dos elementos observados.",
            "statements": statements,
            "correctIndices": correct_indices,
        }
    elif mechanic == "DRAG_DROP_LABEL":
        vocab = VOCAB_BANK[zone]
        offset = (idx * 2) % len(vocab)
        pairs_src = (vocab + vocab)[offset:offset + 4]
        experiment_content = {
            "kind": "drag_drop",
            "prompt": "Arrastra cada palabra hacia su definición correcta.",
            "pairs": [{"label": term, "definition": definition} for term, definition in pairs_src],
        }
    elif mechanic == "PREDICT_OUTCOME":
        options = list(PREDICT_BANK[zone])
        correct_index = idx % len(options)
        experiment_content = {
            "kind": "predict",
            "scenario": story,
            "options": options,
            "correctIndex": correct_index,
        }
    else:
        experiment_content = {"kind": "generic", "instructions": objective}

    steps = [
        ("OBSERVE", f"Observa con atención: {story}",
         {"focusHint": "Fíjate en los detalles antes de continuar."}),
        ("QUESTION", "Construye tu pregunta científica con las tarjetas.",
         {"connectors": ["¿Por qué?", "¿Qué pasa si?"], "topic": tags[0] if tags else "ciencia"}),
        ("HYPOTHESIS", "Construye tu hipótesis: SI... ENTONCES... PORQUE...",
         {"variableHint": tags[0] if tags else "variable",
          "resultHint": "resultado esperado", "explanationHint": "explicación científica"}),
        ("EXPERIMENT", objective, experiment_content),
        ("ANALYZE", "Analiza tus resultados. ¿Confirman tu hipótesis?",
         {"question": "¿Qué descubriste al experimentar?"}),
        ("DISCOVERY", "¡Desbloqueaste un nuevo descubrimiento científico!",
         {"missionId": mid}),
    ]
    for order, (stype, prompt, content) in enumerate(steps, start=1):
        mission_steps.append({
            "missionId": mid, "orderIndex": order,
            "stepType": stype, "promptText": prompt, "contentJson": content,
        })

assert len(mission_steps) == 40 * 6, len(mission_steps)

# ---------------------------------------------------------------------------
# 4. COLECCIONABLES (20) — Museo Científico Personal
# ---------------------------------------------------------------------------

collection_items = [
    ("lupa", "Lupa de investigador", "La primera herramienta de todo joven científico.", "INICIAL", "Completa tu primera misión."),
    ("cuaderno", "Cuaderno científico", "Donde se registran todas las observaciones.", "INICIAL", "Escribe tu primera entrada de diario."),
    ("regla", "Regla de medición", "Para medir con precisión cualquier descubrimiento.", "INICIAL", "Completa tu primer experimento."),
    ("microscopio", "Microscopio básico", "Revela detalles invisibles a simple vista.", "INICIAL", "Completa la Sala de Observación."),
    ("robot", "Robot ayudante", "Un pequeño robot que asiste en tareas repetitivas.", "INTERMEDIO", "Completa el Laboratorio Experimental."),
    ("telescopio", "Telescopio de campo", "Para observar el cielo desde la Academia.", "INTERMEDIO", "Completa 3 misiones de Planeta Tierra."),
    ("sensores", "Kit de sensores", "Mide temperatura, luz y humedad automáticamente.", "INTERMEDIO", "Completa 5 experimentos."),
    ("brujula", "Brújula de explorador", "Nunca te pierdas en el Biodescubrimiento.", "INTERMEDIO", "Completa Biodescubrimiento."),
    ("balanza", "Balanza de precisión", "Compara pesos con exactitud.", "INTERMEDIO", "Completa 3 desafíos de tipo Construir."),
    ("termometro", "Termómetro de laboratorio", "Mide temperatura en todos tus experimentos.", "INTERMEDIO", "Completa 3 experimentos de temperatura."),
    ("iman", "Imán de laboratorio", "Explora fuerzas invisibles.", "INTERMEDIO", "Completa 5 hipótesis válidas."),
    ("prisma", "Prisma de luz", "Descompone la luz en colores.", "INTERMEDIO", "Completa el Centro de Datos."),
    ("satelite", "Satélite miniatura", "Un modelo del satélite que orbita el planeta.", "AVANZADO", "Alcanza el nivel Científico en Formación."),
    ("estacion_meteorologica", "Estación meteorológica", "Registra el clima de la Academia en tiempo real.", "AVANZADO", "Completa 6 misiones de Planeta Tierra."),
    ("laboratorio_avanzado", "Laboratorio avanzado", "Un laboratorio completo para grandes descubrimientos.", "AVANZADO", "Completa 20 misiones en total."),
    ("dron_exploracion", "Dron de exploración", "Sobrevuela zonas difíciles de alcanzar.", "AVANZADO", "Completa 10 desafíos con éxito."),
    ("telescopio_espacial", "Telescopio espacial", "Observa más allá de lo que el ojo humano puede ver.", "AVANZADO", "Desbloquea 10 insignias."),
    ("secuenciador", "Secuenciador genético", "Analiza el código de la vida.", "AVANZADO", "Completa Biodescubrimiento con puntuación perfecta."),
    ("submarino", "Submarino de investigación", "Explora las profundidades desconocidas.", "AVANZADO", "Completa las 40 misiones."),
    ("cohete", "Cohete de la Academia", "El símbolo máximo de un Gran Descubridor.", "AVANZADO", "Alcanza el nivel Gran Descubridor."),
]
assert len(collection_items) == 20

# ---------------------------------------------------------------------------
# 5. INSIGNIAS (15)
# ---------------------------------------------------------------------------

badges = [
    ("primeros_pasos", "Primeros Pasos", "Completaste tu primera misión científica.", "PROGRESO"),
    ("observador_agudo", "Observador Agudo", "Completaste la Sala de Observación.", "ZONA"),
    ("cientifico_laboratorio", "Científico de Laboratorio", "Completaste el Laboratorio Experimental.", "ZONA"),
    ("amigo_naturaleza", "Amigo de la Naturaleza", "Completaste Biodescubrimiento.", "ZONA"),
    ("guardian_planeta", "Guardián del Planeta", "Completaste Planeta Tierra.", "ZONA"),
    ("analista_datos", "Analista de Datos", "Completaste el Centro de Datos.", "ZONA"),
    ("constructor_hipotesis", "Constructor de Hipótesis", "Creaste 10 hipótesis válidas.", "HABILIDAD"),
    ("racha_detective", "Racha Detective", "Resolviste 5 desafíos Detective con éxito.", "DESAFIO"),
    ("ordenador_experto", "Ordenador Experto", "Resolviste 10 desafíos de Ordenar.", "DESAFIO"),
    ("cazador_patrones", "Cazador de Patrones", "Resolviste 10 desafíos de Patrones.", "DESAFIO"),
    ("clasificador_maestro", "Clasificador Maestro", "Resolviste 10 desafíos de Clasificar.", "DESAFIO"),
    ("diario_completo", "Diario Completo", "Escribiste 10 entradas en tu diario científico.", "HABILIDAD"),
    ("voz_investigador", "Voz de Investigador", "Grabaste 5 notas de voz en tu diario.", "HABILIDAD"),
    ("coleccionista", "Coleccionista", "Desbloqueaste 10 objetos del Museo Científico.", "PROGRESO"),
    ("gran_descubridor", "Gran Descubridor", "Alcanzaste el nivel máximo de la Academia.", "PROGRESO"),
]
assert len(badges) == 15

# ---------------------------------------------------------------------------
# Escritura de archivos Kotlin
# ---------------------------------------------------------------------------

HEADER = f"""package {PKG}

// ============================================================================
// ARCHIVO GENERADO por tools/generate_seed_data.py — NO EDITAR A MANO.
// Para cambiar el contenido, edita el script y vuelve a ejecutarlo.
// ============================================================================
"""


def write_missions():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.ScientificMissionEntity",
             "", "object SeedMissions {", "    val ALL: List<ScientificMissionEntity> = listOf("]
    for m in missions:
        req = "null" if m["requiredMissionId"] is None else kstr(m["requiredMissionId"])
        tags = "listOf(" + ", ".join(kstr(t) for t in m["tags"]) + ")"
        lines.append(
            "        ScientificMissionEntity("
            f'id = {kstr(m["id"])}, zone = {kstr(m["zone"])}, orderIndex = {m["orderIndex"]}, '
            f'title = {kstr(m["title"])}, story = {kstr(m["story"])}, objective = {kstr(m["objective"])}, '
            f'mechanicType = {kstr(m["mechanicType"])}, difficulty = {m["difficulty"]}, '
            f'xpReward = {m["xpReward"]}, starReward = {m["starReward"]}, tags = {tags}, '
            f'requiredMissionId = {req}),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedMissions.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def write_mission_steps():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.MissionStepEntity",
             "", "object SeedMissionSteps {", "    val ALL: List<MissionStepEntity> = listOf("]
    for s in mission_steps:
        lines.append(
            "        MissionStepEntity("
            f'missionId = {kstr(s["missionId"])}, orderIndex = {s["orderIndex"]}, '
            f'stepType = {kstr(s["stepType"])}, promptText = {kstr(s["promptText"])}, '
            f'contentJson = {kjson(s["contentJson"])}),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedMissionSteps.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def write_discoveries():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.ScientificDiscoveryEntity",
             "", "object SeedDiscoveries {", "    val ALL: List<ScientificDiscoveryEntity> = listOf("]
    for d in discoveries:
        lines.append(
            "        ScientificDiscoveryEntity("
            f'id = {kstr(d["id"])}, missionId = {kstr(d["missionId"])}, '
            f'title = {kstr(d["title"])}, description = {kstr(d["description"])}),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedDiscoveries.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def write_experiments():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.ExperimentEntity",
             "import com.investigawarma.app.data.local.entity.ExperimentParameterEntity",
             "", "object SeedExperiments {", "    val ALL: List<ExperimentEntity> = listOf("]
    for e in experiments:
        mid = "null" if e["missionId"] is None else kstr(e["missionId"])
        lines.append(
            "        ExperimentEntity("
            f'id = {kstr(e["id"])}, zone = {kstr(e["zone"])}, title = {kstr(e["title"])}, '
            f'description = {kstr(e["description"])}, simulatorType = {kstr(e["simulatorType"])}, '
            f'difficulty = {e["difficulty"]}, missionId = {mid}),'
        )
    lines.append("    )")
    lines.append("")
    lines.append("    val PARAMETERS: List<ExperimentParameterEntity> = listOf(")
    for p in experiment_parameters:
        lines.append(
            "        ExperimentParameterEntity("
            f'experimentId = {kstr(p["experimentId"])}, name = {kstr(p["name"])}, unit = {kstr(p["unit"])}, '
            f'minValue = {p["minValue"]}f, maxValue = {p["maxValue"]}f, stepValue = {p["stepValue"]}f, '
            f'defaultValue = {p["defaultValue"]}f),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedExperiments.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def write_challenges():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.ChallengeEntity",
             "", "object SeedChallenges {", "    val ALL: List<ChallengeEntity> = listOf("]
    for c in challenges:
        lines.append(
            "        ChallengeEntity("
            f'id = {kstr(c["id"])}, zone = {kstr(c["zone"])}, type = {kstr(c["type"])}, '
            f'title = {kstr(c["title"])}, dataJson = {kjson(c["data"])}, difficulty = {c["difficulty"]}),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedChallenges.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def write_collection_items():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.CollectionItemEntity",
             "", "object SeedCollectionItems {", "    val ALL: List<CollectionItemEntity> = listOf("]
    for key, name, desc, cat, req in collection_items:
        lines.append(
            "        CollectionItemEntity("
            f'id = {kstr("col_" + key)}, key = {kstr(key)}, name = {kstr(name)}, '
            f'description = {kstr(desc)}, category = {kstr(cat)}, requirementDescription = {kstr(req)}),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedCollectionItems.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def write_badges():
    lines = [HEADER,
             "import com.investigawarma.app.data.local.entity.BadgeEntity",
             "", "object SeedBadges {", "    val ALL: List<BadgeEntity> = listOf("]
    for key, name, desc, cat in badges:
        lines.append(
            "        BadgeEntity("
            f'id = {kstr("badge_" + key)}, key = {kstr(key)}, name = {kstr(name)}, '
            f'description = {kstr(desc)}, category = {kstr(cat)}),'
        )
    lines.append("    )")
    lines.append("}")
    with open(os.path.join(OUT_DIR, "SeedBadges.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


def sql_str(s: str) -> str:
    return "'" + str(s).replace("'", "''") + "'"


def write_sample_sql():
    """Escribe database/sample_data.sql: datos REALES (no inventados) de una
    muestra representativa del seed completo — las 2 primeras misiones de cada
    zona con sus pasos, todos los experimentos, todos los desafíos, todos los
    coleccionables y todas las insignias."""
    out_path = os.path.join(os.path.dirname(__file__), "..", "database", "sample_data.sql")
    lines = [
        "-- InvestigaWarma — Datos de muestra REALES extraídos del seed completo.",
        "-- Generado por tools/generate_seed_data.py. Para el set completo (40 misiones,",
        "-- 240 pasos, 30 experimentos, 50 desafíos, 20 coleccionables, 15 insignias)",
        "-- ver app/src/main/java/com/investigawarma/app/data/local/seed/.",
        "",
    ]

    sample_missions = []
    for zone in ZONES:
        zone_missions = [m for m in missions if m["zone"] == zone][:2]
        sample_missions += zone_missions
    sample_mission_ids = {m["id"] for m in sample_missions}

    lines.append("-- Misiones (muestra: 2 por zona)")
    for m in sample_missions:
        req = "NULL" if m["requiredMissionId"] is None else sql_str(m["requiredMissionId"])
        tags_json = json.dumps(m["tags"], ensure_ascii=False)
        lines.append(
            "INSERT INTO scientific_mission (id, zone, orderIndex, title, story, objective, "
            "mechanicType, difficulty, xpReward, starReward, tags, requiredMissionId) VALUES "
            f"({sql_str(m['id'])}, {sql_str(m['zone'])}, {m['orderIndex']}, {sql_str(m['title'])}, "
            f"{sql_str(m['story'])}, {sql_str(m['objective'])}, {sql_str(m['mechanicType'])}, "
            f"{m['difficulty']}, {m['xpReward']}, {m['starReward']}, {sql_str(tags_json)}, {req});"
        )

    lines.append("")
    lines.append("-- Pasos de misión (solo de las misiones de muestra)")
    for s in mission_steps:
        if s["missionId"] not in sample_mission_ids:
            continue
        content_json = json.dumps(s["contentJson"], ensure_ascii=False)
        lines.append(
            "INSERT INTO mission_step (missionId, orderIndex, stepType, promptText, contentJson) VALUES "
            f"({sql_str(s['missionId'])}, {s['orderIndex']}, {sql_str(s['stepType'])}, "
            f"{sql_str(s['promptText'])}, {sql_str(content_json)});"
        )

    lines.append("")
    lines.append("-- Experimentos (completo: 30)")
    for e in experiments:
        mid = "NULL" if e["missionId"] is None else sql_str(e["missionId"])
        lines.append(
            "INSERT INTO experiment (id, zone, title, description, simulatorType, difficulty, missionId) VALUES "
            f"({sql_str(e['id'])}, {sql_str(e['zone'])}, {sql_str(e['title'])}, {sql_str(e['description'])}, "
            f"{sql_str(e['simulatorType'])}, {e['difficulty']}, {mid});"
        )

    lines.append("")
    lines.append("-- Parámetros de experimento (completo: 90)")
    for p in experiment_parameters:
        lines.append(
            "INSERT INTO experiment_parameter (experimentId, name, unit, minValue, maxValue, stepValue, defaultValue) VALUES "
            f"({sql_str(p['experimentId'])}, {sql_str(p['name'])}, {sql_str(p['unit'])}, "
            f"{p['minValue']}, {p['maxValue']}, {p['stepValue']}, {p['defaultValue']});"
        )

    lines.append("")
    lines.append("-- Desafíos (completo: 50)")
    for c in challenges:
        data_json = json.dumps(c["data"], ensure_ascii=False)
        lines.append(
            "INSERT INTO challenge (id, zone, type, title, dataJson, difficulty) VALUES "
            f"({sql_str(c['id'])}, {sql_str(c['zone'])}, {sql_str(c['type'])}, {sql_str(c['title'])}, "
            f"{sql_str(data_json)}, {c['difficulty']});"
        )

    lines.append("")
    lines.append("-- Coleccionables (completo: 20)")
    for key, name, desc, cat, req in collection_items:
        lines.append(
            "INSERT INTO collection_item (id, key, name, description, category, requirementDescription) VALUES "
            f"({sql_str('col_' + key)}, {sql_str(key)}, {sql_str(name)}, {sql_str(desc)}, {sql_str(cat)}, {sql_str(req)});"
        )

    lines.append("")
    lines.append("-- Insignias (completo: 15)")
    for key, name, desc, cat in badges:
        lines.append(
            "INSERT INTO badge (id, key, name, description, category) VALUES "
            f"({sql_str('badge_' + key)}, {sql_str(key)}, {sql_str(name)}, {sql_str(desc)}, {sql_str(cat)});"
        )

    with open(out_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + "\n")


write_missions()
write_mission_steps()
write_discoveries()
write_experiments()
write_challenges()
write_collection_items()
write_badges()
write_sample_sql()

print("Generado:")
print(f"  Misiones: {len(missions)}  (esperado 40)")
print(f"  Pasos de misión: {len(mission_steps)}  (esperado 240)")
print(f"  Descubrimientos: {len(discoveries)}  (esperado 40)")
print(f"  Experimentos: {len(experiments)}  (esperado 30)")
print(f"  Parámetros de experimento: {len(experiment_parameters)}  (esperado 90)")
print(f"  Desafíos: {len(challenges)}  (esperado 50)")
print(f"  Coleccionables: {len(collection_items)}  (esperado 20)")
print(f"  Insignias: {len(badges)}  (esperado 15)")
