/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import AbstractGameMode from '../abstract_game_mode.js'
import { game_mode } from '../game_registry.js'

/**
 * Spleef is a game mode where players have a wand that shoots snowballs that break blocks.
 * All while players must constancy run around as the floor will decay in stages.
 */
@game_mode('spleef')
export default class Spleef extends AbstractGameMode {
  static get schema() {
    return {}
  }
}
