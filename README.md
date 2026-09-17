# Cursor Tick Ring

A highly customizable tick ring anchored to the mouse cursor. Heavily inspired by WoW GCD mouse trackers.

![](docs/demo.gif)

<p align="center">
  <img src="docs/demo.gif">
</p>

## Features

- Gametick synced progress circle around the mouse cursor
- Fill, remaining, and rotating sweep styles
- Heavily configurable (size, color, thickness, idle fadeout, smoothness, circle segments)
- Optional tick labels, tick cycles, click feedback, tick pulse, accented ticks
- Configurable cursor replacements (dot, crosshair, hidden, system).
- Restart cycle and circle overlay hotkeys.
- Experimental attack timer mode (off by default)
    - Current limitation: food delay, special attacks, untested weapons

![attack timer mode demo](https://i.imgur.com/rkuMAw3.mp4)

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
- Version 1.0.1 
    - Added attack timer mode for _most_ weapons. Mode overrides the default tick labels.
    - Removed leftover smoothing animation from sweep mode.