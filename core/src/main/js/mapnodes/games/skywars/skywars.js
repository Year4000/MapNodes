/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import AbstractGameMode from '../abstract_game_mode.js'
import { game_mode } from '../game_registry.js'

/**
 * SkyWars is a game mode that is a FFA where players fight until the last one stands.
 */
@game_mode('skywars')
export default class SkyWars extends AbstractGameMode {
  /** The config schema for the game mode */
  static get schema() {
    return {
      players_team: { type: 'string' },
      // Min players in the
      start_size: { type: 'number', value: 2 },
    }
  }
}
