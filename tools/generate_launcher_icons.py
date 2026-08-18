#!/usr/bin/env python3
"""
Genera los iconos de lanzador PNG de InvestigaWarma para dispositivos con API < 26
(que no soportan adaptive-icon). Dibuja el mismo concepto que los vector drawables
de mipmap-anydpi-v26: "la lupa del joven investigador" sobre fondo degradado.

Uso: python3 tools/generate_launcher_icons.py
"""
from PIL import Image, ImageDraw
import math
import os

SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

DEEP_SPACE = (15, 27, 60)
LAB_VIOLET = (91, 63, 224)
DISCOVERY_CYAN = (32, 211, 194)
SPARK_AMBER = (255, 176, 32)
MISSION_CORAL = (255, 107, 107)
WHITE = (255, 255, 255)


def lerp(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def draw_icon(size: int) -> Image.Image:
    scale = size / 108.0
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))

    # Fondo degradado diagonal deep_space -> lab_violet
    for y in range(size):
        for x in range(0, size, max(1, size // 54)):
            t = (x + y) / (2 * size)
            color = lerp(DEEP_SPACE, LAB_VIOLET, min(1.0, t))
            for dx in range(max(1, size // 54)):
                if x + dx < size:
                    img.putpixel((x + dx, y), color + (255,))

    draw = ImageDraw.Draw(img, "RGBA")

    def s(v):
        return v * scale

    # Mango
    draw.line([(s(70), s(70)), (s(86), s(86))], fill=SPARK_AMBER + (255,), width=max(2, int(s(6))))

    # Aro
    draw.ellipse([s(30), s(30), s(74), s(74)], outline=DISCOVERY_CYAN + (255,), width=max(2, int(s(5))))

    # Cristal
    draw.ellipse([s(33), s(33), s(71), s(71)], fill=DEEP_SPACE + (180,))

    # Estrella (simplificada como polígono)
    cx, cy, r = s(52), s(52), s(11)
    pts = []
    for i in range(10):
        ang = -math.pi / 2 + i * math.pi / 5
        rad = r if i % 2 == 0 else r * 0.45
        pts.append((cx + rad * math.cos(ang), cy + rad * math.sin(ang)))
    draw.polygon(pts, fill=WHITE + (255,))

    # Molécula
    draw.ellipse([s(37), s(57), s(43), s(63)], fill=DISCOVERY_CYAN + (255,))
    draw.ellipse([s(61), s(57), s(67), s(63)], fill=MISSION_CORAL + (255,))
    draw.line([(s(40), s(60)), (s(64), s(60))], fill=WHITE + (255,), width=max(1, int(s(1.5))))

    return img


def main():
    base = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res")
    for folder, size in SIZES.items():
        out_dir = os.path.join(base, folder)
        os.makedirs(out_dir, exist_ok=True)
        icon = draw_icon(size)
        icon.save(os.path.join(out_dir, "ic_launcher.png"))
        # Versión "round" idéntica (el aro de la lupa ya sugiere circularidad)
        icon.save(os.path.join(out_dir, "ic_launcher_round.png"))
        print(f"Generado {folder}/ic_launcher.png ({size}x{size})")


if __name__ == "__main__":
    main()
