"""Generate legacy launcher PNGs (API 21-25 fallback) for the USB shortcut app.

Draws the same trident glyph as the adaptive-icon vector at 4x super-sampling.
Run: python make_icons.py
"""
import os
from PIL import Image, ImageDraw

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app", "src", "main", "res")
BG = (11, 87, 208, 255)      # #0B57D0
FG = (255, 255, 255, 255)

DENSITIES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

SS = 4  # supersample factor


def draw_icon(size):
    w = size * SS
    img = Image.new("RGBA", (w, w), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    # Rounded-square background, slightly inset so launchers can round it further.
    pad = w * 0.02
    d.rounded_rectangle([pad, pad, w - pad, w - pad], radius=w * 0.22, fill=BG)

    # Geometry mirrors ic_launcher_foreground.xml in a 108-unit viewport.
    u = w / 108.0

    def p(x, y):
        return (x * u, y * u)

    r = 3.0 * u
    d.line([p(54, 78), p(54, 36)], fill=FG, width=int(round(6 * u)), joint="curve")
    d.line([p(54, 63), p(41, 51)], fill=FG, width=int(round(6 * u)))
    d.line([p(54, 55), p(69, 43)], fill=FG, width=int(round(6 * u)))

    for cx, cy in [(54, 78), (41, 51), (69, 43)]:
        x, y = p(cx, cy)
        d.ellipse([x - r, y - r, x + r, y + r], fill=FG)

    # left round terminal
    cx, cy = p(41, 51)
    rr = 6.5 * u
    d.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], fill=FG)

    # right square terminal
    d.rectangle([*p(64.5, 38.5), *p(73.5, 47.5)], fill=FG)

    # arrow head
    d.polygon([p(54, 21), p(64, 37), p(44, 37)], fill=FG)

    return img.resize((size, size), Image.LANCZOS)


def main():
    for density, size in DENSITIES.items():
        out_dir = os.path.join(BASE, "mipmap-" + density)
        os.makedirs(out_dir, exist_ok=True)
        path = os.path.join(out_dir, "ic_launcher.png")
        draw_icon(size).save(path, "PNG")
        print("wrote", path, size)


if __name__ == "__main__":
    main()
