#!/usr/bin/env python3
"""
Genera los iconos de lanzador PNG de InvestigaWarma para dispositivos con API < 26
(que no soportan adaptive-icon). Dibuja el mismo concepto que los vector drawables
de mipmap-anydpi-v26: "el pequeño detective" (cabeza con sombrero y lupa) sobre
fondo degradado.

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
SKIN = (243, 199, 160)
HAIR = (46, 26, 71)
HAT_BRIM = (184, 118, 58)
HAT_DOME = (193, 127, 69)
HAT_BAND = (138, 90, 43)
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

    def circle(cx, cy, r, **kwargs):
        draw.ellipse([s(cx - r), s(cy - r), s(cx + r), s(cy + r)], **kwargs)

    def oval(cx, cy, rx, ry, **kwargs):
        draw.ellipse([s(cx - rx), s(cy - ry), s(cx + rx), s(cy + ry)], **kwargs)

    # Mechones de cabello
    circle(38, 52, 6, fill=HAIR + (255,))
    circle(70, 52, 6, fill=HAIR + (255,))

    # Cabeza
    circle(54, 60, 15, fill=SKIN + (255,))

    # Ala y copa del sombrero
    oval(54, 47, 20, 6, fill=HAT_BRIM + (255,))
    oval(54, 39, 13, 11, fill=HAT_DOME + (255,))
    oval(54, 45, 13, 2.5, fill=HAT_BAND + (255,))

    # Ojos y sonrisa
    circle(49, 59, 1.6, fill=HAIR + (255,))
    circle(59, 59, 1.6, fill=HAIR + (255,))
    draw.arc([s(48), s(63), s(60), s(73)], start=20, end=160, fill=HAIR + (255,), width=max(1, int(s(1.6))))

    # Lupa: mango, aro y cristal
    draw.line([(s(80), s(76)), (s(87), s(83))], fill=SPARK_AMBER + (255,), width=max(2, int(s(5.5))))
    draw.ellipse([s(60), s(56), s(84), s(80)], outline=DISCOVERY_CYAN + (255,), width=max(2, int(s(4.5))))
    circle(72, 68, 9.5, fill=DEEP_SPACE + (140,))

    # Destello de descubrimiento en el cristal
    cx, cy = s(72), s(68)
    r_out, r_in = s(7), s(3)
    pts = []
    for i in range(8):
        ang = -math.pi / 2 + i * math.pi / 4
        rad = r_out if i % 2 == 0 else r_in
        pts.append((cx + rad * math.cos(ang), cy + rad * math.sin(ang)))
    draw.polygon(pts, fill=WHITE + (255,))

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
