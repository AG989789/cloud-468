const mongoose = require('../db');

const sharkSchema = new mongoose.Schema({
  name: String
});

module.exports = mongoose.model('Shark', sharkSchema);
