const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.cjs');
const Dotenv = require('dotenv-webpack');


module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',

  devServer: {
    static: path.resolve(__dirname, 'dist'),
    hot: true,
    historyApiFallback: true,
    port: 4001,
    open: false,
  },

  plugins: [
    new Dotenv({
      path: './.env.development',
      systemvars: true,
      silent: true,
    }),
  ],
});