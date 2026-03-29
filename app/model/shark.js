const mongoose = require('mongoose');

const sharkSchema = new mongoose.Schema({
  name: String
});

module.exports = mongoose.model('Shark', sharkSchema);
