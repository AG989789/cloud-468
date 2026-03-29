const mongoose = require('mongoose');

mongoose.connect('mongodb://mongo:27017/sharks', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

module.exports = mongoose;
