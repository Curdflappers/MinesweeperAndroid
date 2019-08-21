# Minesweeper (Android)

A streamlined implementation of the classic game, this time made with Android Studio. Continued from [Minesweeper](https://github.com/Curdflappers/Minesweeper), this implementation doesn't drain battery!

## Setup
[Download the app from Google Play](https://play.google.com/store/apps/details?id=com.curdflappers.minesweeper) or clone this repo and open with Android Studio.

## Current elements

- Customize difficulty (rows, columns, mines) or use preset difficulties (easy, medium, hard)
- Long tap to perform opposite action (flag vs sweep)
- Tap a revealed spot to automatically sweep its neighbors if the appropriate flags have been placed
- First sweep is always safe
- Toast messages appear on game completion
- Flag placement is restricted, users can't place any more flags than necessary
- Timer tracks game time, and high score tracker works for preset difficulties
- Music and sound effects enhance user experience
- Device rotation is supported

## Currently in development

- Save games/preferences between sessions

## Roadmap

v1.0
- Better user configurations
  - Drag a slider and get a preview of the spot size without manually editing rows, columns
  - Choose minefield density instead of a mine count

v1.1
- Autosolver!
  - Watch a computer solve the field

- Hints
  - Use autosolver functionality to request help whenever you're stuck

v1.2
- More flags!
  - Use multiple flag colors to match your playstyle ("definitely" flag, "maybe" flag)
  
- Hide the status bar
  - Make your entire screen a minefield with no distractions!
  
v1.3
- Different game modes!
  - Infinite Sweeper: Field gets denser as you venture from the center
  - No-Flag Mode: Challenge yourself by clearing the field without flags
  - Valley Sweeper: Fixed number of columns, start at the top and work your way down infinite rows
