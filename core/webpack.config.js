const path = require('path')

module.exports = {
  mode: 'production',
  devtool: 'source-map',
  // We are not running on the web so we dont need to download files, so changes reflect that
  optimization: {
    minimize: false,
    namedModules: true,
    namedChunks: true,
    moduleIds: 'named',
    chunkIds: 'named',
  },
  entry: {
    mapnodes: './src/main/js/bindings.js',
  },
  output: {
    path: path.resolve(__dirname, 'src/generated/js'),
    filename: '[name].bundle.js',
  },
  module: {
    rules: [
      // Transpile the source files so we can use newer syntax
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
                  targets: {
                    'node': '7.4.0'
                  }
                },
              ],
            ],
            plugins: [
              '@babel/plugin-proposal-optional-chaining',
              '@babel/plugin-proposal-class-properties',
              [ '@babel/plugin-proposal-decorators', { decoratorsBeforeExport: true } ],
            ]
          }
        }
      },
    ]
  }
}
