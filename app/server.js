const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();

// Middleware
app.use(bodyParser.urlencoded({ extended: true }));

// MongoDB connection (IMPORTANT: use "mongo")
mongoose.connect('mongodb://mongo:27017/sharks', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

// View engine setup
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Routes
const indexRoutes = require('./routes/index');
app.use('/', indexRoutes);

// Start server
app.listen(3000, () => {
  console.log('Server running on port 3000');
});
