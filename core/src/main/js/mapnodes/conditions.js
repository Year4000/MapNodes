/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */

/**
 * Make sure the value is not null
 *
 * @template T
 * @param {T} value
 * @param {string} message
 * @return {T}
 */
export const not_null = (value, message) => {
  if (value == null) {
    throw new Error(message ?? 'The value was null')
  }
  return value
}

/**
 * Make sure the value is true
 *
 * @template T
 * @param {T} value
 * @param {string} message
 * @return {T}
 */
export const is_true = (value, message) => {
  if (!value) {
    throw new Error(message ?? 'The value was not true')
  }
  return value
}

/**
 * Make sure the value is an object
 *
 * @template T
 * @param {T} value
 * @param {string} message
 * @return {T}
 */
export const is_object = (value, message) => {
  if (typeof value !== 'object') {
    throw new Error(`${message ?? 'The value was not an object'} it was '${typeof value}' and its value is: ${value}`)
  }
  return value
}

/** Export the conditions as an object */
export default {
  not_null,
  is_true,
  is_object,
}
