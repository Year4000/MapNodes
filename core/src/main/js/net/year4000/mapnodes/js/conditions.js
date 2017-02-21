
/** Conditions that must be meet */
class Conditions {

  /** Make sure the value is not null */
  static not_null(value, message) {
    if (value == null) {
      throw new Error(message || 'The value was null');
    }
    return value;
  }

  /** Make sure the value is an object */
  static is_object(value, message) {
      if (typeof value === 'object') {
          throw new Error(message || 'The value was not an object');
      }
      return value;
  }
}
