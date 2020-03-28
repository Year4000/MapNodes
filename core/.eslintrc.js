module.exports = {
  parser: '@typescript-eslint/parser',
  plugins: ['@typescript-eslint'],
  extends: ['airbnb-base'],
  parserOptions: {
    ecmaVersion: 11,
    sourceType: 'module',
  },
  env: {
    es6: true,
    node: true,
  },
  rules: {
    semi: ['error', 'never'],
    quotes: ['error', 'single'],
    'object-curly-spacing': ['error', 'always'],
    'no-trailing-spaces': 'error',
    'space-before-function-paren': ['error', {
      anonymous: 'never',
      named: 'never',
      asyncArrow: 'always',
    }],
    'comma-dangle': ['error', 'always-multiline'],
    'no-mixed-spaces-and-tabs': 'error',
    'no-underscore-dangle': 'off',
    'no-plusplus': 'off',
    'import/extensions': 'off',
    'no-mixed-operators': 'off',
    'camelcase': 'off', // todo
    'max-len': 'off',
    'no-unused-vars': 'off',
    'class-methods-use-this': 'off',
    'no-param-reassign': 'off',
    'import/prefer-default-export': 'off',
    'no-console': 'off',
    'max-classes-per-file': 'off',
    'no-restricted-syntax': 'off',
    'no-shadow': 'warn',
    'lines-between-class-members': ['error', 'always', {
      exceptAfterSingleLine: true,
    }],
  },
  globals: {
    $: 'readonly',
    var_dump: 'readonly',
  }
}
