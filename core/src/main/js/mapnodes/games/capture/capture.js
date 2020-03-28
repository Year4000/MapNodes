/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import AbstractGameMode from '../abstract_game_mode.js'
import { game_mode } from '../game_registry.js'

/**
 * Capture is a game mode that teams must capture the opponents banner or wool.
 * Older maps will use wool as the flag, but newer maps will use the banners.
 */
@game_mode('capture')
export default class Capture extends AbstractGameMode {
  static get schema() {
    return {}
  }
}
