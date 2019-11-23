const path = require('path')

module.exports = {
  mode: 'development',
  devtool: 'inline-source-map',
  entry: {
    mapnodes: './src/main/js/bindings.js',
  },
  output: {
    path: path.resolve(__dirname, 'src/generated/js'),
    filename: '[name].bundle.js',
  },
  module: {
    rules: [
      // Transfile the source files so we can use newer syntax
      {
        test: /\.js$/,
        exclude: /(node_modules)/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: [
              [
                '@babel/env',
                {
                  modules: false,
                  targets: {
                    'node': '7.4.0'
                  }
                },
              ],
            ],
            plugins: [
              '@babel/plugin-proposal-class-properties',
              [ '@babel/plugin-proposal-decorators', { decoratorsBeforeExport: true } ],
            ]
          }
        }
      },
    ]
  }
};
