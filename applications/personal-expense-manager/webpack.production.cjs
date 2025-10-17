const { merge } = require('webpack-merge');
const common = require('./webpack.common.cjs');
const Dotenv = require('dotenv-webpack');

module.exports = merge(common, {
  mode: 'production',

  plugins: [
    new Dotenv({
      path: './.env.production',
      systemvars: true,
      silent: true,
    }),
  ],
});