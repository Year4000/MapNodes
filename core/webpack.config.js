const path = require('path')


module.exports = {
  mode: 'production',
  devtool: 'source-map',
  target: 'node',
  // We are not running on the web so we dont need to download files, so changes reflect that
  optimization: {
    minimize: false,
    namedModules: true,
    namedChunks: true,
    moduleIds: 'named',
    chunkIds: 'named',
  },
  entry: {
    mapnodes: './src/main/js/index.js',
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
        use: 'ts-loader',
      },
    ],
  },
}
