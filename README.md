# Cursor Tick Ring

A highly customizable tick ring anchored to the mouse cursor. Heavily inspired by WoW GCD mouse trackers.

<p align="center">
  <img src="docs/demo.gif">
</p>

## Features

- Gametick synced progress circle around the mouse cursor
- Fill, remaining, and rotating sweep styles
- Heavily configurable (size, color, thickness, idle fadeout, smoothness, circle segments)
- Optional sidebar designer with a live preview, sliders, and color pickers
- Optional tick labels, tick cycles, click feedback, tick pulse, accented ticks
- Configurable cursor replacements (dot, crosshair, hidden, system).
- Restart cycle and circle overlay hotkeys.
- Experimental attack timer mode (off by default)
    - Includes food delay and Ancient Magicks casting
    - Current limitations: special attacks and untested weapons

### Sidebar designer

Enable **Sidebar designer** at the top of the plugin settings, then open the ring icon in the sidebar. Choose Ring, Cursor, Label, Effects, or Timing to edit the corresponding settings. Changes save automatically and also appear in the regular config.

The preview animates while the panel is visible, including when logged out. Click the preview to try click feedback. Large designs scale down to fit; attack mode previews a sample four-tick countdown.

Food delay is added after an Eat action is confirmed by inventory consumption. Standard food adds three ticks to an active attack cooldown; karambwans, halibut, and supported gnome foods add two. Combo eating stacks delays. Potions do not add food delay, and eating while already ready to attack does not start a countdown.

### Ring styles
<p align="center">
  <img src="docs/reverse.gif">
  <img src="docs/sweep.gif">
</p>

### Attack Mode

<p align="center">
  <img src="docs/attack.gif">
</p>

## Changelog
- Version 1.0.2
    - Added a sidebar designer with a live cursor preview.
    - Added smooth reset fading to the remaining/reverse style.
    - Added food delay to attack mode, including combo eating.
    - Added the missing Ancient Magicks casting animations.
- Version 1.0.1 
    - Added attack timer mode for _most_ weapons. Mode overrides the default tick labels.
    - Removed leftover smoothing animation from sweep mode.
