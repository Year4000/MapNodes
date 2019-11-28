/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import AbstractGameMode from '../abstract_game_mode.js'
import { game_mode } from '../game_registry.js'

/**
 * Destroy is a game mode where teams must try and destroy the opponents monument.
 */
@game_mode('destroy')
export default class Destroy extends AbstractGameMode {
}
